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
 * Seeds the Occupational Contact Tracing form definition on application startup if it does not
 * already exist. Idempotent: skips creation if the form UUID is already present in the database.
 *
 * One response per beneficiary, same as TB Counselling — the "Type of Contact tracing initiated"
 * (Community / Occupational) choice is a client-side picker deciding whether this form or
 * {@link CommunityContactTracingFormSeeder}'s form is opened; it is not itself a stored question.
 *
 * @author Piramal Swasthya
 */
@Component
@RequiredArgsConstructor
public class OccupationalContactTracingFormSeeder {

    private static final Logger log = LoggerFactory.getLogger(OccupationalContactTracingFormSeeder.class);

    public static final String FORM_UUID = "OCCUPATION_CONTACT_TRACING";

    private final DynamicFormDefinitionService formService;
    private final DynamicFormRepo formRepo;

    @PostConstruct
    public void seed() {
        if (formRepo.findByFormUuid(FORM_UUID).isPresent()) {
            log.info("OccupationalContactTracingFormSeeder: form '{}' already exists — skipping seed.", FORM_UUID);
            return;
        }
        log.info("OccupationalContactTracingFormSeeder: seeding Occupational Contact Tracing form...");
        formService.createForm(buildFormDto());
        log.info("OccupationalContactTracingFormSeeder: seed complete.");
    }

    // ── Form Definition ──────────────────────────────────────────────────────────

    private DynamicFormDTO buildFormDto() {
        DynamicFormDTO dto = new DynamicFormDTO();
        dto.setFormUuid(FORM_UUID);
        dto.setFormName("Occupational Contact Tracing");
        dto.setFormType(FormType.OCCUPATION_CONTACT_TRACING);
        dto.setIsActive(true);

        List<FormSectionDTO> sections = new ArrayList<>();
        sections.add(buildOccupationAndExposureSection());
        dto.setSections(sections);
        return dto;
    }

    // ── Section: Occupation & Exposure Details ───────────────────────────────────

