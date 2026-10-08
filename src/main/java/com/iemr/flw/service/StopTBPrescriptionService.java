package com.iemr.flw.service;

import com.iemr.flw.domain.identity.RMNCHMBeneficiarydetail;
import com.iemr.flw.domain.identity.RMNCHMBeneficiarymapping;
import com.iemr.flw.domain.iemr.BenVisitDetail;
import com.iemr.flw.repo.identity.BeneficiaryRepo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.Period;
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

    @Autowired
    private BeneficiaryRepo beneficiaryRepo;

    private final Logger logger = LoggerFactory.getLogger(StopTBPrescriptionService.class);

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
        // Processed='N' so the removal is uploaded to central again (central updates rows it already has).
        em.createNativeQuery("UPDATE db_iemr.t_benchiefcomplaint SET Deleted = true, Processed = 'N', ModifiedBy = :user "
                        + "WHERE BeneficiaryRegID = :ben AND VisitCode = :visitCode AND Deleted = false")
                .setParameter("user", createdBy)
                .setParameter("ben", beneficiaryRegID)
                .setParameter("visitCode", visit.getVisitCode())
                .executeUpdate();
        for (Map<String, Object> c : complaints) {
            Integer complaintID = toInt(c.get("chiefComplaintID"));
            String complaint = toStr(c.get("chiefComplaint"));
            // ID from the chief complaint master is required: getAll and reports identify new records by it.
            if (complaintID == null) {
                throw new IllegalArgumentException("chiefComplaintID is required for every chief complaint"
                        + (complaint != null ? " (" + complaint + ")" : ""));
            }
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
        if (drugs.isEmpty()) {
            // Remarks without medicine are still kept, as MMU saves the visit instruction without drugs.
            String instruction = toStr(prescription.get("instruction"));
            if (instruction != null && !instruction.isBlank()) {
                result.put("prescriptionID", insertPrescription(beneficiaryRegID, visit, instruction, createdBy, vanID,
                        parkingPlaceID));
            }
            return result;
        }

        Integer facilityID = campConfigService.getFacilityID();
        if (facilityID == null) {
            throw new IllegalStateException("No store is mapped to van " + vanID + " (m_van.FacilityID)");
        }

        // 1. Validate every line and check total stock per drug before writing anything.
        Map<Integer, Integer> requested = new LinkedHashMap<>();
        Map<Integer, Object[]> items = new HashMap<>();
        for (Map<String, Object> d : drugs) {
            Integer drugID = toInt(d.get("drugID"));
            if (drugID == null) throw new IllegalArgumentException("drugID is required for every drug");
            if (!items.containsKey(drugID)) items.put(drugID, getStoreItem(facilityID, drugID));
            Integer qty = toInt(d.get("qtyPrescribed"));
            if (qty == null || qty <= 0) {
                // No quantity from the app: tablets/capsules = doses per day x days, other forms = 1.
                qty = calculateQuantity(d, items.get(drugID));
                if (qty == null) {
                    throw new IllegalArgumentException("qtyPrescribed is required for drugID " + drugID
                            + " (tablet/capsule quantity needs frequency and duration)");
                }
                d.put("qtyPrescribed", qty); // saved in t_prescribeddrug.QtyPrescribed
            }
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
        long prescriptionID = insertPrescription(beneficiaryRegID, visit, toStr(prescription.get("instruction")),
                createdBy, vanID, parkingPlaceID);

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
        Object[] patient = getPatientDetails(beneficiaryRegID);
        em.createNativeQuery("INSERT INTO db_iemr.t_patientissue (BeneficiaryRegID, BenVisitID, VisitCode, FacilityID, "
                        + "PatientName, Age, Gender, PrescriptionID, Reference, IssueType, IssuedBy, ProviderServiceMapID, "
                        + "Deleted, Processed, CreatedBy, SyncFacilityID, VanID, ParkingPlaceID) VALUES (:ben, :visitID, "
                        + ":visitCode, :facilityID, :name, :age, :gender, :prescriptionID, :ref, 'System', :user, :psm, false, "
                        + "'N', :user, :facilityID, :van, :pp)")
                .setParameter("ben", beneficiaryRegID)
                .setParameter("name", patient[0])
                .setParameter("age", patient[1])
                .setParameter("gender", patient[2])
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
                // Same update as Inventory-API ItemStockEntryRepo.updateStock, plus Processed='N' so the new
                // quantity of an already-synced batch is uploaded again; central updates the existing row.
                em.createNativeQuery("UPDATE db_iemr.t_itemstockentry SET QuantityInHand = QuantityInHand - :qty, "
                                + "Processed = 'N' WHERE VanSerialNo = :batch AND FacilityID = :facilityID")
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

    /**
     * OPD records for getAll from the standard tables, one per visit that was saved with the new payload
     * (complaint with ChiefComplaintID or drug with DrugID). Older visits have only the free-text copy in
     * tb_stoptb_general_opd and are added by the caller. Field names and formats match that table's response:
     * chiefComplaint = JSON list of names, medication/dosage/frequency/duration = comma separated.
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getStandardOpdRecords(Integer providerServiceMapID, Integer villageID) {
        String villageFilter = villageID != null
                ? "AND v.BeneficiaryRegID IN (SELECT f.beneficiary_reg_id FROM db_iemr.i_ben_flow_outreach f "
                        + "WHERE f.providerServiceMapID = :psm AND f.villageID = :villageID) "
                : "";
        Query visitQuery = em.createNativeQuery("SELECT v.BenVisitID, v.BeneficiaryRegID, v.VisitCode, v.CreatedBy, "
                        + "v.CreatedDate, v.LastModDate FROM db_iemr.t_benvisitdetail v "
                        + "WHERE v.ProviderServiceMapID = :psm AND v.Deleted = false "
                        + "AND (EXISTS (SELECT 1 FROM db_iemr.t_benchiefcomplaint c WHERE c.BeneficiaryRegID = v.BeneficiaryRegID "
                        + "  AND c.VisitCode = v.VisitCode AND c.ChiefComplaintID IS NOT NULL AND c.Deleted = false) "
                        + " OR EXISTS (SELECT 1 FROM db_iemr.t_prescribeddrug d WHERE d.BeneficiaryRegID = v.BeneficiaryRegID "
                        + "  AND d.VisitCode = v.VisitCode AND d.DrugID IS NOT NULL AND d.Deleted = false)) "
                        + villageFilter + "ORDER BY v.CreatedDate DESC")
                .setParameter("psm", providerServiceMapID);
        if (villageID != null) visitQuery.setParameter("villageID", villageID);
        List<Object[]> visits = visitQuery.getResultList();

        Map<Long, Map<String, Object>> byVisit = new LinkedHashMap<>();
        for (Object[] v : visits) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", ((Number) v[0]).longValue());
            m.put("beneficiaryRegID", ((Number) v[1]).longValue());
            m.put("visitCode", ((Number) v[2]).longValue());
            m.put("providerServiceMapID", providerServiceMapID);
            m.put("chiefComplaint", new ArrayList<String>());
            m.put("medication", new StringJoiner(", "));
            m.put("dosage", new StringJoiner(", "));
            m.put("frequency", new StringJoiner(", "));
            m.put("duration", new StringJoiner(", "));
            m.put("drugs", new ArrayList<Map<String, Object>>());
            m.put("notes", null);
            m.put("createdBy", v[3]);
            m.put("createdDate", v[4]);
            m.put("updateDate", v[5]);
            m.put("updatedBy", null);
            byVisit.put(((Number) v[2]).longValue(), m);
        }
        if (byVisit.isEmpty()) return new ArrayList<>();

        List<Long> visitCodes = new ArrayList<>(byVisit.keySet());
        for (int i = 0; i < visitCodes.size(); i += 500) {
            List<Long> chunk = visitCodes.subList(i, Math.min(i + 500, visitCodes.size()));

            for (Object o : em.createNativeQuery("SELECT VisitCode, ChiefComplaint, LastModDate FROM db_iemr.t_benchiefcomplaint "
                            + "WHERE VisitCode IN (:codes) AND ChiefComplaintID IS NOT NULL AND Deleted = false ORDER BY ID")
                    .setParameter("codes", chunk).getResultList()) {
                Object[] r = (Object[]) o;
                Map<String, Object> m = byVisit.get(((Number) r[0]).longValue());
                if (r[1] != null) ((List<String>) m.get("chiefComplaint")).add(r[1].toString());
                latest(m, r[2]);
            }

            for (Object o : em.createNativeQuery("SELECT d.VisitCode, d.GenericDrugName, d.Dose, d.Frequency, d.Duration, "
                            + "d.DuartionUnit, d.LastModDate, d.DrugID, i.ItemFormID, d.DrugForm, d.QtyPrescribed, "
                            + "d.SpecialInstruction FROM db_iemr.t_prescribeddrug d "
                            + "LEFT JOIN db_iemr.m_item i ON i.ItemID = d.DrugID "
                            + "WHERE d.VisitCode IN (:codes) AND d.DrugID IS NOT NULL AND d.Deleted = false "
                            + "ORDER BY d.PrescribedDrugID")
                    .setParameter("codes", chunk).getResultList()) {
                Object[] r = (Object[]) o;
                Map<String, Object> m = byVisit.get(((Number) r[0]).longValue());
                ((StringJoiner) m.get("medication")).add(r[1] != null ? r[1].toString() : "");
                ((StringJoiner) m.get("dosage")).add(r[2] != null ? r[2].toString() : "");
                ((StringJoiner) m.get("frequency")).add(r[3] != null ? r[3].toString() : "");
                ((StringJoiner) m.get("duration")).add(r[4] == null ? "" : r[5] == null ? r[4].toString() : r[4] + " " + r[5]);
                latest(m, r[6]);

                // Full prescription per drug, so a saved record can show form, quantity and instructions.
                Map<String, Object> drug = new LinkedHashMap<>();
                drug.put("drugID", ((Number) r[7]).intValue());
                drug.put("drugName", r[1]);
                drug.put("itemFormID", r[8] != null ? ((Number) r[8]).intValue() : null);
                drug.put("drugForm", r[9]);
                drug.put("dose", r[2]);
                drug.put("frequency", r[3]);
                drug.put("duration", r[4] != null ? toInt(r[4]) : null);
                drug.put("durationUnit", r[5]);
                drug.put("qtyPrescribed", r[10] != null ? ((Number) r[10]).intValue() : null);
                drug.put("instructions", r[11]);
                ((List<Map<String, Object>>) m.get("drugs")).add(drug);
            }

            // Latest prescription instruction per visit is the visit's note.
            for (Object o : em.createNativeQuery("SELECT VisitCode, Instruction, LastModDate FROM db_iemr.t_prescription "
                            + "WHERE VisitCode IN (:codes) AND Deleted = false ORDER BY PrescriptionID")
                    .setParameter("codes", chunk).getResultList()) {
                Object[] r = (Object[]) o;
                Map<String, Object> m = byVisit.get(((Number) r[0]).longValue());
                if (r[1] != null && !r[1].toString().isBlank()) m.put("notes", r[1].toString());
                latest(m, r[2]);
            }
        }

        com.google.gson.Gson gson = new com.google.gson.Gson();
        List<Map<String, Object>> records = new ArrayList<>();
        for (Map<String, Object> m : byVisit.values()) {
            List<String> complaints = (List<String>) m.get("chiefComplaint");
            m.put("chiefComplaint", complaints.isEmpty() ? null : gson.toJson(complaints));
            for (String key : new String[] { "medication", "dosage", "frequency", "duration" }) {
                String joined = m.get(key).toString();
                m.put(key, joined.replace(",", "").isBlank() ? null : joined);
            }
            records.add(m);
        }
        return records;
    }

    // updateDate = latest change on the visit, so the app picks up a later save on the same day.
    private void latest(Map<String, Object> m, Object modDate) {
        if (modDate instanceof Date && (m.get("updateDate") == null || ((Date) modDate).after((Date) m.get("updateDate")))) {
            m.put("updateDate", modDate);
        }
    }

    // Name, age (years), gender for the issue row shown on the Inventory dispense screen. Missing details
    // never block dispensing, same as Inventory-API.
    private Object[] getPatientDetails(Long beneficiaryRegID) {
        try {
            RMNCHMBeneficiarymapping mapping = beneficiaryRepo.getById(BigInteger.valueOf(beneficiaryRegID));
            RMNCHMBeneficiarydetail detail = (mapping != null && mapping.getBenDetailsId() != null)
                    ? beneficiaryRepo.getDetailsById(mapping.getBenDetailsId())
                    : null;
            if (detail == null) return new Object[3];
            StringJoiner name = new StringJoiner(" ");
            for (String part : new String[] { detail.getFirstName(), detail.getMiddleName(), detail.getLastName() }) {
                if (part != null && !part.isBlank()) name.add(part.trim());
            }
            Integer age = detail.getDob() != null
                    ? Period.between(detail.getDob().toLocalDateTime().toLocalDate(), LocalDate.now()).getYears()
                    : null;
            String fullName = name.length() > 150 ? name.toString().substring(0, 150) : name.toString();
            return new Object[] { fullName.isEmpty() ? null : fullName, age, detail.getGender() };
        } catch (Exception e) {
            logger.warn("Cannot read patient details for benRegID " + beneficiaryRegID + ": " + e.getMessage());
            return new Object[3];
        }
    }

    // t_prescription row for the visit (Stop TB service line, same as the visit).
    private long insertPrescription(Long beneficiaryRegID, BenVisitDetail visit, String instruction, String createdBy,
            Integer vanID, Integer parkingPlaceID) {
        em.createNativeQuery("INSERT INTO db_iemr.t_prescription (BeneficiaryRegID, BenVisitID, ProviderServiceMapID, VisitCode, "
                        + "Instruction, Deleted, Processed, CreatedBy, VanID, ParkingPlaceID) "
                        + "VALUES (:ben, :visitID, :psm, :visitCode, :instruction, false, 'N', :user, :van, :pp)")
                .setParameter("ben", beneficiaryRegID)
                .setParameter("visitID", visit.getBenVisitId())
                .setParameter("psm", visit.getProviderServiceMapID())
                .setParameter("visitCode", visit.getVisitCode())
                .setParameter("instruction", instruction)
                .setParameter("user", createdBy)
                .setParameter("van", vanID)
                .setParameter("pp", parkingPlaceID)
                .executeUpdate();
        long prescriptionID = lastInsertId();
        stampVanSerialNo("t_prescription", "PrescriptionID", prescriptionID);
        return prescriptionID;
    }

    /**
     * Units to give when the app sends no quantity. Tablets/capsules: doses per day x days (SOS = 1 per day,
     * or 1 when no duration). Other forms (syrup, cream, injection, drops...): 1 bottle/tube/vial. Null when a
     * tablet/capsule quantity cannot be worked out (missing/unknown frequency or missing duration).
     */
    private Integer calculateQuantity(Map<String, Object> drug, Object[] item) {
        Integer formID = item[7] != null ? ((Number) item[7]).intValue() : null;
        if (formID == null || (formID != 1 && formID != 2)) return 1; // 1 = Tablet, 2 = Capsule
        String frequency = Optional.ofNullable(toStr(drug.get("frequency"))).orElse("").toUpperCase();
        if (frequency.contains("SINGLE DOSE") || frequency.contains("STAT")) return 1;

        Integer duration = toInt(drug.get("duration"));
        boolean sos = frequency.contains("SOS");
        if (duration == null || duration <= 0) return sos ? 1 : null;
        String unit = Optional.ofNullable(toStr(drug.get("durationUnit"))).orElse("Day").toLowerCase();
        int days = unit.startsWith("week") ? duration * 7 : unit.startsWith("month") ? duration * 30
                : unit.startsWith("year") ? duration * 365 : duration;

        if (frequency.contains("WEEK")) return (days + 6) / 7; // Once in a Week
        int perDay;
        if (frequency.contains("QID") || frequency.contains("FOUR")) perDay = 4;
        else if (frequency.contains("TID") || frequency.contains("THRICE")) perDay = 3;
        else if (frequency.contains("BD") || frequency.contains("TWICE")) perDay = 2;
        else if (frequency.contains("OD") || frequency.contains("ONCE DAILY") || sos) perDay = 1; // SOS: up to 1 a day
        else return null; // unknown frequency
        return perDay * days;
    }

    // ItemID, ItemName, strength+UOM, form, route, isEDL, IssueType, ItemFormID — only if the drug is mapped to
    // the store on its own service line (same join as v_drugforprescription, so the save accepts what the list shows).
    private Object[] getStoreItem(Integer facilityID, Integer drugID) {
        List<?> rows = em.createNativeQuery("SELECT i.ItemID, i.ItemName, CONCAT_WS(' ', i.Strength, u.UOMName), "
                        + "f.ItemFormName, r.RouteName, i.isEDL, c.IssueType, i.ItemFormID "
                        + "FROM db_iemr.m_item i "
                        + "JOIN db_iemr.m_itemfacilitymapping m ON m.ItemID = i.ItemID AND m.FacilityID = :facilityID "
                        + "AND m.ProviderServiceMapID = i.ProviderServiceMapID AND m.Deleted = false "
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

    // A batch is usable if it has no expiry date (counted by v_drugforprescription too) or lasts the course.
    private static final String USABLE_BATCH = "AND (ExpiryDate IS NULL OR ExpiryDate > DATE_ADD(CURDATE(), INTERVAL :days DAY)) ";

    // Usable batches (VanSerialNo, QuantityInHand), locked for this transaction, in the category's issue order.
    @SuppressWarnings("unchecked")
    private List<Object[]> lockBatches(Integer facilityID, Integer drugID, String issueType, int durationDays) {
        String order;
        if ("Last In First Out".equalsIgnoreCase(issueType)) order = "CreatedDate DESC, ItemStockEntryID DESC";
        else if ("First In First Out".equalsIgnoreCase(issueType)) order = "CreatedDate ASC, ItemStockEntryID ASC";
        else order = "ExpiryDate IS NULL, ExpiryDate ASC, ItemStockEntryID ASC"; // First Expiry First Out (default)
        Query q = em.createNativeQuery("SELECT VanSerialNo, QuantityInHand FROM db_iemr.t_itemstockentry "
                        + "WHERE FacilityID = :facilityID AND ItemID = :drugID AND Deleted = false AND QuantityInHand > 0 "
                        + USABLE_BATCH + "ORDER BY " + order + " FOR UPDATE")
                .setParameter("facilityID", facilityID)
                .setParameter("drugID", drugID)
                .setParameter("days", durationDays);
        return (List<Object[]>) q.getResultList();
    }

    private int availableQuantity(Integer facilityID, Integer drugID, int durationDays) {
        Object v = em.createNativeQuery("SELECT COALESCE(SUM(QuantityInHand), 0) FROM db_iemr.t_itemstockentry "
                        + "WHERE FacilityID = :facilityID AND ItemID = :drugID AND Deleted = false AND QuantityInHand > 0 "
                        + USABLE_BATCH)
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
