package com.iemr.flw.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.iemr.flw.service.BeneficiaryService;
import com.iemr.flw.service.DiagnosticOrderService;
import com.iemr.flw.service.DynamicFormResponseService;
import com.iemr.flw.service.StopTBService;
import com.iemr.flw.service.StopTBSyncService;
import com.iemr.flw.service.TBConfirmedCaseService;
import com.iemr.flw.service.TBSuspectedService;
import com.iemr.flw.service.sync.StopTBSyncType;
import com.iemr.flw.service.sync.SyncToken;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * STOP-TB changes feed. AMRIT-DB V132 / V32 install t_sync_change_log and its triggers on every
 * server; tablets call the feed on their camp hub (or UAT central when testing).
 *
 * The tablet keeps one opaque token. With none it gets a first full download in pages (snapshot);
 * after that, each call returns only the records with change-log lines after its token, in their
 * current state, plus the next token. Village scope is resolved here through i_ben_flow_outreach,
 * the same way today's getAll endpoints do.
 *
 * @author Piramal Swasthya
 */
@Service
public class StopTBSyncServiceImpl implements StopTBSyncService {

    private final Logger logger = LoggerFactory.getLogger(StopTBSyncServiceImpl.class);

    static final int DEFAULT_LIMIT = 200;
    static final int MAX_LIMIT = 500;
    /**
     * Beneficiary records are the expensive ones (several identity lookups and a FHIR call each), so a
     * page carries at most this many; the token resumes from the last one sent.
     */
    static final int MAX_BENEFICIARIES_PER_PAGE = 25;
    /** Lines younger than this may still have earlier seqs uncommitted; they wait for the next call. */
    static final int SAFE_LAG_SECONDS = 5;
    /** The hub keeps 30 days of log; a token older than this may point at cleaned-up lines. */
    static final long MAX_TOKEN_AGE_SECONDS = 29L * 24 * 3600;
    private static final long SUPPORT_CHECK_TTL_MS = 5 * 60 * 1000L;
    private static final String FLOW_TABLE = "db_iemr.i_ben_flow_outreach";

    // Same date formats the existing getAll endpoints use for these records.
    private final Gson stopTbGson = new GsonBuilder().serializeNulls().create();

    @PersistenceContext(unitName = "db_iemr")
    private EntityManager em;

    @Autowired
    private BeneficiaryService beneficiaryService;
    @Autowired
    private StopTBService stopTBService;
    @Autowired
    private TBSuspectedService tbSuspectedService;
    @Autowired
    private TBConfirmedCaseService tbConfirmedCaseService;
    @Autowired
    private DynamicFormResponseService dynamicFormResponseService;
    @Autowired
    private DiagnosticOrderService diagnosticOrderService;
    @Autowired
    private ObjectMapper objectMapper;

    private volatile Boolean supported;
    private volatile long supportCheckedAt;

    @Override
    public Map<String, Object> getChanges(String token, Integer providerServiceMapID, List<Integer> villageIds,
                                          List<String> types, Integer limit, String authorisation) {
        if (!isSupported()) {
            Map<String, Object> out = new LinkedHashMap<>();
            out.put("supported", false);
            return out;
        }
        if (providerServiceMapID == null) throw new IllegalArgumentException("providerServiceMapID is required");
        if (villageIds == null || villageIds.isEmpty()) throw new IllegalArgumentException("villageIds is required");
        int pageSize = limit == null || limit <= 0 ? DEFAULT_LIMIT : Math.min(limit, MAX_LIMIT);

        String serverId = serverId();
        SyncToken parsed = SyncToken.parse(token);
        long now = System.currentTimeMillis() / 1000;
        boolean usable = parsed != null && serverId.equals(parsed.serverId())
                && (parsed.kind() == SyncToken.Kind.SNAPSHOT || now - parsed.issuedAt() <= MAX_TOKEN_AGE_SECONDS);

        Scope scope = new Scope(providerServiceMapID, villageIds, requestedTypes(types));
        // A token covers only the types it was issued for; newly requested types need a snapshot.
        usable = usable && parsed.typesMask() == scope.typesMask();
        if (!usable) {
            if (parsed != null) logger.info("Sync token {} not usable here, starting a snapshot", parsed);
            return snapshotPage(serverId, SyncToken.snapshot(serverId, safePoint(), 0, 0, scope.typesMask()),
                    scope, pageSize, authorisation);
        }
        return parsed.kind() == SyncToken.Kind.SNAPSHOT
                ? snapshotPage(serverId, parsed, scope, pageSize, authorisation)
                : deltaPage(serverId, parsed.seq(), scope, pageSize, authorisation);
    }

