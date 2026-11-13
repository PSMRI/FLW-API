package com.iemr.flw.service.impl;

import com.iemr.flw.domain.iemr.BenVisitDetail;
import com.iemr.flw.domain.iemr.DiagnosticOrder;
import com.iemr.flw.domain.iemr.DiagnosticResult;
import com.iemr.flw.domain.iemr.TBSuspected;
import com.iemr.flw.domain.iemr.User;
import com.iemr.flw.dto.DiagnosticOrderRequestDto;
import com.iemr.flw.dto.DiagnosticOrderResultDto;
import com.iemr.flw.dto.DiagnosticResultItemDto;
import com.iemr.flw.dto.DiagnosticOrderStatusSummaryDto;
import com.iemr.flw.dto.ManualDiagnosticResultRequestDto;
import com.iemr.flw.dto.VendorHealthDto;
import com.iemr.flw.integration.provider.DiagnosticCancelResult;
import com.iemr.flw.integration.provider.DiagnosticDocumentAsset;
import com.iemr.flw.integration.provider.DiagnosticPollResult;
import com.iemr.flw.integration.provider.TrueNatResultRules;
import com.iemr.flw.integration.provider.DiagnosticProvider;
import com.iemr.flw.integration.provider.DiagnosticProviderFactory;
import com.iemr.flw.integration.provider.DiagnosticPushResult;
import com.iemr.flw.masterEnum.DiagnosticOrderStatus;
import com.iemr.flw.masterEnum.DiagnosticCloseStage;
import com.iemr.flw.masterEnum.DiagnosticResultType;
import com.iemr.flw.masterEnum.DiagnosticOrderType;
import com.iemr.flw.repo.identity.BeneficiaryRepo;
import com.iemr.flw.repo.iemr.DiagnosticDocumentRepo;
import com.iemr.flw.repo.iemr.DiagnosticOrderRepo;
import com.iemr.flw.repo.iemr.DiagnosticResultRepo;
import com.iemr.flw.repo.iemr.EmployeeMasterRepo;
import com.iemr.flw.repo.iemr.TBSuspectedRepo;
import com.iemr.flw.service.CampConfigService;
import com.iemr.flw.service.DiagnosticDocumentService;
import com.iemr.flw.service.DiagnosticOrderService;
import com.iemr.flw.service.TBStopVisitService;
import com.iemr.flw.utils.JwtUtil;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class DiagnosticOrderServiceImpl implements DiagnosticOrderService {

    private static final Logger logger = LoggerFactory.getLogger(DiagnosticOrderServiceImpl.class);

    private static final Set<String> BLOCKING_STATUSES = Set.of(
            DiagnosticOrderStatus.PENDING.name(),
            DiagnosticOrderStatus.COMPLETED.name(),
            DiagnosticOrderStatus.MANUAL_ENTRY.name(),
            DiagnosticOrderStatus.INVALID.name());

    private static final Set<String> NON_REUSABLE_ON_CLOSE_STATUSES = Set.of(
            DiagnosticOrderStatus.COMPLETED.name(),
            DiagnosticOrderStatus.FAILED.name(),
            DiagnosticOrderStatus.CLOSED.name());

    // Statuses the scheduler polls and the daily cutoff expires — matches findXrayDueForPoll /
    // findTrueNatDueForPoll / findAllOutstandingOrders. INVALID (TrueNat only) is still polled: the
    // machine retests the same order itself and the retest result arrives on it later.
    private static final Set<String> OUTSTANDING_STATUSES = Set.of(
            DiagnosticOrderStatus.PENDING.name(),
            DiagnosticOrderStatus.IN_PROGRESS.name(),
            DiagnosticOrderStatus.INVALID.name());

    // Invalid test outcomes: the result is still recorded (result row, tb_suspected write-back, documents)
    // but the order is CLOSED instead of COMPLETED, so a retest can be pushed. The vendor reports an
    // invalid MTB run as "Error-2"; a manual entry uses "Invalid/Error".
    private static final String XRAY_INVALID_RESULT = "AI Invalid Result";
    private static final String XRAY_TB_PRESUMPTIVE = "TB Presumptive";
    private static final String SPUTUM_INVALID_POLLED_RESULT = "Error-2";
    private static final String SPUTUM_INVALID_MANUAL_RESULT = "Invalid/Error";

    @Autowired
    private DiagnosticOrderRepo diagnosticOrderRepo;

    @Autowired
    private DiagnosticResultRepo diagnosticResultRepo;

    @Autowired
    private TBSuspectedRepo tbSuspectedRepo;

    @Autowired
    private DiagnosticProviderFactory providerFactory;

    @Autowired
    private DiagnosticDocumentService diagnosticDocumentService;

    @Autowired
    private DiagnosticDocumentRepo diagnosticDocumentRepo;

    @Autowired
    private CampConfigService campConfigService;

    @Autowired
    private BeneficiaryRepo beneficiaryRepo;

    @Autowired
    private TBStopVisitService tbStopVisitService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmployeeMasterRepo employeeMasterRepo;

    @Override
    public DiagnosticOrder createAndPushOrderByUser(DiagnosticOrderRequestDto request, String jwtToken) throws Exception {
        return createAndPushOrder(request, resolveActingUserName(jwtToken));
    }

    private String resolveActingUserName(String jwtToken) {
        try {
            User user = employeeMasterRepo.findUserByUserID(jwtUtil.extractUserId(jwtToken));
            if (user != null && user.getUserName() != null && !user.getUserName().isBlank()) {
                return user.getUserName();
            }
        } catch (Exception e) {
            logger.warn("Could not resolve acting user's username from m_user: {}", e.getMessage());
        }
        return jwtUtil.extractUsername(jwtToken);
    }

    @Override
    public DiagnosticOrder createAndPushOrderAsSystem(DiagnosticOrderRequestDto request) throws Exception {
        return createAndPushOrder(request, "SYSTEM");
    }

    // One lock per beneficiary, held across visit lookup → dedup check → insert → vendor push. Without it,
    // two simultaneous pushes for the same beneficiary could both pass the dedup check (or both create a
    // visit for today) and send the same patient to the vendor twice — shown as duplicate names on the
    // TrueNat machine. Keyed by beneficiary, not orderType, because the visit is shared across order types.
    // (release-3.9.3 keys its lock by beneficiary+orderType because there the client supplies the
    // visitCode; here the server creates the visit, so the key must be the beneficiary alone.)
    // In-memory, so it only serialises within this JVM: correct while orders are created only by the
    // single van server that can reach the vendor. Same mechanism as release-3.9.3: one ReentrantLock per
    // beneficiary, held weakly so an idle beneficiary's lock is garbage-collected — any thread waiting on
    // or holding it keeps a strong reference, so concurrent callers always share the same instance.
    // User requests wait at most LOCK_WAIT_SECONDS and then fail with a retry message, instead of piling
    // up behind a slow vendor push (bounded by the EmrLiteRestTemplateConfig timeouts).
    private final LoadingCache<Long, Lock> beneficiaryOrderLocks = CacheBuilder.newBuilder()
            .weakValues()
            .build(CacheLoader.from(beneficiaryId -> new ReentrantLock()));

    private static final long LOCK_WAIT_SECONDS = 30;

    private <T> T withBeneficiaryLock(Long beneficiaryId, Callable<T> action) throws Exception {
        Lock lock = beneficiaryLock(beneficiaryId);
        if (!lock.tryLock(LOCK_WAIT_SECONDS, TimeUnit.SECONDS)) {
            throw new IllegalStateException("Another diagnostic order operation is still in progress for beneficiaryId="
                    + beneficiaryId + " — please retry shortly");
        }
        try {
            return action.call();
        } finally {
            lock.unlock();
        }
    }

    // For background work with no caller to retry it (the automatic retest): waits as long as it takes.
    private <T> T withBeneficiaryLockBlocking(Long beneficiaryId, Callable<T> action) throws Exception {
        Lock lock = beneficiaryLock(beneficiaryId);
        lock.lock();
        try {
            return action.call();
        } finally {
            lock.unlock();
        }
    }

    // For the poll scheduler: never waits. A busy beneficiary (user push/manual entry in progress) is
    // simply left for the next tick, rather than stalling the single scheduler thread behind it.
    private <T> Optional<T> withBeneficiaryLockIfFree(Long beneficiaryId, Callable<T> action) throws Exception {
        Lock lock = beneficiaryLock(beneficiaryId);
        if (!lock.tryLock()) {
            return Optional.empty();
        }
        try {
            return Optional.ofNullable(action.call());
        } finally {
            lock.unlock();
        }
    }

    private Lock beneficiaryLock(Long beneficiaryId) {
        if (beneficiaryId == null) {
            throw new IllegalArgumentException("beneficiaryId is required");
        }
        return beneficiaryOrderLocks.getUnchecked(beneficiaryId);
    }

    private DiagnosticOrder createAndPushOrder(DiagnosticOrderRequestDto request, String createdBy) throws Exception {
        return withBeneficiaryLock(request.getBeneficiaryId(), () -> createAndPushOrderLocked(request, createdBy));
    }

    private DiagnosticOrder createAndPushOrderLocked(DiagnosticOrderRequestDto request, String createdBy) throws Exception {
        Long beneficiaryId            = request.getBeneficiaryId();
        DiagnosticOrderType orderType = DiagnosticOrderType.fromCode(request.getOrderType());
        String orderEvent            = request.getOrderEvent();
        String patientFirstName      = request.getPatient().getFirstName();
        String patientLastName       = request.getPatient().getLastName();
        String patientDateOfBirth    = request.getPatient().getDateOfBirth();
        String patientSex            = request.getPatient().getSex();

        CloseReason close = resolveCloseReason(orderType, request.getReasonToCloseXray(),
                request.getReasonToCloseMTB(), request.getReasonToCloseRif());
        if (close != null) {
            if (close.stage() == DiagnosticCloseStage.RIF) {
                return closeRifStageLocked(beneficiaryId, orderType, close.reason(), createdBy);
            }
            return closeOrder(beneficiaryId, orderType, orderEvent, patientFirstName, patientLastName,
                    patientDateOfBirth, patientSex, close, createdBy);
        }

        Integer vanID = campConfigService.getVanID();
        Integer parkingPlaceID = campConfigService.getParkingPlaceID();

        Long beneficiaryRegID = beneficiaryRepo.getRegIDFromBenId(beneficiaryId);
        if (beneficiaryRegID == null) {
            throw new Exception("No beneficiaryRegID found for beneficiaryId=" + beneficiaryId);
        }
        BenVisitDetail visit = tbStopVisitService.getOrCreateVisitForToday(beneficiaryRegID, null, createdBy, vanID,
                parkingPlaceID);
        Long visitCode = visit.getVisitCode();

        String providerCode = providerFactory.getProviderCodeForOrderType(orderType);
        String externalOrderId = String.format("%s-%d-%s", UUID.randomUUID(), visitCode, orderType.name());

        Optional<DiagnosticOrder> latestForType = diagnosticOrderRepo
                .findFirstByBeneficiaryIdAndOrderTypeAndDeletedFalseOrderByCreatedDateDesc(beneficiaryId, orderType.name());

        Optional<DiagnosticOrder> existing = (latestForType.isPresent() && visitCode.equals(latestForType.get().getVisitCode()))
                ? latestForType
                : diagnosticOrderRepo.findFirstByBeneficiaryIdAndVisitCodeAndOrderTypeAndDeletedFalseOrderByCreatedDateDesc(beneficiaryId, visitCode, orderType.name());

        if (existing.isPresent()) {
            String existingStatus = existing.get().getStatus();
            if (!DiagnosticOrderStatus.FAILED.name().equals(existingStatus)
                    && !DiagnosticOrderStatus.CLOSED.name().equals(existingStatus)) {
                // Still active or already resolved (PENDING/COMPLETED/legacy IN_PROGRESS/MANUAL_ENTRY)
                // — returned as-is.
                return existing.get();
            }
            // FAILED and CLOSED are never reused, even for the same visit — both stay as-is for
            // history, and a fresh push always creates a brand new row instead of overwriting either.
        }

        if (latestForType.isPresent()
                && BLOCKING_STATUSES.contains(latestForType.get().getStatus())
                && !visitCode.equals(latestForType.get().getVisitCode())) {
            DiagnosticOrder blocker = latestForType.get();
            logger.info("Duplicate order push suppressed for beneficiaryId={}, orderType={}: unresolved order id={} "
                            + "(visitCode={}, status={}) already exists — returning it instead of pushing a new order for visitCode={}",
                    beneficiaryId, orderType, blocker.getId(), blocker.getVisitCode(), blocker.getStatus(), visitCode);
            return blocker;
        }

        // Neither FAILED nor CLOSED is ever reused above, so this is always a brand new row — a
        // beneficiary's diagnostic order history is a sequence of rows, not one row overwritten in place.
        DiagnosticOrder order = new DiagnosticOrder();
        order.setVanID(vanID);
        order.setParkingPlaceID(parkingPlaceID);
        order.setOrderEvent(orderEvent);
        order.setBeneficiaryId(beneficiaryId);
        order.setVisitCode(visitCode);
        order.setProviderServiceName(providerCode);
        order.setProviderCode(providerCode);
        order.setOrderType(orderType.name());
        order.setExternalOrderId(externalOrderId);
        boolean noVendor = providerCode == null || providerCode.isBlank();
        order.setStatus(noVendor ? DiagnosticOrderStatus.MANUAL_ENTRY.name() : DiagnosticOrderStatus.PENDING.name());
        order.setPatientFirstName(patientFirstName);
        order.setPatientLastName(patientLastName);
        order.setPatientDateOfBirth(patientDateOfBirth);
        order.setPatientSex(patientSex);
        order.setCreatedBy(createdBy);
        order.setModifiedBy(createdBy);

        try {
            order = diagnosticOrderRepo.save(order);
        } catch (DataIntegrityViolationException dive) {
            Optional<DiagnosticOrder> winner = diagnosticOrderRepo
                    .findFirstByBeneficiaryIdAndVisitCodeAndOrderTypeAndDeletedFalseOrderByCreatedDateDesc(beneficiaryId, visitCode, orderType.name());
            if (winner.isPresent()) {
                logger.warn("Lost create race for beneficiaryId={}, visitCode={}, orderType={} — returning existing order id={}",
                        beneficiaryId, visitCode, orderType, winner.get().getId());
                return winner.get();
            }
            throw dive;
        }

        if (noVendor) {
            logger.info("No active vendor configured for orderType={}, beneficiaryId={} — order saved for manual entry",
                    orderType, beneficiaryId);
            // Status is MANUAL_ENTRY (set above) — awaiting manual entry via submitManualResult.
            order = diagnosticOrderRepo.save(order);
            if (order.getVanSerialNo() == null) diagnosticOrderRepo.updateVanSerialNo(order.getId());
            return order;
        }

        return pushToProvider(order, providerCode);
    }

    // Pushes an already-saved PENDING order to the vendor and saves the outcome — FAILED (with the
    // error) if the push is rejected or throws. Shared by createAndPushOrder and pushRetestOrder.
    private DiagnosticOrder pushToProvider(DiagnosticOrder order, String providerCode) {
        try {
            DiagnosticProvider provider = providerFactory.getProvider(providerCode);
            DiagnosticPushResult pushResult = provider.pushOrder(order);
            order.setPushResponseJson(pushResult.getRawResponseJson());
            if (pushResult.isSuccess()) {
                order.setProviderOrderId(pushResult.getProviderOrderId());
            } else {
                order.setStatus(DiagnosticOrderStatus.FAILED.name());
                order.setErrorMessage(pushResult.getErrorMessage());
            }
        } catch (Exception e) {
            logger.error("Failed to push order to provider, orderId={}: {}", order.getId(), e.getMessage());
            order.setStatus(DiagnosticOrderStatus.FAILED.name());
            order.setErrorMessage(e.getMessage());
        }
        order = diagnosticOrderRepo.save(order);
        // Check the field itself, not a point-in-time "isNew" flag — a flag computed before save()
        // goes stale forever once the row exists (e.g. this row was the "winner" of a lost create
        // race below, or a prior attempt crashed between save() and this line). Re-checking the
        // real value self-heals any row still stuck at NULL, on whichever call next touches it.
        if (order.getVanSerialNo() == null) diagnosticOrderRepo.updateVanSerialNo(order.getId());

        return order;
    }

    // Shared by createAndPushOrder (push with a reasonToClose*) and submitManualResult (manualResult
    // with a reasonToClose*) for the XRAY and MTB stages — resolves the same visit/provider/externalOrderId a normal push would, then
    // closes the order via saveRefusedOrder. Identical outcome regardless of which endpoint triggered it.
    private DiagnosticOrder closeOrder(Long beneficiaryId, DiagnosticOrderType orderType, String orderEvent,
                                       String patientFirstName, String patientLastName, String patientDateOfBirth, String patientSex,
                                       CloseReason close, String actingUserId) throws Exception {
        Integer vanID = campConfigService.getVanID();
        Integer parkingPlaceID = campConfigService.getParkingPlaceID();

        Long beneficiaryRegID = beneficiaryRepo.getRegIDFromBenId(beneficiaryId);
        if (beneficiaryRegID == null) {
            throw new Exception("No beneficiaryRegID found for beneficiaryId=" + beneficiaryId);
        }
        BenVisitDetail visit = tbStopVisitService.getOrCreateVisitForToday(beneficiaryRegID, null, actingUserId, vanID,
                parkingPlaceID);
        Long visitCode = visit.getVisitCode();

        String providerCode = providerFactory.getProviderCodeForOrderType(orderType);
        String externalOrderId = String.format("%s-%d-%s", UUID.randomUUID(), visitCode, orderType.name());
        return saveRefusedOrder(beneficiaryId, visitCode, orderType, orderEvent, providerCode, externalOrderId,
                patientFirstName, patientLastName, patientDateOfBirth, patientSex, close, actingUserId);
    }

    // manualResult's reasonToClose variant has no patient/orderEvent in its request — it sources them
    // from the beneficiary's own most recent order for this orderType (whatever its status), since that
    // information already exists there. No prior order at all means there's nothing to source it from.
    private DiagnosticOrder closeOrderManually(Long beneficiaryId, DiagnosticOrderType orderType,
                                               CloseReason close, String actingUserId) throws Exception {
        return withBeneficiaryLock(beneficiaryId, () -> {
            if (close.stage() == DiagnosticCloseStage.RIF) {
                return closeRifStageLocked(beneficiaryId, orderType, close.reason(), actingUserId);
            }
            DiagnosticOrder source = diagnosticOrderRepo
                    .findFirstByBeneficiaryIdAndOrderTypeAndDeletedFalseOrderByCreatedDateDesc(beneficiaryId, orderType.name())
                    .orElseThrow(() -> new Exception("No diagnostic order found for beneficiaryId=" + beneficiaryId
                            + ", orderType=" + orderType.name() + " — cannot close a record that was never created"));
            return closeOrder(beneficiaryId, orderType, source.getOrderEvent(), source.getPatientFirstName(),
                    source.getPatientLastName(), source.getPatientDateOfBirth(), source.getPatientSex(), close,
                    actingUserId);
        });
    }

    // Refusals are keyed to the latest order for this beneficiary+orderType (not the exact visitCode
    // of this request), since a refusal can be recorded outside the visit that originally created the
    // order. A COMPLETED, FAILED or already-CLOSED latest order is left untouched (treated as "not found")
    // and a new CLOSED row is created instead, so its history (e.g. a FAILED row's errorMessage) survives.
    // Refused orders are saved as-is and never pushed to the vendor.
    private DiagnosticOrder saveRefusedOrder(Long beneficiaryId, Long visitCode, DiagnosticOrderType orderType,
                                             String orderEvent, String providerCode, String externalOrderId, String patientFirstName,
                                             String patientLastName, String patientDateOfBirth, String patientSex, CloseReason close,
                                             String actingUserId) {
        Optional<DiagnosticOrder> latest = diagnosticOrderRepo
                .findFirstByBeneficiaryIdAndOrderTypeAndDeletedFalseOrderByCreatedDateDesc(beneficiaryId, orderType.name());
        if (latest.isPresent() && NON_REUSABLE_ON_CLOSE_STATUSES.contains(latest.get().getStatus())) {
            latest = Optional.empty();
        }

        DiagnosticOrder order = latest.orElseGet(DiagnosticOrder::new);
        if (order.getVanID() == null) {
            order.setVanID(campConfigService.getVanID());
            order.setParkingPlaceID(campConfigService.getParkingPlaceID());
        }
        if (latest.isEmpty()) {
            order.setOrderEvent(orderEvent);
            order.setBeneficiaryId(beneficiaryId);
            order.setVisitCode(visitCode);
            order.setProviderServiceName(providerCode);
            order.setProviderCode(providerCode);
            order.setOrderType(orderType.name());
            order.setExternalOrderId(externalOrderId);
            order.setPatientFirstName(patientFirstName);
            order.setPatientLastName(patientLastName);
            order.setPatientDateOfBirth(patientDateOfBirth);
            order.setPatientSex(patientSex);
            order.setCreatedBy(actingUserId);
        }
        order.setStatus(DiagnosticOrderStatus.CLOSED.name());
        order.setReasonToClose(close.stage(), close.reason());
        order.setErrorMessage(null);
        order.setModifiedBy(actingUserId);
        order.setManuallyEnteredBy(actingUserId);
        order.setProcessed("N");

        try {
            order = diagnosticOrderRepo.save(order);
            if (order.getVanSerialNo() == null) diagnosticOrderRepo.updateVanSerialNo(order.getId());
            notifyProviderOrderClosed(order, close.reason());
            return order;
        } catch (DataIntegrityViolationException dive) {
            Optional<DiagnosticOrder> winner = diagnosticOrderRepo
                    .findFirstByBeneficiaryIdAndVisitCodeAndOrderTypeAndDeletedFalseOrderByCreatedDateDesc(beneficiaryId, visitCode, orderType.name());
            if (winner.isPresent()) {
                logger.warn("Lost create race for beneficiaryId={}, visitCode={}, orderType={} — returning existing order id={}",
                        beneficiaryId, visitCode, orderType, winner.get().getId());
                return winner.get();
            }
            throw dive;
        }
    }

    @Override
    public void notifyProviderOrderClosed(DiagnosticOrder order, String reason) {
        String providerCode = order.getProviderCode();
        if (providerCode == null || providerCode.isBlank()) {
            return; // no vendor was ever involved (manual-entry order)
        }
        if (order.getPushResponseJson() == null || order.getPushResponseJson().isEmpty()) {
            return; // never actually confirmed as pushed to the vendor — nothing to cancel there
        }
        try {
            DiagnosticCancelResult result = providerFactory.getProvider(providerCode).cancelOrder(order, reason);
            order.setCancelResponseJson(result.getRawResponseJson());
            order.setProcessed("N");
            diagnosticOrderRepo.save(order);
        } catch (Exception e) {
            // Transport-level failure only (unreachable, timeout) — a provider-level rejection is
            // already captured in the returned result above, not thrown.
            logger.warn("Failed to notify provider of order cancellation for externalOrderId={}: {}",
                    order.getExternalOrderId(), e.getMessage());
        }
    }

    @Override
    public DiagnosticOrderResultDto processResult(DiagnosticOrder order, DiagnosticPollResult pollResult) throws Exception {
        return processResult(order, pollResult, false, "SYSTEM");
    }

    // Saves a poll/manual outcome. Results are one tb_diagnostic_result row per test: the order's own
    // test (resultType = orderType) and, on MTB/MTB_PLUS orders, an MDR_RIF row once RIF data exists.
    private DiagnosticOrderResultDto processResult(DiagnosticOrder order, DiagnosticPollResult pollResult,
                                                   boolean writeBackWhenClosed, String actingUser) throws Exception {
        Optional<DiagnosticResult> existingMain = mainResult(order);
        Optional<DiagnosticResult> existingRif = rifResult(order);
        boolean hasRif = hasRifData(pollResult);
        // A poll that brings nothing new must not rewrite the rows: every rewrite resets processed
        // (so MMU re-uploads the order) and would re-send it to every tablet.
        boolean mainUnchanged = existingMain.isPresent() && sameMainResult(order, existingMain.get(), pollResult);
        boolean rifUnchanged = !hasRif || (existingRif.isPresent() && sameRifResult(existingRif.get(), pollResult));
        boolean orderUnchanged = sameOrderStatus(order, pollResult);
        if (mainUnchanged && rifUnchanged && orderUnchanged && !writeBackWhenClosed) {
            ingestAssets(order, pollResult);
            diagnosticOrderRepo.recordUnchangedPoll(order.getId(), new Timestamp(System.currentTimeMillis()),
                    order.getRetryCount());
            DiagnosticOrderResultDto unchanged = toResultDto(order);
            applyResults(unchanged, existingMain.orElse(null), existingRif.orElse(null));
            return unchanged;
        }

        DiagnosticResult main = saveResultRow(order, existingMain, order.getOrderType(), actingUser, row -> {
            row.setProviderStatus(mainRowStatus(order, pollResult));
            row.setResultSummary(pollResult.getResultSummary());
            // The vendor embeds each asset's base64 file content directly inline in its response body
            // when assets are requested — never persist that raw JSON verbatim (it's unencrypted, unlike
            // the same content on local disk). Only store rawResponseJson from the asset-free call.
            if (pollResult.getAssets() == null || pollResult.getAssets().isEmpty()) {
                row.setRawResponseJson(pollResult.getRawResponseJson());
            }
            row.setTbPresence(pollResult.getTbPresence());
            row.setTbConfidence(pollResult.getTbConfidence());
        });
        DiagnosticResult rif = existingRif.orElse(null);
        if (hasRif) {
            rif = saveResultRow(order, existingRif, DiagnosticResultType.MDR_RIF.name(), actingUser, row -> {
                row.setProviderStatus(rifStatus(pollResult));
                row.setResultSummary(blankToNull(pollResult.getDrugResistanceSummary()));
                row.setDrugResistancePresence(pollResult.getDrugResistancePresence());
            });
        }

        ingestAssets(order, pollResult);

        order.setStatus(pollResult.getStatus().name());
        order.setErrorMessage(pollResult.getErrorMessage());
        if (pollResult.getProviderOrderId() != null) {
            order.setProviderOrderId(pollResult.getProviderOrderId());
        }
        order.setLastPolledAt(new Timestamp(System.currentTimeMillis()));
        order.setModifiedBy(actingUser);
        order.setProcessed("N");
        diagnosticOrderRepo.save(order);

        // writeBackWhenClosed is set by the manual paths (manual entry, closing the RIF test, the
        // vendor replacing a manual result) and for invalid outcomes. On those paths a positive MTB
        // still waiting for RIF (PENDING for a vendor order, MANUAL_ENTRY without one) is written to
        // tb_suspected straight away too; mdrRifResult follows when RIF is in.
        // An INVALID result is written back too (as the invalid outcome was before); the machine's
        // retest result overwrites it later.
        if (DiagnosticOrderStatus.COMPLETED.name().equals(order.getStatus())
                || DiagnosticOrderStatus.INVALID.name().equals(order.getStatus())
                || (DiagnosticOrderStatus.FAILED.name().equals(order.getStatus()) && main.getResultSummary() != null)
                || (writeBackWhenClosed && DiagnosticOrderStatus.CLOSED.name().equals(order.getStatus()))
                || (writeBackWhenClosed && main.getResultSummary() != null
                    && (DiagnosticOrderStatus.MANUAL_ENTRY.name().equals(order.getStatus())
                        || DiagnosticOrderStatus.PENDING.name().equals(order.getStatus())))) {
            recordTbSuspectedResult(order, main, rif != null ? rif.getResultSummary() : null);
        }

        DiagnosticOrderResultDto dto = toResultDto(order);
        applyResults(dto, main, rif);
        return dto;
    }

    // Inserts or updates one result row of this order. Only a first insert can lose a race (unique
    // external_order_id + result_type); then the winner's row is returned. Any other integrity failure
    // means the result was not saved, so the order must not move on without it.
    private DiagnosticResult saveResultRow(DiagnosticOrder order, Optional<DiagnosticResult> existing, String resultType,
                                           String actingUser, java.util.function.Consumer<DiagnosticResult> fill) {
        DiagnosticResult row = existing.orElseGet(DiagnosticResult::new);
        row.setExternalOrderId(order.getExternalOrderId());
        row.setResultType(resultType);
        row.setBeneficiaryId(order.getBeneficiaryId());
        fill.accept(row);
        row.setCreatedBy(row.getCreatedBy() != null ? row.getCreatedBy() : actingUser);
        row.setModifiedBy(actingUser);
        if (row.getVanID() == null) {
            // Inherit from the parent order rather than re-reading Redis — the result belongs
            // to whichever van originated the order, not whichever van happens to be polling now.
            row.setVanID(order.getVanID());
            row.setParkingPlaceID(order.getParkingPlaceID());
        }
        try {
            diagnosticResultRepo.save(row);
            if (row.getVanSerialNo() == null) diagnosticResultRepo.updateVanSerialNo(row.getId());
            return row;
        } catch (DataIntegrityViolationException dive) {
            if (existing.isPresent()) throw dive;
            logger.warn("Lost result upsert race for externalOrderId={}, resultType={}",
                    order.getExternalOrderId(), resultType);
            return diagnosticResultRepo
                    .findByExternalOrderIdAndResultTypeAndDeletedFalse(order.getExternalOrderId(), resultType)
                    .orElse(row);
        }
    }

    // The order's own test row (X-ray, MTB or MTB_PLUS).
    private Optional<DiagnosticResult> mainResult(DiagnosticOrder order) {
        if (order.getExternalOrderId() == null) return Optional.empty();
        return diagnosticResultRepo.findByExternalOrderIdAndResultTypeAndDeletedFalse(
                order.getExternalOrderId(), order.getOrderType());
    }

    // The MDR RIF row of an MTB/MTB_PLUS order, once its RIF test exists.
    private Optional<DiagnosticResult> rifResult(DiagnosticOrder order) {
        if (order.getExternalOrderId() == null || !TrueNatResultRules.isTrueNat(order.getOrderType())) {
            return Optional.empty();
        }
        return diagnosticResultRepo.findByExternalOrderIdAndResultTypeAndDeletedFalse(
                order.getExternalOrderId(), DiagnosticResultType.MDR_RIF.name());
    }

    private static boolean hasRifData(DiagnosticPollResult p) {
        return blankToNull(p.getDrugResistanceSummary()) != null || p.getDrugResistanceStatus() != null
                || p.getDrugResistancePresence() != null;
    }

    // RIF row status: the vendor's RIF component status, else COMPLETED once a RIF result exists.
    private static String rifStatus(DiagnosticPollResult p) {
        if (p.getDrugResistanceStatus() != null) return p.getDrugResistanceStatus();
        return DiagnosticOrderStatus.COMPLETED.name();
    }

    // Best-effort link from a diagnostic order back to the tb_suspected referral that presumably
    // prompted it. Not every diagnostic order originates from a TB referral, so a miss here is
    // expected and must never fail/block the push or result flow — just log and move on.
    private TBSuspected findTbSuspected(Long beneficiaryId) {
        TBSuspected tbSuspected = tbSuspectedRepo.findFirstByBenIdOrderByCreatedDateDesc(beneficiaryId);
        if (tbSuspected == null) {
            logger.warn("No tb_suspected row for beneficiaryId={} — skipping write-back", beneficiaryId);
        }
        return tbSuspected;
    }

    // drugResistanceSummary is the MDR RIF result the vendor runs on the same MTB order when MTB is
    // positive; null when no RIF test ran (MTB negative, manual entry), so an earlier value is kept.
    private void recordTbSuspectedResult(DiagnosticOrder order, DiagnosticResult result, String drugResistanceSummary) {
        TBSuspected tbSuspected = findTbSuspected(order.getBeneficiaryId());
        if (tbSuspected == null) return;
        DiagnosticOrderType type = DiagnosticOrderType.fromCode(order.getOrderType());
        if (type == DiagnosticOrderType.XRAY_CHEST) {
            tbSuspected.setIsChestXRayDone(true);
            tbSuspected.setChestXRayResult(result.getResultSummary());
        } else if (type == DiagnosticOrderType.MTB){
            tbSuspected.setIsSputumCollected(true);
            tbSuspected.setSputumTestResult(result.getResultSummary());
            if (drugResistanceSummary != null) {
                tbSuspected.setMdrRifResult(drugResistanceSummary);
            }
        }
        tbSuspected.setProcessed("N");
        tbSuspectedRepo.save(tbSuspected);
    }

    // Shared by processResult (single combined save+ingest call, e.g. submitManualResult) and
    // pollOnce's second, asset-only call (which must NOT re-save the already-saved result/order
    // fields a second time).
    private void ingestAssets(DiagnosticOrder order, DiagnosticPollResult pollResult) {
        if (pollResult.getAssets() == null) {
            return;
        }
        for (DiagnosticDocumentAsset asset : pollResult.getAssets()) {
            try {
                diagnosticDocumentService.ingestAsset(order.getBeneficiaryId(), order.getOrderType(),
                        order.getExternalOrderId(), asset);
            } catch (Exception e) {
                logger.error("Failed to ingest document asset for orderId={}, assetType={}, fileName={}: {}",
                        order.getId(), asset.getType(), asset.getFileName(), e.getMessage());
            }
        }
    }

    // The scheduler loads its due-for-poll batch at the start of a tick and polls each order in turn, so
    // by the time an order's turn comes it may have been completed/closed by a manual entry. Polling and
    // saving that stale copy would overwrite the manual result (whole-row save, no @Version), so the
    // order is re-read under the beneficiary lock and skipped unless it is still awaiting a result.
    // Failures are recorded on that same fresh row, inside the lock, for the same reason.
    @Override
    public DiagnosticPollResult pollOnce(DiagnosticOrder order) throws Exception {
        Optional<DiagnosticPollResult> polled = withBeneficiaryLockIfFree(order.getBeneficiaryId(), () -> {
            Optional<DiagnosticOrder> current = findOutstandingOrder(order.getId());
            if (current.isEmpty()) {
                logger.info("Skipping poll for orderId={}: no longer PENDING/IN_PROGRESS", order.getId());
                return null;
            }
            DiagnosticOrder fresh = current.get();
            try {
                return pollOnceLocked(fresh);
            } catch (Exception e) {
                int retries = (fresh.getRetryCount() == null ? 0 : fresh.getRetryCount()) + 1;
                if (java.util.Objects.equals(fresh.getErrorMessage(), e.getMessage())) {
                    // Same failure as last time: only the attempt is recorded (see recordUnchangedPoll).
                    diagnosticOrderRepo.recordUnchangedPoll(fresh.getId(), new Timestamp(System.currentTimeMillis()), retries);
                } else {
                    fresh.setRetryCount(retries);
                    fresh.setLastPolledAt(new Timestamp(System.currentTimeMillis()));
                    fresh.setErrorMessage(e.getMessage());
                    fresh.setModifiedBy("SYSTEM");
                    fresh.setProcessed("N");
                    diagnosticOrderRepo.save(fresh);
                }
                throw e;
            }
        });
        if (polled.isEmpty()) {
            logger.debug("Poll for orderId={} skipped or deferred (beneficiary busy / order resolved)", order.getId());
        }
        return polled.orElse(null);
    }

    // Same stale-copy problem as pollOnce: the daily expiry batch is loaded up front, so each order is
    // re-read under the lock and only closed if it is still outstanding.
    @Override
    public void expireOrder(DiagnosticOrder order, String reason) throws Exception {
        withBeneficiaryLock(order.getBeneficiaryId(), () -> {
            Optional<DiagnosticOrder> current = findOutstandingOrder(order.getId());
            if (current.isEmpty()) {
                logger.info("Skipping expiry for orderId={}: no longer PENDING/IN_PROGRESS", order.getId());
                return null;
            }
            DiagnosticOrder fresh = current.get();
            fresh.setStatus(DiagnosticOrderStatus.CLOSED.name());
            fresh.setReasonToClose(stageOf(fresh), reason);
            fresh.setLastPolledAt(new Timestamp(System.currentTimeMillis()));
            fresh.setModifiedBy("SYSTEM");
            fresh.setProcessed("N");
            diagnosticOrderRepo.save(fresh);
            notifyProviderOrderClosed(fresh, reason);
            return null;
        });
    }

    // Same stale-copy rule as pollOnce: re-read under the lock, so an order that was reopened, deleted
    // or got its documents after the batch was loaded is skipped instead of polled again.
    @Override
    public boolean fetchMissingDocuments(DiagnosticOrder order) throws Exception {
        Optional<Boolean> received = withBeneficiaryLockIfFree(order.getBeneficiaryId(), () -> {
            Optional<DiagnosticOrder> current = diagnosticOrderRepo.findById(order.getId())
                    .filter(o -> !Boolean.TRUE.equals(o.getDeleted()))
                    .filter(o -> DiagnosticOrderStatus.COMPLETED.name().equals(o.getStatus()));
            if (current.isEmpty()) {
                logger.info("Skipping document fetch for orderId={}: no longer COMPLETED", order.getId());
                return false;
            }
            DiagnosticOrder fresh = current.get();
            DiagnosticPollResult vendor = providerFactory.getProvider(fresh.getProviderCode()).pollResult(fresh, true);
            if (vendor == null) {
                return false;
            }
            boolean gotDocuments = vendor.getAssets() != null && !vendor.getAssets().isEmpty();
            if (gotDocuments) {
                ingestAssets(fresh, vendor);
            } else {
                logger.info("Vendor returned no documents yet for orderId={}", fresh.getId());
            }
            boolean overwritten = fresh.getManuallyEnteredBy() != null && overwriteManualResult(fresh, vendor);
            return gotDocuments || overwritten;
        });
        return received.orElse(false);
    }

    // A manually entered result is replaced by the vendor's wherever the vendor has a value and it
    // differs: the MTB / X-ray result, and the MDR RIF result. A value the vendor doesn't have yet
    // (e.g. RIF still running) keeps what was entered. The order status is then worked out the same
    // way as for manual entry, and the result row and tb_suspected are rewritten via processResult.
    private boolean overwriteManualResult(DiagnosticOrder order, DiagnosticPollResult vendor) throws Exception {
        DiagnosticResult storedMain = mainResult(order).orElse(null);
        DiagnosticResult storedRif = rifResult(order).orElse(null);
        String storedSummary = storedMain != null ? storedMain.getResultSummary() : null;
        String storedRifSummary = storedRif != null ? storedRif.getResultSummary() : null;

        String vendorSummary = blankToNull(vendor.getResultSummary());
        String vendorRif = blankToNull(vendor.getDrugResistanceSummary());
        boolean summaryChanged = vendorSummary != null && !vendorSummary.equalsIgnoreCase(
                storedSummary == null ? "" : storedSummary.trim());
        boolean rifChanged = vendorRif != null && !vendorRif.equalsIgnoreCase(
                storedRifSummary == null ? "" : storedRifSummary.trim());
        // A RIF status change with no RIF result (e.g. the vendor's RIF test FAILED) counts too.
        String vendorRifStatus = vendor.getDrugResistanceStatus();
        boolean rifStatusChanged = vendorRifStatus != null && !vendorRifStatus.equalsIgnoreCase(
                storedRif != null && storedRif.getProviderStatus() != null ? storedRif.getProviderStatus() : "");
        if (!summaryChanged && !rifChanged && !rifStatusChanged) {
            return false;
        }

        String summary = vendorSummary != null ? vendorSummary : storedSummary;
        Boolean tbPresence = summaryChanged ? vendor.getTbPresence()
                : (storedMain != null ? storedMain.getTbPresence() : null);
        String rifSummary = vendorRif != null ? vendorRif : storedRifSummary;
        Boolean drugResistance = vendorRif != null ? TrueNatResultRules.classifyDrugResistance(vendorRif)
                : (storedRif != null ? storedRif.getDrugResistancePresence() : null);
        String rifStatus = vendorRifStatus != null ? vendorRifStatus
                : vendorRif != null ? DiagnosticOrderStatus.COMPLETED.name()
                : (storedRif != null ? storedRif.getProviderStatus() : null);

        // Same rules as the poll. A test the vendor reports FAILED makes the order FAILED; an invalid
        // vendor MTB/RIF result ("Error-2") makes it INVALID, so the poll resumes and picks up the
        // machine's retest; any other invalid result (X-ray, a manual "Invalid/Error" kept) closes it.
        // Otherwise it stays COMPLETED: a positive MTB with no RIF yet is picked up again by the
        // back-fill until the vendor's RIF report arrives.
        boolean trueNat = TrueNatResultRules.isTrueNat(order.getOrderType());
        DiagnosticOrderStatus status = trueNat && DiagnosticOrderStatus.FAILED.equals(vendor.getStatus())
                ? DiagnosticOrderStatus.FAILED : DiagnosticOrderStatus.COMPLETED;
        DiagnosticPollResult merged = new DiagnosticPollResult(status, null, summary, null, null, null,
                tbPresence, null, drugResistance, rifSummary, rifStatus);
        if (!markInvalidTrueNatResult(order, merged) && status == DiagnosticOrderStatus.COMPLETED
                && (isInvalidResult(order.getOrderType(), summary, SPUTUM_INVALID_POLLED_RESULT)
                    || isInvalidResult(order.getOrderType(), summary, SPUTUM_INVALID_MANUAL_RESULT))) {
            merged.setStatus(DiagnosticOrderStatus.CLOSED);
        }
        status = merged.getStatus();

        logger.warn("Overwriting manually entered result with vendor's for orderId={} (externalOrderId={}): "
                        + "{} '{}' -> '{}', MDR_RIF '{}' -> '{}', status {} -> {}",
                order.getId(), order.getExternalOrderId(), order.getOrderType(), storedSummary, summary,
                storedRifSummary, rifSummary, order.getStatus(), status);
        processResult(order, merged, true, "SYSTEM");
        if (summaryChanged && status == DiagnosticOrderStatus.COMPLETED) {
            pushMtbOrderAfterPositiveXray(order, tbPresence);
        }
        return true;
    }

    // providerStatus of the order's own test row (X-ray / MTB / MTB_PLUS), kept apart from the order
    // status: a finished MTB is COMPLETED while the order stays PENDING / INVALID / CLOSED over its RIF
    // test (whose row carries the RIF status). The vendor's component status when it gave one;
    // otherwise (manual entry, a manual result kept over an empty vendor one, the vendor replacing a
    // manual result, closing RIF) COMPLETED for a valid MTB result, else the order status.
    private static String mainRowStatus(DiagnosticOrder order, DiagnosticPollResult p) {
        if (p.getMainStatus() != null) {
            return p.getMainStatus();
        }
        String summary = blankToNull(p.getResultSummary());
        if (TrueNatResultRules.isTrueNat(order.getOrderType()) && summary != null
                && !isInvalidResult(order.getOrderType(), summary, SPUTUM_INVALID_POLLED_RESULT)
                && !isInvalidResult(order.getOrderType(), summary, SPUTUM_INVALID_MANUAL_RESULT)) {
            return DiagnosticOrderStatus.COMPLETED.name();
        }
        return p.getStatus() == null ? null : p.getStatus().name();
    }

    private static boolean sameMainResult(DiagnosticOrder order, DiagnosticResult stored, DiagnosticPollResult polled) {
        return java.util.Objects.equals(stored.getProviderStatus(), mainRowStatus(order, polled))
                && java.util.Objects.equals(stored.getResultSummary(), polled.getResultSummary())
                && java.util.Objects.equals(stored.getTbPresence(), polled.getTbPresence())
                && java.util.Objects.equals(stored.getTbConfidence(), polled.getTbConfidence())
                && (polled.getAssets() == null || polled.getAssets().isEmpty());
    }

    private static boolean sameRifResult(DiagnosticResult stored, DiagnosticPollResult polled) {
        return java.util.Objects.equals(stored.getProviderStatus(), rifStatus(polled))
                && java.util.Objects.equals(stored.getResultSummary(), blankToNull(polled.getDrugResistanceSummary()))
                && java.util.Objects.equals(stored.getDrugResistancePresence(), polled.getDrugResistancePresence());
    }

    private static boolean sameOrderStatus(DiagnosticOrder order, DiagnosticPollResult polled) {
        return polled.getStatus() != null
                && polled.getStatus().name().equals(order.getStatus())
                && java.util.Objects.equals(order.getErrorMessage(), polled.getErrorMessage())
                && (polled.getProviderOrderId() == null
                    || polled.getProviderOrderId().equals(order.getProviderOrderId()));
    }

    private Optional<DiagnosticOrder> findOutstandingOrder(Long orderId) {
        return diagnosticOrderRepo.findById(orderId)
                .filter(o -> !Boolean.TRUE.equals(o.getDeleted()))
                .filter(o -> OUTSTANDING_STATUSES.contains(o.getStatus()));
    }

    private DiagnosticPollResult pollOnceLocked(DiagnosticOrder order) throws Exception {
        // A null pushResponseJson means the order was never actually confirmed as pushed to the
        // vendor (e.g. a stale/legacy row) — polling the vendor for it would be meaningless, so
        // fail it outright instead of burning a vendor call every tick.
        if (order.getPushResponseJson() == null || order.getPushResponseJson().isEmpty()) {
            order.setStatus(DiagnosticOrderStatus.FAILED.name());
            order.setErrorMessage("No push response recorded for this order — cannot poll");
            order.setLastPolledAt(new Timestamp(System.currentTimeMillis()));
            order.setModifiedBy("SYSTEM");
            order.setProcessed("N");
            diagnosticOrderRepo.save(order);
            return null;
        }
        DiagnosticProvider provider = providerFactory.getProvider(order.getProviderCode());
        DiagnosticPollResult result = keepManualWhereVendorEmpty(order, provider.pollResult(order, false));
        // An invalid MTB/RIF run is retested by the machine on this same order: INVALID, saved below via
        // processResult like any open status, and polled on — no close, no retest order, no reports yet.
        markInvalidTrueNatResult(order, result);
        if (DiagnosticOrderStatus.COMPLETED.equals(result.getStatus())) {
            // Save as COMPLETED (or CLOSED for an invalid outcome) now, without assets, so a failure
            // fetching/ingesting assets below doesn't also lose the already-confirmed status (see
            // pollSingle's catch).
            boolean invalid = closeIfInvalidPolledResult(order, result);
            processResult(order, result, invalid, "SYSTEM");
            if (invalid) {
                // Before the asset fetch below: if that throws, this CLOSED order is never polled
                // again, so the retest must already exist by then.
                pushRetestOrder(order);
            } else {
                // Also before the asset fetch, for the same reason.
                pushMtbOrderAfterPositiveXray(order, result.getTbPresence());
            }
            result = provider.pollResult(order, true);
            // Only ingest the documents this time — the result/order fields were already saved above
            // and this second, asset-bearing response carries the same status/summary, so re-saving
            // them again would just be a redundant duplicate write.
            ingestAssets(order, result);
        } else if (order.getManuallyEnteredBy() != null && vendorChangedManualResult(order, result)) {
            // Vendor replaced part of a manual result while the order is still open: rewrite
            // tb_suspected too, since the manual value is already there.
            processResult(order, result, true, "SYSTEM");
        } else {
            processResult(order, result);
        }
        return result;
    }

    // Order whose result was entered by hand and is still being polled (a positive MTB waiting for
    // RIF): the vendor's values replace the manual ones, but a value the vendor doesn't have yet
    // (empty summary while its test runs, no RIF yet) keeps what was entered.
    private DiagnosticPollResult keepManualWhereVendorEmpty(DiagnosticOrder order, DiagnosticPollResult vendor) {
        if (vendor == null || order.getManuallyEnteredBy() == null) {
            return vendor;
        }
        mainResult(order).ifPresent(manual -> {
            if (blankToNull(vendor.getResultSummary()) == null) {
                vendor.setResultSummary(manual.getResultSummary());
                vendor.setTbPresence(manual.getTbPresence());
                vendor.setMainStatus(null);
            }
        });
        if (!hasRifData(vendor)) {
            rifResult(order).ifPresent(manualRif -> {
                vendor.setDrugResistanceSummary(manualRif.getResultSummary());
                vendor.setDrugResistancePresence(manualRif.getDrugResistancePresence());
                vendor.setDrugResistanceStatus(manualRif.getProviderStatus());
            });
        }
        return vendor;
    }

    private boolean vendorChangedManualResult(DiagnosticOrder order, DiagnosticPollResult merged) {
        String storedSummary = mainResult(order).map(DiagnosticResult::getResultSummary).orElse(null);
        String storedRif = rifResult(order).map(DiagnosticResult::getResultSummary).orElse(null);
        return !java.util.Objects.equals(storedSummary, merged.getResultSummary())
                || !java.util.Objects.equals(storedRif, blankToNull(merged.getDrugResistanceSummary()));
    }

    private DiagnosticOrder findLatestOrder(Long beneficiaryId, String orderType) throws Exception {
        DiagnosticOrderType type = DiagnosticOrderType.fromCode(orderType);
        return diagnosticOrderRepo
                .findFirstByBeneficiaryIdAndOrderTypeAndDeletedFalseOrderByCreatedDateDesc(beneficiaryId, type.name())
                .orElseThrow(() -> new Exception(
                        "DiagnosticOrder not found for beneficiaryId=" + beneficiaryId + ", orderType=" + orderType));
    }

    // Resolves a specific order by visitCode when given (retest disambiguation), otherwise
    // falls back to "latest" - matching the pre-multi-order default callers already rely on.
    private DiagnosticOrder resolveOrder(Long beneficiaryId, String orderType, Long visitCode) throws Exception {
        if (visitCode == null) {
            return findLatestOrder(beneficiaryId, orderType);
        }
        DiagnosticOrderType type = DiagnosticOrderType.fromCode(orderType);
        return diagnosticOrderRepo.findFirstByBeneficiaryIdAndVisitCodeAndOrderTypeAndDeletedFalseOrderByCreatedDateDesc(beneficiaryId, visitCode, type.name())
                .orElseThrow(() -> new Exception("DiagnosticOrder not found for beneficiaryId=" + beneficiaryId
                        + ", visitCode=" + visitCode + ", orderType=" + orderType));
    }

    @Override
    public DiagnosticOrderResultDto triggerManualPoll(Long beneficiaryId, String orderType, Long visitCode) throws Exception {
        // Locked, and the order read inside the lock, so this can't interleave with a manual entry or a
        // scheduler poll for the same beneficiary and save over what they just wrote.
        return withBeneficiaryLock(beneficiaryId, () -> {
            DiagnosticOrder order = resolveOrder(beneficiaryId, orderType, visitCode);
            if (DiagnosticOrderStatus.MANUAL_ENTRY.name().equals(order.getStatus())) {
                throw new IllegalStateException("Cannot poll a MANUAL_ENTRY order — no vendor is involved, "
                        + "submit the result via manualResult instead");
            }
            if (order.getManuallyEnteredBy() != null) {
                throw new IllegalStateException("Cannot poll this order: its result was entered manually by "
                        + order.getManuallyEnteredBy() + " and a vendor poll would overwrite it");
            }
            DiagnosticProvider provider = providerFactory.getProvider(order.getProviderCode());
            boolean wasCompleted = DiagnosticOrderStatus.COMPLETED.name().equals(order.getStatus());
            DiagnosticPollResult pollResult = provider.pollResult(order, true);
            markInvalidTrueNatResult(order, pollResult);
            boolean invalid = closeIfInvalidPolledResult(order, pollResult);
            DiagnosticOrderResultDto dto = processResult(order, pollResult, invalid, "SYSTEM");
            // Same follow-up as the scheduled poll: an X-ray this call completes as TB Presumptive gets
            // its MTB order. Only on the transition to COMPLETED — the scheduler skips a completed order,
            // so this is the only chance; re-polling an already completed X-ray creates nothing.
            if (!wasCompleted && DiagnosticOrderStatus.COMPLETED.name().equals(order.getStatus())) {
                pushMtbOrderAfterPositiveXray(order, pollResult.getTbPresence());
            }
            return dto;
        });
    }

    @Override
    public DiagnosticOrder retryPoll(Long beneficiaryId, String orderType, Long visitCode) throws Exception {
        // Locked so the terminal-status check and the reset to PENDING can't straddle a manual entry
        // that completes the order in between (which would reopen it for polling).
        return withBeneficiaryLock(beneficiaryId, () -> retryPollLocked(beneficiaryId, orderType, visitCode));
    }

    private DiagnosticOrder retryPollLocked(Long beneficiaryId, String orderType, Long visitCode) throws Exception {
        DiagnosticOrder order = resolveOrder(beneficiaryId, orderType, visitCode);
        String status = order.getStatus();

        if (DiagnosticOrderStatus.COMPLETED.name().equals(status)
                || DiagnosticOrderStatus.CLOSED.name().equals(status)) {
            throw new IllegalStateException("Cannot retry polling for order in terminal status " + status
                    + " — create a new order instead");
        }
        if (DiagnosticOrderStatus.MANUAL_ENTRY.name().equals(status)) {
            throw new IllegalStateException("Cannot retry polling for a MANUAL_ENTRY order — no vendor is involved, "
                    + "submit the result via manualResult instead");
        }

        // retriedAt is kept as an audit timestamp only — the scheduler no longer uses it to anchor a
        // poll window; a retried order is simply picked up on the next regular tick like any other
        // PENDING order (see DiagnosticPollSchedulerService).
        order.setRetriedAt(new Timestamp(System.currentTimeMillis()));
        order.setStatus(DiagnosticOrderStatus.PENDING.name());
        order.setErrorMessage(null);
        order.setProcessed("N");
        return diagnosticOrderRepo.save(order);
    }

    @Override
    public DiagnosticOrder getOrder(Long beneficiaryId, String orderType, Long visitCode) throws Exception {
        return resolveOrder(beneficiaryId, orderType, visitCode);
    }

    @Override
    public List<DiagnosticOrder> getOrdersByBeneficiaryId(Long beneficiaryId) throws Exception {
        return diagnosticOrderRepo.findByBeneficiaryId(beneficiaryId);
    }

    @Override
    public DiagnosticOrderResultDto getOrderResult(Long beneficiaryId, String orderType, Long visitCode) {
        Optional<DiagnosticOrder> orderOpt = visitCode != null
                ? diagnosticOrderRepo.findFirstByBeneficiaryIdAndVisitCodeAndOrderTypeAndDeletedFalseOrderByCreatedDateDesc(beneficiaryId, visitCode, orderType)
                : diagnosticOrderRepo.findFirstByBeneficiaryIdAndOrderTypeAndDeletedFalseOrderByCreatedDateDesc(beneficiaryId, orderType);
        if (orderOpt.isEmpty()) {
            return notFoundResult(beneficiaryId, orderType, visitCode);
        }

        DiagnosticOrder order = orderOpt.get();
        DiagnosticOrderResultDto dto = toResultDto(order);
        applyResults(dto, mainResult(order).orElse(null), rifResult(order).orElse(null));
        return dto;
    }

    private static DiagnosticOrderResultDto notFoundResult(Long beneficiaryId, String orderType, Long visitCode) {
        DiagnosticOrderResultDto dto = new DiagnosticOrderResultDto();
        dto.setBeneficiaryId(beneficiaryId);
        dto.setVisitCode(visitCode);
        dto.setOrderType(orderType);
        dto.setStatus("NOT_FOUND");
        return dto;
    }

    // results = the order's own test row first, then its MDR_RIF row; missing rows are left out.
    private static void applyResults(DiagnosticOrderResultDto dto, DiagnosticResult main, DiagnosticResult rif) {
        List<DiagnosticResultItemDto> items = new ArrayList<>();
        if (main != null) items.add(toResultItem(main));
        if (rif != null) items.add(toResultItem(rif));
        dto.setResults(items);
    }

    private static DiagnosticResultItemDto toResultItem(DiagnosticResult row) {
        DiagnosticResultItemDto item = new DiagnosticResultItemDto();
        item.setResultType(row.getResultType());
        item.setProviderStatus(row.getProviderStatus());
        item.setResultSummary(row.getResultSummary());
        item.setTbPresence(row.getTbPresence());
        item.setTbConfidence(row.getTbConfidence());
        item.setDrugResistancePresence(row.getDrugResistancePresence());
        return item;
    }

    private static void applyReasonToClose(DiagnosticOrderResultDto dto, DiagnosticOrder order) {
        dto.setReasonToCloseXray(order.getReasonToCloseXray());
        dto.setReasonToCloseMTB(order.getReasonToCloseMTB());
        dto.setReasonToCloseMDRRIF(order.getReasonToCloseMDRRIF());
    }

    @Override
    public java.util.Map<Long, java.util.Map<String, Object>> getOrderRecordsByIds(java.util.Collection<Long> ids) {
        java.util.Map<Long, java.util.Map<String, Object>> out = new java.util.LinkedHashMap<>();
        for (DiagnosticOrder order : diagnosticOrderRepo.findAllById(ids)) {
            if (Boolean.TRUE.equals(order.getDeleted())) continue;
            DiagnosticOrderResultDto dto = toResultDto(order);
            applyResults(dto, mainResult(order).orElse(null), rifResult(order).orElse(null));
            java.util.Map<String, Object> m = new java.util.LinkedHashMap<>();
            m.put("id", order.getId());
            m.put("beneficiaryId", order.getBeneficiaryId());
            m.put("visitCode", order.getVisitCode());
            m.put("createdDate", order.getCreatedDate());
            m.put("externalOrderId", dto.getExternalOrderId());
            m.put("orderType", dto.getOrderType());
            m.put("status", dto.getStatus());
            m.put("errorMessage", dto.getErrorMessage());
            m.put("reasonToCloseXray", dto.getReasonToCloseXray());
            m.put("reasonToCloseMTB", dto.getReasonToCloseMTB());
            m.put("reasonToCloseMDRRIF", dto.getReasonToCloseMDRRIF());
            m.put("results", dto.getResults());
            out.put(order.getId(), m);
        }
        return out;
    }

    private DiagnosticOrderResultDto toResultDto(DiagnosticOrder order) {
        DiagnosticOrderResultDto dto = new DiagnosticOrderResultDto();
        dto.setBeneficiaryId(order.getBeneficiaryId());
        dto.setVisitCode(order.getVisitCode());
        dto.setExternalOrderId(order.getExternalOrderId());
        dto.setOrderType(order.getOrderType());
        dto.setStatus(order.getStatus());
        dto.setErrorMessage(order.getErrorMessage());
        applyReasonToClose(dto, order);
        return dto;
    }

    @Override
    public DiagnosticOrderStatusSummaryDto getOrderStatusSummary(String orderType, Integer villageId,
                                                                 Integer providerServiceMapId) {
        DiagnosticOrderType type = DiagnosticOrderType.fromCode(orderType);
        List<Long> awaitingProviderResult = new ArrayList<>();
        List<Long> completed = new ArrayList<>();
        List<Long> failed = new ArrayList<>();
        List<Long> closed = new ArrayList<>();
        List<Long> awaitingManualEntry = new ArrayList<>();
        List<Long> invalid = new ArrayList<>();
        // One query instead of one per bucket; buckets are the same status groups the per-bucket
        // queries used (matched case-insensitively, like the database comparison was).
        for (Object[] row : diagnosticOrderRepo.findLatestOrderStatusByBeneficiary(type.name(), villageId, providerServiceMapId)) {
            Long beneficiaryId = row[0] != null ? ((Number) row[0]).longValue() : null;
            String status = row[1] != null ? row[1].toString().toUpperCase(java.util.Locale.ROOT) : "";
            switch (status) {
                case "PENDING", "IN_PROGRESS" -> awaitingProviderResult.add(beneficiaryId);
                case "COMPLETED" -> completed.add(beneficiaryId);
                case "FAILED" -> failed.add(beneficiaryId);
                case "CLOSED", "CANCELLED", "EXPIRED", "REFUSED" -> closed.add(beneficiaryId);
                case "MANUAL_ENTRY" -> awaitingManualEntry.add(beneficiaryId);
                case "INVALID" -> invalid.add(beneficiaryId);
                default -> { }
            }
        }
        return new DiagnosticOrderStatusSummaryDto(awaitingProviderResult, completed, failed, closed, awaitingManualEntry, invalid);
    }

    @Override
    public VendorHealthDto checkVendorHealth(String orderType) throws Exception {
        DiagnosticOrderType type = DiagnosticOrderType.fromCode(orderType);
        String providerCode = providerFactory.getProviderCodeForOrderType(type);
        boolean isDeviceIntegrated = providerCode != null && !providerCode.isBlank();
        boolean isConnected = false;
        if (isDeviceIntegrated) {
            try {
                isConnected = providerFactory.getProvider(providerCode).checkHealth(type);
            } catch (Exception e) {
                logger.warn("Vendor health check failed for providerCode={}, orderType={}: {}",
                        providerCode, orderType, e.getMessage());
            }
        }
        return new VendorHealthDto(isConnected, isDeviceIntegrated);
    }

    @Override
    public DiagnosticOrderResultDto submitManualResult(ManualDiagnosticResultRequestDto request, String jwtToken)
            throws Exception {
        boolean hasResult = (request.getResultSummary() != null && !request.getResultSummary().isBlank())
                || (request.getMdrRifResult() != null && !request.getMdrRifResult().isBlank());
        DiagnosticOrderType orderType = DiagnosticOrderType.fromCode(request.getOrderType());
        CloseReason close = resolveCloseReason(orderType, request.getReasonToCloseXray(),
                request.getReasonToCloseMTB(), request.getReasonToCloseRif());
        if (hasResult == (close != null)) {
            throw new IllegalArgumentException("Provide either a result (resultSummary and/or mdrRifResult) or "
                    + "one reasonToClose (reasonToCloseXray / reasonToCloseMTB / reasonToCloseRif), not both");
        }

        String actingUserName = resolveActingUserName(jwtToken);

        if (close != null) {
            DiagnosticOrder closed = closeOrderManually(request.getBeneficiaryId(), orderType, close, actingUserName);
            DiagnosticOrderResultDto dto = toResultDto(closed);
            applyResults(dto, mainResult(closed).orElse(null), rifResult(closed).orElse(null));
            return dto;
        }

        return withBeneficiaryLock(request.getBeneficiaryId(),
                () -> submitManualResultLocked(request, actingUserName));
    }

    private DiagnosticOrderResultDto submitManualResultLocked(ManualDiagnosticResultRequestDto request,
                                                              String actingUserName) throws Exception {
        DiagnosticOrder order = findLatestOrder(request.getBeneficiaryId(), request.getOrderType());
        if (DiagnosticOrderStatus.COMPLETED.name().equals(order.getStatus())) {
            throw new IllegalStateException("Cannot submit manual result: order is already COMPLETED (beneficiaryId="
                    + request.getBeneficiaryId() + ", orderType=" + request.getOrderType() + ")");
        }
        String mtbSummary = blankToNull(request.getResultSummary());
        String mdrRifResult = blankToNull(request.getMdrRifResult());
        boolean trueNat = TrueNatResultRules.isTrueNat(order.getOrderType());
        if (mdrRifResult != null && !trueNat) {
            throw new IllegalArgumentException("mdrRifResult only applies to MTB/MTB_PLUS orders, not "
                    + order.getOrderType());
        }
        if (mtbSummary == null) {
            // RIF entered on its own: the MTB result must already be stored, and positive.
            mtbSummary = mainResult(order).map(DiagnosticResult::getResultSummary).orElse(null);
            if (mtbSummary == null) {
                throw new IllegalStateException("Cannot submit mdrRifResult: no MTB result stored yet for this order "
                        + "(beneficiaryId=" + request.getBeneficiaryId() + ") — send resultSummary first or with it");
            }
        }
        if (mdrRifResult != null && !TrueNatResultRules.needsMdrRif(mtbSummary)) {
            throw new IllegalArgumentException("mdrRifResult is only run on a positive MTB result; MTB result is '"
                    + mtbSummary + "'");
        }
        order.setManuallyEnteredBy(actingUserName);
        // Same rules as the vendor poll: an invalid test outcome is recorded like a normal result but
        // CLOSES the order; a positive MTB leaves the order open until its MDR RIF result is in.
        // While open, an order the vendor received stays PENDING so the poll keeps running and brings
        // the vendor's MTB/RIF result and reports (its values replace these where they differ); an
        // order with no vendor stays MANUAL_ENTRY, waiting for mdrRifResult or reasonToCloseRif.
        // An invalid RIF result entered by hand is handled like an invalid MTB result entered by hand:
        // the order is CLOSED (the MTB row keeps its result, the RIF row records the invalid value).
        boolean invalidRif = isInvalidRifResult(mdrRifResult);
        DiagnosticOrderStatus status;
        if (isInvalidResult(order.getOrderType(), mtbSummary, SPUTUM_INVALID_MANUAL_RESULT) || invalidRif) {
            status = DiagnosticOrderStatus.CLOSED;
        } else if (trueNat && mdrRifResult == null && TrueNatResultRules.needsMdrRif(mtbSummary)) {
            status = order.getProviderOrderId() != null ? DiagnosticOrderStatus.PENDING : DiagnosticOrderStatus.MANUAL_ENTRY;
        } else {
            status = DiagnosticOrderStatus.COMPLETED;
        }
        // tbPresence as the vendor poll derives it: X-ray -> only "TB Presumptive" on a completed result
        // is positive; TrueNat -> from the MTB result.
        Boolean tbPresence = trueNat
                ? TrueNatResultRules.classifyTbPresence(order.getOrderType(), mtbSummary)
                : (status == DiagnosticOrderStatus.COMPLETED ? XRAY_TB_PRESUMPTIVE.equalsIgnoreCase(mtbSummary) : null);
        DiagnosticPollResult pollResult = new DiagnosticPollResult(
                status, null, mtbSummary, null, null, null, tbPresence, null,
                TrueNatResultRules.classifyDrugResistance(mdrRifResult), mdrRifResult,
                invalidRif ? DiagnosticOrderStatus.CLOSED.name() : null);
        return processResult(order, pollResult, true, actingUserName);
    }

    private record CloseReason(DiagnosticCloseStage stage, String reason) {
    }

    // At most one reasonToClose* may be sent, and it must fit the order type: Xray for XRAY_CHEST,
    // MTB/Rif for MTB and MTB_PLUS. Null when none was sent.
    private static CloseReason resolveCloseReason(DiagnosticOrderType orderType, String xray, String mtb, String rif) {
        List<CloseReason> given = new ArrayList<>();
        if (blankToNull(xray) != null) given.add(new CloseReason(DiagnosticCloseStage.XRAY, xray.trim()));
        if (blankToNull(mtb) != null) given.add(new CloseReason(DiagnosticCloseStage.MTB, mtb.trim()));
        if (blankToNull(rif) != null) given.add(new CloseReason(DiagnosticCloseStage.RIF, rif.trim()));
        if (given.isEmpty()) {
            return null;
        }
        if (given.size() > 1) {
            throw new IllegalArgumentException("Send only one of reasonToCloseXray, reasonToCloseMTB, reasonToCloseRif");
        }
        CloseReason close = given.get(0);
        boolean xrayOrder = orderType == DiagnosticOrderType.XRAY_CHEST;
        if (xrayOrder != (close.stage() == DiagnosticCloseStage.XRAY)) {
            throw new IllegalArgumentException(xrayOrder
                    ? "XRAY_CHEST orders are closed with reasonToCloseXray"
                    : orderType.name() + " orders are closed with reasonToCloseMTB or reasonToCloseRif");
        }
        return close;
    }

    // Stage for a close with no explicit choice (the daily expiry): RIF when a TrueNat order's MTB
    // result is in and positive, otherwise the order's own test.
    private DiagnosticCloseStage stageOf(DiagnosticOrder order) {
        if (DiagnosticOrderType.XRAY_CHEST.name().equals(order.getOrderType())) {
            return DiagnosticCloseStage.XRAY;
        }
        boolean mtbPositive = mainResult(order)
                .map(r -> TrueNatResultRules.needsMdrRif(r.getResultSummary()))
                .orElse(false);
        return mtbPositive ? DiagnosticCloseStage.RIF : DiagnosticCloseStage.MTB;
    }

    // reasonToCloseRif: the beneficiary's latest order of this type has a positive MTB result and is
    // still waiting for MDR RIF. That same order is CLOSED with reason_to_close_mdrrif; its MTB result
    // stays and is written back to tb_suspected like a finished result. Caller holds the beneficiary lock.
    private DiagnosticOrder closeRifStageLocked(Long beneficiaryId, DiagnosticOrderType orderType, String reason,
                                                String actingUserId) throws Exception {
        DiagnosticOrder order = diagnosticOrderRepo
                .findFirstByBeneficiaryIdAndOrderTypeAndDeletedFalseOrderByCreatedDateDesc(beneficiaryId, orderType.name())
                .orElseThrow(() -> new IllegalStateException("No " + orderType.name()
                        + " order found for beneficiaryId=" + beneficiaryId + " to close the MDR RIF test on"));
        String mtbSummary = mainResult(order).map(DiagnosticResult::getResultSummary).orElse(null);
        boolean awaitingRif = Set.of(DiagnosticOrderStatus.PENDING.name(), DiagnosticOrderStatus.IN_PROGRESS.name(),
                DiagnosticOrderStatus.MANUAL_ENTRY.name(), DiagnosticOrderStatus.INVALID.name()).contains(order.getStatus());
        if (!awaitingRif || !TrueNatResultRules.needsMdrRif(mtbSummary)) {
            throw new IllegalStateException("reasonToCloseRif needs the latest " + orderType.name() + " order to have a "
                    + "positive MTB result and still be waiting for MDR RIF (status=" + order.getStatus()
                    + ", MTB result=" + mtbSummary + "); use reasonToCloseMTB to close the MTB test instead");
        }
        order.setReasonToClose(DiagnosticCloseStage.RIF, reason);
        order.setManuallyEnteredBy(actingUserId);
        // RIF didn't take place: only reason_to_close_mdrrif (set above) records it — no MDR_RIF result
        // row is created. The MTB row keeps its result.
        DiagnosticPollResult pollResult = new DiagnosticPollResult(DiagnosticOrderStatus.CLOSED, null, mtbSummary,
                null, null, null, TrueNatResultRules.classifyTbPresence(order.getOrderType(), mtbSummary), null,
                null, null);
        processResult(order, pollResult, true, actingUserId);
        notifyProviderOrderClosed(order, reason);
        return order;
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    // sputumInvalidResult differs by source: SPUTUM_INVALID_POLLED_RESULT for vendor polls,
    // SPUTUM_INVALID_MANUAL_RESULT for manual entry. The X-ray value is the same for both.
    // isInvalidResult goes by order type, so it can't judge a RIF value: an invalid RIF result is the
    // manual "Invalid/Error" or the vendor's "Error-2".
    private static boolean isInvalidRifResult(String rifResult) {
        String rif = blankToNull(rifResult);
        return rif != null && (SPUTUM_INVALID_MANUAL_RESULT.equalsIgnoreCase(rif)
                || SPUTUM_INVALID_POLLED_RESULT.equalsIgnoreCase(rif));
    }

    private static boolean isInvalidResult(String orderTypeCode, String resultSummary, String sputumInvalidResult) {
        if (resultSummary == null) {
            return false;
        }
        String summary = resultSummary.trim();
        DiagnosticOrderType type = DiagnosticOrderType.fromCode(orderTypeCode);
        if (type == DiagnosticOrderType.XRAY_CHEST) {
            return XRAY_INVALID_RESULT.equalsIgnoreCase(summary);
        }
        if (type == DiagnosticOrderType.MTB) {
            return sputumInvalidResult.equalsIgnoreCase(summary);
        }
        return false;
    }

    // Automatic retest after a polled invalid outcome: a brand new PENDING row copying the closed
    // order's beneficiary, visit, vendor and patient details, with only a fresh externalOrderId,
    // pushed to the vendor straight away. Never throws — a failure here must not undo the close.
    private void pushRetestOrder(DiagnosticOrder closed) {
        try {
            withBeneficiaryLockBlocking(closed.getBeneficiaryId(), () -> {
                pushRetestOrderLocked(closed);
                return null;
            });
        } catch (Exception e) {
            logger.error("Failed to create retest order after invalid result, closedOrderId={}: {}",
                    closed.getId(), e.getMessage());
        }
    }

    private void pushRetestOrderLocked(DiagnosticOrder closed) {
        // A user push may have created a fresh order for this beneficiary+orderType after this one was
        // closed — the retest would then put the same patient on the vendor twice, so skip it.
        Optional<DiagnosticOrder> latest = diagnosticOrderRepo
                .findFirstByBeneficiaryIdAndOrderTypeAndDeletedFalseOrderByCreatedDateDesc(
                        closed.getBeneficiaryId(), closed.getOrderType());
        if (latest.isPresent() && !latest.get().getId().equals(closed.getId())
                && BLOCKING_STATUSES.contains(latest.get().getStatus())) {
            logger.info("Retest skipped for closedOrderId={}: newer order id={} (status={}) already exists",
                    closed.getId(), latest.get().getId(), latest.get().getStatus());
            return;
        }
        try {
            DiagnosticOrder retest = new DiagnosticOrder();
            retest.setVanID(closed.getVanID());
            retest.setParkingPlaceID(closed.getParkingPlaceID());
            retest.setOrderEvent(closed.getOrderEvent());
            retest.setBeneficiaryId(closed.getBeneficiaryId());
            retest.setVisitCode(closed.getVisitCode());
            retest.setProviderServiceName(closed.getProviderServiceName());
            retest.setProviderCode(closed.getProviderCode());
            retest.setOrderType(closed.getOrderType());
            retest.setExternalOrderId(String.format("%s-%d-%s", UUID.randomUUID(), closed.getVisitCode(),
                    closed.getOrderType()));
            retest.setStatus(DiagnosticOrderStatus.PENDING.name());
            retest.setPatientFirstName(closed.getPatientFirstName());
            retest.setPatientLastName(closed.getPatientLastName());
            retest.setPatientDateOfBirth(closed.getPatientDateOfBirth());
            retest.setPatientSex(closed.getPatientSex());
            retest.setCreatedBy("SYSTEM");
            retest.setModifiedBy("SYSTEM");
            retest = diagnosticOrderRepo.save(retest);
            retest = pushToProvider(retest, closed.getProviderCode());
            logger.info("Retest order created after invalid result: closedOrderId={}, retestOrderId={}, "
                            + "externalOrderId={}, status={}", closed.getId(), retest.getId(), retest.getExternalOrderId(),
                    retest.getStatus());
        } catch (Exception e) {
            logger.error("Failed to create retest order after invalid result, closedOrderId={}: {}",
                    closed.getId(), e.getMessage());
        }
    }

    // An X-ray the vendor reports as TB Presumptive (tbPresence true) is followed by an MTB order for
    // the same patient, created through createAndPushOrderLocked exactly like a push from the app:
    // same visit lookup, dedup (an MTB order the app already pushed is returned, not duplicated) and
    // vendor routing (MANUAL_ENTRY when MTB has no vendor). The caller already holds the beneficiary
    // lock. Never throws — a failure here must not undo the saved X-ray result.
    private void pushMtbOrderAfterPositiveXray(DiagnosticOrder xray, Boolean tbPresence) {
        if (!DiagnosticOrderType.XRAY_CHEST.name().equals(xray.getOrderType()) || !Boolean.TRUE.equals(tbPresence)) {
            return;
        }
        try {
            DiagnosticOrderRequestDto request = new DiagnosticOrderRequestDto();
            request.setBeneficiaryId(xray.getBeneficiaryId());
            request.setOrderType(DiagnosticOrderType.MTB.name());
            request.setOrderEvent(xray.getOrderEvent());
            DiagnosticOrderRequestDto.PatientDto patient = new DiagnosticOrderRequestDto.PatientDto();
            patient.setFirstName(xray.getPatientFirstName());
            patient.setLastName(xray.getPatientLastName());
            patient.setDateOfBirth(xray.getPatientDateOfBirth());
            patient.setSex(xray.getPatientSex());
            request.setPatient(patient);
            DiagnosticOrder mtb = createAndPushOrderLocked(request, "SYSTEM");
            logger.info("MTB order after TB Presumptive X-ray: xrayOrderId={}, mtbOrderId={}, externalOrderId={}, status={}",
                    xray.getId(), mtb.getId(), mtb.getExternalOrderId(), mtb.getStatus());
        } catch (Exception e) {
            logger.error("Failed to create MTB order after TB Presumptive X-ray, xrayOrderId={}: {}",
                    xray.getId(), e.getMessage());
        }
    }

    // MTB/MTB_PLUS: an invalid run — the MTB or RIF result "Error-2" — sets the poll result to INVALID.
    // The machine retests the same order by itself, so it is polled on rather than closed. A test the
    // vendor reports FAILED is not invalid: the order stays FAILED, as the vendor reported it.
    // "Indeterminate" is a real RIF result, not an error. Returns true when it changed the status.
    private static boolean markInvalidTrueNatResult(DiagnosticOrder order, DiagnosticPollResult result) {
        if (result == null || !TrueNatResultRules.isTrueNat(order.getOrderType())
                || DiagnosticOrderStatus.FAILED.equals(result.getStatus())) {
            return false;
        }
        String mtb = blankToNull(result.getResultSummary());
        String rif = blankToNull(result.getDrugResistanceSummary());
        boolean invalid = (DiagnosticOrderStatus.COMPLETED.equals(result.getStatus())
                        && SPUTUM_INVALID_POLLED_RESULT.equalsIgnoreCase(mtb == null ? "" : mtb))
                || SPUTUM_INVALID_POLLED_RESULT.equalsIgnoreCase(rif == null ? "" : rif);
        if (invalid) {
            result.setStatus(DiagnosticOrderStatus.INVALID);
            // Only the test that failed is INVALID: an invalid MTB run marks its own row; with a RIF
            // error the MTB row keeps its (valid) status and the RIF row the vendor's RIF status.
            if (SPUTUM_INVALID_POLLED_RESULT.equalsIgnoreCase(mtb == null ? "" : mtb)) {
                result.setMainStatus(DiagnosticOrderStatus.INVALID.name());
            }
        }
        return invalid;
    }

    // A vendor poll that COMPLETED with an invalid outcome is downgraded to CLOSED in place.
    // Returns true when that happened, so the caller still writes the result back to tb_suspected.
    private static boolean closeIfInvalidPolledResult(DiagnosticOrder order, DiagnosticPollResult result) {
        if (DiagnosticOrderStatus.COMPLETED.equals(result.getStatus())
                && isInvalidResult(order.getOrderType(), result.getResultSummary(), SPUTUM_INVALID_POLLED_RESULT)) {
            result.setStatus(DiagnosticOrderStatus.CLOSED);
            return true;
        }
        return false;
    }
}
