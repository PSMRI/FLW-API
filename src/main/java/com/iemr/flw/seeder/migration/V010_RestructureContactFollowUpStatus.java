/*
 * AMRIT – Accessible Medical Records via Integrated Technology
 * Integrated EHR (Electronic Health Records) Solution
 *
 * Copyright (C) "Piramal Swasthya Management and Research Institute"
 *
 * This file is part of AMRIT.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses/.
 */
package com.iemr.flw.seeder.migration;

import com.iemr.flw.domain.iemr.FormSection;
import com.iemr.flw.domain.iemr.FormVersion;
import com.iemr.flw.domain.iemr.QuestionOption;
import com.iemr.flw.domain.iemr.SectionQuestion;
import com.iemr.flw.dto.iemr.OptionConditionDTO;
import com.iemr.flw.dto.iemr.QuestionOptionDTO;
import com.iemr.flw.dto.iemr.QuestionValidationDTO;
import com.iemr.flw.dto.iemr.SectionQuestionDTO;
import com.iemr.flw.masterEnum.QuestionType;
import com.iemr.flw.masterEnum.ValidationType;
import com.iemr.flw.repo.iemr.FormSectionRepo;
import com.iemr.flw.repo.iemr.QuestionOptionRepo;
import com.iemr.flw.repo.iemr.QuestionValidationRepo;
import com.iemr.flw.repo.iemr.SectionQuestionRepo;
import com.iemr.flw.service.DynamicFormReconciliationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * Restructures the Contact Follow Up form's "Screening &amp; Referral" section (CFU_SEC_1) per the
 * revised Contact Follow-Up spec (PRD v203). The changes go into a new version 2 (via
 * {@link FormVersionCloner}); version 1 keeps the seeded structure, so responses submitted against it
 * keep their meaning.
 *
 * Final version-2 order (FINAL_QUESTION_ORDER):
 *
 *  1. Date (CFU_FOLLOW_UP_DATE, mandatory date, not in the future) — AUTO_FILL "TODAY".
 *  2. "Chest X-ray Result" (CFU_CAMP_XRAY_RESULT, AUTO_FILL, optional) — AUTO_FILL "CAMP_XRAY_RESULT":
 *     the client fills it from the X-ray done in the camp, if any.
 *  3. "NAAT Result" (CFU_CAMP_NAAT_RESULT, AUTO_FILL, optional) — AUTO_FILL "CAMP_NAAT_RESULT": the client
 *     fills it from the NAAT done in the camp, if any.
 *  4. "Is beneficiary Referred to PHI?" (CFU_REFERRED_TO_PHI, mandatory Yes/No).
 *  5. "Has beneficiary visited the PHI?" (CFU_VISITED_PHI, Yes/No) — shown when #4 = Yes; No → submit.
 *  6. "Is Chest X-ray conducted?" (CFU_XRAY_CONDUCTED, Yes/No) — shown when #5 = Yes.
 *  7. "Chest X-ray Result" (CFU_XRAY_RESULT: Normal, TB Presumptive, Abnormal but not TB Presumptive)
 *     — shown when #6 = Yes.
 *  8. "Is NAAT conducted?" (CFU_NAAT_CONDUCTED, Yes/No) — shown when #5 = Yes.
 *  9. "NAAT Result" (CFU_NAAT_RESULT: MTB Detected, MTB Not Detected, Indeterminate) — shown when #8 = Yes.
 * 10. "Is Cy-TB Test conducted?" (CFU_CYTB_CONDUCTED, Yes/No) — shown when #5 = Yes.
 * 11. "Cy-TB Test Result" (CFU_CYTB_RESULT: Positive, Negative) — shown when #10 = Yes.
 * 12. "Treatment recommended by MO/ TU/ PHI" (CFU_CLINICAL_SCREENING_STATUS, radio: Recommend for TPT,
 *     Not recommended TPT, Recommend for ATT) — shown when #5 = Yes. Its option rows are the seeded
 *     ones renamed in place (TPT_ELIGIBLE, NO_TREATMENT, FULL_TREATMENT); optionUuid is deliberately left
 *     unchanged so clients keep mapping existing answers to the same option.
 * 13. Remarks (CFU_REMARKS, optional free text, max 200 chars, all characters allowed).
 *
 * "Advised to visit health facility" (CFU_ADVISED_VISIT_FACILITY) and "Have you visited the health
 * facility" (CFU_VISITED_FACILITY) are soft-removed — never hard-deleted, historical responses may
 * reference them. Every question above is flagged requiresBackFill, so the client makes sure each has a
 * value on beneficiaries whose earlier responses were captured on version 1 (for the camp results, by
 * auto-filling them). #4, #5, #6, #8 and #10 are flagged editableAfterSubmit,
 * and the section itself is set isEditable — the submit path skips every answer in a non-editable section,
 * so the follow-up can be continued after submit.
 *
 * Deliberately not implemented: routing a record on "Recommend for TPT" (TPT Follow up) / "Recommend for
 * ATT" (Confirmed TB Cases) — no option opens another form; those flows will be built separately. Handled
 * statically by the client: hiding #2 when #6 = Yes and #3 when #8 = Yes, and hiding #6 / #8 when the camp
 * result was auto-filled (the engine has no hide action); enabling "Recommend for ATT" only when the X-ray
 * result is not Normal or NAAT = MTB Detected (the engine has no option-level conditions); and multiline
 * rendering of Remarks (there is no multiline question type).
 *
 * Re-runnable: questions/options/conditions/validations go through ensureX or uuid/optionValue lookups,
 * labels, option order, Remarks' limit and the final display order are set to the same values each run,
 * and version 2 is reused once created. Re-running it also brings a database where an earlier revision of
 * this migration already ran up to this one.
 */
