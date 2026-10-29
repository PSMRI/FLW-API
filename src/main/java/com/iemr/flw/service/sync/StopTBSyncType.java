package com.iemr.flw.service.sync;

import java.util.List;

/**
 * The record types the STOP-TB changes feed (/stoptb/sync/changes) serves, in snapshot order
 * (beneficiaries first, so a tablet has the person before their TB records).
 *
 * For each type:
 *  - {@code logEntity}: the entity name the hub's change-log triggers write (AMRIT-DB
 *    db/migration/dbiemr_hub and dbidentity_hub);
 *  - {@code snapshotKeySql}: the keys in scope, for the first full download, with the same filters
 *    the type's getAll endpoint applies. Parameters: :psm, :villages, :after, :limit;
 *  - {@code sourceTables}: every table the type's record is built from. Each must have the change-log
 *    triggers, or edits to it would never reach tablets. The feed checks this on the hub
 *    ({@code StopTBSyncServiceImpl#isSupported}) and a unit test checks every mapped table is either
 *    here or deliberately not synced.
 *
 * @author Piramal Swasthya
 */
public enum StopTBSyncType {

    BENEFICIARY("beneficiary", "beneficiary",
            "SELECT DISTINCT f.beneficiary_reg_id FROM db_iemr.i_ben_flow_outreach f "
                    + "WHERE f.providerServiceMapID = :psm AND f.villageID IN (:villages) AND f.deleted = 0 "
                    + "AND f.beneficiary_reg_id > :after ORDER BY f.beneficiary_reg_id LIMIT :limit",
            List.of("db_identity.i_beneficiarymapping", "db_identity.i_beneficiarydetails",
                    "db_identity.i_beneficiarydetails_rmnch", "db_identity.i_bornbirthdeatils",
                    "db_identity.i_beneficiaryaddress", "db_identity.i_beneficiarycontacts",
                    "db_identity.i_beneficiaryimage", "db_identity.i_beneficiaryaccount",
                    "db_identity.i_householddetails", "db_iemr.t_phy_anthropometry", "db_iemr.t_phy_vitals",
                    "db_iemr.i_ben_flow_outreach")),

    TB_SCREENING("tbScreening", "tb_screening",
            "SELECT s.id FROM db_iemr.tb_screening s WHERE (s.deleted = 0 OR s.deleted IS NULL) "
                    + "AND s.ben_reg_id IN (" + Scope.REG + ") AND s.id > :after ORDER BY s.id LIMIT :limit",
            List.of("db_iemr.tb_screening")),

    GENERAL_EXAMINATION("generalExamination", "general_examination",
            "SELECT e.id FROM db_iemr.tb_stoptb_general_examination e WHERE e.deleted = 0 "
                    + "AND e.beneficiary_reg_id IN (" + Scope.REG + ") AND e.id > :after ORDER BY e.id LIMIT :limit",
            List.of("db_iemr.tb_stoptb_general_examination")),

    /** OPD visits saved to the standard tables, keyed by BenVisitID. */
    GENERAL_OPD("generalOpd", "opd_visit",
            "SELECT v.BenVisitID FROM db_iemr.t_benvisitdetail v WHERE v.Deleted = 0 AND v.ProviderServiceMapID = :psm "
                    + "AND v.BeneficiaryRegID IN (" + Scope.REG + ") AND v.BenVisitID > :after ORDER BY v.BenVisitID LIMIT :limit",
            // t_prescription holds the visit's note (latest prescription instruction)
            List.of("db_iemr.t_benvisitdetail", "db_iemr.t_benchiefcomplaint", "db_iemr.t_prescribeddrug",
                    "db_iemr.t_prescription")),

    /** Older free-text OPD rows; same record shape as GENERAL_OPD. */
    GENERAL_OPD_LEGACY("generalOpdLegacy", "general_opd",
            "SELECT o.id FROM db_iemr.tb_stoptb_general_opd o WHERE o.deleted = 0 "
                    + "AND o.ben_reg_id IN (" + Scope.REG + ") AND o.id > :after ORDER BY o.id LIMIT :limit",
            List.of("db_iemr.tb_stoptb_general_opd")),

    DIAGNOSTICS("diagnostics", "diagnostics",
            "SELECT d.id FROM db_iemr.tb_stoptb_diagnostics d WHERE d.deleted = 0 "
                    + "AND d.ben_reg_id IN (" + Scope.REG + ") AND d.id > :after ORDER BY d.id LIMIT :limit",
            List.of("db_iemr.tb_stoptb_diagnostics")),

    TB_SUSPECTED("tbSuspected", "tb_suspected",
            "SELECT t.id FROM db_iemr.tb_suspected t WHERE t.ben_id IN (" + Scope.BEN_ID + ") "
                    + "AND t.id > :after ORDER BY t.id LIMIT :limit",
            List.of("db_iemr.tb_suspected")),

    TB_CONFIRMED("tbConfirmed", "tb_confirmed",
            "SELECT c.id FROM db_iemr.tb_confirmed_cases c WHERE c.ben_id IN (" + Scope.BEN_ID + ") "
                    + "AND c.id > :after ORDER BY c.id LIMIT :limit",
            List.of("db_iemr.tb_confirmed_cases")),

    FORM_RESPONSE("formResponse", "form_response",
            "SELECT r.responseId FROM db_iemr.t_form_response r WHERE r.beneficiaryId IN (" + Scope.BEN_ID_NOT_DELETED + ") "
                    + "AND r.responseId > :after ORDER BY r.responseId LIMIT :limit",
            List.of("db_iemr.t_form_response", "db_iemr.t_section_response", "db_iemr.t_question_response")),

    DIAGNOSTIC_ORDER("diagnosticOrder", "diagnostic_order",
            "SELECT o.id FROM db_iemr.tb_diagnostic_order o WHERE o.deleted = 0 "
                    + "AND o.beneficiary_id IN (" + Scope.BEN_ID_NOT_DELETED + ") AND o.id > :after ORDER BY o.id LIMIT :limit",
            List.of("db_iemr.tb_diagnostic_order", "db_iemr.tb_diagnostic_result"));

    /** Beneficiaries in the requested villages, the same way today's getAll endpoints scope. */
    static final class Scope {
        static final String REG = "SELECT f.beneficiary_reg_id FROM db_iemr.i_ben_flow_outreach f "
                + "WHERE f.providerServiceMapID = :psm AND f.villageID IN (:villages)";
        static final String BEN_ID = "SELECT f.beneficiary_id FROM db_iemr.i_ben_flow_outreach f "
                + "WHERE f.providerServiceMapID = :psm AND f.villageID IN (:villages)";
        static final String BEN_ID_NOT_DELETED = BEN_ID + " AND f.deleted = 0";

        private Scope() {
        }
    }

    private final String apiName;
    private final String logEntity;
    private final String snapshotKeySql;
    private final List<String> sourceTables;

    StopTBSyncType(String apiName, String logEntity, String snapshotKeySql, List<String> sourceTables) {
        this.apiName = apiName;
        this.logEntity = logEntity;
        this.snapshotKeySql = snapshotKeySql;
        this.sourceTables = sourceTables;
    }

    /** The "type" field of an item in the feed. */
    public String apiName() {
        return apiName;
    }

    public String logEntity() {
        return logEntity;
    }

    public String snapshotKeySql() {
        return snapshotKeySql;
    }

    public List<String> sourceTables() {
        return sourceTables;
    }

    public static StopTBSyncType fromLogEntity(String entity) {
        for (StopTBSyncType t : values()) {
            if (t.logEntity.equals(entity)) return t;
        }
        return null;
    }
}
