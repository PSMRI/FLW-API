package com.iemr.flw.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigInteger;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * STOP-855: findUnchangedOpd must skip only a true re-send (or an empty OPD) and let every real edit through.
 * The saved OPD below is what the laptop DB holds for the beneficiary's latest OPD visit.
 */
class StopTBPrescriptionServiceRepeatOpdTest {

    private static final long BEN = 9001L;
    private static final long VISIT_CODE = 1004060000123L;
    private static final long PRESCRIPTION_ID = 77L;
    private static final int PSM = 1732;

    private StopTBPrescriptionService service;
    private EntityManager em;

    // What the DB holds (rows as native queries return them).
    private List<Object> latestVisit;
    private List<Object> prescriptionRows;
    private List<Object> complaintRows;
    private List<Object> drugRows;

    @BeforeEach
    void setUp() {
        service = new StopTBPrescriptionService();
        em = mock(EntityManager.class);
        ReflectionTestUtils.setField(service, "em", em);
        ReflectionTestUtils.setField(service, "dispenseEnabled", false);

        latestVisit = new ArrayList<>(List.of(BigInteger.valueOf(VISIT_CODE)));
        prescriptionRows = new ArrayList<>();
        prescriptionRows.add(new Object[] { BigInteger.valueOf(PRESCRIPTION_ID), "Take rest" });
        complaintRows = new ArrayList<>();
        complaintRows.add(new Object[] { 5, 3, "Days", null });
        drugRows = new ArrayList<>();
        drugRows.add(new Object[] { 10, "Twice a day", "5", "Day", "After food", "One Tab", 10, "Paracetamol", "Tablet" });

        when(em.createNativeQuery(anyString())).thenAnswer(inv -> {
            String sql = inv.getArgument(0);
            Query q = mock(Query.class, Answers.RETURNS_SELF);
            List<Object> rows;
            if (sql.contains("FROM db_iemr.t_benvisitdetail")) rows = latestVisit;
            else if (sql.contains("SELECT PrescriptionID, Instruction")) rows = prescriptionRows;
            else if (sql.contains("FROM db_iemr.t_benchiefcomplaint")) rows = complaintRows;
            else if (sql.contains("FROM db_iemr.t_prescribeddrug")) rows = drugRows;
            else throw new AssertionError("unexpected query: " + sql);
            doReturn(rows).when(q).getResultList();
            return q;
        });
    }

    // ---- payload builders (same keys as the app's GeneralOpdSaveRequest)

    private static Map<String, Object> complaint(Object id, Object duration, Object unit, Object description) {
        Map<String, Object> c = new HashMap<>();
        c.put("chiefComplaintID", id);
        c.put("chiefComplaint", "Cough");
        c.put("duration", duration);
        c.put("unitOfDuration", unit);
        c.put("description", description);
        return c;
    }

    private static Map<String, Object> drug(Object id, Object freq, Object duration, Object unit, Object instr,
            Object dose, Object qty) {
        Map<String, Object> d = new HashMap<>();
        d.put("drugID", id);
        d.put("drugName", "Paracetamol");
        d.put("drugForm", "Tablet");
        d.put("frequency", freq);
        d.put("duration", duration);
        d.put("durationUnit", unit);
        d.put("instructions", instr);
        d.put("dose", dose);
        d.put("qtyPrescribed", qty);
        return d;
    }

    private static Map<String, Object> prescription(String instruction, List<Map<String, Object>> drugs) {
        Map<String, Object> p = new HashMap<>();
        p.put("instruction", instruction);
        p.put("drugs", drugs);
        return p;
    }

    private static List<Map<String, Object>> sameComplaints() {
        return List.of(complaint(5, 3, "Days", null));
    }

    private static Map<String, Object> samePrescription() {
        return prescription("Take rest", List.of(drug(10, "Twice a day", 5, "Day", "After food", "One Tab", 10)));
    }

    private Map<String, Object> check(List<Map<String, Object>> complaints, Map<String, Object> prescription) {
        return service.findUnchangedOpd(BEN, PSM, complaints, prescription).orElse(null);
    }

    private static void assertSkipped(Map<String, Object> r, Long visitCode) {
        assertNotNull(r, "expected re-send to be skipped");
        assertEquals(Boolean.TRUE, r.get("duplicate"));
        assertEquals(visitCode, r.get("visitCode"));
    }

    // ---- re-sends: nothing written

    @Test
    void identicalResend_isSkipped() {
        Map<String, Object> r = check(sameComplaints(), samePrescription());
        assertSkipped(r, VISIT_CODE);
        assertEquals(PRESCRIPTION_ID, r.get("prescriptionID"));
    }