@Component
@RequiredArgsConstructor
public class V010_RestructureContactFollowUpStatus implements FormStructureMigration {

    private static final String FORM_UUID = "CONTACT_FOLLOW_UP";
    private static final String SECTION_UUID = "CFU_SEC_1";
    private static final String DATE_UUID = "CFU_FOLLOW_UP_DATE";
    private static final String CAMP_XRAY_RESULT_UUID = "CFU_CAMP_XRAY_RESULT";
    private static final String CAMP_NAAT_RESULT_UUID = "CFU_CAMP_NAAT_RESULT";
    private static final String REFERRED_TO_PHI_UUID = "CFU_REFERRED_TO_PHI";
    private static final String VISITED_PHI_UUID = "CFU_VISITED_PHI";
    private static final String XRAY_CONDUCTED_UUID = "CFU_XRAY_CONDUCTED";
    private static final String XRAY_RESULT_UUID = "CFU_XRAY_RESULT";
    private static final String NAAT_CONDUCTED_UUID = "CFU_NAAT_CONDUCTED";
    private static final String NAAT_RESULT_UUID = "CFU_NAAT_RESULT";
    private static final String CYTB_CONDUCTED_UUID = "CFU_CYTB_CONDUCTED";
    private static final String CYTB_RESULT_UUID = "CFU_CYTB_RESULT";
    private static final String STATUS_QUESTION_UUID = "CFU_CLINICAL_SCREENING_STATUS";
    private static final String REMARKS_UUID = "CFU_REMARKS";
    private static final int REMARKS_MAX_LENGTH = 200;
    private static final int TARGET_VERSION = 2;

    private static final List<String> REMOVED_QUESTION_UUIDS = List.of(
            "CFU_ADVISED_VISIT_FACILITY",
            "CFU_VISITED_FACILITY");

    /** Active questions top to bottom; all are new or changed in version 2. */
    private static final List<String> FINAL_QUESTION_ORDER = List.of(
            DATE_UUID,
            CAMP_XRAY_RESULT_UUID,
            CAMP_NAAT_RESULT_UUID,
            REFERRED_TO_PHI_UUID,
            VISITED_PHI_UUID,
            XRAY_CONDUCTED_UUID,
            XRAY_RESULT_UUID,
            NAAT_CONDUCTED_UUID,
            NAAT_RESULT_UUID,
            CYTB_CONDUCTED_UUID,
            CYTB_RESULT_UUID,
            STATUS_QUESTION_UUID,
            REMARKS_UUID);

    private static final Set<String> EDITABLE_AFTER_SUBMIT_UUIDS = Set.of(
            REFERRED_TO_PHI_UUID,
            VISITED_PHI_UUID,
            XRAY_CONDUCTED_UUID,
            NAAT_CONDUCTED_UUID,
            CYTB_CONDUCTED_UUID);

    private record Choice(String label, String labelHindi, String value) {}

    private record OptionRename(String oldValue, String newValue, String label, String labelHindi, int order) {}