    // ── Snapshot: everything in scope, type by type, keyset-paged ─────────────

    private Map<String, Object> snapshotPage(String serverId, SyncToken at, Scope scope, int pageSize,
                                             String authorisation) {
        StopTBSyncType[] types = StopTBSyncType.values();
        int typeIndex = at.typeIndex();
        long after = at.afterKey();
        List<Map<String, Object>> items = new ArrayList<>();

        while (items.size() < pageSize && typeIndex < types.length) {
            StopTBSyncType type = types[typeIndex];
            if (!scope.types.contains(type)) {
                typeIndex++;
                after = 0;
                continue;
            }
            int want = pageSize - items.size();
            if (type == StopTBSyncType.BENEFICIARY) want = Math.min(want, MAX_BENEFICIARIES_PER_PAGE);
            List<Long> keys = snapshotKeys(type, scope, after, want);
            Map<Long, String> records = build(type, keys, authorisation);
            for (Long key : keys) {
                String data = records.get(key);
                if (data != null) items.add(item(type, "upsert", key, at.seq(), data));
            }
            if (keys.size() < want) {
                typeIndex++;
                after = 0;
            } else {
                after = keys.get(keys.size() - 1);
                break;
            }
        }

        boolean done = typeIndex >= types.length;
        SyncToken next = done
                ? SyncToken.delta(serverId, at.seq(), System.currentTimeMillis() / 1000, scope.typesMask())
                : SyncToken.snapshot(serverId, at.seq(), typeIndex, after, scope.typesMask());
        return page(serverId, true, items, next, !done);
    }

    @SuppressWarnings("unchecked")
    private List<Long> snapshotKeys(StopTBSyncType type, Scope scope, long after, int limit) {
        List<Object> rows = em.createNativeQuery(type.snapshotKeySql())
                .setParameter("psm", scope.psm)
                .setParameter("villages", scope.villages)
                .setParameter("after", after)
                .setParameter("limit", limit)
                .getResultList();
        List<Long> keys = new ArrayList<>(rows.size());
        for (Object r : rows) keys.add(((Number) r).longValue());
        return keys;
    }

    // ── Delta: records with log lines after the token ─────────────────────────

    @SuppressWarnings("unchecked")
    private Map<String, Object> deltaPage(String serverId, long from, Scope scope, int pageSize,
                                          String authorisation) {
        long safe = Math.max(safePoint(), from);
        // One row per record: its latest line in range, and that line's op. Deduplicates a record
        // changed many times since the token.
        List<Object[]> rows = em.createNativeQuery(
                        "SELECT l.entity, l.row_pk, l.seq, l.op FROM db_iemr.t_sync_change_log l JOIN ("
                                + "  SELECT c.entity, c.row_pk, MAX(c.seq) AS last_seq FROM db_iemr.t_sync_change_log c "
                                + "  WHERE c.seq > :from AND c.seq <= :safe AND c.entity IN (:entities) AND ("
                                + "    (c.ben_reg_id IS NOT NULL AND EXISTS (SELECT 1 FROM db_iemr.i_ben_flow_outreach f "
                                + "      WHERE f.beneficiary_reg_id = c.ben_reg_id AND f.providerServiceMapID = :psm AND f.villageID IN (:villages)))"
                                + "    OR (c.beneficiary_id IS NOT NULL AND EXISTS (SELECT 1 FROM db_iemr.i_ben_flow_outreach f "
                                + "      WHERE f.beneficiary_id = c.beneficiary_id AND f.providerServiceMapID = :psm AND f.villageID IN (:villages))))"
                                + "  GROUP BY c.entity, c.row_pk ORDER BY last_seq LIMIT :limit"
                                + ") g ON l.seq = g.last_seq ORDER BY l.seq")
                .setParameter("from", from)
                .setParameter("safe", safe)
                .setParameter("entities", scope.logEntities())
                .setParameter("psm", scope.psm)
                .setParameter("villages", scope.villages)
                .setParameter("limit", pageSize)
                .getResultList();
        boolean full = rows.size() == pageSize;

        // Keep beneficiary records per page bounded: stop before the one over the cap. The token then
        // resumes from the last row kept, so nothing after it is skipped.
        int beneficiaries = 0;
        for (int i = 0; i < rows.size(); i++) {
            if (!StopTBSyncType.BENEFICIARY.logEntity().equals(rows.get(i)[0])) continue;
            if (++beneficiaries > MAX_BENEFICIARIES_PER_PAGE) {
                rows = new ArrayList<>(rows.subList(0, i));
                full = true;
                break;
            }
        }

        // Build each type's records in one batch, then emit in log order.
        Map<StopTBSyncType, List<Long>> keysByType = new EnumMap<>(StopTBSyncType.class);
        for (Object[] r : rows) {
            StopTBSyncType type = StopTBSyncType.fromLogEntity((String) r[0]);
            if (type != null) keysByType.computeIfAbsent(type, t -> new ArrayList<>()).add(((Number) r[1]).longValue());
        }
        Map<StopTBSyncType, Map<Long, String>> built = new EnumMap<>(StopTBSyncType.class);
        keysByType.forEach((type, keys) -> built.put(type, build(type, keys, authorisation)));

        List<Map<String, Object>> items = new ArrayList<>();
        long lastSeq = from;
        for (Object[] r : rows) {
            lastSeq = ((Number) r[2]).longValue();
            StopTBSyncType type = StopTBSyncType.fromLogEntity((String) r[0]);
            if (type == null) continue;
            long key = ((Number) r[1]).longValue();
            String data = built.get(type).get(key);
            String op = r[3] == null ? "U" : r[3].toString();
            if (data != null && !"D".equals(op)) {
                items.add(item(type, "upsert", key, lastSeq, data));
            } else if ("D".equals(op) || type != StopTBSyncType.BENEFICIARY) {
                // Gone, soft-deleted or no longer a record the app keeps (e.g. an OPD visit with no
                // complaint or drug). A beneficiary that just failed to build is never deleted on the
                // tablet; only a deleted mapping removes it.
                items.add(item(type, "delete", key, lastSeq, null));
            }
        }

        boolean hasMore = full;
        long nextSeq = hasMore ? lastSeq : safe;
        return page(serverId, false, items,
                SyncToken.delta(serverId, nextSeq, System.currentTimeMillis() / 1000, scope.typesMask()), hasMore);
    }

