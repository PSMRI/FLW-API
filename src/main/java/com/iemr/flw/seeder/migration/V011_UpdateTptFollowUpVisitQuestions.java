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
import com.iemr.flw.dto.iemr.QuestionOptionDTO;
import com.iemr.flw.dto.iemr.QuestionValidationDTO;
import com.iemr.flw.dto.iemr.SectionQuestionDTO;
import com.iemr.flw.masterEnum.QuestionType;
import com.iemr.flw.masterEnum.ValidationType;
import com.iemr.flw.repo.iemr.QuestionOptionRepo;
import com.iemr.flw.repo.iemr.SectionQuestionRepo;
import com.iemr.flw.service.DynamicFormReconciliationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Updates the TPT Follow Up form per the revised TPT Follow-Up spec. The changes go into a new
 * version 2 (via {@link FormVersionCloner}); version 1 keeps the seeded structure, so responses
 * submitted against it keep their meaning.
 *
 * "Registration &amp; Regimen Details" section (TFU_SEC_1):
 *
 * 0. Replaces all of "TPT Regimen advised" (TFU_REGIMEN_ADVISED)'s options with REGIMEN_OPTIONS, in
 *    that order: 6H, 3HP and 1HP are kept as they are; "3RH" is the existing 3HR option (same
 *    regimen — Rifampicin + Isoniazid daily for 3 months) with its value renamed 3HR → 3RH in place
 *    before the replacement; optionUuid is deliberately left unchanged (as V010 does) so clients keep
 *    mapping existing answers to the same option. 6Lfx and 4R are added; 6H (Modified dose) is removed.
 *
 * "Follow-up Visit" section (TFU_SEC_2):
 *
 * 1. Soft-removes "Adherence to Medicines" (TFU_ADHERENCE, Regular/Irregular radio) — never
 *    hard-deleted, the spec keeps the field for reference and historical responses may reference it.
 * 2. Renames "Adverse Drug Reactions" (TFU_ADR) to "Side effects". Only the label text changes —
 *    questionUuid, type and conditions are untouched, so existing answers keep mapping to the same
 *    question. Edited directly on the entity and saved via its repo (as V010 does).
 * 3. Replaces all of TFU_ADR's options with SIDE_EFFECT_OPTIONS, in that order, and clears its
 *    "UNKNOWN" default value — that option is no longer offered.
 * 4. Replaces all of "TPT outcome status" (TFU_OUTCOME_STATUS)'s options with OUTCOME_STATUS_OPTIONS,
 *    in that order. "Treatment Completed" and "Died" are the existing COMPLETED and DIED_DURING_TPT
 *    options relabelled, so their SHOW_QUESTION conditions (outcome date; outcome date + cause of
 *    death) and the MANDATORY_IF validations on TFU_OUTCOME_STATUS=COMPLETED/DIED_DURING_TPT keep
 *    working unchanged. DEVELOPED_ACTIVE_TB and OTHER are removed, and so is "Other" (TFU_OUTCOME_OTHER),
 *    the question only the OTHER option revealed.
 * 5. Adds "Remarks" (TFU_OUTCOME_REMARKS, optional free text, max 500 chars) in the removed
 *    TFU_OUTCOME_OTHER's place, always shown (no condition targets it).
 *
 * 6. Flags TFU_REGIMEN_ADVISED with requiresBackFill, so the client asks for it on beneficiaries whose
 *    earlier responses were captured on version 1. The changed Follow-up Visit questions — TFU_ADR,
 *    TFU_OUTCOME_STATUS and TFU_OUTCOME_REMARKS — are explicitly set requiresBackFill = false: TFU_SEC_2 is
 *    a POST_SUBMIT section, so every visit is a new entry and there is no earlier answer to back-fill.
 *
 * Option replacement (0, 3, 4): options not in the list are soft-removed (never hard-deleted — historical
 * responses may reference them). Listed options reuse the row with the same optionValue where one
 * exists (reactivated if a previous change removed it), so its optionUuid and existing answers stay
 * stable; missing ones are created.
 *
 * Every step is idempotent (version 2 is reused once created, lookups by uuid/optionValue, same-value
 * updates), so a re-run is a no-op.
 */
