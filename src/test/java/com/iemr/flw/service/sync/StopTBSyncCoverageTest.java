package com.iemr.flw.service.sync;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Every table FLW-API maps must be either synced to STOP-TB tablets through the change log
 * (listed in a {@link StopTBSyncType}'s sourceTables, which the hub checks for triggers) or listed
 * below as deliberately not synced. A new entity fails this test until someone decides which, so a
 * STOP-TB table can't be added without its change-log triggers.
 */
class StopTBSyncCoverageTest {

    /** STOP-TB-adjacent tables that the app does not pull, with the reason. */
    static final Map<String, String> NOT_SYNCED_STOPTB = Map.ofEntries(
            Map.entry("db_iemr.t_phy_generalexam", "copy of the general examination for the standard tables; the app pulls tb_stoptb_general_examination"),
            Map.entry("db_iemr.t_benreferdetails", "copy written on save for the standard tables; not read by any STOP-TB pull"),
            Map.entry("db_iemr.tb_stoptb_registration", "not read by any service"),
            Map.entry("db_iemr.tb_diagnostic_document", "the app does not pull documents"),
            Map.entry("db_iemr.tb_diagnostic_provider_token", "vendor credentials, server only"),
            Map.entry("db_identity.m_beneficiaryregidmapping", "BenRegId/BeneficiaryID pool, fixed once assigned"),
            Map.entry("db_iemr.t_dynamic_form", "form definitions: master data, pulled separately"),
            Map.entry("db_iemr.t_form_version", "form definitions: master data, pulled separately"),
            Map.entry("db_iemr.t_form_section", "form definitions: master data, pulled separately"),
            Map.entry("db_iemr.t_section_question", "form definitions: master data, pulled separately"),
            Map.entry("db_iemr.t_question_option", "form definitions: master data, pulled separately"),
            Map.entry("db_iemr.t_question_validation", "form definitions: master data, pulled separately"),
            Map.entry("db_iemr.t_option_condition", "form definitions: master data, pulled separately"),
            Map.entry("db_iemr.t_dynamic_form_migration_log", "server-side bookkeeping"),
            Map.entry("db_iemr.m_user", "user master, read at login"),
            Map.entry("db_iemr.v_userservicerolemapping", "view of user roles, read at login"),
            Map.entry("db_iemr.general_opd_entry", "not part of the STOP-TB pull"));

    /** Tables of other FLW programmes (ASHA app), not used by STOP-TB. */
    static final Set<String> OTHER_PROGRAMMES = Set.of(
            "db_iemr.adolescent_health", "db_iemr.ahd_form", "db_iemr.anc_counselling_care", "db_iemr.asha_profile",
            "db_iemr.asha_supervisor_mapping", "db_iemr.campaign_filariasis_mda", "db_iemr.campaign_ors",
            "db_iemr.campaign_pulse_polio", "db_iemr.cdtf_visit_details", "db_iemr.deworming_form",
            "db_iemr.eligible_couple_tracking", "db_iemr.high_risk_assess", "db_iemr.i_mobilization_mosquito_net",
            "db_iemr.incentive_activity_record", "db_iemr.incentive_pending_activity", "db_iemr.irs_round",
            "db_iemr.leprosy_follow_up", "db_iemr.m_immunizationservicevaccination", "db_iemr.m_incentive_activity",
            "db_iemr.m_incentive_activity_lang_mapping", "db_iemr.m_otp_beneficiary", "db_iemr.m_smstemplate",
            "db_iemr.maa_meeting", "db_iemr.malaria_follow_up", "db_iemr.non_pregnant_high_risk_assess",
            "db_iemr.non_pregnant_high_risk_track", "db_iemr.phc_review_meeting", "db_iemr.pregnant_high_risk_assess",
            "db_iemr.pregnant_high_risk_track", "db_iemr.sammelan_attachment", "db_iemr.sammelan_record", "db_iemr.screening_aesje",
            "db_iemr.screening_filaria", "db_iemr.screening_kala_azar", "db_iemr.screening_leprosy",
            "db_iemr.screening_malaria", "db_iemr.t_anc_visit", "db_iemr.t_anccare", "db_iemr.t_cbacdetails",
            "db_iemr.t_cdr", "db_iemr.t_child_register", "db_iemr.t_childvaccinedetail1", "db_iemr.t_delivery_outcome",
            "db_iemr.t_eligible_couple_register", "db_iemr.t_eye_checkup", "db_iemr.t_hbnc_part1", "db_iemr.t_hbnc_part2",
            "db_iemr.t_hbnc_visit", "db_iemr.t_hbnc_visit_card", "db_iemr.t_hbyc", "db_iemr.t_hbyc_child_visits",
            "db_iemr.t_ifa_distribution", "db_iemr.t_ifa_distribution_data", "db_iemr.t_infant_register",
            "db_iemr.t_mda_distribution_data", "db_iemr.t_mdsr", "db_iemr.t_micro_birth_plan", "db_iemr.t_ors_distribution",
            "db_iemr.t_pmsma", "db_iemr.t_pnc_visit", "db_iemr.t_pnccare", "db_iemr.t_pregnant_woman_register",
            "db_iemr.t_sam_visit", "db_iemr.uwin_session_record", "db_iemr.vhnc_form", "db_iemr.vhnd_form",
            "db_identity.i_cbac_additional_details", "db_identity.i_cbacdetails");

    @Test
    void everyMappedTableIsSyncedOrDeliberatelyNot() throws Exception {
        Set<String> synced = new HashSet<>();
        for (StopTBSyncType t : StopTBSyncType.values()) synced.addAll(t.sourceTables());

        List<String> unclassified = new ArrayList<>();
        for (String table : mappedTables()) {
            if (!synced.contains(table) && !NOT_SYNCED_STOPTB.containsKey(table) && !OTHER_PROGRAMMES.contains(table)) {
                unclassified.add(table);
            }
        }
        Collections.sort(unclassified);
        assertTrue(unclassified.isEmpty(), "Tables neither synced to STOP-TB tablets nor listed as not synced: "
                + unclassified + ". If the STOP-TB app reads it, add it to a StopTBSyncType's sourceTables and add "
                + "change-log triggers in a new AMRIT-DB migration; otherwise list it here with a reason.");
    }

    @Test
    void syncedTablesAreNotAlsoListedAsNotSynced() {
        for (StopTBSyncType t : StopTBSyncType.values()) {
            for (String table : t.sourceTables()) {
                assertTrue(!NOT_SYNCED_STOPTB.containsKey(table) && !OTHER_PROGRAMMES.contains(table), table);
            }
        }
    }

    /** schema.table for every @Entity under com.iemr.flw.domain, lower-case. */
    static Set<String> mappedTables() throws ClassNotFoundException {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Entity.class));
        Set<String> tables = new TreeSet<>();
        for (BeanDefinition bd : scanner.findCandidateComponents("com.iemr.flw.domain")) {
            Class<?> c = Class.forName(bd.getBeanClassName());
            Table t = c.getAnnotation(Table.class);
            String name = t != null && !t.name().isEmpty() ? t.name() : c.getSimpleName();
            String schema = t != null && !t.schema().isEmpty() ? t.schema()
                    : c.getPackageName().endsWith(".identity") ? "db_identity" : "db_iemr";
            tables.add((schema + "." + name).toLowerCase(Locale.ROOT));
        }
        return tables;
    }
}
