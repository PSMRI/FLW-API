package com.iemr.flw.seeder.migration;

import com.iemr.flw.domain.iemr.FormSection;
import com.iemr.flw.domain.iemr.FormVersion;
import com.iemr.flw.domain.iemr.OptionCondition;
import com.iemr.flw.domain.iemr.QuestionOption;
import com.iemr.flw.domain.iemr.QuestionValidation;
import com.iemr.flw.domain.iemr.SectionQuestion;
import com.iemr.flw.dto.iemr.OptionConditionDTO;
import com.iemr.flw.masterEnum.QuestionType;
import com.iemr.flw.masterEnum.ValidationType;
import com.iemr.flw.repo.iemr.OptionConditionRepo;
import com.iemr.flw.repo.iemr.QuestionValidationRepo;
import com.iemr.flw.repo.iemr.SectionQuestionRepo;
import com.iemr.flw.service.DynamicFormReconciliationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Brings forms seeded before #319/#320 in line with the current seeders. Those PRs edited the
 * Occupational Contact Tracing, Community Contact Tracing and TPT Follow Up seeders in place, and a
 * seeder skips a form that already exists, so an environment seeded earlier never received them.
 * The changes go into a new version 3 of each form (via {@link FormVersionCloner}), cloned from
 * version 2; versions 1 and 2 are left as they are, so responses submitted against them keep their
 * meaning, and new responses use version 3. Each form must be at version 2 or 3 when this runs:
 * V001_1 creates version 2 of both contact tracing forms and V011 creates version 2 of TPT Follow Up.
 *
 * OCCUPATION_CONTACT_TRACING (OCT_SEC_1)
 *   - OCT_INSTITUTION_ADDRESS, OCT_OCCUPATION_OTHER: isMandatory = true
 *   - OCT_REMARKS: max length 3000 → 500
 * COMMUNITY_CONTACT_TRACING (CCT_SEC_1)
 *   - CCT_EXPOSURE_SETTING_OTHER, CCT_RELATIONSHIP_OTHER: isMandatory = true
 *   - CCT_REMARKS: max length 3000 → 500
 * TPT_FOLLOW_UP (TFU_SEC_2)
 *   - TFU_OUTCOME_DATE: isMandatory = true
 *   - TFU_ADR: DROPDOWN → DROPDOWN_MULTI, isMandatory = true
 *   - TFU_DISCOMFORT = YES → SHOW_QUESTION TFU_ADR. visibleByDefault is not stored: it is computed
 *     from conditions targeting the question, so this condition alone makes TFU_ADR hidden by default.
 *
 * Every step sets a value only when it differs and finds rows by uuid/optionValue/type, and version 3
 * is reused once created, so a re-run changes nothing.
 */
@Component
@RequiredArgsConstructor
public class V013_AlignSeederOnlyChanges implements FormStructureMigration {

    private static final int TARGET_VERSION = 3;
    private static final int REMARKS_MAX_LENGTH = 500;

    private final SectionQuestionRepo questionRepo;
    private final QuestionValidationRepo validationRepo;
    private final OptionConditionRepo conditionRepo;
    private final FormVersionCloner versionCloner;

    @Override
    public String migrationId() {
        return "V013";
    }

    @Override
    public void apply(DynamicFormReconciliationService svc) {
        alignContactTracing(svc, "OCCUPATION_CONTACT_TRACING", "OCT_SEC_1",
                List.of("OCT_INSTITUTION_ADDRESS", "OCT_OCCUPATION_OTHER"), "OCT_REMARKS");
        alignContactTracing(svc, "COMMUNITY_CONTACT_TRACING", "CCT_SEC_1",
                List.of("CCT_EXPOSURE_SETTING_OTHER", "CCT_RELATIONSHIP_OTHER"), "CCT_REMARKS");
        alignTptFollowUp(svc);
    }

