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
 * Seeds the TB Counselling (v2) form definition on application startup if it does not already exist.
 * Idempotent: skips creation if the form UUID is already present in the database.
 *
 * Adds an initial GENERAL_INFO consent gate ("Has the beneficiary agreed for counselling?") ahead
 * of the PRE_SUBMIT sections, and uses CHECKBOX questions in place of Yes/No RADIO questions.
 *
 * @author Piramal Swasthya
 */
@Component
@RequiredArgsConstructor
public class TbCounsellingV2FormSeeder {

    private static final Logger log = LoggerFactory.getLogger(TbCounsellingV2FormSeeder.class);

    public static final String FORM_UUID = "TB_COUNSELLING_V2";

    private final DynamicFormDefinitionService formService;
    private final DynamicFormRepo formRepo;

    @PostConstruct
    public void seed() {
        if (formRepo.findByFormUuid(FORM_UUID).isPresent()) {
            log.info("TbCounsellingV2FormSeeder: form '{}' already exists — skipping seed.", FORM_UUID);
            return;
        }
        log.info("TbCounsellingV2FormSeeder: seeding TB Counselling (v2) form...");
        formService.createForm(buildFormDto());
        log.info("TbCounsellingV2FormSeeder: seed complete.");
    }

    // ── Form Definition ──────────────────────────────────────────────────────────

    private DynamicFormDTO buildFormDto() {
        DynamicFormDTO dto = new DynamicFormDTO();
        dto.setFormUuid(FORM_UUID);
        dto.setFormName("TB Counselling");
        dto.setFormType(FormType.TB_COUNSELLING_V2);
        dto.setIsActive(true);
        dto.setFollowUpDelayDays(15);

        List<FormSectionDTO> sections = new ArrayList<>();
        sections.add(buildGeneralInfoSection());
        sections.add(buildSectionA());
        sections.add(buildSectionB());
        sections.add(buildSectionC());
        sections.add(buildSectionD());
        sections.add(buildSectionE());
        dto.setSections(sections);
        return dto;
    }

    // ── General Info: Counselling Consent ────────────────────────────────────────

    private FormSectionDTO buildGeneralInfoSection() {
        FormSectionDTO s = section("TB2_SEC_GENERAL_INFO", "General Information", "सामान्य जानकारी",
                SectionPhase.GENERAL_INFO, 1, true, false, true);
        List<SectionQuestionDTO> qs = new ArrayList<>();

        // "Has the beneficiary agreed for counselling?" — RADIO: Yes | No
        SectionQuestionDTO consentQ = new SectionQuestionDTO();
        consentQ.setQuestionUuid("TB2_GI_Q1");
        consentQ.setQuestionText("Has the beneficiary agreed for counselling?");
        consentQ.setQuestionTextHindi("क्या लाभार्थी काउंसलिंग के लिए सहमत हुआ है?");
        consentQ.setQuestionType(QuestionType.RADIO);
        consentQ.setIsMandatory(true);
        consentQ.setDisplayOrder(1);
        consentQ.setVisibleByDefault(true);

        // "Yes" option — no conditions, sections A-E remain enabled by default
        QuestionOptionDTO yesOpt = option("Yes", "हाँ", "YES", "हाँ", 1, List.of());

        // "No" option — disable validation for sections A-E + show refusal reason field
        List<OptionConditionDTO> noConditions = new ArrayList<>();
        noConditions.add(disableSectionValidation("TB2_SEC_A"));
        noConditions.add(disableSectionValidation("TB2_SEC_B"));
        noConditions.add(disableSectionValidation("TB2_SEC_C"));
        noConditions.add(disableSectionValidation("TB2_SEC_D"));
        noConditions.add(disableSectionValidation("TB2_SEC_E"));
        noConditions.add(showQuestion("TB2_GI_REFUSAL"));
        QuestionOptionDTO noOpt = option("No", "नहीं", "NO", "नहीं", 2, noConditions);

        List<QuestionOptionDTO> consentOptions = new ArrayList<>();
        consentOptions.add(yesOpt);
        consentOptions.add(noOpt);
        consentQ.setOptions(consentOptions);
        consentQ.setValidations(List.of());
        qs.add(consentQ);

        // Reason for refusal — hidden by default, mandatory when shown, max 300 chars
        qs.add(textQuestion("TB2_GI_REFUSAL", "Reason for refusal", "इनकार का कारण", 2, true, 300, false));

        s.setQuestions(qs);
        return s;
    }