@Component
@RequiredArgsConstructor
public class V011_UpdateTptFollowUpVisitQuestions implements FormStructureMigration {

    private static final String FORM_UUID = "TPT_FOLLOW_UP";
    private static final String REGISTRATION_SECTION_UUID = "TFU_SEC_1";
    private static final String SECTION_UUID = "TFU_SEC_2";
    private static final String REGIMEN_QUESTION_UUID = "TFU_REGIMEN_ADVISED";
    private static final String ADHERENCE_QUESTION_UUID = "TFU_ADHERENCE";
    private static final String ADR_QUESTION_UUID = "TFU_ADR";
    private static final String ADR_NEW_TEXT = "Side effects";
    private static final String ADR_NEW_TEXT_HINDI = "दुष्प्रभाव";
    private static final String OUTCOME_STATUS_QUESTION_UUID = "TFU_OUTCOME_STATUS";
    private static final String OUTCOME_OTHER_QUESTION_UUID = "TFU_OUTCOME_OTHER";
    private static final String OUTCOME_REMARKS_QUESTION_UUID = "TFU_OUTCOME_REMARKS";
    private static final int OUTCOME_REMARKS_MAX_LENGTH = 500;
    private static final int TARGET_VERSION = 2;

    private record OptionSpec(String label, String labelHindi, String value) {}

    private static final List<OptionSpec> REGIMEN_OPTIONS = List.of(
            new OptionSpec("6H — Isoniazid daily for 6 months",
                    "6H — 6 महीने के लिए प्रतिदिन आइसोनियाज़िड", "6H"),
            new OptionSpec("3RH — Rifampicin + Isoniazid daily for 3 months",
                    "3RH — 3 महीने के लिए प्रतिदिन रिफैम्पिसिन + आइसोनियाज़िड", "3RH"),
            new OptionSpec("3HP — Isoniazid + Rifapentine weekly for 3 months",
                    "3HP — 3 महीने के लिए साप्ताहिक आइसोनियाज़िड + रिफापेंटाइन", "3HP"),
            new OptionSpec("6Lfx — Levofloxacin daily for 6 months",
                    "6Lfx — 6 महीने के लिए प्रतिदिन लेवोफ़्लॉक्सासिन", "6LFX"),
            new OptionSpec("4R — Rifampicin daily for 4 months",
                    "4R — 4 महीने के लिए प्रतिदिन रिफैम्पिसिन", "4R"),
            new OptionSpec("1HP — Isoniazid + Rifapentine daily for 1 month",
                    "1HP — 1 महीने के लिए प्रतिदिन आइसोनियाज़िड + रिफापेंटाइन", "1HP"));

