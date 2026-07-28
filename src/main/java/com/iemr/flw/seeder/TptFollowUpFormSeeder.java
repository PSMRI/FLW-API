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
 * Seeds the TPT (Tuberculosis Preventive Treatment) Follow Up form definition on application
 * startup if it does not already exist. Idempotent: skips creation if the form UUID is already
 * present in the database.
 *
 * Two sections:
 * - "Registration &amp; Regimen Details" (PRE_SUBMIT) — captured once, on the first visit only.
 * - "Follow-up Visit" (POST_SUBMIT) — repeatable: the dynamic-form-response engine now creates a
 *   new SectionResponse instance every time this section is completed (instead of overwriting the
 *   previous one), so calling /complete once per visit accumulates a history of follow-up visits
 *   rather than replacing the last one. See DynamicFormResponseServiceImpl/FormResponseItemSaver's
 *   upsertSectionResponse.
 *
 * Known gaps, deliberately not modeled (no existing mechanism in this engine covers them):
 * - "TPT Regimen advised" is described as only mandatory once "TPT start date" has a value, but
 *   ValidationType.MANDATORY_IF can only express "mandatory when another field equals a specific
 *   value", not "mandatory when another field has any value" — modeled as unconditionally
 *   mandatory instead.
 * - "TPT start date cannot be before the contact-screening date" and "Follow-up visit date cannot
 *   be before TPT start date" are cross-question date comparisons; ValidationType.MIN_DATE only
 *   supports a literal ISO date or "TODAY", not a reference to another question's answer.
 * - "TPT expected completion date" is described as auto-calculated from the regimen; there is no
 *   computation engine here, so it is modeled as a plain DATE question the client fills in.
 * - Selecting "Developed active TB during TPT" is meant to create a new TB Presumptive Case record
 *   and close this TPT case — that is a real write into another case-management domain and is out
 *   of scope here; no condition is wired for it.
 * - No field exists for a custom submit-button label ("Save Record") or question placeholder/help
 *   text, so neither is modeled.
 *
 * @author Piramal Swasthya
 */
@Component
@RequiredArgsConstructor
public class TptFollowUpFormSeeder {

    private static final Logger log = LoggerFactory.getLogger(TptFollowUpFormSeeder.class);

    public static final String FORM_UUID = "TPT_FOLLOW_UP";

    private final DynamicFormDefinitionService formService;
    private final DynamicFormRepo formRepo;

    @PostConstruct
    public void seed() {
        if (formRepo.findByFormUuid(FORM_UUID).isPresent()) {
            log.info("TptFollowUpFormSeeder: form '{}' already exists — skipping seed.", FORM_UUID);
            return;
        }
        log.info("TptFollowUpFormSeeder: seeding TPT Follow Up form...");
        formService.createForm(buildFormDto());
        log.info("TptFollowUpFormSeeder: seed complete.");
    }

    // ── Form Definition ──────────────────────────────────────────────────────────

    private DynamicFormDTO buildFormDto() {
        DynamicFormDTO dto = new DynamicFormDTO();
        dto.setFormUuid(FORM_UUID);
        dto.setFormName("TPT Follow Up");
        dto.setFormType(FormType.TPT_FOLLOW_UP);
        dto.setIsActive(true);

        List<FormSectionDTO> sections = new ArrayList<>();
        sections.add(buildRegistrationSection());
        sections.add(buildFollowUpVisitSection());
        dto.setSections(sections);
        return dto;
    }

    // ── Section 1: Registration & Regimen Details (PRE_SUBMIT, first visit only) ────────────────

