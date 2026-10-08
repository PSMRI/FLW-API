package com.iemr.flw.service;

import com.iemr.flw.domain.iemr.BenVisitDetail;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Stop TB nurse OPD: chief complaints, prescription and dispensing written to the same standard
 * tables MMU/TM and Inventory-API use, inside the caller's transaction.
 *
 * - Chief complaints  -> t_benchiefcomplaint (one row per complaint, like TM saveBenChiefComplaints)
 * - Prescription      -> t_prescription + t_prescribeddrug (DrugID + QtyPrescribed, like TM doctor save)
 * - Dispensing        -> t_patientissue + t_itemstockexit (ExitType T_PatientIssue) and
 *                        t_itemstockentry.QuantityInHand reduced, like Inventory-API issuePatientDrugs.
 *
 * Batches are picked by m_itemcategory.IssueType (FEFO / FIFO / LIFO) from non-expired batches that
 * last the treatment duration, and are locked (SELECT ... FOR UPDATE) so two nurses can never take the
 * same stock. If any drug is short, nothing is saved (InsufficientStockException rolls back the caller).
 *
 * Native SQL only, no new entity mappings: FLW runs with ddl-auto=create.
 */
@Service
public class StopTBPrescriptionService {

    private static final String REFERENCE_PREFIX = "StopTB:";

    @PersistenceContext(unitName = "db_iemr")
    private EntityManager em;

    @Autowired
    private CampConfigService campConfigService;

    public static class InsufficientStockException extends RuntimeException {
        public InsufficientStockException(String message) {
            super(message);
        }
    }