    // ── Section A: Disease Awareness ─────────────────────────────────────────────

    private FormSectionDTO buildSectionA() {
        FormSectionDTO s = section("TB2_SEC_A", "Disease Awareness", "बीमारी के बारे में जागरूकता", SectionPhase.PRE_SUBMIT, 2, true, false, false);
        List<SectionQuestionDTO> qs = new ArrayList<>();
        qs.add(checkboxQuestion("TB2_A_Q1", "TB disease explained to patient", "मरीज़ को टीबी बीमारी के बारे में समझाया गया।", 1, true));
        qs.add(checkboxQuestion("TB2_A_Q2", "Transmission route explained", "ट्रांसमिशन के तरीके के बारे में जानकारी", 2, true));
        qs.add(checkboxQuestion("TB2_A_Q3", "Symptoms explained", "लक्षणों की जानकारी", 3, true));
        qs.add(checkboxQuestion("TB2_A_Q4", "Treatment duration explained", "इलाज की अवधि के बारे में जानकारी", 4, true));
        qs.add(textQuestion("TB2_A_REMARKS", "Disease awareness notes", "बीमारी के बारे में जानकारी देने वाले नोट्स", 5, false, 500, true));
        s.setQuestions(qs);
        return s;
    }

    // ── Section B: Do's and Don'ts ───────────────────────────────────────────────

    private FormSectionDTO buildSectionB() {
        FormSectionDTO s = section("TB2_SEC_B", "Do's and Don'ts", "करो और ना करो", SectionPhase.PRE_SUBMIT, 3, true, false, false);
        List<SectionQuestionDTO> qs = new ArrayList<>();
        qs.add(checkboxQuestion("TB2_B_Q1", "Cover mouth while coughing — advised", "खांसते समय मुंह ढकने की सलाह दी जाती है।", 1, true));
        qs.add(checkboxQuestion("TB2_B_Q2", "Complete full treatment course — advised", "इलाज का पूरा कोर्स पूरा करने की सलाह दी जाती है।", 2, true));
        qs.add(checkboxQuestion("TB2_B_Q3", "Regular follow-up attendance — advised", "इलाज का पूरा कोर्स पूरा करने की सलाह दी जाती है।", 3, true));
        qs.add(checkboxQuestion("TB2_B_Q4", "Nutritional guidance provided", "पोषण संबंधी मार्गदर्शन प्रदान किया गया", 4, true));
        qs.add(checkboxQuestion("TB2_B_Q5", "No smoking / alcohol — advised", "धूम्रपान / शराब न लेने की सलाह दी जाती है।", 5, true));
        qs.add(checkboxQuestion("TB2_B_Q6", "Isolation precautions explained", "अलगाव सावधानियों के बारे में बताया गया", 6, true));
        qs.add(textQuestion("TB2_B_REMARKS", "Do's & Don'ts notes", "क्या करें और क्या न करें - नोट्स", 7, false, 500, true));
        s.setQuestions(qs);
        return s;
    }

    // ── Section C: Government Schemes ────────────────────────────────────────────

