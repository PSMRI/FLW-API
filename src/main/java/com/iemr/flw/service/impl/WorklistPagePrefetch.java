package com.iemr.flw.service.impl;

import com.iemr.flw.domain.iemr.BenAnthropometryDetail;
import com.iemr.flw.domain.iemr.BenFlowStatus;
import com.iemr.flw.domain.iemr.BenPhysicalVitalDetail;
import com.iemr.flw.domain.identity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Loads everything the worklist builder needs for one page with one query per table, instead of
 * ~16 queries per beneficiary. Each query keeps the ordering of the per-row query it replaces, so
 * the response is unchanged.
 *
 * IDs are written into the SQL as numeric literals rather than bound as a collection: Hibernate 6.4.1
 * in this service throws "Binding is multi-valued" for every IN (:list) parameter. The values are
 * Long/BigInteger read from the database, so the literal list cannot carry anything but digits.
 */
@Component
public class WorklistPagePrefetch {

    @PersistenceContext(unitName = "db_identity")
    private EntityManager identityEm;

    @PersistenceContext(unitName = "db_iemr")
    private EntityManager iemrEm;

    public static class Page {
        final Map<BigInteger, List<RMNCHMBeneficiarymapping>> mappingsByAddress = new HashMap<>();
        final Map<BigInteger, RMNCHMBeneficiarydetail> details = new HashMap<>();
        final Map<BigInteger, RMNCHMBeneficiaryAccount> accounts = new HashMap<>();
        final Map<BigInteger, RMNCHMBeneficiaryImage> images = new HashMap<>();
        final Map<BigInteger, RMNCHMBeneficiaryaddress> addresses = new HashMap<>();
        final Map<BigInteger, RMNCHMBeneficiarycontact> contacts = new HashMap<>();
        final Map<Long, List<BigInteger>> benIdsByRegId = new HashMap<>();
        final Map<Long, List<RMNCHBeneficiaryDetailsRmnch>> rmnchByRegId = new HashMap<>();
        final Map<Long, List<RMNCHBornBirthDetails>> bornBirthByRegId = new HashMap<>();
        final Map<Long, List<RMNCHHouseHoldDetails>> householdsById = new HashMap<>();
        final Map<Long, List<BenFlowStatus>> flowsByRegId = new HashMap<>();
        final Map<Long, List<BenAnthropometryDetail>> anthropometryByRegId = new HashMap<>();
        final Map<Long, List<BenPhysicalVitalDetail>> vitalsByRegId = new HashMap<>();
        final Set<Long> regIdsWithHealthId = new HashSet<>();
        final Map<String, Integer> userIdByName = new HashMap<>();

        public List<RMNCHMBeneficiarymapping> mappings(BigInteger addressId) {
            return mappingsByAddress.getOrDefault(addressId, Collections.emptyList());
        }
        public RMNCHMBeneficiarydetail detail(BigInteger id) { return details.get(id); }
        public RMNCHMBeneficiaryAccount account(BigInteger id) { return accounts.get(id); }
        public RMNCHMBeneficiaryImage image(BigInteger id) { return images.get(id); }
        public RMNCHMBeneficiaryaddress address(BigInteger id) { return addresses.get(id); }
        public RMNCHMBeneficiarycontact contact(BigInteger id) { return contacts.get(id); }
        public List<BigInteger> benIds(Long regId) { return benIdsByRegId.getOrDefault(regId, Collections.emptyList()); }
        public List<RMNCHBeneficiaryDetailsRmnch> rmnch(Long regId) { return rmnchByRegId.getOrDefault(regId, Collections.emptyList()); }
        public List<RMNCHBornBirthDetails> bornBirth(Long regId) { return bornBirthByRegId.getOrDefault(regId, Collections.emptyList()); }
        public List<RMNCHHouseHoldDetails> households(Long householdId) { return householdsById.getOrDefault(householdId, Collections.emptyList()); }
        public List<BenFlowStatus> flows(Long regId) { return flowsByRegId.getOrDefault(regId, Collections.emptyList()); }
        public List<BenAnthropometryDetail> anthropometry(Long regId) { return anthropometryByRegId.getOrDefault(regId, Collections.emptyList()); }
        public List<BenPhysicalVitalDetail> vitals(Long regId) { return vitalsByRegId.getOrDefault(regId, Collections.emptyList()); }
        public boolean hasHealthId(Long regId) { return regIdsWithHealthId.contains(regId); }
        public Map<String, Integer> userIdCache() { return userIdByName; }
    }