    private static final List<OptionRename> STATUS_OPTION_RENAMES = List.of(
            new OptionRename("TPT_ELIGIBLE", "RECOMMEND_FOR_TPT", "Recommend for TPT", "टीपीटी के लिए सिफारिश", 1),
            new OptionRename("NO_TREATMENT", "TPT_NOT_RECOMMENDED", "Not recommended TPT", "टीपीटी की सिफारिश नहीं", 2),
            new OptionRename("FULL_TREATMENT", "RECOMMEND_FOR_ATT", "Recommend for ATT", "एटीटी के लिए सिफारिश", 3));

    private static final List<Choice> XRAY_RESULTS = List.of(
            new Choice("Normal", "सामान्य", "NORMAL"),
            new Choice("TB Presumptive", "टीबी संभावित", "TB_PRESUMPTIVE"),
            new Choice("Abnormal but not TB Presumptive", "असामान्य लेकिन टीबी संभावित नहीं", "ABNORMAL_NOT_TB_PRESUMPTIVE"));

    private static final List<Choice> NAAT_RESULTS = List.of(
            new Choice("MTB Detected", "MTB पाया गया", "MTB_DETECTED"),
            new Choice("MTB Not Detected", "MTB नहीं पाया गया", "MTB_NOT_DETECTED"),
            new Choice("Indeterminate", "अनिर्धारित", "INDETERMINATE"));

    private static final List<Choice> CYTB_RESULTS = List.of(
            new Choice("Positive", "पॉजिटिव", "POSITIVE"),
            new Choice("Negative", "नेगेटिव", "NEGATIVE"));

    private static final List<Choice> YES_NO = List.of(
            new Choice("Yes", "हाँ", "YES"),
            new Choice("No", "नहीं", "NO"));

    private final QuestionOptionRepo optionRepo;
    private final SectionQuestionRepo questionRepo;
    private final QuestionValidationRepo validationRepo;
    private final FormSectionRepo sectionRepo;
    private final FormVersionCloner versionCloner;

    @Override
    public String migrationId() {
        return "V010";
    }

    @Override
    public void apply(DynamicFormReconciliationService svc) {
        FormVersion version = versionCloner.ensureVersion(svc, FORM_UUID, TARGET_VERSION);
        FormSection section = svc.section(version, SECTION_UUID)
                .orElseThrow(() -> new IllegalStateException("Section " + SECTION_UUID + " not found"));

        REMOVED_QUESTION_UUIDS.forEach(uuid -> svc.question(section, uuid)
                .ifPresent(q -> svc.unlinkQuestion(q.getQuestionId())));

        // New questions are appended (no displayOrder → no shifting); the final order is set at the end.
        addDateQuestion(svc, section);
        addAutoFillQuestion(svc, section, CAMP_XRAY_RESULT_UUID, "Chest X-ray Result",
                "छाती के एक्स-रे का परिणाम", "CAMP_XRAY_RESULT", "Auto-filled from the X-ray done in the camp");
        addAutoFillQuestion(svc, section, CAMP_NAAT_RESULT_UUID, "NAAT Result",
                "NAAT परिणाम", "CAMP_NAAT_RESULT", "Auto-filled from the NAAT done in the camp");
        addChoiceQuestion(svc, section, REFERRED_TO_PHI_UUID, "Is beneficiary Referred to PHI?",
                "क्या लाभार्थी को पीएचआई रेफर किया गया है?", QuestionType.RADIO, YES_NO);
        addChoiceQuestion(svc, section, VISITED_PHI_UUID, "Has beneficiary visited the PHI?",
                "क्या लाभार्थी ने पीएचआई का दौरा किया है?", QuestionType.RADIO, YES_NO);
        addChoiceQuestion(svc, section, XRAY_CONDUCTED_UUID, "Is Chest X-ray conducted?",
                "क्या छाती का एक्स-रे किया गया?", QuestionType.RADIO, YES_NO);
        addChoiceQuestion(svc, section, XRAY_RESULT_UUID, "Chest X-ray Result",
                "छाती के एक्स-रे का परिणाम", QuestionType.RADIO, XRAY_RESULTS);
        addChoiceQuestion(svc, section, NAAT_CONDUCTED_UUID, "Is NAAT conducted?",
                "क्या NAAT जांच की गई?", QuestionType.RADIO, YES_NO);
        addChoiceQuestion(svc, section, NAAT_RESULT_UUID, "NAAT Result",
                "NAAT परिणाम", QuestionType.RADIO, NAAT_RESULTS);
        addChoiceQuestion(svc, section, CYTB_CONDUCTED_UUID, "Is Cy-TB Test conducted?",
                "क्या Cy-TB टेस्ट किया गया?", QuestionType.RADIO, YES_NO);
        addChoiceQuestion(svc, section, CYTB_RESULT_UUID, "Cy-TB Test Result",
                "Cy-TB टेस्ट परिणाम", QuestionType.RADIO, CYTB_RESULTS);
        restructureStatusQuestion(svc, section);
        addRemarksQuestion(svc, section);

        showWhenYes(svc, version, section, REFERRED_TO_PHI_UUID, VISITED_PHI_UUID);
        showWhenYes(svc, version, section, VISITED_PHI_UUID, XRAY_CONDUCTED_UUID);
        showWhenYes(svc, version, section, VISITED_PHI_UUID, NAAT_CONDUCTED_UUID);
        showWhenYes(svc, version, section, VISITED_PHI_UUID, CYTB_CONDUCTED_UUID);
        showWhenYes(svc, version, section, VISITED_PHI_UUID, STATUS_QUESTION_UUID);
        showWhenYes(svc, version, section, XRAY_CONDUCTED_UUID, XRAY_RESULT_UUID);
        showWhenYes(svc, version, section, NAAT_CONDUCTED_UUID, NAAT_RESULT_UUID);
        showWhenYes(svc, version, section, CYTB_CONDUCTED_UUID, CYTB_RESULT_UUID);

        applyFinalOrderAndFlags(svc, section);
    }