    // Hindi left empty, as seeded for this question.
    private static final List<OptionSpec> SIDE_EFFECT_OPTIONS = List.of(
            new OptionSpec("Nausea", "", "NAUSEA"),
            new OptionSpec("Vomiting", "", "VOMITING"),
            new OptionSpec("Abdominal Pain", "", "ABDOMINAL_PAIN"),
            new OptionSpec("Flue like syndrome- Chills, Dry cough, Shortness of breath, loss of appetite, body ache and malaise, dizziness, headache",
                    "", "FLU_LIKE_SYNDROME_WITH_DIZZINESS_HEADACHE"),
            new OptionSpec("Yellowish discoloration of skin and eyes", "", "YELLOWISH_DISCOLORATION_SKIN_EYES"),
            new OptionSpec("Discoloration of body fluids", "", "DISCOLORATION_BODY_FLUIDS"),
            new OptionSpec("Dark colour Urine", "", "DARK_COLOUR_URINE"),
            new OptionSpec("Pale stool", "", "PALE_STOOL"),
            new OptionSpec("Skin Rash", "", "SKIN_RASH"),
            new OptionSpec("Itching", "", "ITCHING"),
            new OptionSpec("Tingling/burning/numbness in hands and feet", "", "TINGLING_BURNING_NUMBNESS_HANDS_FEET"),
            new OptionSpec("Flue like syndrome- Chills, Dry cough, Shortness of breath, loss of appetite, body ache and malaise",
                    "", "FLU_LIKE_SYNDROME"),
            new OptionSpec("Sleepiness, lethargy", "", "SLEEPINESS_LETHARGY"),
            new OptionSpec("Persistent episodes of unformed watery stools", "", "PERSISTENT_UNFORMED_WATERY_STOOLS"),
            new OptionSpec("Mental changes and signs of bleeding", "", "MENTAL_CHANGES_SIGNS_BLEEDING"),
            new OptionSpec("Convulsions", "", "CONVULSIONS"),
            new OptionSpec("Anaemia", "", "ANAEMIA"),
            new OptionSpec("Arthralgia", "", "ARTHRALGIA"),
            new OptionSpec("Decreased appetite", "", "DECREASED_APPETITE"),
            new OptionSpec("Hypotension / Syncope", "", "HYPOTENSION_SYNCOPE"),
            new OptionSpec("Conjuctivities", "", "CONJUCTIVITIES"),
            new OptionSpec("Shock", "", "SHOCK"));

    private static final List<OptionSpec> OUTCOME_STATUS_OPTIONS = List.of(
            new OptionSpec("Treatment Completed", "उपचार पूर्ण", "COMPLETED"),
            new OptionSpec("Died", "मृत्यु", "DIED_DURING_TPT"),
            new OptionSpec("Treatment failure", "उपचार विफलता", "TREATMENT_FAILURE"),
            new OptionSpec("Lost to follow-up", "फॉलो-अप में खोया", "LOST_TO_FOLLOW_UP"),
            new OptionSpec("TPT discontinuation due to toxicity", "विषाक्तता के कारण टीपीटी बंद", "TPT_DISCONTINUED_TOXICITY"),
            new OptionSpec("Not evaluated", "मूल्यांकन नहीं किया गया", "NOT_EVALUATED"),
            new OptionSpec("Treatment regimen changed", "उपचार पद्धति बदली गई", "TREATMENT_REGIMEN_CHANGED"));

    private final SectionQuestionRepo questionRepo;
    private final QuestionOptionRepo optionRepo;
    private final FormVersionCloner versionCloner;

    @Override
    public String migrationId() {
        return "V011";
    }

    @Override
    public void apply(DynamicFormReconciliationService svc) {
        FormVersion version = versionCloner.ensureVersion(svc, FORM_UUID, TARGET_VERSION);

        FormSection registration = svc.section(version, REGISTRATION_SECTION_UUID)
                .orElseThrow(() -> new IllegalStateException("Section " + REGISTRATION_SECTION_UUID + " not found"));
        SectionQuestion regimen = requireQuestion(svc, registration, REGIMEN_QUESTION_UUID);
        svc.option(regimen, "3HR").ifPresent(opt -> {
            opt.setOptionValue("3RH");
            optionRepo.save(opt);
        });
        replaceOptions(svc, regimen, REGIMEN_OPTIONS);

        FormSection section = svc.section(version, SECTION_UUID)
                .orElseThrow(() -> new IllegalStateException("Section " + SECTION_UUID + " not found"));

        svc.question(section, ADHERENCE_QUESTION_UUID)
                .ifPresent(q -> svc.unlinkQuestion(q.getQuestionId()));

        SectionQuestion adr = requireQuestion(svc, section, ADR_QUESTION_UUID);
        adr.setQuestionText(ADR_NEW_TEXT);
        adr.setQuestionTextHindi(ADR_NEW_TEXT_HINDI);
        adr.setDefaultValue(null);
        questionRepo.save(adr);
        replaceOptions(svc, adr, SIDE_EFFECT_OPTIONS);

        replaceOptions(svc, requireQuestion(svc, section, OUTCOME_STATUS_QUESTION_UUID), OUTCOME_STATUS_OPTIONS);
        svc.question(section, OUTCOME_OTHER_QUESTION_UUID).ifPresent(other -> {
            svc.unlinkQuestion(other.getQuestionId());
            addOutcomeRemarksQuestion(svc, section, other.getDisplayOrder());
        });

        setRequiresBackFill(svc, registration, REGIMEN_QUESTION_UUID, true);
        setRequiresBackFill(svc, section, ADR_QUESTION_UUID, false);
        setRequiresBackFill(svc, section, OUTCOME_STATUS_QUESTION_UUID, false);
        setRequiresBackFill(svc, section, OUTCOME_REMARKS_QUESTION_UUID, false);
    }