    // ── Records, in the same JSON each type's existing endpoint returns ───────

    private Map<Long, String> build(StopTBSyncType type, List<Long> keys, String authorisation) {
        Map<Long, String> out = new HashMap<>();
        if (keys.isEmpty()) return out;
        try {
            switch (type) {
                case BENEFICIARY:
                    return beneficiaryService.getBeneficiaryRecordsByRegIds(keys, authorisation);
                case TB_SCREENING:
                    stopTBService.getNurseTBScreeningsByIds(keys).forEach((k, m) -> out.put(k, stopTbGson.toJson(m)));
                    return out;
                case GENERAL_EXAMINATION:
                    stopTBService.getGeneralExaminationsByIds(keys).forEach((k, m) -> out.put(k, stopTbGson.toJson(m)));
                    return out;
                case GENERAL_OPD:
                    stopTBService.getGeneralOpdByVisitIds(keys).forEach((k, m) -> out.put(k, stopTbGson.toJson(m)));
                    return out;
                case GENERAL_OPD_LEGACY:
                    stopTBService.getLegacyGeneralOpdByIds(keys).forEach((k, m) -> out.put(k, stopTbGson.toJson(m)));
                    return out;
                case DIAGNOSTICS:
                    stopTBService.getDiagnosticsByIds(keys).forEach((k, m) -> out.put(k, stopTbGson.toJson(m)));
                    return out;
                case TB_SUSPECTED:
                    return tbSuspectedService.getRecordsByIds(keys);
                case TB_CONFIRMED:
                    return tbConfirmedCaseService.getRecordsByIds(keys);
                case FORM_RESPONSE:
                    for (Map.Entry<Long, ?> e : dynamicFormResponseService.getResponsesByIds(keys).entrySet()) {
                        out.put(e.getKey(), objectMapper.writeValueAsString(e.getValue()));
                    }
                    return out;
                case DIAGNOSTIC_ORDER:
                    for (Map.Entry<Long, Map<String, Object>> e : diagnosticOrderService.getOrderRecordsByIds(keys).entrySet()) {
                        out.put(e.getKey(), objectMapper.writeValueAsString(e.getValue()));
                    }
                    return out;
                default:
                    return out;
            }
        } catch (Exception e) {
            // A failed batch must not turn into deletes on the tablet: fail the whole call, so the
            // token doesn't move and the tablet retries.
            throw new IllegalStateException("Could not build " + type.apiName() + " records: " + e.getMessage(), e);
        }
    }