    private void addDateQuestion(DynamicFormReconciliationService svc, FormSection section) {
        SectionQuestionDTO dto = new SectionQuestionDTO();
        dto.setQuestionUuid(DATE_UUID);
        dto.setQuestionText("Date");
        dto.setQuestionTextHindi("तारीख");
        dto.setQuestionType(QuestionType.DATE);
        dto.setIsMandatory(true);
        SectionQuestion date = svc.ensureQuestion(section, dto);

        QuestionValidationDTO maxDate = new QuestionValidationDTO();
        maxDate.setValidationType(ValidationType.MAX_DATE);
        maxDate.setValidationParam("TODAY");
        maxDate.setErrorMessage("Cannot be a future date");
        svc.ensureValidation(date, maxDate);

        svc.ensureValidation(date, autoFill("TODAY", "Auto-filled with today's date"));
    }

    private void addAutoFillQuestion(DynamicFormReconciliationService svc, FormSection section, String uuid,
            String text, String textHindi, String source, String message) {
        SectionQuestionDTO dto = new SectionQuestionDTO();
        dto.setQuestionUuid(uuid);
        dto.setQuestionText(text);
        dto.setQuestionTextHindi(textHindi);
        dto.setQuestionType(QuestionType.AUTO_FILL);
        dto.setIsMandatory(false);
        SectionQuestion question = svc.ensureQuestion(section, dto);
        svc.ensureValidation(question, autoFill(source, message));
    }

    private QuestionValidationDTO autoFill(String source, String message) {
        QuestionValidationDTO dto = new QuestionValidationDTO();
        dto.setValidationType(ValidationType.AUTO_FILL);
        dto.setValidationParam(source);
        dto.setErrorMessage(message);
        return dto;
    }

    // Labels are (re)applied on the entity too, so a question created by an earlier revision picks them up.
    private void addChoiceQuestion(DynamicFormReconciliationService svc, FormSection section, String uuid,
            String text, String textHindi, QuestionType type, List<Choice> choices) {
        SectionQuestionDTO dto = new SectionQuestionDTO();
        dto.setQuestionUuid(uuid);
        dto.setQuestionText(text);
        dto.setQuestionTextHindi(textHindi);
        dto.setQuestionType(type);
        dto.setIsMandatory(true);
        SectionQuestion question = svc.ensureQuestion(section, dto);
        question.setQuestionText(text);
        question.setQuestionTextHindi(textHindi);
        questionRepo.save(question);

        for (int i = 0; i < choices.size(); i++) {
            Choice choice = choices.get(i);
            QuestionOptionDTO option = new QuestionOptionDTO();
            option.setOptionLabel(choice.label());
            option.setOptionLabelHindi(choice.labelHindi());
            option.setOptionValue(choice.value());
            option.setOptionValueHindi(choice.labelHindi());
            option.setDisplayOrder(i + 1);
            QuestionOption saved = svc.ensureOption(question, option);
            saved.setOptionLabel(choice.label());
            saved.setOptionLabelHindi(choice.labelHindi());
            optionRepo.save(saved);
        }
    }