    private void alignContactTracing(DynamicFormReconciliationService svc, String formUuid, String sectionUuid,
                                     List<String> mandatoryQuestionUuids, String remarksQuestionUuid) {
        FormVersion version = versionCloner.ensureVersion(svc, formUuid, TARGET_VERSION);
        FormSection section = requireSection(svc, version, sectionUuid);
        mandatoryQuestionUuids.forEach(uuid -> makeMandatory(requireQuestion(svc, section, uuid)));
        setMaxLength(requireQuestion(svc, section, remarksQuestionUuid), REMARKS_MAX_LENGTH);
    }

    private void alignTptFollowUp(DynamicFormReconciliationService svc) {
        FormVersion version = versionCloner.ensureVersion(svc, "TPT_FOLLOW_UP", TARGET_VERSION);
        FormSection section = requireSection(svc, version, "TFU_SEC_2");

        makeMandatory(requireQuestion(svc, section, "TFU_OUTCOME_DATE"));
        setMaxLength(requireQuestion(svc, section, "TFU_REMARKS"), REMARKS_MAX_LENGTH);

        SectionQuestion adr = requireQuestion(svc, section, "TFU_ADR");
        if (adr.getQuestionType() != QuestionType.DROPDOWN_MULTI || !Boolean.TRUE.equals(adr.getIsMandatory())) {
            adr.setQuestionType(QuestionType.DROPDOWN_MULTI);
            adr.setIsMandatory(true);
            questionRepo.save(adr);
        }

        QuestionOption discomfortYes = svc.option(requireQuestion(svc, section, "TFU_DISCOMFORT"), "YES")
                .orElseThrow(() -> new IllegalStateException("Option TFU_DISCOMFORT=YES not found"));
        OptionConditionDTO showAdr = new OptionConditionDTO();
        showAdr.setActionType("SHOW_QUESTION");
        showAdr.setTargetQuestionUuid("TFU_ADR");
        OptionCondition condition = svc.ensureCondition(discomfortYes, showAdr, version);
        // ensureCondition also matches a soft-removed row; bring it back rather than leave it inactive.
        if (!Boolean.TRUE.equals(condition.getIsActive())) {
            condition.setIsActive(true);
            conditionRepo.save(condition);
        }
    }

    private void makeMandatory(SectionQuestion question) {
        if (!Boolean.TRUE.equals(question.getIsMandatory())) {
            question.setIsMandatory(true);
            questionRepo.save(question);
        }
    }

    // Sets the question's maxLength and its MAX_LENGTH validation. The existing validation row is updated
    // in place (not replaced), so there is exactly one active MAX_LENGTH rule afterwards.
    private void setMaxLength(SectionQuestion question, int maxLength) {
        if (!Integer.valueOf(maxLength).equals(question.getMaxLength())) {
            question.setMaxLength(maxLength);
            questionRepo.save(question);
        }
        String param = String.valueOf(maxLength);
        String message = "Must be " + maxLength + " characters or fewer";
        List<QuestionValidation> maxLengthRules = validationRepo.findBySectionQuestion_QuestionId(question.getQuestionId())
                .stream()
                .filter(v -> v.getValidationType() == ValidationType.MAX_LENGTH)
                .filter(v -> Boolean.TRUE.equals(v.getIsActive()))
                .toList();
        boolean kept = false;
        for (QuestionValidation rule : maxLengthRules) {
            if (!kept) {
                kept = true;
                if (!param.equals(rule.getValidationParam()) || !message.equals(rule.getErrorMessage())) {
                    rule.setValidationParam(param);
                    rule.setErrorMessage(message);
                    validationRepo.save(rule);
                }
            } else {
                rule.setIsActive(false);
                validationRepo.save(rule);
            }
        }
    }

    private FormSection requireSection(DynamicFormReconciliationService svc, FormVersion version, String uuid) {
        return svc.section(version, uuid)
                .orElseThrow(() -> new IllegalStateException("Section " + uuid + " not found"));
    }

    private SectionQuestion requireQuestion(DynamicFormReconciliationService svc, FormSection section, String uuid) {
        return svc.question(section, uuid)
                .orElseThrow(() -> new IllegalStateException("Question " + uuid + " not found"));
    }
}