    private FormSectionDTO buildSectionC() {
        FormSectionDTO s = section("TB2_SEC_C", "Government Schemes", "सरकारी योजनाएं", SectionPhase.PRE_SUBMIT, 4, true, false, false);
        List<SectionQuestionDTO> qs = new ArrayList<>();
        qs.add(checkboxQuestion("TB2_C_Q1", "Nikshay Poshan Yojana (NPY) eligibility explained", "निक्षय पोषण योजना (एनपीवाई) पात्रता के बारे में बताया गया", 1, true));
        qs.add(checkboxQuestion("TB2_C_Q2", "DOTS free treatment explained", "DOTS मुफ़्त इलाज के बारे में जानकारी", 2, true));
        qs.add(textQuestion("TB2_C_REMARKS", "Schemes notes", "योजनाओं के नोट्स", 3, false, 300, true));
        s.setQuestions(qs);
        return s;
    }

    // ── Section D: Treatment Regimen ─────────────────────────────────────────────

    private FormSectionDTO buildSectionD() {
        FormSectionDTO s = section("TB2_SEC_D", "Treatment Regimen", "इलाज का तरीका", SectionPhase.PRE_SUBMIT, 5, true, false, false);
        List<SectionQuestionDTO> qs = new ArrayList<>();
        qs.add(checkboxQuestion("TB2_D_Q1", "Regimen explained to patient", "मरीज़ को इलाज का तरीका समझाया गया।", 1, true));
        qs.add(checkboxQuestion("TB2_D_Q2", "Medication names explained", "दवाओं के नामों की जानकारी", 2, true));
        qs.add(checkboxQuestion("TB2_D_Q3", "Side effects explained", "साइड इफ़ेक्ट्स के बारे में जानकारी", 3, true));
        qs.add(checkboxQuestion("TB2_D_Q4", "Importance of adherence explained", "निर्देशों का पालन करने का महत्व समझाया गया", 4, true));
        qs.add(textQuestion("TB2_D_REMARKS", "Treatment regimen notes", "इलाज के तरीके से जुड़े नोट्स", 5, false, 300, true));
        s.setQuestions(qs);
        return s;
    }

    // ── Section E: Impact Assessment ─────────────────────────────────────────────

    private FormSectionDTO buildSectionE() {
        FormSectionDTO s = section("TB2_SEC_E", "Impact Assessment", "प्रभाव का आकलन", SectionPhase.PRE_SUBMIT, 6, true, true, true);
        List<SectionQuestionDTO> qs = new ArrayList<>();
        qs.add(textQuestion("TB2_E_Q1",
                "How has TB disease impacted the life of this beneficiary",
                "टीबी बीमारी ने इस लाभार्थी के जीवन को कैसे प्रभावित किया है",
                1, false, 400, true));
        s.setQuestions(qs);
        return s;
    }

    // ── Builder Helpers ───────────────────────────────────────────────────────────

    private FormSectionDTO section(String uuid, String name, String nameHindi, SectionPhase phase,
                                   int order, boolean required, boolean hasSubmitButton, boolean isEditable) {
        FormSectionDTO s = new FormSectionDTO();
        s.setSectionUuid(uuid);
        s.setSectionName(name);
        s.setSectionNameHindi(nameHindi);
        s.setSectionPhase(phase);
        s.setDisplayOrder(order);
        s.setIsRequired(required);
        s.setHasSubmitButton(hasSubmitButton);
        s.setIsEditable(isEditable);
        s.setQuestions(new ArrayList<>());
        return s;
    }

    private SectionQuestionDTO checkboxQuestion(String uuid, String text, String textHindi, int order, boolean mandatory) {
        SectionQuestionDTO q = new SectionQuestionDTO();
        q.setQuestionUuid(uuid);
        q.setQuestionText(text);
        q.setQuestionTextHindi(textHindi);
        q.setQuestionType(QuestionType.CHECKBOX);
        q.setIsMandatory(mandatory);
        q.setDisplayOrder(order);
        q.setVisibleByDefault(true);
        q.setOptions(List.of(option("Checked", "चेक किया गया", "CHECKED", "चेक किया गया", 1, List.of())));
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