    /** The page's addresses in the order of {@code ids} (the worklist page order); ids with no row are skipped, as before. */
    public List<RMNCHMBeneficiaryaddress> addressesInOrder(List<BigInteger> ids) {
        Map<BigInteger, RMNCHMBeneficiaryaddress> byId = new HashMap<>();
        putAll(byId, entities(identityEm, RMNCHMBeneficiaryaddress.class,
                "SELECT * FROM db_identity.i_beneficiaryaddress WHERE BenAddressID IN (%s)",
                new LinkedHashSet<>(ids)), RMNCHMBeneficiaryaddress::getBenAddressID);
        return ids.stream().map(byId::get).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public Page load(List<RMNCHMBeneficiaryaddress> pageAddresses) {
        Page page = new Page();
        pageAddresses.forEach(a -> page.addresses.put(a.getBenAddressID(), a));
        // The builder looks mappings up by address.getId(), which is the VanSerialNo column, not the
        // address primary key - keep that exactly.
        Set<Object> lookupIds = pageAddresses.stream().map(RMNCHMBeneficiaryaddress::getId)
                .filter(Objects::nonNull).collect(Collectors.toCollection(LinkedHashSet::new));
        if (lookupIds.isEmpty()) return page;

        // getByAddressID: ORDER BY BenMapId DESC
        for (RMNCHMBeneficiarymapping m : entities(identityEm, RMNCHMBeneficiarymapping.class,
                "SELECT * FROM db_identity.i_beneficiarymapping WHERE BenAddressId IN (%s) ORDER BY BenMapId DESC", lookupIds)) {
            page.mappingsByAddress.computeIfAbsent(m.getBenAddressId(), k -> new ArrayList<>()).add(m);
        }
        // the builder only uses the first (newest) mapping of each address
        List<RMNCHMBeneficiarymapping> used = page.mappingsByAddress.values().stream()
                .map(l -> l.get(0)).collect(Collectors.toList());

        putAll(page.details, entities(identityEm, RMNCHMBeneficiarydetail.class,
                "SELECT * FROM db_identity.i_beneficiarydetails WHERE BeneficiaryDetailsId IN (%s)",
                ids(used, RMNCHMBeneficiarymapping::getBenDetailsId)), RMNCHMBeneficiarydetail::getBeneficiaryDetailsId);
        putAll(page.accounts, entities(identityEm, RMNCHMBeneficiaryAccount.class,
                "SELECT * FROM db_identity.i_beneficiaryaccount WHERE BenAccountID IN (%s)",
                ids(used, RMNCHMBeneficiarymapping::getBenAccountID)), RMNCHMBeneficiaryAccount::getBenAccountID);
        putAll(page.images, entities(identityEm, RMNCHMBeneficiaryImage.class,
                "SELECT * FROM db_identity.i_beneficiaryimage WHERE BenImageId IN (%s)",
                ids(used, RMNCHMBeneficiarymapping::getBenImageId)), RMNCHMBeneficiaryImage::getBenImageId);
        putAll(page.contacts, entities(identityEm, RMNCHMBeneficiarycontact.class,
                "SELECT * FROM db_identity.i_beneficiarycontacts WHERE BenContactsID IN (%s)",
                ids(used, RMNCHMBeneficiarymapping::getBenContactsId)), RMNCHMBeneficiarycontact::getBenContactsID);
        Set<Object> missingAddressIds = ids(used, RMNCHMBeneficiarymapping::getBenAddressId);
        missingAddressIds.removeAll(page.addresses.keySet());
        putAll(page.addresses, entities(identityEm, RMNCHMBeneficiaryaddress.class,
                "SELECT * FROM db_identity.i_beneficiaryaddress WHERE BenAddressID IN (%s)", missingAddressIds),
                RMNCHMBeneficiaryaddress::getBenAddressID);

        Set<Object> regIds = ids(used, RMNCHMBeneficiarymapping::getBenRegId);
        if (regIds.isEmpty()) return page;

        for (Object[] r : rows(identityEm,
                "SELECT BenRegId, BeneficiaryID FROM db_identity.m_beneficiaryregidmapping WHERE BenRegId IN (%s)", regIds)) {
            page.benIdsByRegId.computeIfAbsent(toLong(r[0]), k -> new ArrayList<>()).add(toBigInteger(r[1]));
        }
        // getDetailsByRegID: ORDER BY beneficiaryDetails_RmnchId DESC
        group(page.rmnchByRegId, entities(identityEm, RMNCHBeneficiaryDetailsRmnch.class,
                "SELECT * FROM db_identity.i_beneficiarydetails_rmnch WHERE BeneficiaryRegID IN (%s) ORDER BY beneficiaryDetails_RmnchId DESC",
                regIds), RMNCHBeneficiaryDetailsRmnch::getBenRegId);
        group(page.bornBirthByRegId, entities(identityEm, RMNCHBornBirthDetails.class,
                "SELECT * FROM db_identity.i_bornbirthdeatils WHERE BeneficiaryRegID IN (%s) ORDER BY BornBirthDeatilsId",
                regIds), RMNCHBornBirthDetails::getBenRegId);

        // households of the newest rmnch row (the one the builder uses)
        Set<Object> householdIds = page.rmnchByRegId.values().stream().map(l -> l.get(0).getHouseoldId())
                .filter(Objects::nonNull).collect(Collectors.toCollection(LinkedHashSet::new));
        group(page.householdsById, entities(identityEm, RMNCHHouseHoldDetails.class,
                "SELECT * FROM db_identity.i_householddetails WHERE houseoldId IN (%s) ORDER BY houseHoldDetailsId",
                householdIds), RMNCHHouseHoldDetails::getHouseoldId);

        group(page.flowsByRegId, entities(iemrEm, BenFlowStatus.class,
                "SELECT * FROM db_iemr.i_ben_flow_outreach WHERE beneficiary_reg_id IN (%s) ORDER BY ben_flow_id",
                regIds), BenFlowStatus::getBeneficiaryRegID);
        // findByBeneficiaryRegIDOrderByCreatedDateDesc
        group(page.anthropometryByRegId, entities(iemrEm, BenAnthropometryDetail.class,
                "SELECT * FROM db_iemr.t_phy_anthropometry WHERE BeneficiaryRegID IN (%s) ORDER BY CreatedDate DESC",
                regIds), BenAnthropometryDetail::getBeneficiaryRegID);
        group(page.vitalsByRegId, entities(iemrEm, BenPhysicalVitalDetail.class,
                "SELECT * FROM db_iemr.t_phy_vitals WHERE BeneficiaryRegID IN (%s) ORDER BY CreatedDate DESC",
                regIds), BenPhysicalVitalDetail::getBeneficiaryRegID);

        for (Object id : iemrEm.createNativeQuery(String.format(
                "SELECT DISTINCT BeneficiaryRegID FROM db_iemr.m_benhealthidmapping WHERE BeneficiaryRegID IN (%s)",
                literalList(regIds))).getResultList()) {
            page.regIdsWithHealthId.add(toLong(id));
        }
        return page;
    }

    @SuppressWarnings("unchecked")
    private static <T> List<T> entities(EntityManager em, Class<T> type, String sql, Collection<?> ids) {
        if (ids.isEmpty()) return Collections.emptyList();
        return em.createNativeQuery(String.format(sql, literalList(ids)), type).getResultList();
    }

    @SuppressWarnings("unchecked")
    private static List<Object[]> rows(EntityManager em, String sql, Collection<?> ids) {
        if (ids.isEmpty()) return Collections.emptyList();
        return em.createNativeQuery(String.format(sql, literalList(ids))).getResultList();
    }

    private static <T> Set<Object> ids(List<T> rows, Function<T, Object> getter) {
        return rows.stream().map(getter).filter(Objects::nonNull).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private static <K, V> void putAll(Map<K, V> target, List<V> rows, Function<V, K> key) {
        rows.forEach(r -> target.putIfAbsent(key.apply(r), r));
    }

    private static <V> void group(Map<Long, List<V>> target, List<V> rows, Function<V, Long> key) {
        rows.forEach(r -> target.computeIfAbsent(key.apply(r), k -> new ArrayList<>()).add(r));
    }

    // Only whole numbers ever reach the SQL text: anything else is rejected rather than quoted.
    private static String literalList(Collection<?> ids) {
        return ids.stream().map(id -> {
            if (id instanceof Number) return new BigInteger(id.toString()).toString();
            throw new IllegalArgumentException("Non-numeric id in worklist prefetch: " + id);
        }).collect(Collectors.joining(","));
    }

    private static Long toLong(Object o) { return o == null ? null : ((Number) o).longValue(); }

    private static BigInteger toBigInteger(Object o) { return o == null ? null : new BigInteger(o.toString()); }
}
