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
package com.iemr.flw.seeder;

import com.iemr.flw.dto.iemr.DynamicFormDTO;
import com.iemr.flw.dto.iemr.FormSectionDTO;
import com.iemr.flw.dto.iemr.OptionConditionDTO;
import com.iemr.flw.dto.iemr.QuestionOptionDTO;
import com.iemr.flw.dto.iemr.QuestionValidationDTO;
import com.iemr.flw.dto.iemr.SectionQuestionDTO;
import com.iemr.flw.masterEnum.FormType;
import com.iemr.flw.masterEnum.QuestionType;
import com.iemr.flw.masterEnum.ValidationType;
import com.iemr.flw.repo.iemr.DynamicFormRepo;
import com.iemr.flw.service.DynamicFormDefinitionService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Seeds the TB Counselling form definition on application startup if it does not already exist.
 * Idempotent: skips creation if the form UUID is already present in the database.
 *
 * @author Piramal Swasthya
 */
@Component
public class TbCounsellingFormSeeder {

    private static final Logger log = LoggerFactory.getLogger(TbCounsellingFormSeeder.class);

    public static final String FORM_UUID = "TB_COUNSELLING";

    private final DynamicFormDefinitionService formService;
    private final DynamicFormRepo formRepo;

    public TbCounsellingFormSeeder(DynamicFormDefinitionService formService,
                                   DynamicFormRepo formRepo) {
        this.formService = formService;
        this.formRepo = formRepo;
    }

    @PostConstruct
    public void seed() {
        if (formRepo.findByFormUuid(FORM_UUID).isPresent()) {
            log.info("TbCounsellingFormSeeder: form '{}' already exists — skipping seed.", FORM_UUID);
            return;
        }
        log.info("TbCounsellingFormSeeder: seeding TB Counselling form...");
        formService.createForm(buildFormDto());
        log.info("TbCounsellingFormSeeder: seed complete.");
    }

    // ── Form Definition ──────────────────────────────────────────────────────────

    private DynamicFormDTO buildFormDto() {
        DynamicFormDTO dto = new DynamicFormDTO();
        dto.setFormUuid(FORM_UUID);
        dto.setFormName("TB Counselling");
        dto.setFormType(FormType.TB_COUNSELLING);
        dto.setIsActive(true);
        dto.setFollowUpDelayDays(15);

        List<FormSectionDTO> sections = new ArrayList<>();
        sections.add(buildSectionA());
        sections.add(buildSectionB());
        sections.add(buildSectionC());
        sections.add(buildSectionD());
        sections.add(buildSectionE());
        sections.add(buildSectionF());
        dto.setSections(sections);
        return dto;
    }

    // ── Section A: Disease Awareness ─────────────────────────────────────────────

    private FormSectionDTO buildSectionA() {
        FormSectionDTO s = section("TB_SEC_A", "Disease Awareness", "PRE_SUBMIT", 1, true, false);
        List<SectionQuestionDTO> qs = new ArrayList<>();
        qs.add(yesNoRadio("TB_A_Q1", "TB disease explained to patient", 1, true));
        qs.add(yesNoRadio("TB_A_Q2", "Transmission route explained", 2, true));
        qs.add(yesNoRadio("TB_A_Q3", "Symptoms explained", 3, true));
        qs.add(yesNoRadio("TB_A_Q4", "Treatment duration explained", 4, true));
        qs.add(textQuestion("TB_A_REMARKS", "Disease awareness notes", 5, false, 500, true));
        s.setQuestions(qs);
        return s;
    }

    // ── Section B: Do's and Don'ts ───────────────────────────────────────────────

    private FormSectionDTO buildSectionB() {
        FormSectionDTO s = section("TB_SEC_B", "Do's and Don'ts", "PRE_SUBMIT", 2, true, false);
        List<SectionQuestionDTO> qs = new ArrayList<>();
        qs.add(yesNoRadio("TB_B_Q1", "Cover mouth while coughing — advised", 1, true));
        qs.add(yesNoRadio("TB_B_Q2", "Complete full treatment course — advised", 2, true));
        qs.add(yesNoRadio("TB_B_Q3", "Regular follow-up attendance — advised", 3, true));
        qs.add(yesNoRadio("TB_B_Q4", "Nutritional guidance provided", 4, true));
        qs.add(yesNoRadio("TB_B_Q5", "No smoking / alcohol — advised", 5, true));
        qs.add(yesNoRadio("TB_B_Q6", "Isolation precautions explained", 6, true));
        qs.add(textQuestion("TB_B_REMARKS", "Do's & Don'ts notes", 7, false, 500, true));
        s.setQuestions(qs);
        return s;
    }