    // Options are matched by their seeded value or, after an earlier run, their renamed value.
    private void restructureStatusQuestion(DynamicFormReconciliationService svc, FormSection section) {
        SectionQuestion status = svc.question(section, STATUS_QUESTION_UUID)
                .orElseThrow(() -> new IllegalStateException("Question " + STATUS_QUESTION_UUID + " not found"));
        status.setQuestionText("Treatment recommended by MO/ TU/ PHI");
        status.setQuestionTextHindi("एमओ/ टीयू/ पीएचआई द्वारा अनुशंसित उपचार");
        status.setQuestionType(QuestionType.RADIO);
        questionRepo.save(status);

        STATUS_OPTION_RENAMES.forEach(rename -> svc.option(status, rename.oldValue())
                .or(() -> svc.option(status, rename.newValue()))
                .ifPresent(opt -> {
                    opt.setOptionLabel(rename.label());
                    opt.setOptionLabelHindi(rename.labelHindi());
                    opt.setOptionValue(rename.newValue());
                    opt.setOptionValueHindi(rename.labelHindi());
                    opt.setDisplayOrder(rename.order());
                    optionRepo.save(opt);
                }));
    }

    // An earlier revision created Remarks with a 500-char limit; its MAX_LENGTH row is moved to 200 in place.
    private void addRemarksQuestion(DynamicFormReconciliationService svc, FormSection section) {
        SectionQuestionDTO dto = new SectionQuestionDTO();
        dto.setQuestionUuid(REMARKS_UUID);
        dto.setQuestionText("Remarks");
        dto.setQuestionTextHindi("टिप्पणियाँ");
        dto.setQuestionType(QuestionType.TEXT);
        dto.setIsMandatory(false);
        dto.setMaxLength(REMARKS_MAX_LENGTH);
        SectionQuestion remarks = svc.ensureQuestion(section, dto);
        remarks.setIsMandatory(false);
        remarks.setMaxLength(REMARKS_MAX_LENGTH);
        questionRepo.save(remarks);

        String limit = String.valueOf(REMARKS_MAX_LENGTH);
        String message = "Must be " + REMARKS_MAX_LENGTH + " characters or fewer";
        validationRepo.findBySectionQuestion_QuestionId(remarks.getQuestionId()).stream()
                .filter(v -> v.getValidationType() == ValidationType.MAX_LENGTH && !limit.equals(v.getValidationParam()))
                .forEach(v -> {
                    v.setValidationParam(limit);
                    v.setErrorMessage(message);
                    validationRepo.save(v);
                });

        QuestionValidationDTO maxLength = new QuestionValidationDTO();
        maxLength.setValidationType(ValidationType.MAX_LENGTH);
        maxLength.setValidationParam(limit);
        maxLength.setErrorMessage(message);
        svc.ensureValidation(remarks, maxLength);
    }

    private void showWhenYes(DynamicFormReconciliationService svc, FormVersion version, FormSection section,
            String sourceQuestionUuid, String targetQuestionUuid) {
        SectionQuestion source = svc.question(section, sourceQuestionUuid)
                .orElseThrow(() -> new IllegalStateException("Question " + sourceQuestionUuid + " not found"));
        QuestionOption yes = svc.option(source, "YES")
                .orElseThrow(() -> new IllegalStateException("Option " + sourceQuestionUuid + "=YES not found"));
        OptionConditionDTO show = new OptionConditionDTO();
        show.setActionType("SHOW_QUESTION");
        show.setTargetQuestionUuid(targetQuestionUuid);
        svc.ensureCondition(yes, show, version);
    }

    // Soft-removed questions go after the active ones so they never collide with a live display slot.
    private void applyFinalOrderAndFlags(DynamicFormReconciliationService svc, FormSection section) {
        section.setIsEditable(true);
        sectionRepo.save(section);

        for (int i = 0; i < FINAL_QUESTION_ORDER.size(); i++) {
            String uuid = FINAL_QUESTION_ORDER.get(i);
            SectionQuestion question = svc.question(section, uuid)
                    .orElseThrow(() -> new IllegalStateException("Question " + uuid + " not found"));
            question.setDisplayOrder(i + 1);
            question.setRequiresBackFill(true);
            question.setEditableAfterSubmit(EDITABLE_AFTER_SUBMIT_UUIDS.contains(uuid));
            questionRepo.save(question);
        }
        for (int i = 0; i < REMOVED_QUESTION_UUIDS.size(); i++) {
            int order = FINAL_QUESTION_ORDER.size() + i + 1;
            svc.question(section, REMOVED_QUESTION_UUIDS.get(i)).ifPresent(q -> {
                q.setDisplayOrder(order);
                questionRepo.save(q);
            });
        }
    }
}