    @Test
    void resendWithOtherCaseSpacesOrderAndNumbersAsText_isSkipped() {
        Map<String, Object> p = prescription(" take REST ",
                List.of(drug("10", "TWICE A DAY ", "5", "day", "after food", "one tab", "10")));
        assertSkipped(check(List.of(complaint("5", "3", "days", "")), p), VISIT_CODE);
    }

    @Test
    void resendWithDrugsInDifferentOrder_isSkipped() {
        drugRows.add(new Object[] { 11, "Once a day", "3", "Day", null, "One Tab", 3, "Paracetamol", "Tablet" });
        Map<String, Object> p = prescription("Take rest", List.of(
                drug(11, "Once a day", 3, "Day", null, "One Tab", 3),
                drug(10, "Twice a day", 5, "Day", "After food", "One Tab", 10)));
        assertSkipped(check(sameComplaints(), p), VISIT_CODE);
    }

    @Test
    void emptyOpd_withSavedOpd_isSkipped() {
        assertSkipped(check(List.of(), prescription("", List.of())), VISIT_CODE);
        assertSkipped(check(null, null), VISIT_CODE);
    }

    @Test
    void emptyOpd_withNothingSaved_isSkipped_noEmptyVisit() {
        latestVisit.clear();
        assertSkipped(check(List.of(), null), null);
    }

    @Test
    void emptyComplaintsButSamePrescription_isSkipped() {
        assertSkipped(check(List.of(), samePrescription()), VISIT_CODE);
    }

    @Test
    void sameComplaintsAndNoPrescription_isSkipped() {
        assertSkipped(check(sameComplaints(), null), VISIT_CODE);
    }

    @Test
    void quantityNotSent_comparedAsTheSaveWouldCalculateIt() {
        // Syrup (form 3): the save stores 1 bottle when no quantity is sent.
        drugRows.set(0, new Object[] { 10, "Twice a day", "5", "Day", "After food", null, 1, "Paracetamol", "Syrup" });
        Map<String, Object> d = drug(10, "Twice a day", 5, "Day", "After food", null, null);
        d.put("itemFormID", 3);
        d.put("drugForm", "Syrup");
        assertSkipped(check(sameComplaints(), prescription("Take rest", List.of(d))), VISIT_CODE);
    }

    // ---- real edits: must be saved (null)

    @Test
    void firstOpd_isSaved() {
        latestVisit.clear();
        assertNull(check(sameComplaints(), samePrescription()));
    }

    @Test
    void drugChanged_isSaved() {
        assertNull(check(sameComplaints(),
                prescription("Take rest", List.of(drug(12, "Twice a day", 5, "Day", "After food", "One Tab", 10)))));
    }

    @Test
    void drugAdded_isSaved() {
        assertNull(check(sameComplaints(), prescription("Take rest", List.of(
                drug(10, "Twice a day", 5, "Day", "After food", "One Tab", 10),
                drug(11, "Once a day", 3, "Day", null, "One Tab", 3)))));
    }

    @Test
    void drugRemoved_isSaved() {
        drugRows.add(new Object[] { 11, "Once a day", "3", "Day", null, "One Tab", 3, "Paracetamol", "Tablet" });
        assertNull(check(sameComplaints(), samePrescription()));
    }

    @Test
    void frequencyChanged_isSaved() {
        assertNull(check(sameComplaints(),
                prescription("Take rest", List.of(drug(10, "Once a day", 5, "Day", "After food", "One Tab", 10)))));
    }

    @Test
    void durationChanged_isSaved() {
        assertNull(check(sameComplaints(),
                prescription("Take rest", List.of(drug(10, "Twice a day", 7, "Day", "After food", "One Tab", 10)))));
    }

    @Test
    void doseChanged_isSaved() {
        assertNull(check(sameComplaints(),
                prescription("Take rest", List.of(drug(10, "Twice a day", 5, "Day", "After food", "Two Tabs", 10)))));
    }

    @Test
    void quantityChanged_isSaved() {
        assertNull(check(sameComplaints(),
                prescription("Take rest", List.of(drug(10, "Twice a day", 5, "Day", "After food", "One Tab", 20)))));
    }

    @Test
    void drugInstructionChanged_isSaved() {
        assertNull(check(sameComplaints(),
                prescription("Take rest", List.of(drug(10, "Twice a day", 5, "Day", "Before food", "One Tab", 10)))));
    }

    @Test
    void prescriptionInstructionChanged_isSaved() {
        assertNull(check(sameComplaints(),
                prescription("Drink water", List.of(drug(10, "Twice a day", 5, "Day", "After food", "One Tab", 10)))));
    }