    // ── Section C: Government Schemes ────────────────────────────────────────────

    private FormSectionDTO buildSectionC() {
        FormSectionDTO s = section("TB_SEC_C", "Government Schemes", "PRE_SUBMIT", 3, false, false);
        List<SectionQuestionDTO> qs = new ArrayList<>();
        qs.add(yesNoRadio("TB_C_Q1", "Nikshay Poshan Yojana (NPY) eligibility explained", 1, false));
        qs.add(yesNoRadio("TB_C_Q2", "DOTS free treatment explained", 2, false));
        qs.add(textQuestion("TB_C_REMARKS", "Schemes notes", 3, false, 300, true));
        s.setQuestions(qs);
        return s;
    }

    // ── Section D: Treatment Regimen ─────────────────────────────────────────────

    private FormSectionDTO buildSectionD() {
        FormSectionDTO s = section("TB_SEC_D", "Treatment Regimen", "PRE_SUBMIT", 4, true, false);
        List<SectionQuestionDTO> qs = new ArrayList<>();
        qs.add(yesNoRadio("TB_D_Q1", "Regimen explained to patient", 1, true));
        qs.add(yesNoRadio("TB_D_Q2", "Medication names explained", 2, true));
        qs.add(yesNoRadio("TB_D_Q3", "Side effects explained", 3, true));
        qs.add(yesNoRadio("TB_D_Q4", "Importance of adherence explained", 4, true));
        qs.add(textQuestion("TB_D_REMARKS", "Treatment regimen notes", 5, false, 300, true));
        s.setQuestions(qs);
        return s;
    }

    // ── Section E: Counselling Completion ────────────────────────────────────────

    private FormSectionDTO buildSectionE() {
        FormSectionDTO s = section("TB_SEC_E", "Counselling Completion", "PRE_SUBMIT", 5, true, true);
        List<SectionQuestionDTO> qs = new ArrayList<>();

        // Counselling completion status — RADIO: Complete | Refused
        SectionQuestionDTO statusQ = new SectionQuestionDTO();
        statusQ.setQuestionUuid("TB_E_Q1");
        statusQ.setQuestionText("Counselling completion status");
        statusQ.setQuestionType(QuestionType.RADIO);
        statusQ.setIsMandatory(true);
        statusQ.setDisplayOrder(1);
        statusQ.setVisibleByDefault(true);

        // "Complete" option — no conditions
        QuestionOptionDTO completeOpt = option("Complete", "COMPLETE", 1, List.of());

        // "Refused" option — 5 conditions: disable sections A-D + show refusal text box
        List<OptionConditionDTO> refusedConditions = new ArrayList<>();
        refusedConditions.add(disableSectionValidation("TB_SEC_A"));
        refusedConditions.add(disableSectionValidation("TB_SEC_B"));
        refusedConditions.add(disableSectionValidation("TB_SEC_C"));
        refusedConditions.add(disableSectionValidation("TB_SEC_D"));
        refusedConditions.add(showQuestion("TB_E_REFUSAL"));
        QuestionOptionDTO refusedOpt = option("Refused", "REFUSED", 2, refusedConditions);

        List<QuestionOptionDTO> statusOptions = new ArrayList<>();
        statusOptions.add(completeOpt);
        statusOptions.add(refusedOpt);
        statusQ.setOptions(statusOptions);
        statusQ.setValidations(List.of());
        qs.add(statusQ);

        // Reason for refusal — hidden by default, max 300 chars
        qs.add(textQuestion("TB_E_REFUSAL", "Reason for refusal", 2, true, 300, false));

        // Counsellor remarks — optional, max 500 chars
        qs.add(textQuestion("TB_E_REMARKS", "Counsellor remarks", 3, false, 500, true));

        s.setQuestions(qs);
        return s;
    }

    // ── Section F: Follow Up to TU ───────────────────────────────────────────────