    private FormSectionDTO buildOccupationAndExposureSection() {
        FormSectionDTO s = section("OCT_SEC_1", "Occupation & Exposure Details", "व्यवसाय और एक्सपोज़र विवरण",
                SectionPhase.PRE_SUBMIT, 1, true, true, true);
        List<SectionQuestionDTO> qs = new ArrayList<>();

        // Occupation of confirmed case — single-select, no default. Several options reveal
        // downstream address questions; "Other occupation" reveals its own free-text field.
        SectionQuestionDTO occupation = new SectionQuestionDTO();
        occupation.setQuestionUuid("OCT_OCCUPATION");
        occupation.setQuestionText("Occupation");
        occupation.setQuestionTextHindi("व्यवसाय");
        occupation.setQuestionType(QuestionType.RADIO);
        occupation.setIsMandatory(true);
        occupation.setDisplayOrder(1);
        occupation.setVisibleByDefault(true);
        List<QuestionOptionDTO> occupationOptions = new ArrayList<>();
        occupationOptions.add(option("Student", "छात्र", "STUDENT", "छात्र", 1,
                List.of(showQuestion("OCT_INSTITUTION_ADDRESS"))));
        occupationOptions.add(option("Homemaker", "गृहिणी", "HOMEMAKER", "गृहिणी", 2, List.of()));
        occupationOptions.add(option("Farmer", "किसान", "FARMER", "किसान", 3, List.of()));
        occupationOptions.add(option("Laborer / Daily Wage Worker", "मजदूर / दैनिक वेतन भोगी कर्मचारी",
                "LABORER_DAILY_WAGE", "मजदूर / दैनिक वेतन भोगी कर्मचारी", 4,
                List.of(showQuestion("OCT_EMPLOYMENT_ADDRESS"))));
        occupationOptions.add(option("Self-employed / Business", "स्वरोजगार / व्यवसाय",
                "SELF_EMPLOYED_BUSINESS", "स्वरोजगार / व्यवसाय", 5,
                List.of(showQuestion("OCT_EMPLOYMENT_ADDRESS"))));
        occupationOptions.add(option("Government Employee", "सरकारी कर्मचारी",
                "GOVERNMENT_EMPLOYEE", "सरकारी कर्मचारी", 6,
                List.of(showQuestion("OCT_EMPLOYMENT_ADDRESS"))));
        occupationOptions.add(option("Private Employee", "निजी कर्मचारी",
                "PRIVATE_EMPLOYEE", "निजी कर्मचारी", 7,
                List.of(showQuestion("OCT_EMPLOYMENT_ADDRESS"))));
        occupationOptions.add(option("Health Care Worker", "स्वास्थ्य कर्मी",
                "HEALTH_CARE_WORKER", "स्वास्थ्य कर्मी", 8,
                List.of(showQuestion("OCT_EMPLOYMENT_ADDRESS"))));
        occupationOptions.add(option("Retired Person", "सेवानिवृत्त व्यक्ति", "RETIRED_PERSON", "सेवानिवृत्त व्यक्ति", 9, List.of()));
        occupationOptions.add(option("Priest", "पुजारी", "PRIEST", "पुजारी", 10, List.of()));
        occupationOptions.add(option("Other occupation", "अन्य व्यवसाय", "OTHER_OCCUPATION", "अन्य व्यवसाय", 11,
                List.of(showQuestion("OCT_OCCUPATION_OTHER"))));
        occupation.setOptions(occupationOptions);
        occupation.setValidations(List.of());
        qs.add(occupation);

        // Other occupation — hidden by default, mandatory only when "Other occupation" is selected
        qs.add(mandatoryIfQuestion("OCT_OCCUPATION_OTHER", "Other occupation", "अन्य व्यवसाय", 2, 500,
                List.of("OCT_OCCUPATION=OTHER_OCCUPATION")));

        // Name and Address of Employment — mandatory when occupation is any of the 5 employment types
        SectionQuestionDTO employmentAddress = mandatoryIfQuestion("OCT_EMPLOYMENT_ADDRESS", "Name and Address of Employment",
                "रोजगार का नाम और पता", 3, 500,
                List.of("OCT_OCCUPATION=LABORER_DAILY_WAGE",
                        "OCT_OCCUPATION=SELF_EMPLOYED_BUSINESS",
                        "OCT_OCCUPATION=GOVERNMENT_EMPLOYEE",
                        "OCT_OCCUPATION=PRIVATE_EMPLOYEE",
                        "OCT_OCCUPATION=HEALTH_CARE_WORKER"));
        employmentAddress.setIsMandatory(true);
        qs.add(employmentAddress);

        // Name and Address of Institution — mandatory only when occupation = Student
        qs.add(mandatoryIfQuestion("OCT_INSTITUTION_ADDRESS", "Name and Address of Institution",
                "संस्थान का नाम और पता", 4, 500,
                List.of("OCT_OCCUPATION=STUDENT")));

        qs.add(textQuestionRegex("OCT_NO_OF_CONTACTS", "No. of contacts", "संपर्कों की संख्या", 5, true,
                "^([1-9][0-9]{0,2}|1000)$", "Enter a whole number between 1 and 1000", true));

        SectionQuestionDTO typeOfSpace = new SectionQuestionDTO();
        typeOfSpace.setQuestionUuid("OCT_TYPE_OF_SPACE");
        typeOfSpace.setQuestionText("Type of Space");
        typeOfSpace.setQuestionTextHindi("स्थान का प्रकार");
        typeOfSpace.setQuestionType(QuestionType.RADIO);
        typeOfSpace.setIsMandatory(true);
        typeOfSpace.setDisplayOrder(6);
        typeOfSpace.setVisibleByDefault(true);
        typeOfSpace.setOptions(List.of(
                option("Open space", "खुला स्थान", "OPEN_SPACE", "खुला स्थान", 1, List.of()),
                option("Closed space with ventilation", "वेंटिलेशन के साथ बंद स्थान",
                        "CLOSED_SPACE_VENTILATED", "वेंटिलेशन के साथ बंद स्थान", 2, List.of()),
                option("Closed space with no ventilation", "बिना वेंटिलेशन के बंद स्थान",
                        "CLOSED_SPACE_NOT_VENTILATED", "बिना वेंटिलेशन के बंद स्थान", 3, List.of())));
        typeOfSpace.setValidations(List.of());
        qs.add(typeOfSpace);

        qs.add(numberPickerQuestionRegex("OCT_DURATION_HOURS", "Daily duration of contact", "संपर्क की दैनिक अवधि", 7, true,
                "^([0-9]|1[0-9]|2[0-4])$", "Enter whole hours between 0 and 24", true));

        qs.add(textQuestionMaxLength("OCT_REMARKS", "Any other Significant Information/Notes/Remarks",
                "कोई अन्य महत्वपूर्ण जानकारी/टिप्पणी", 8, false, 3000, true));

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

    private SectionQuestionDTO textQuestionRegex(String uuid, String text, String textHindi, int order,
                                                  boolean mandatory, String regex, String errorMessage, boolean visible) {
        SectionQuestionDTO q = new SectionQuestionDTO();
        q.setQuestionUuid(uuid);
        q.setQuestionText(text);
        q.setQuestionTextHindi(textHindi);
        q.setQuestionType(QuestionType.TEXT);
        q.setIsMandatory(mandatory);
        q.setDisplayOrder(order);
        q.setVisibleByDefault(visible);
        q.setOptions(List.of());

        QuestionValidationDTO v = new QuestionValidationDTO();
        v.setValidationType(ValidationType.REGEX);
        v.setValidationParam(regex);
        v.setErrorMessage(errorMessage);
        q.setValidations(List.of(v));
        return q;
    }

    private SectionQuestionDTO numberPickerQuestionRegex(String uuid, String text, String textHindi, int order,
                                                          boolean mandatory, String regex, String errorMessage, boolean visible) {
        SectionQuestionDTO q = new SectionQuestionDTO();
        q.setQuestionUuid(uuid);
        q.setQuestionText(text);
        q.setQuestionTextHindi(textHindi);
        q.setQuestionType(QuestionType.NUMBER_PICKER);
        q.setIsMandatory(mandatory);
        q.setDisplayOrder(order);
        q.setVisibleByDefault(visible);
        q.setOptions(List.of());

        QuestionValidationDTO v = new QuestionValidationDTO();
        v.setValidationType(ValidationType.REGEX);
        v.setValidationParam(regex);
        v.setErrorMessage(errorMessage);
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