    @Test
    void complaintAdded_isSaved() {
        assertNull(check(List.of(complaint(5, 3, "Days", null), complaint(6, 2, "Days", null)), samePrescription()));
    }

    @Test
    void complaintChanged_isSaved() {
        assertNull(check(List.of(complaint(6, 3, "Days", null)), samePrescription()));
    }

    @Test
    void complaintDurationChanged_isSaved() {
        assertNull(check(List.of(complaint(5, 10, "Days", null)), samePrescription()));
    }

    @Test
    void complaintUnitChanged_isSaved() {
        assertNull(check(List.of(complaint(5, 3, "Weeks", null)), samePrescription()));
    }

    @Test
    void complaintDescriptionChanged_isSaved() {
        assertNull(check(List.of(complaint(5, 3, "Days", "with blood")), samePrescription()));
    }

    @Test
    void instructionOnlyAddedWhereNoPrescriptionSaved_isSaved() {
        prescriptionRows.clear();
        drugRows.clear();
        assertNull(check(sameComplaints(), prescription("Take rest", List.of())));
    }

    @Test
    void dispensingOn_checkIsOff() {
        ReflectionTestUtils.setField(service, "dispenseEnabled", true);
        assertNull(check(sameComplaints(), samePrescription()));
        verifyNoInteractions(em);
    }

    // ---- save guards: empty input never wipes saved data

    @Test
    void saveChiefComplaints_emptyList_writesNothing() {
        service.saveChiefComplaints(List.of(), BEN, null, "nurse", 406, 1);
        service.saveChiefComplaints(null, BEN, null, "nurse", 406, 1);
        verifyNoInteractions(em);
    }

    @Test
    void prescribe_noDrugsNoInstruction_writesNothing() {
        Map<String, Object> r = service.prescribeAndDispense(prescription("  ", List.of()), BEN, null, null, "nurse",
                406, 1);
        assertNotNull(r);
        assertFalse(r.containsKey("prescriptionID"));
        verifyNoInteractions(em);
    }

    // ---- CodeRabbit review (PR #415)

    @Test
    void drugNameChanged_isSaved() {
        Map<String, Object> d = drug(10, "Twice a day", 5, "Day", "After food", "One Tab", 10);
        d.put("drugName", "Paracetamol 650");
        assertNull(check(sameComplaints(), prescription("Take rest", List.of(d))));
    }

    @Test
    void drugFormChanged_isSaved() {
        Map<String, Object> d = drug(10, "Twice a day", 5, "Day", "After food", "One Tab", 10);
        d.put("drugForm", "Capsule");
        assertNull(check(sameComplaints(), prescription("Take rest", List.of(d))));
    }

    @Test
    void fractionalDurationChanged_isSaved_butFiveEqualsFivePointZero() {
        assertNull(check(sameComplaints(),
                prescription("Take rest", List.of(drug(10, "Twice a day", "5.5", "Day", "After food", "One Tab", 10)))));
        assertSkipped(check(sameComplaints(),
                prescription("Take rest", List.of(drug(10, "Twice a day", "5.0", "Day", "After food", "One Tab", 10)))),
                VISIT_CODE);
    }

    @Test
    void latestVisitLookup_isScopedToTheServiceLine() {
        List<String> sqls = new ArrayList<>();
        List<Object> psmParams = new ArrayList<>();
        doAnswer(inv -> {
            String sql = inv.getArgument(0);
            sqls.add(sql);
            Query q = mock(Query.class, Answers.RETURNS_SELF);
            doAnswer(a -> { psmParams.add(a.getArgument(1)); return q; })
                    .when(q).setParameter(org.mockito.ArgumentMatchers.eq("psm"), org.mockito.ArgumentMatchers.any());
            doReturn(new ArrayList<>()).when(q).getResultList();
            return q;
        }).when(em).createNativeQuery(anyString());
        assertNull(check(sameComplaints(), samePrescription()));
        assertTrue(sqls.get(0).contains("v.ProviderServiceMapID = :psm"));
        assertEquals(List.of(PSM), psmParams);
    }

    @Test
    void dispensingOn_emptyComplaintList_stillReplacesAsBefore() {
        ReflectionTestUtils.setField(service, "dispenseEnabled", true);
        Query q = mock(Query.class, Answers.RETURNS_SELF);
        doReturn(q).when(em).createNativeQuery(anyString());
        com.iemr.flw.domain.iemr.BenVisitDetail visit = new com.iemr.flw.domain.iemr.BenVisitDetail();
        visit.setVisitCode(VISIT_CODE);
        service.saveChiefComplaints(List.of(), BEN, visit, "nurse", 406, 1);
        verify(q).executeUpdate();
    }
}