    private FormSectionDTO buildSectionF() {
        FormSectionDTO s = section("TB_SEC_F", "Follow Up to TU", "POST_SUBMIT", 6, true, true);
        List<SectionQuestionDTO> qs = new ArrayList<>();

        // "Has the patient started TB treatment?" — RADIO: Yes | No
        SectionQuestionDTO startedQ = new SectionQuestionDTO();
        startedQ.setQuestionUuid("TB_F_Q1");
        startedQ.setQuestionText("Has the patient started the prescribed TB treatment regimen?");
        startedQ.setQuestionType(QuestionType.RADIO);
        startedQ.setIsMandatory(true);
        startedQ.setDisplayOrder(1);
        startedQ.setVisibleByDefault(true);

        QuestionOptionDTO yesOpt = option("Yes", "YES", 1, List.of());

        List<OptionConditionDTO> noConditions = new ArrayList<>();
        noConditions.add(showQuestion("TB_F_NO_REASON"));
        QuestionOptionDTO noOpt = option("No", "NO", 2, noConditions);

        List<QuestionOptionDTO> startedOptions = new ArrayList<>();
        startedOptions.add(yesOpt);
        startedOptions.add(noOpt);
        startedQ.setOptions(startedOptions);
        startedQ.setValidations(List.of());
        qs.add(startedQ);

        // Reason for not starting — hidden by default, max 500 chars
        qs.add(textQuestion("TB_F_NO_REASON",
                "Reason for not starting the prescribed TB treatment regimen",
                2, false, 500, false));

        // DOTS centre visit
        qs.add(yesNoRadio("TB_F_Q2",
                "Has the patient visited the DOTS centre / referred health facility for treatment collection?",
                3, true));

        // Side effects reported
        qs.add(yesNoRadio("TB_F_Q3",
                "Has the patient reported side effects to the treating doctor or DOTS centre?",
                4, true));

        s.setQuestions(qs);
        return s;
    }

    // ── Builder Helpers ───────────────────────────────────────────────────────────

    private FormSectionDTO section(String uuid, String name, String phase,
                                   int order, boolean required, boolean hasSubmitButton) {
        FormSectionDTO s = new FormSectionDTO();
        s.setSectionUuid(uuid);
        s.setSectionName(name);
        s.setSectionPhase(phase);
        s.setDisplayOrder(order);
        s.setIsRequired(required);
        s.setHasSubmitButton(hasSubmitButton);
        s.setQuestions(new ArrayList<>());
        return s;
    }

    private SectionQuestionDTO yesNoRadio(String uuid, String text, int order, boolean mandatory) {
        SectionQuestionDTO q = new SectionQuestionDTO();
        q.setQuestionUuid(uuid);
        q.setQuestionText(text);
        q.setQuestionType(QuestionType.RADIO);
        q.setIsMandatory(mandatory);
        q.setDisplayOrder(order);
        q.setVisibleByDefault(true);

        List<QuestionOptionDTO> opts = new ArrayList<>();
        opts.add(option("Yes", "YES", 1, List.of()));
        opts.add(option("No", "NO", 2, List.of()));
        q.setOptions(opts);
        q.setValidations(List.of());
        return q;
    }

    private SectionQuestionDTO textQuestion(String uuid, String text, int order,
                                            boolean mandatory, int maxLength, boolean visible) {
        SectionQuestionDTO q = new SectionQuestionDTO();
        q.setQuestionUuid(uuid);
        q.setQuestionText(text);
        q.setQuestionType(QuestionType.TEXT);
        q.setIsMandatory(mandatory);
        q.setDisplayOrder(order);
        q.setMaxLength(maxLength);
        q.setVisibleByDefault(visible);
        q.setOptions(List.of());

        QuestionValidationDTO v = new QuestionValidationDTO();
        v.setValidationType(ValidationType.MAX_LENGTH);
        v.setValidationParam(String.valueOf(maxLength));
        v.setErrorMessage("Must be " + maxLength + " characters or fewer");
        q.setValidations(List.of(v));
        return q;
    }

    private QuestionOptionDTO option(String label, String value, int order,
                                     List<OptionConditionDTO> conditions) {
        QuestionOptionDTO o = new QuestionOptionDTO();
        o.setOptionLabel(label);
        o.setOptionValue(value);
        o.setDisplayOrder(order);
        o.setConditions(conditions);
        return o;
    }

    private OptionConditionDTO showQuestion(String targetQuestionUuid) {
        OptionConditionDTO c = new OptionConditionDTO();
        c.setActionType("SHOW_QUESTION");
        c.setTargetQuestionUuid(targetQuestionUuid);
        return c;
    }

    private OptionConditionDTO disableSectionValidation(String targetSectionUuid) {
        OptionConditionDTO c = new OptionConditionDTO();
        c.setActionType("DISABLE_SECTION_VALIDATION");
        c.setTargetSectionUuid(targetSectionUuid);
        return c;
    }
}