    private Map<String, Object> item(StopTBSyncType type, String op, long key, long version, String data) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("type", type.apiName());
        m.put("op", op);
        m.put("key", key);
        m.put("version", version);
        // The record exactly as the type's existing endpoint returns it, so the app reuses its parser.
        m.put("data", data == null ? null : JsonParser.parseString(data));
        return m;
    }

    private Map<String, Object> page(String serverId, boolean snapshot, List<Map<String, Object>> items,
                                     SyncToken next, boolean hasMore) {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("supported", true);
        out.put("serverId", serverId);
        out.put("snapshot", snapshot);
        out.put("items", items);
        out.put("nextToken", next.encode());
        out.put("hasMore", hasMore);
        return out;
    }

    // ── Hub state ─────────────────────────────────────────────────────────────

    private String serverId() {
        return String.valueOf(em.createNativeQuery("SELECT @@server_uuid").getSingleResult());
    }

    private long safePoint() {
        Object r = em.createNativeQuery("SELECT COALESCE(MAX(seq), 0) FROM db_iemr.t_sync_change_log "
                        + "WHERE changed_at <= NOW(6) - INTERVAL " + SAFE_LAG_SECONDS + " SECOND")
                .getSingleResult();
        return ((Number) r).longValue();
    }

    /**
     * True only where the change log exists AND every source table has its insert, update and delete
     * triggers. Otherwise a change could be missed silently, so the feed reports itself unsupported and
     * the app keeps using the full pull.
     */
    @SuppressWarnings("unchecked")
    boolean isSupported() {
        long now = System.currentTimeMillis();
        if (supported != null && now - supportCheckedAt < SUPPORT_CHECK_TTL_MS) return supported;
        boolean ok;
        try {
            Number logTables = (Number) em.createNativeQuery("SELECT COUNT(*) FROM information_schema.tables "
                    + "WHERE table_schema = 'db_iemr' AND table_name = 't_sync_change_log'").getSingleResult();
            Set<String> withAllTriggers = new HashSet<>();
            List<Object[]> rows = em.createNativeQuery("SELECT LOWER(CONCAT(event_object_schema, '.', event_object_table)), "
                    + "COUNT(DISTINCT event_manipulation) FROM information_schema.triggers "
                    + "WHERE trigger_name LIKE 'trg\\_scl\\_%' GROUP BY event_object_schema, event_object_table").getResultList();
            for (Object[] r : rows) {
                String table = (String) r[0];
                // The flow table only decides scope: insert and update are enough (rows are never deleted).
                int needed = FLOW_TABLE.equals(table) ? 2 : 3;
                if (((Number) r[1]).intValue() >= needed) withAllTriggers.add(table);
            }
            List<String> missing = new ArrayList<>();
            for (StopTBSyncType type : StopTBSyncType.values()) {
                for (String table : type.sourceTables()) {
                    if (!withAllTriggers.contains(table)) missing.add(table);
                }
            }
            ok = logTables.intValue() > 0 && missing.isEmpty();
            if (logTables.intValue() > 0 && !missing.isEmpty()) {
                logger.error("STOP-TB changes feed disabled: change-log triggers missing on {}", missing);
            }
        } catch (Exception e) {
            logger.warn("STOP-TB changes feed: could not check the change log: {}", e.getMessage());
            ok = false;
        }
        supported = ok;
        supportCheckedAt = now;
        return ok;
    }

    private static Set<StopTBSyncType> requestedTypes(List<String> names) {
        Set<StopTBSyncType> out = EnumSet.noneOf(StopTBSyncType.class);
        if (names == null || names.isEmpty()) {
            out.addAll(EnumSet.allOf(StopTBSyncType.class));
            return out;
        }
        for (StopTBSyncType t : StopTBSyncType.values()) {
            if (names.contains(t.apiName())) out.add(t);
        }
        if (out.isEmpty()) throw new IllegalArgumentException("No known record types in " + names);
        return out;
    }

    private static final class Scope {
        final Integer psm;
        final List<Integer> villages;
        final Set<StopTBSyncType> types;

        Scope(Integer psm, List<Integer> villages, Set<StopTBSyncType> types) {
            this.psm = psm;
            this.villages = villages;
            this.types = types;
        }

        long typesMask() {
            long mask = 0;
            for (StopTBSyncType t : types) mask |= 1L << t.ordinal();
            return mask;
        }

        List<String> logEntities() {
            List<String> out = new ArrayList<>();
            for (StopTBSyncType t : types) out.add(t.logEntity());
            return out;
        }
    }
}