    private FormSectionDTO buildRegistrationSection() {
        FormSectionDTO s = section("TFU_SEC_1", "Registration & Regimen Details", "पंजीकरण और रेजिमेन विवरण",
                SectionPhase.PRE_SUBMIT, 1, true, true, true);
        List<SectionQuestionDTO> qs = new ArrayList<>();

        SectionQuestionDTO registrationDate = new SectionQuestionDTO();
        registrationDate.setQuestionUuid("TFU_REGISTRATION_DATE");
        registrationDate.setQuestionText("Date of Registration");
        registrationDate.setQuestionTextHindi("पंजीकरण की तारीख");
        registrationDate.setQuestionType(QuestionType.DATE);
        registrationDate.setIsMandatory(true);
        registrationDate.setDisplayOrder(1);
        registrationDate.setVisibleByDefault(true);
        registrationDate.setOptions(List.of());
        registrationDate.setValidations(List.of());
        qs.add(registrationDate);

        // TPT Regimen advised — unconditionally mandatory (see class javadoc re: the
        // start-date dependency that MANDATORY_IF cannot express).
        SectionQuestionDTO regimen = new SectionQuestionDTO();
        regimen.setQuestionUuid("TFU_REGIMEN_ADVISED");
        regimen.setQuestionText("TPT Regimen advised");
        regimen.setQuestionTextHindi("सुझाया गया टीपीटी रेजिमेन");
        regimen.setQuestionType(QuestionType.RADIO);
        regimen.setIsMandatory(true);
        regimen.setDisplayOrder(2);
        regimen.setVisibleByDefault(true);
        regimen.setOptions(List.of(
                option("6H — Isoniazid daily for 6 months", "6H — 6 महीने के लिए प्रतिदिन आइसोनियाज़िड",
                        "6H", "6H — 6 महीने के लिए प्रतिदिन आइसोनियाज़िड", 1, List.of()),
                option("3HP — Isoniazid + Rifapentine weekly for 3 months",
                        "3HP — 3 महीने के लिए साप्ताहिक आइसोनियाज़िड + रिफापेंटाइन",
                        "3HP", "3HP — 3 महीने के लिए साप्ताहिक आइसोनियाज़िड + रिफापेंटाइन", 2, List.of()),
                option("3HR — Isoniazid + Rifampicin daily for 3 months",
                        "3HR — 3 महीने के लिए प्रतिदिन आइसोनियाज़िड + रिफैम्पिसिन",
                        "3HR", "3HR — 3 महीने के लिए प्रतिदिन आइसोनियाज़िड + रिफैम्पिसिन", 3, List.of()),
                option("1HP — Isoniazid + Rifapentine daily for 1 month",
                        "1HP — 1 महीने के लिए प्रतिदिन आइसोनियाज़िड + रिफापेंटाइन",
                        "1HP", "1HP — 1 महीने के लिए प्रतिदिन आइसोनियाज़िड + रिफापेंटाइन", 4, List.of()),
                option("6H (Modified dose) — Isoniazid daily for 6 months with dose adjustment for weight or age",
                        "6H (संशोधित खुराक) — वजन या आयु के अनुसार खुराक समायोजन के साथ 6 महीने के लिए प्रतिदिन आइसोनियाज़िड",
                        "6H_MODIFIED_DOSE",
                        "6H (संशोधित खुराक) — वजन या आयु के अनुसार खुराक समायोजन के साथ 6 महीने के लिए प्रतिदिन आइसोनियाज़िड",
                        5, List.of())));
        regimen.setValidations(List.of());
        qs.add(regimen);

        qs.add(dateQuestionMaxToday("TFU_START_DATE", "TPT start date", "टीपीटी शुरू होने की तारीख", 3, true));

        // Auto-calculated in principle (regimen start + duration) — no computation engine exists
        // here, so this is a plain date field the client is expected to fill in.
        qs.add(dateQuestionMaxToday("TFU_EXPECTED_COMPLETION_DATE", "TPT expected completion date",
                "टीपीटी पूर्ण होने की अपेक्षित तारीख", 4, true));

        s.setQuestions(qs);
        return s;
    }

    // ── Section 2: Follow-up Visit (POST_SUBMIT, repeatable) ─────────────────────────────────────

