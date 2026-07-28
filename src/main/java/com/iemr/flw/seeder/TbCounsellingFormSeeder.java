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
import com.iemr.flw.masterEnum.SectionPhase;
import com.iemr.flw.masterEnum.ValidationType;
import com.iemr.flw.repo.iemr.DynamicFormRepo;
import com.iemr.flw.service.DynamicFormDefinitionService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class TbCounsellingFormSeeder {

    private static final Logger log = LoggerFactory.getLogger(TbCounsellingFormSeeder.class);

    public static final String FORM_UUID = "TB_COUNSELLING";

    private final DynamicFormDefinitionService formService;
    private final DynamicFormRepo formRepo;

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
//        sections.add(buildSectionF());
        dto.setSections(sections);
        return dto;
    }

    // ── Section A: Disease Awareness ─────────────────────────────────────────────

    private FormSectionDTO buildSectionA() {
        FormSectionDTO s = section("TB_SEC_A", "Disease Awareness", "बीमारी के बारे में जागरूकता", SectionPhase.PRE_SUBMIT, 1, true, false);
        List<SectionQuestionDTO> qs = new ArrayList<>();
        qs.add(yesNoRadio("TB_A_Q1", "TB disease explained to patient", "मरीज़ को टीबी बीमारी के बारे में समझाया गया।", 1, true));
        qs.add(yesNoRadio("TB_A_Q2", "Transmission route explained", "ट्रांसमिशन के तरीके के बारे में जानकारी", 2, true));
        qs.add(yesNoRadio("TB_A_Q3", "Symptoms explained", "लक्षणों की जानकारी", 3, true));
        qs.add(yesNoRadio("TB_A_Q4", "Treatment duration explained", "इलाज की अवधि के बारे में जानकारी", 4, true));
        qs.add(textQuestion("TB_A_REMARKS", "Disease awareness notes", "बीमारी के बारे में जानकारी देने वाले नोट्स", 5, false, 500, true));
        s.setQuestions(qs);
        return s;
    }

    // ── Section B: Do's and Don'ts ───────────────────────────────────────────────

    private FormSectionDTO buildSectionB() {
        FormSectionDTO s = section("TB_SEC_B", "Do's and Don'ts", "करो और ना करो", SectionPhase.PRE_SUBMIT, 2, true, false);
        List<SectionQuestionDTO> qs = new ArrayList<>();
        qs.add(yesNoRadio("TB_B_Q1", "Cover mouth while coughing — advised", "खांसते समय मुंह ढकने की सलाह दी जाती है।", 1, true));
        qs.add(yesNoRadio("TB_B_Q2", "Complete full treatment course — advised", "इलाज का पूरा कोर्स पूरा करने की सलाह दी जाती है।", 2, true));
        qs.add(yesNoRadio("TB_B_Q3", "Regular follow-up attendance — advised", "इलाज का पूरा कोर्स पूरा करने की सलाह दी जाती है।", 3, true));
        qs.add(yesNoRadio("TB_B_Q4", "Nutritional guidance provided", "पोषण संबंधी मार्गदर्शन प्रदान किया गया", 4, true));
        qs.add(yesNoRadio("TB_B_Q5", "No smoking / alcohol — advised", "धूम्रपान / शराब न लेने की सलाह दी जाती है।", 5, true));
        qs.add(yesNoRadio("TB_B_Q6", "Isolation precautions explained", "अलगाव सावधानियों के बारे में बताया गया", 6, true));
        qs.add(textQuestion("TB_B_REMARKS", "Do's & Don'ts notes", "क्या करें और क्या न करें - नोट्स", 7, false, 500, true));
        s.setQuestions(qs);
        return s;
    }

    // ── Section C: Government Schemes ────────────────────────────────────────────

    private FormSectionDTO buildSectionC() {
        FormSectionDTO s = section("TB_SEC_C", "Government Schemes", "सरकारी योजनाएं", SectionPhase.PRE_SUBMIT, 3, false, false);
        List<SectionQuestionDTO> qs = new ArrayList<>();
        qs.add(yesNoRadio("TB_C_Q1", "Nikshay Poshan Yojana (NPY) eligibility explained", "निक्षय पोषण योजना (एनपीवाई) पात्रता के बारे में बताया गया", 1, true));
        qs.add(yesNoRadio("TB_C_Q2", "DOTS free treatment explained", "DOTS मुफ़्त इलाज के बारे में जानकारी", 2, true));
        qs.add(textQuestion("TB_C_REMARKS", "Schemes notes", "योजनाओं के नोट्स", 3, false, 300, true));
        s.setQuestions(qs);
        return s;
    }

    // ── Section D: Treatment Regimen ─────────────────────────────────────────────

    private FormSectionDTO buildSectionD() {
        FormSectionDTO s = section("TB_SEC_D", "Treatment Regimen", "इलाज का तरीका", SectionPhase.PRE_SUBMIT, 4, true, false);
        List<SectionQuestionDTO> qs = new ArrayList<>();
        qs.add(yesNoRadio("TB_D_Q1", "Regimen explained to patient", "मरीज़ को इलाज का तरीका समझाया गया।", 1, true));
        qs.add(yesNoRadio("TB_D_Q2", "Medication names explained", "दवाओं के नामों की जानकारी", 2, true));
        qs.add(yesNoRadio("TB_D_Q3", "Side effects explained", "साइड इफ़ेक्ट्स के बारे में जानकारी", 3, true));
        qs.add(yesNoRadio("TB_D_Q4", "Importance of adherence explained", "निर्देशों का पालन करने का महत्व समझाया गया", 4, true));
        qs.add(textQuestion("TB_D_REMARKS", "Treatment regimen notes", "इलाज के तरीके से जुड़े नोट्स", 5, false, 300, true));
        s.setQuestions(qs);
        return s;
    }

    // ── Section E: Counselling Completion ────────────────────────────────────────

    private FormSectionDTO buildSectionE() {
        FormSectionDTO s = section("TB_SEC_E", "Counselling Completion", "काउंसलिंग पूरी होना", SectionPhase.PRE_SUBMIT, 5, true, true);
        List<SectionQuestionDTO> qs = new ArrayList<>();

        // Counselling completion status — RADIO: Complete | Refused
        SectionQuestionDTO statusQ = new SectionQuestionDTO();
        statusQ.setQuestionUuid("TB_E_Q1");
        statusQ.setQuestionText("Counselling completion status");
        statusQ.setQuestionTextHindi("काउंसलिंग पूरी होने की स्थिति");
        statusQ.setQuestionType(QuestionType.RADIO);
        statusQ.setIsMandatory(true);
        statusQ.setDisplayOrder(1);
        statusQ.setVisibleByDefault(true);

        // "Complete" option — no conditions
        QuestionOptionDTO completeOpt = option("Complete", "सम्पूर्ण","COMPLETE", "सम्पूर्ण", 1, List.of());

        // "Refused" option — 5 conditions: disable sections A-D + show refusal text box
        List<OptionConditionDTO> refusedConditions = new ArrayList<>();
        refusedConditions.add(disableSectionValidation("TB_SEC_A"));
        refusedConditions.add(disableSectionValidation("TB_SEC_B"));
        refusedConditions.add(disableSectionValidation("TB_SEC_C"));
        refusedConditions.add(disableSectionValidation("TB_SEC_D"));
        refusedConditions.add(showQuestion("TB_E_REFUSAL"));
        QuestionOptionDTO refusedOpt = option("Refused", "अस्वीकार करना", "REFUSED", "अस्वीकार करना", 2, refusedConditions);

        List<QuestionOptionDTO> statusOptions = new ArrayList<>();
        statusOptions.add(completeOpt);
        statusOptions.add(refusedOpt);
        statusQ.setOptions(statusOptions);
        statusQ.setValidations(List.of());
        qs.add(statusQ);

        // Reason for refusal — hidden by default, max 300 chars
        qs.add(textQuestion("TB_E_REFUSAL", "Reason for refusal", "इनकार का कारण",2, true, 300, false));

        // Counsellor remarks — optional, max 500 chars
        qs.add(textQuestion("TB_E_REMARKS", "Counsellor remarks", "काउंसलर की टिप्पणी", 3, false, 500, true));

        s.setQuestions(qs);
        return s;
    }

    // ── Section F: Follow Up to TU ───────────────────────────────────────────────

    private FormSectionDTO buildSectionF() {
        FormSectionDTO s = section("TB_SEC_F", "Follow Up to TU", "TU के बाद की कार्रवाई", SectionPhase.POST_SUBMIT, 6, true, true);
        List<SectionQuestionDTO> qs = new ArrayList<>();

        // "Has the patient started TB treatment?" — RADIO: Yes | No
        SectionQuestionDTO startedQ = new SectionQuestionDTO();
        startedQ.setQuestionUuid("TB_F_Q1");
        startedQ.setQuestionText("Has the patient started the prescribed TB treatment regimen?");
        startedQ.setQuestionTextHindi("क्या मरीज़ ने टीबी के लिए बताया गया इलाज शुरू कर दिया है?");
        startedQ.setQuestionType(QuestionType.RADIO);
        startedQ.setIsMandatory(true);
        startedQ.setDisplayOrder(1);
        startedQ.setVisibleByDefault(true);

        QuestionOptionDTO yesOpt = option("Yes", "हाँ", "YES", "हाँ", 1, List.of());

        List<OptionConditionDTO> noConditions = new ArrayList<>();
        noConditions.add(showQuestion("TB_F_NO_REASON"));
        QuestionOptionDTO noOpt = option("No", "नहीं", "NO", "नहीं", 2, noConditions);

        List<QuestionOptionDTO> startedOptions = new ArrayList<>();
        startedOptions.add(yesOpt);
        startedOptions.add(noOpt);
        startedQ.setOptions(startedOptions);
        startedQ.setValidations(List.of());
        qs.add(startedQ);

        // Reason for not starting — hidden by default, max 500 chars
        qs.add(textQuestion("TB_F_NO_REASON",
                "Reason for not starting the prescribed TB treatment regimen",
                "टीबी के लिए तय इलाज का तरीका शुरू न करने का कारण",
                2, false, 500, false));

        // DOTS centre visit
        qs.add(yesNoRadio("TB_F_Q2",
                "Has the patient visited the DOTS centre / referred health facility for treatment collection?",
                "क्या मरीज़ इलाज लेने के लिए DOTS सेंटर या रेफर किए गए स्वास्थ्य केंद्र गया है?",
                3, true));

        // Side effects reported
        qs.add(yesNoRadio("TB_F_Q3",
                "Has the patient reported side effects to the treating doctor or DOTS centre?",
                "क्या मरीज़ ने इलाज करने वाले डॉक्टर या DOTS सेंटर को साइड इफ़ेक्ट्स के बारे में बताया है?",
                4, true));

        s.setQuestions(qs);
        return s;
    }

    // ── Builder Helpers ───────────────────────────────────────────────────────────

    private FormSectionDTO section(String uuid, String name, String nameHindi, SectionPhase phase,
                                   int order, boolean required, boolean hasSubmitButton) {
        FormSectionDTO s = new FormSectionDTO();
        s.setSectionUuid(uuid);
        s.setSectionName(name);
        s.setSectionNameHindi(nameHindi);
        s.setSectionPhase(phase);
        s.setDisplayOrder(order);
        s.setIsRequired(required);
        s.setHasSubmitButton(hasSubmitButton);
        s.setQuestions(new ArrayList<>());
        return s;
    }

    private SectionQuestionDTO yesNoRadio(String uuid, String text, String textHindi, int order, boolean mandatory) {
        SectionQuestionDTO q = new SectionQuestionDTO();
        q.setQuestionUuid(uuid);
        q.setQuestionText(text);
        q.setQuestionTextHindi(textHindi);
        q.setQuestionType(QuestionType.RADIO);
        q.setIsMandatory(mandatory);
        q.setDisplayOrder(order);
        q.setVisibleByDefault(true);

        List<QuestionOptionDTO> opts = new ArrayList<>();
        opts.add(option("Yes", "हाँ","YES","हाँ", 1, List.of()));
        opts.add(option("No", "नहीं","NO", "नहीं", 2, List.of()));
        q.setOptions(opts);
        q.setValidations(List.of());
        return q;
    }

    private SectionQuestionDTO textQuestion(String uuid, String text, String textHindi, int order,
                                            boolean mandatory, int maxLength, boolean visible) {
        SectionQuestionDTO q = new SectionQuestionDTO();
        q.setQuestionUuid(uuid);
        q.setQuestionText(text);
        q.setQuestionTextHindi(textHindi);
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

    private QuestionOptionDTO option(String label, String labelHindi, String value, String valueHindi, int order,
                                     List<OptionConditionDTO> conditions) {
        QuestionOptionDTO o = new QuestionOptionDTO();
        o.setOptionLabel(label);
        o.setOptionLabelHindi(labelHindi);
        o.setOptionValue(value);
        o.setOptionValueHindi(valueHindi);
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