    private void setRequiresBackFill(DynamicFormReconciliationService svc, FormSection section, String uuid,
                                     boolean requiresBackFill) {
        SectionQuestion question = requireQuestion(svc, section, uuid);
        question.setRequiresBackFill(requiresBackFill);
        questionRepo.save(question);
    }

    // Takes the removed Other question's displayOrder; ensureQuestion shifts it and everything after down by one.
    private void addOutcomeRemarksQuestion(DynamicFormReconciliationService svc, FormSection section, int displayOrder) {
        SectionQuestionDTO dto = new SectionQuestionDTO();
        dto.setQuestionUuid(OUTCOME_REMARKS_QUESTION_UUID);
        dto.setQuestionText("Remarks");
        dto.setQuestionTextHindi("टिप्पणियाँ");
        dto.setQuestionType(QuestionType.TEXT);
        dto.setIsMandatory(false);
        dto.setMaxLength(OUTCOME_REMARKS_MAX_LENGTH);
        dto.setDisplayOrder(displayOrder);
        SectionQuestion remarks = svc.ensureQuestion(section, dto);

        QuestionValidationDTO maxLength = new QuestionValidationDTO();
        maxLength.setValidationType(ValidationType.MAX_LENGTH);
        maxLength.setValidationParam(String.valueOf(OUTCOME_REMARKS_MAX_LENGTH));
        maxLength.setErrorMessage("Must be " + OUTCOME_REMARKS_MAX_LENGTH + " characters or fewer");
        svc.ensureValidation(remarks, maxLength);
    }

    private SectionQuestion requireQuestion(DynamicFormReconciliationService svc, FormSection section, String uuid) {
        return svc.question(section, uuid)
                .orElseThrow(() -> new IllegalStateException("Question " + uuid + " not found"));
    }

    private void replaceOptions(DynamicFormReconciliationService svc, SectionQuestion question, List<OptionSpec> specs) {
        Set<String> keptValues = specs.stream().map(OptionSpec::value).collect(Collectors.toSet());
        optionRepo.findBySectionQuestion_QuestionIdOrderByDisplayOrderAsc(question.getQuestionId()).stream()
                .filter(opt -> !keptValues.contains(opt.getOptionValue()))
                .forEach(opt -> svc.removeOption(opt.getOptionId()));

        // No displayOrder on create → appended without shifting; final order is set explicitly below.
        for (int i = 0; i < specs.size(); i++) {
            OptionSpec spec = specs.get(i);
            QuestionOption option = svc.option(question, spec.value())
                    .orElseGet(() -> svc.ensureOption(question, newOption(spec)));
            option.setOptionLabel(spec.label());
            option.setOptionLabelHindi(spec.labelHindi());
            option.setOptionValueHindi(spec.labelHindi());
            option.setDisplayOrder(i + 1);
            option.setIsActive(true);
            optionRepo.save(option);
        }
    }

    private QuestionOptionDTO newOption(OptionSpec spec) {
        QuestionOptionDTO dto = new QuestionOptionDTO();
        dto.setOptionLabel(spec.label());
        dto.setOptionLabelHindi(spec.labelHindi());
        dto.setOptionValue(spec.value());
        dto.setOptionValueHindi(spec.labelHindi());
        return dto;
    }
}