    private FormSectionDTO buildFollowUpVisitSection() {
        FormSectionDTO s = section("TFU_SEC_2", "Follow-up Visit", "फॉलो-अप विज़िट",
                SectionPhase.POST_SUBMIT, 2, true, true, true);
        List<SectionQuestionDTO> qs = new ArrayList<>();

        qs.add(dateQuestionMaxToday("TFU_VISIT_DATE", "Follow-up visit date", "फॉलो-अप विज़िट की तारीख", 1, true));

        // TPT outcome status — "Completed"/"Died during TPT" reveal further fields below.
        // "Developed active TB during TPT" is meant to create a new TB Presumptive Case record and
        // close this TPT case — that case-management write is out of scope here (see class javadoc).
        SectionQuestionDTO outcomeStatus = new SectionQuestionDTO();
        outcomeStatus.setQuestionUuid("TFU_OUTCOME_STATUS");
        outcomeStatus.setQuestionText("TPT outcome status");
        outcomeStatus.setQuestionTextHindi("टीपीटी परिणाम की स्थिति");
        outcomeStatus.setQuestionType(QuestionType.RADIO);
        outcomeStatus.setIsMandatory(true);
        outcomeStatus.setDisplayOrder(2);
        outcomeStatus.setVisibleByDefault(true);
        outcomeStatus.setOptions(List.of(
                option("Completed", "पूर्ण", "COMPLETED", "पूर्ण", 1,
                        List.of(showQuestion("TFU_OUTCOME_DATE"))),
                option("Lost to follow-up", "फॉलो-अप में खोया", "LOST_TO_FOLLOW_UP", "फॉलो-अप में खोया", 2,
                        List.of()),
                option("Developed active TB during TPT", "टीपीटी के दौरान सक्रिय टीबी विकसित हुई",
                        "DEVELOPED_ACTIVE_TB", "टीपीटी के दौरान सक्रिय टीबी विकसित हुई", 3, List.of()),
                option("Died during TPT", "टीपीटी के दौरान मृत्यु", "DIED_DURING_TPT", "टीपीटी के दौरान मृत्यु", 4,
                        List.of(showQuestion("TFU_OUTCOME_DATE"), showQuestion("TFU_CAUSE_OF_DEATH"))),
                option("Other", "अन्य", "OTHER", "अन्य", 5,
                        List.of(showQuestion("TFU_OUTCOME_OTHER")))));
        outcomeStatus.setValidations(List.of());
        qs.add(outcomeStatus);

        qs.add(mandatoryIfDateQuestion("TFU_OUTCOME_DATE", "Date of TPT outcome", "टीपीटी परिणाम की तारीख", 3,
                List.of("TFU_OUTCOME_STATUS=COMPLETED", "TFU_OUTCOME_STATUS=DIED_DURING_TPT")));

        qs.add(mandatoryIfQuestion("TFU_CAUSE_OF_DEATH", "Cause of death", "मृत्यु का कारण", 4, 200,
                List.of("TFU_OUTCOME_STATUS=DIED_DURING_TPT")));

        qs.add(mandatoryIfQuestion("TFU_OUTCOME_OTHER", "Other", "अन्य", 5, 200,
                List.of("TFU_OUTCOME_STATUS=OTHER")));

        qs.add(textQuestionMaxLength("TFU_REMARKS",
                "Any other significant Information (e.g. missed doses, side effects, counselling notes)",
                "कोई अन्य महत्वपूर्ण जानकारी (जैसे छूटी हुई खुराक, दुष्प्रभाव, परामर्श नोट्स)",
                6, false, 3000, true));

        SectionQuestionDTO adherence = new SectionQuestionDTO();
        adherence.setQuestionUuid("TFU_ADHERENCE");
        adherence.setQuestionText("Adherence to Medicines");
        adherence.setQuestionTextHindi("दवाओं का पालन");
        adherence.setQuestionType(QuestionType.RADIO);
        adherence.setIsMandatory(true);
        adherence.setDisplayOrder(7);
        adherence.setVisibleByDefault(true);
        adherence.setOptions(List.of(
                option("Regular", "नियमित", "REGULAR", "नियमित", 1, List.of()),
                option("Irregular", "अनियमित", "IRREGULAR", "अनियमित", 2, List.of())));
        adherence.setValidations(List.of());
        qs.add(adherence);

        // Any discomfort — "Yes" reveals the Adverse Drug Reactions question below.
        SectionQuestionDTO discomfort = new SectionQuestionDTO();
        discomfort.setQuestionUuid("TFU_DISCOMFORT");
        discomfort.setQuestionText("Any discomfort");
        discomfort.setQuestionTextHindi("कोई असुविधा");
        discomfort.setQuestionType(QuestionType.RADIO);
        discomfort.setIsMandatory(true);
        discomfort.setDisplayOrder(8);
        discomfort.setVisibleByDefault(true);
        discomfort.setOptions(List.of(
                option("Yes", "हाँ", "YES", "हाँ", 1, List.of()),
                option("No", "नहीं", "NO", "नहीं", 2, List.of())));
        discomfort.setValidations(List.of());
        qs.add(discomfort);

        // Adverse Drug Reactions — hidden by default, mandatory only when discomfort was reported.
        SectionQuestionDTO adr = new SectionQuestionDTO();
        adr.setQuestionUuid("TFU_ADR");
        adr.setQuestionText("Adverse Drug Reactions");
        adr.setQuestionTextHindi("प्रतिकूल दवा प्रतिक्रियाएं");
        adr.setQuestionType(QuestionType.DROPDOWN);
        adr.setIsMandatory(false);
        adr.setDisplayOrder(9);
        adr.setVisibleByDefault(true);
        adr.setDefaultValue("NONE_REPORTED");
        adr.setOptions(List.of(
                option("Nausea", "", "NAUSEA", "", 1, List.of()),
                option("Vomiting", "", "VOMITING", "", 2, List.of()),
                option("Abdominal Pain", "", "ABDOMINAL_PAIN", "", 3, List.of()),
                option("Flue like syndrome- Chills, Dry cough, Shortness of breath, loss of appetite, body ache and malaise, dizziness, headache",
                        "", "FLU_LIKE_SYNDROME_WITH_DIZZINESS_HEADACHE", "", 4, List.of()),
                option("Yellowish discoloration of skin and eyes", "", "YELLOWISH_DISCOLORATION_SKIN_EYES", "", 5, List.of()),
                option("Discoloration of body fluids", "", "DISCOLORATION_BODY_FLUIDS", "", 6, List.of()),
                option("Dark colour Urine", "", "DARK_COLOUR_URINE", "", 7, List.of()),
                option("Pale stool", "", "PALE_STOOL", "", 8, List.of()),
                option("Skin Rash", "", "SKIN_RASH", "", 9, List.of()),
                option("Itching", "", "ITCHING", "", 10, List.of()),
                option("Tingling/burning/numbness in hands and feet", "", "TINGLING_BURNING_NUMBNESS_HANDS_FEET", "", 11, List.of()),
                option("Flue like syndrome- Chills, Dry cough, Shortness of breath, loss of appetite, body ache and malaise",
                        "", "FLU_LIKE_SYNDROME", "", 12, List.of()),
                option("Sleepiness, lethargy", "", "SLEEPINESS_LETHARGY", "", 13, List.of()),
                option("Persistent episodes of unformed watery stools", "", "PERSISTENT_UNFORMED_WATERY_STOOLS", "", 14, List.of()),
                option("Mental changes and signs of bleeding", "", "MENTAL_CHANGES_SIGNS_BLEEDING", "", 15, List.of()),
                option("Convulsions", "", "CONVULSIONS", "", 16, List.of()),
                option("Anaemia", "", "ANAEMIA", "", 17, List.of()),
                option("Arthralgia", "", "ARTHRALGIA", "", 18, List.of()),
                option("Decreased appetite", "", "DECREASED_APPETITE", "", 19, List.of()),
                option("Hypotension / Syncope", "", "HYPOTENSION_SYNCOPE", "", 20, List.of()),
                option("Conjuctivities", "", "CONJUCTIVITIES", "", 21, List.of()),
                option("Shock", "", "SHOCK", "", 22, List.of()),
                option("None Reported", "", "NONE_REPORTED", "", 23, List.of())));
        adr.setValidations(List.of());
        qs.add(adr);

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

    private SectionQuestionDTO dateQuestionMaxToday(String uuid, String text, String textHindi, int order,
                                                     boolean mandatory) {
        SectionQuestionDTO q = new SectionQuestionDTO();
        q.setQuestionUuid(uuid);
        q.setQuestionText(text);
        q.setQuestionTextHindi(textHindi);
        q.setQuestionType(QuestionType.DATE);
        q.setIsMandatory(mandatory);
        q.setDisplayOrder(order);
        q.setVisibleByDefault(true);
        q.setOptions(List.of());

        QuestionValidationDTO maxDate = new QuestionValidationDTO();
        maxDate.setValidationType(ValidationType.MAX_DATE);
        maxDate.setValidationParam("TODAY");
        maxDate.setErrorMessage("Cannot be a future date");
        q.setValidations(List.of(maxDate));
        return q;
    }

    private SectionQuestionDTO textQuestionMaxLength(String uuid, String text, String textHindi, int order,
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

    /** Hidden-by-default free-text question that becomes mandatory when ANY of {@code mandatoryIfParams}
     *  ("QUESTION_UUID=OPTION_VALUE" per entry) is satisfied. Always carries a MAX_LENGTH validation too. */
    private SectionQuestionDTO mandatoryIfQuestion(String uuid, String text, String textHindi, int order,
                                                    int maxLength, List<String> mandatoryIfParams) {
        SectionQuestionDTO q = new SectionQuestionDTO();
        q.setQuestionUuid(uuid);
        q.setQuestionText(text);
        q.setQuestionTextHindi(textHindi);
        q.setQuestionType(QuestionType.TEXT);
        q.setIsMandatory(false);
        q.setDisplayOrder(order);
        q.setMaxLength(maxLength);
        q.setVisibleByDefault(false);
        q.setOptions(List.of());

        List<QuestionValidationDTO> validations = new ArrayList<>();
        QuestionValidationDTO maxLen = new QuestionValidationDTO();
        maxLen.setValidationType(ValidationType.MAX_LENGTH);
        maxLen.setValidationParam(String.valueOf(maxLength));
        maxLen.setErrorMessage("Must be " + maxLength + " characters or fewer");
        validations.add(maxLen);

        for (String param : mandatoryIfParams) {
            QuestionValidationDTO mandatoryIf = new QuestionValidationDTO();
            mandatoryIf.setValidationType(ValidationType.MANDATORY_IF);
            mandatoryIf.setValidationParam(param);
            mandatoryIf.setErrorMessage("This field is mandatory");
            validations.add(mandatoryIf);
        }

        q.setValidations(validations);
        return q;
    }

    /** Hidden-by-default date question that becomes mandatory when ANY of {@code mandatoryIfParams}
     *  ("QUESTION_UUID=OPTION_VALUE" per entry) is satisfied. Carries a MAX_DATE=TODAY validation too. */
    private SectionQuestionDTO mandatoryIfDateQuestion(String uuid, String text, String textHindi, int order,
                                                        List<String> mandatoryIfParams) {
        SectionQuestionDTO q = new SectionQuestionDTO();
        q.setQuestionUuid(uuid);
        q.setQuestionText(text);
        q.setQuestionTextHindi(textHindi);
        q.setQuestionType(QuestionType.DATE);
        q.setIsMandatory(false);
        q.setDisplayOrder(order);
        q.setVisibleByDefault(false);
        q.setOptions(List.of());

        List<QuestionValidationDTO> validations = new ArrayList<>();
        QuestionValidationDTO maxDate = new QuestionValidationDTO();
        maxDate.setValidationType(ValidationType.MAX_DATE);
        maxDate.setValidationParam("TODAY");
        maxDate.setErrorMessage("Cannot be a future date");
        validations.add(maxDate);

        for (String param : mandatoryIfParams) {
            QuestionValidationDTO mandatoryIf = new QuestionValidationDTO();
            mandatoryIf.setValidationType(ValidationType.MANDATORY_IF);
            mandatoryIf.setValidationParam(param);
            mandatoryIf.setErrorMessage("This field is mandatory");
            validations.add(mandatoryIf);
        }

        q.setValidations(validations);
        return q;
    }

    private QuestionOptionDTO option(String label, String labelHindi, String value, String valueHindi,
                                      int order, List<OptionConditionDTO> conditions) {
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
}