    /** Previous result for a submissionId already saved, or null. Lets the app retry safely. */
    @Transactional(propagation = Propagation.MANDATORY)
    public Map<String, Object> findPreviousSubmission(Long beneficiaryRegID, String submissionId) {
        if (submissionId == null || submissionId.isBlank()) return null;
        List<?> rows = em.createNativeQuery("SELECT PatientIssueID, VisitCode, PrescriptionID FROM db_iemr.t_patientissue "
                        + "WHERE BeneficiaryRegID = :ben AND Reference = :ref AND Deleted = false")
                .setParameter("ben", beneficiaryRegID)
                .setParameter("ref", REFERENCE_PREFIX + submissionId)
                .getResultList();
        if (rows.isEmpty()) return null;
        Object[] row = (Object[]) rows.get(0);
        Integer facilityID = campConfigService.getFacilityID();

        List<?> issued = em.createNativeQuery("SELECT e.ItemID, i.ItemName, SUM(x.Quantity) "
                        + "FROM db_iemr.t_itemstockexit x "
                        + "JOIN db_iemr.t_itemstockentry e ON e.VanSerialNo = x.ItemStockEntryID AND e.FacilityID = x.FacilityID "
                        + "JOIN db_iemr.m_item i ON i.ItemID = e.ItemID "
                        + "WHERE x.ExitType = 'T_PatientIssue' AND x.ExitTypeID = :issueID AND x.FacilityID = :facilityID "
                        + "GROUP BY e.ItemID, i.ItemName")
                .setParameter("issueID", ((Number) row[0]).longValue())
                .setParameter("facilityID", facilityID)
                .getResultList();
        List<Map<String, Object>> dispensed = new ArrayList<>();
        for (Object o : issued) {
            Object[] r = (Object[]) o;
            int drugID = ((Number) r[0]).intValue();
            Map<String, Object> d = new LinkedHashMap<>();
            d.put("drugID", drugID);
            d.put("drugName", r[1]);
            d.put("qtyIssued", ((Number) r[2]).intValue());
            d.put("availableQuantity", availableQuantity(facilityID, drugID, 0));
            dispensed.add(d);
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("beneficiaryRegID", beneficiaryRegID);
        result.put("visitCode", row[1] != null ? ((Number) row[1]).longValue() : null);
        result.put("prescriptionID", row[2] != null ? ((Number) row[2]).longValue() : null);
        result.put("dispensed", dispensed);
        result.put("duplicate", true);
        return result;
    }

    /**
     * Replaces the visit's chief complaints with the given list (edit = remove old + insert new, as MMU
     * does on update). Rows without an ID and without a name are skipped.
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public void saveChiefComplaints(List<Map<String, Object>> complaints, Long beneficiaryRegID, BenVisitDetail visit,
            String createdBy, Integer vanID, Integer parkingPlaceID) {
        if (complaints == null) return;
        em.createNativeQuery("UPDATE db_iemr.t_benchiefcomplaint SET Deleted = true, ModifiedBy = :user "
                        + "WHERE BeneficiaryRegID = :ben AND VisitCode = :visitCode AND Deleted = false")
                .setParameter("user", createdBy)
                .setParameter("ben", beneficiaryRegID)
                .setParameter("visitCode", visit.getVisitCode())
                .executeUpdate();
        for (Map<String, Object> c : complaints) {
            Integer complaintID = toInt(c.get("chiefComplaintID"));
            String complaint = toStr(c.get("chiefComplaint"));
            if (complaintID == null && (complaint == null || complaint.isBlank())) continue;
            em.createNativeQuery("INSERT INTO db_iemr.t_benchiefcomplaint (BeneficiaryRegID, BenVisitID, ProviderServiceMapID, "
                            + "VisitCode, ChiefComplaintID, ChiefComplaint, Duration, UnitOfDuration, Description, Deleted, "
                            + "Processed, CreatedBy, VanID, ParkingPlaceID) VALUES (:ben, :visitID, :psm, :visitCode, :cid, "
                            + ":name, :duration, :unit, :description, false, 'N', :user, :van, :pp)")
                    .setParameter("ben", beneficiaryRegID)
                    .setParameter("visitID", visit.getBenVisitId())
                    .setParameter("psm", visit.getProviderServiceMapID())
                    .setParameter("visitCode", visit.getVisitCode())
                    .setParameter("cid", complaintID)
                    .setParameter("name", complaint)
                    .setParameter("duration", toInt(c.get("duration")))
                    .setParameter("unit", toStr(c.get("unitOfDuration")))
                    .setParameter("description", toStr(c.get("description")))
                    .setParameter("user", createdBy)
                    .setParameter("van", vanID)
                    .setParameter("pp", parkingPlaceID)
                    .executeUpdate();
            stampVanSerialNo("t_benchiefcomplaint", "ID", lastInsertId());
        }
    }

    /**
     * Saves the prescription and issues every drug from the camp store. Returns prescriptionID and,
     * per drug, the quantity issued and what is left in the store.
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public Map<String, Object> prescribeAndDispense(Map<String, Object> prescription, Long beneficiaryRegID,
            BenVisitDetail visit, String submissionId, String createdBy, Integer vanID, Integer parkingPlaceID) {
        List<Map<String, Object>> drugs = toList(prescription.get("drugs"));
        Map<String, Object> result = new LinkedHashMap<>();
        if (drugs.isEmpty()) return result;

        Integer facilityID = campConfigService.getFacilityID();
        if (facilityID == null) {
            throw new IllegalStateException("No store is mapped to van " + vanID + " (m_van.FacilityID)");
        }

        // 1. Validate every line and check total stock per drug before writing anything.
        Map<Integer, Integer> requested = new LinkedHashMap<>();
        Map<Integer, Object[]> items = new HashMap<>();
        for (Map<String, Object> d : drugs) {
            Integer drugID = toInt(d.get("drugID"));
            Integer qty = toInt(d.get("qtyPrescribed"));
            if (drugID == null) throw new IllegalArgumentException("drugID is required for every drug");
            if (qty == null || qty <= 0) throw new IllegalArgumentException("qtyPrescribed must be more than 0 for drugID " + drugID);
            if (!items.containsKey(drugID)) items.put(drugID, getStoreItem(facilityID, drugID));
            requested.merge(drugID, qty, Integer::sum);
        }
        List<String> shortages = new ArrayList<>();
        Map<Integer, List<Object[]>> lockedBatches = new HashMap<>();
        for (Map.Entry<Integer, Integer> r : requested.entrySet()) {
            Object[] item = items.get(r.getKey());
            int durationDays = maxDurationDays(drugs, r.getKey());
            List<Object[]> batches = lockBatches(facilityID, r.getKey(), (String) item[6], durationDays);
            lockedBatches.put(r.getKey(), batches);
            int available = batches.stream().mapToInt(b -> ((Number) b[1]).intValue()).sum();
            if (available < r.getValue()) {
                shortages.add(item[1] + " (drugID " + r.getKey() + "): asked " + r.getValue() + ", available " + available);
            }
        }
        if (!shortages.isEmpty()) {
            throw new InsufficientStockException("Insufficient stock - " + String.join("; ", shortages));
        }

        // 2. Prescription + prescribed drugs (Stop TB service line, same as the visit).
        Integer storePsm = getStorePsm(facilityID);
        em.createNativeQuery("INSERT INTO db_iemr.t_prescription (BeneficiaryRegID, BenVisitID, ProviderServiceMapID, VisitCode, "
                        + "Instruction, Deleted, Processed, CreatedBy, VanID, ParkingPlaceID) "
                        + "VALUES (:ben, :visitID, :psm, :visitCode, :instruction, false, 'N', :user, :van, :pp)")
                .setParameter("ben", beneficiaryRegID)
                .setParameter("visitID", visit.getBenVisitId())
                .setParameter("psm", visit.getProviderServiceMapID())
                .setParameter("visitCode", visit.getVisitCode())
                .setParameter("instruction", toStr(prescription.get("instruction")))
                .setParameter("user", createdBy)
                .setParameter("van", vanID)
                .setParameter("pp", parkingPlaceID)
                .executeUpdate();
        long prescriptionID = lastInsertId();
        stampVanSerialNo("t_prescription", "PrescriptionID", prescriptionID);

        for (Map<String, Object> d : drugs) {
            Integer drugID = toInt(d.get("drugID"));
            Object[] item = items.get(drugID);
            em.createNativeQuery("INSERT INTO db_iemr.t_prescribeddrug (BeneficiaryRegID, BenVisitID, ProviderServiceMapID, "
                            + "VisitCode, PrescriptionID, DrugForm, DrugID, GenericDrugName, DrugStrength, Dose, Route, Frequency, "
                            + "Duration, DuartionUnit, SpecialInstruction, QtyPrescribed, isEDL, Deleted, Processed, CreatedBy, "
                            + "VanID, ParkingPlaceID) VALUES (:ben, :visitID, :psm, :visitCode, :prescriptionID, :form, :drugID, "
                            + ":name, :strength, :dose, :route, :frequency, :duration, :durationUnit, :instruction, :qty, "
                            + ":isEDL, false, 'N', :user, :van, :pp)")
                    .setParameter("ben", beneficiaryRegID)
                    .setParameter("visitID", visit.getBenVisitId())
                    .setParameter("psm", visit.getProviderServiceMapID())
                    .setParameter("visitCode", visit.getVisitCode())
                    .setParameter("prescriptionID", prescriptionID)
                    .setParameter("form", item[3])
                    .setParameter("drugID", drugID)
                    .setParameter("name", item[1])
                    .setParameter("strength", item[2])
                    .setParameter("dose", toStr(d.get("dose")))
                    .setParameter("route", item[4])
                    .setParameter("frequency", toStr(d.get("frequency")))
                    .setParameter("duration", toStr(d.get("duration")))
                    .setParameter("durationUnit", toStr(d.get("durationUnit")))
                    .setParameter("instruction", toStr(d.get("instructions")))
                    .setParameter("qty", toInt(d.get("qtyPrescribed")))
                    .setParameter("isEDL", item[5])
                    .setParameter("user", createdBy)
                    .setParameter("van", vanID)
                    .setParameter("pp", parkingPlaceID)
                    .executeUpdate();
            stampVanSerialNo("t_prescribeddrug", "PrescribedDrugID", lastInsertId());
        }

        // 3. Patient issue header (same shape as Inventory-API issuePatientDrugs, IssueType 'System').
        em.createNativeQuery("INSERT INTO db_iemr.t_patientissue (BeneficiaryRegID, BenVisitID, VisitCode, FacilityID, "
                        + "PrescriptionID, Reference, IssueType, IssuedBy, ProviderServiceMapID, Deleted, Processed, CreatedBy, "
                        + "SyncFacilityID, VanID, ParkingPlaceID) VALUES (:ben, :visitID, :visitCode, :facilityID, "
                        + ":prescriptionID, :ref, 'System', :user, :psm, false, 'N', :user, :facilityID, :van, :pp)")
                .setParameter("ben", beneficiaryRegID)
                .setParameter("visitID", visit.getBenVisitId())
                .setParameter("visitCode", visit.getVisitCode())
                .setParameter("facilityID", facilityID)
                .setParameter("prescriptionID", prescriptionID)
                .setParameter("ref", submissionId != null && !submissionId.isBlank() ? REFERENCE_PREFIX + submissionId : null)
                .setParameter("user", createdBy)
                .setParameter("psm", storePsm)
                .setParameter("van", vanID)
                .setParameter("pp", parkingPlaceID)
                .executeUpdate();
        long patientIssueID = lastInsertId();
        stampVanSerialNo("t_patientissue", "PatientIssueID", patientIssueID);

        // 4. Take each drug from its locked batches in issue order; one stock exit per batch used.
        List<Map<String, Object>> dispensed = new ArrayList<>();
        for (Map.Entry<Integer, Integer> r : requested.entrySet()) {
            int remaining = r.getValue();
            for (Object[] batch : lockedBatches.get(r.getKey())) {
                if (remaining == 0) break;
                long batchSerialNo = ((Number) batch[0]).longValue();
                int take = Math.min(remaining, ((Number) batch[1]).intValue());
                em.createNativeQuery("INSERT INTO db_iemr.t_itemstockexit (ItemStockEntryID, FacilityID, Quantity, "
                                + "ProviderServiceMapID, ExitTypeID, ExitType, Deleted, Processed, CreatedBy, SyncFacilityID, "
                                + "VanID, ParkingPlaceID) VALUES (:batch, :facilityID, :qty, :psm, :issueID, 'T_PatientIssue', "
                                + "false, 'N', :user, :facilityID, :van, :pp)")
                        .setParameter("batch", batchSerialNo)
                        .setParameter("facilityID", facilityID)
                        .setParameter("qty", take)
                        .setParameter("psm", storePsm)
                        .setParameter("issueID", patientIssueID)
                        .setParameter("user", createdBy)
                        .setParameter("van", vanID)
                        .setParameter("pp", parkingPlaceID)
                        .executeUpdate();
                stampVanSerialNo("t_itemstockexit", "ItemStockExitID", lastInsertId());
                // Same update as Inventory-API ItemStockEntryRepo.updateStock
                em.createNativeQuery("UPDATE db_iemr.t_itemstockentry SET QuantityInHand = QuantityInHand - :qty "
                                + "WHERE VanSerialNo = :batch AND FacilityID = :facilityID")
                        .setParameter("qty", take)
                        .setParameter("batch", batchSerialNo)
                        .setParameter("facilityID", facilityID)
                        .executeUpdate();
                remaining -= take;
            }
            Map<String, Object> d = new LinkedHashMap<>();
            d.put("drugID", r.getKey());
            d.put("drugName", items.get(r.getKey())[1]);
            d.put("qtyIssued", r.getValue());
            d.put("availableQuantity", availableQuantity(facilityID, r.getKey(), 0));
            dispensed.add(d);
        }

        result.put("prescriptionID", prescriptionID);
        result.put("dispensed", dispensed);
        return result;
    }

    // ItemID, ItemName, strength+UOM, form, route, isEDL, IssueType — only if the drug is mapped to the store.
    private Object[] getStoreItem(Integer facilityID, Integer drugID) {
        List<?> rows = em.createNativeQuery("SELECT i.ItemID, i.ItemName, CONCAT_WS(' ', i.Strength, u.UOMName), "
                        + "f.ItemFormName, r.RouteName, i.isEDL, c.IssueType "
                        + "FROM db_iemr.m_item i "
                        + "JOIN db_iemr.m_itemfacilitymapping m ON m.ItemID = i.ItemID AND m.FacilityID = :facilityID AND m.Deleted = false "
                        + "LEFT JOIN db_iemr.m_uom u ON u.UOMID = i.UOMID "
                        + "LEFT JOIN db_iemr.m_itemform f ON f.ItemFormID = i.ItemFormID "
                        + "LEFT JOIN db_iemr.m_routeofadmin r ON r.RouteID = i.RouteID "
                        + "LEFT JOIN db_iemr.m_itemcategory c ON c.ItemCategoryID = i.ItemCategoryID "
                        + "WHERE i.ItemID = :drugID AND i.Deleted = false")
                .setParameter("facilityID", facilityID)
                .setParameter("drugID", drugID)
                .getResultList();
        if (rows.isEmpty()) {
            throw new IllegalArgumentException("drugID " + drugID + " is not available in this camp's store");
        }
        return (Object[]) rows.get(0);
    }

    // Usable batches (VanSerialNo, QuantityInHand), locked for this transaction, in the category's issue order.
    @SuppressWarnings("unchecked")
    private List<Object[]> lockBatches(Integer facilityID, Integer drugID, String issueType, int durationDays) {
        String order;
        if ("Last In First Out".equalsIgnoreCase(issueType)) order = "CreatedDate DESC, ItemStockEntryID DESC";
        else if ("First In First Out".equalsIgnoreCase(issueType)) order = "CreatedDate ASC, ItemStockEntryID ASC";
        else order = "ExpiryDate ASC, ItemStockEntryID ASC"; // First Expiry First Out (default)
        Query q = em.createNativeQuery("SELECT VanSerialNo, QuantityInHand FROM db_iemr.t_itemstockentry "
                        + "WHERE FacilityID = :facilityID AND ItemID = :drugID AND Deleted = false AND QuantityInHand > 0 "
                        + "AND ExpiryDate > DATE_ADD(CURDATE(), INTERVAL :days DAY) ORDER BY " + order + " FOR UPDATE")
                .setParameter("facilityID", facilityID)
                .setParameter("drugID", drugID)
                .setParameter("days", durationDays);
        return (List<Object[]>) q.getResultList();
    }

    private int availableQuantity(Integer facilityID, Integer drugID, int durationDays) {
        Object v = em.createNativeQuery("SELECT COALESCE(SUM(QuantityInHand), 0) FROM db_iemr.t_itemstockentry "
                        + "WHERE FacilityID = :facilityID AND ItemID = :drugID AND Deleted = false AND QuantityInHand > 0 "
                        + "AND ExpiryDate > DATE_ADD(CURDATE(), INTERVAL :days DAY)")
                .setParameter("facilityID", facilityID)
                .setParameter("drugID", drugID)
                .setParameter("days", durationDays)
                .getSingleResult();
        return ((Number) v).intValue();
    }

    private Integer getStorePsm(Integer facilityID) {
        Object v = em.createNativeQuery("SELECT ProviderServiceMapID FROM db_iemr.m_facility WHERE FacilityID = :facilityID")
                .setParameter("facilityID", facilityID)
                .getSingleResult();
        return v != null ? ((Number) v).intValue() : null;
    }

    // Longest treatment among the lines for this drug, so the batch lasts until the course ends.
    private int maxDurationDays(List<Map<String, Object>> drugs, Integer drugID) {
        int max = 0;
        for (Map<String, Object> d : drugs) {
            if (!drugID.equals(toInt(d.get("drugID")))) continue;
            Integer n = toInt(d.get("duration"));
            if (n == null || n <= 0) continue;
            String unit = Optional.ofNullable(toStr(d.get("durationUnit"))).orElse("Day").toLowerCase();
            int days = unit.startsWith("week") ? n * 7 : unit.startsWith("month") ? n * 30 : unit.startsWith("year") ? n * 365 : n;
            max = Math.max(max, days);
        }
        return max;
    }

    private long lastInsertId() {
        return ((Number) em.createNativeQuery("SELECT LAST_INSERT_ID()").getSingleResult()).longValue();
    }

    // VanSerialNo = own ID, as every MMU/Inventory save does, so van-to-server sync can identify the row.
    private void stampVanSerialNo(String table, String idColumn, long id) {
        em.createNativeQuery("UPDATE db_iemr." + table + " SET VanSerialNo = " + idColumn + " WHERE " + idColumn + " = :id")
                .setParameter("id", id)
                .executeUpdate();
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> toList(Object v) {
        if (v instanceof List) return (List<Map<String, Object>>) v;
        return Collections.emptyList();
    }

    private Integer toInt(Object v) {
        if (v == null) return null;
        if (v instanceof Number) return ((Number) v).intValue();
        String s = v.toString().trim();
        if (s.isEmpty()) return null;
        try {
            return (int) Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String toStr(Object v) {
        return v != null ? v.toString() : null;
    }
}
