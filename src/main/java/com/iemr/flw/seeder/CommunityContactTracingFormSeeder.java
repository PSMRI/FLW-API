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
 * Seeds the Community Contact Tracing form definition on application startup if it does not
 * already exist. Idempotent: skips creation if the form UUID is already present in the database.
 *
 * One response per beneficiary, same as TB Counselling — the "Type of Contact tracing initiated"
 * (Community / Occupational) choice is a client-side picker deciding whether this form or
 * {@link OccupationalContactTracingFormSeeder}'s form is opened; it is not itself a stored question.
 *
 * @author Piramal Swasthya
 */
@Component
@RequiredArgsConstructor
public class CommunityContactTracingFormSeeder {

    private static final Logger log = LoggerFactory.getLogger(CommunityContactTracingFormSeeder.class);

    public static final String FORM_UUID = "COMMUNITY_CONTACT_TRACING";

    private final DynamicFormDefinitionService formService;
    private final DynamicFormRepo formRepo;

    @PostConstruct
    public void seed() {
        if (formRepo.findByFormUuid(FORM_UUID).isPresent()) {
            log.info("CommunityContactTracingFormSeeder: form '{}' already exists — skipping seed.", FORM_UUID);
            return;
        }
        log.info("CommunityContactTracingFormSeeder: seeding Community Contact Tracing form...");
        formService.createForm(buildFormDto());
        log.info("CommunityContactTracingFormSeeder: seed complete.");
    }

    // ── Form Definition ──────────────────────────────────────────────────────────

    private DynamicFormDTO buildFormDto() {
        DynamicFormDTO dto = new DynamicFormDTO();
        dto.setFormUuid(FORM_UUID);
        dto.setFormName("Community Contact Tracing");
        dto.setFormType(FormType.COMMUNITY_CONTACT_TRACING);
        dto.setIsActive(true);

        List<FormSectionDTO> sections = new ArrayList<>();
        sections.add(buildContactAndExposureSection());
        dto.setSections(sections);
        return dto;
    }

    // ── Section: Contact & Exposure Details ──────────────────────────────────────

    private FormSectionDTO buildContactAndExposureSection() {
        FormSectionDTO s = section("CCT_SEC_1", "Contact & Exposure Details", "संपर्क और एक्सपोज़र विवरण",
                SectionPhase.PRE_SUBMIT, 1, true, true, true);
        List<SectionQuestionDTO> qs = new ArrayList<>();

        qs.add(textQuestionRegex("CCT_NO_OF_CONTACTS", "No. of contacts", "संपर्कों की संख्या", 1, true,
                "^([1-9][0-9]{0,2}|1000)$", "Enter a whole number between 1 and 1000", true));

        // Relationship of community contacts — multi-select; "Other" reveals a free-text field
        SectionQuestionDTO relationship = new SectionQuestionDTO();
        relationship.setQuestionUuid("CCT_RELATIONSHIP");
        relationship.setQuestionText("Relationship with community contacts");
        relationship.setQuestionTextHindi("सामुदायिक संपर्कों के साथ संबंध");
        relationship.setQuestionType(QuestionType.CHECKBOX_MULTI);
        relationship.setIsMandatory(true);
        relationship.setDisplayOrder(2);
        relationship.setVisibleByDefault(true);
        List<QuestionOptionDTO> relationshipOptions = new ArrayList<>();
        relationshipOptions.add(option("Neighbour", "पड़ोसी", "NEIGHBOUR", "पड़ोसी", 1,
                List.of(showQuestion("CCT_RELATIONSHIP_COUNT_NEIGHBOUR"))));
        relationshipOptions.add(option("Friend", "दोस्त", "FRIEND", "दोस्त", 2,
                List.of(showQuestion("CCT_RELATIONSHIP_COUNT_FRIEND"))));
        relationshipOptions.add(option("Fellow worshipper (temple/mosque/church)", "साथी उपासक (मंदिर/मस्जिद/चर्च)",
                "FELLOW_WORSHIPPER", "साथी उपासक (मंदिर/मस्जिद/चर्च)", 3,
                List.of(showQuestion("CCT_RELATIONSHIP_COUNT_FELLOW_WORSHIPPER"))));
        relationshipOptions.add(option("Fellow commuter", "साथी यात्री", "FELLOW_COMMUTER", "साथी यात्री", 4,
                List.of(showQuestion("CCT_RELATIONSHIP_COUNT_FELLOW_COMMUTER"))));
        relationshipOptions.add(option("Community group member", "सामुदायिक समूह सदस्य",
                "COMMUNITY_GROUP_MEMBER", "सामुदायिक समूह सदस्य", 5,
                List.of(showQuestion("CCT_RELATIONSHIP_COUNT_COMMUNITY_GROUP_MEMBER"))));
        relationshipOptions.add(option("Fellow patient (clinic/hospital)", "साथी मरीज़ (क्लिनिक/अस्पताल)",
                "FELLOW_PATIENT", "साथी मरीज़ (क्लिनिक/अस्पताल)", 6,
                List.of(showQuestion("CCT_RELATIONSHIP_COUNT_FELLOW_PATIENT"))));
        relationshipOptions.add(option("Other", "अन्य", "OTHER", "अन्य", 7, List.of(
                showQuestion("CCT_RELATIONSHIP_OTHER"),
                showQuestion("CCT_RELATIONSHIP_COUNT_OTHER"))));
        relationship.setOptions(relationshipOptions);
        relationship.setValidations(List.of());
        qs.add(relationship);

        // Other (relationship) — hidden by default, mandatory only when "Other" is selected above
        qs.add(mandatoryIfQuestion("CCT_RELATIONSHIP_OTHER", "Other", "अन्य", 3, 100,
                List.of("CCT_RELATIONSHIP=OTHER")));

        // Per-relationship contact counts — each hidden by default, shown/mandatory only when its
        // matching CCT_RELATIONSHIP option is selected. Kept separate per option (rather than one
        // shared count question) since CCT_RELATIONSHIP is multi-select and a shared field couldn't
        // disambiguate which count belongs to which relationship type.
        qs.add(mandatoryIfNumericQuestion("CCT_RELATIONSHIP_COUNT_NEIGHBOUR", "Number of neighbours", "पड़ोसियों की संख्या", 4,
                "^([1-9][0-9]{0,2}|1000)$", "Enter a whole number between 1 and 1000", "CCT_RELATIONSHIP=NEIGHBOUR"));
        qs.add(mandatoryIfNumericQuestion("CCT_RELATIONSHIP_COUNT_FRIEND", "Number of friends", "दोस्तों की संख्या", 5,
                "^([1-9][0-9]{0,2}|1000)$", "Enter a whole number between 1 and 1000", "CCT_RELATIONSHIP=FRIEND"));
        qs.add(mandatoryIfNumericQuestion("CCT_RELATIONSHIP_COUNT_FELLOW_WORSHIPPER", "Number of fellow worshippers", "साथी उपासकों की संख्या", 6,
                "^([1-9][0-9]{0,2}|1000)$", "Enter a whole number between 1 and 1000", "CCT_RELATIONSHIP=FELLOW_WORSHIPPER"));
        qs.add(mandatoryIfNumericQuestion("CCT_RELATIONSHIP_COUNT_FELLOW_COMMUTER", "Number of fellow commuters", "साथी यात्रियों की संख्या", 7,
                "^([1-9][0-9]{0,2}|1000)$", "Enter a whole number between 1 and 1000", "CCT_RELATIONSHIP=FELLOW_COMMUTER"));
        qs.add(mandatoryIfNumericQuestion("CCT_RELATIONSHIP_COUNT_COMMUNITY_GROUP_MEMBER", "Number of community group members", "सामुदायिक समूह सदस्यों की संख्या", 8,
                "^([1-9][0-9]{0,2}|1000)$", "Enter a whole number between 1 and 1000", "CCT_RELATIONSHIP=COMMUNITY_GROUP_MEMBER"));
        qs.add(mandatoryIfNumericQuestion("CCT_RELATIONSHIP_COUNT_FELLOW_PATIENT", "Number of fellow patients", "साथी मरीज़ों की संख्या", 9,
                "^([1-9][0-9]{0,2}|1000)$", "Enter a whole number between 1 and 1000", "CCT_RELATIONSHIP=FELLOW_PATIENT"));
        qs.add(mandatoryIfNumericQuestion("CCT_RELATIONSHIP_COUNT_OTHER", "Number of other contacts", "अन्य संपर्कों की संख्या", 10,
                "^([1-9][0-9]{0,2}|1000)$", "Enter a whole number between 1 and 1000", "CCT_RELATIONSHIP=OTHER"));

        // Place of exposure — multi-select; "Other Place" reveals a free-text field
        SectionQuestionDTO exposureSetting = new SectionQuestionDTO();
        exposureSetting.setQuestionUuid("CCT_EXPOSURE_SETTING");
        exposureSetting.setQuestionText("Place of exposure");
        exposureSetting.setQuestionTextHindi("एक्सपोज़र का स्थान");
        exposureSetting.setQuestionType(QuestionType.CHECKBOX_MULTI);
        exposureSetting.setIsMandatory(true);
        exposureSetting.setDisplayOrder(11);
        exposureSetting.setVisibleByDefault(true);
        List<QuestionOptionDTO> exposureOptions = new ArrayList<>();
        exposureOptions.add(option("Place of worship", "पूजा स्थल", "PLACE_OF_WORSHIP", "पूजा स्थल", 1, List.of()));
        exposureOptions.add(option("Community hall", "सामुदायिक हॉल", "COMMUNITY_HALL", "सामुदायिक हॉल", 2, List.of()));
        exposureOptions.add(option("Market", "बाज़ार", "MARKET", "बाज़ार", 3, List.of()));
        exposureOptions.add(option("Water collection point", "पानी संग्रहण स्थल",
                "WATER_COLLECTION_POINT", "पानी संग्रहण स्थल", 4, List.of()));
        exposureOptions.add(option("Community meetings", "सामुदायिक बैठकें",
                "COMMUNITY_MEETINGS", "सामुदायिक बैठकें", 5, List.of()));
        exposureOptions.add(option("De-addiction centre", "नशा मुक्ति केंद्र",
                "DE_ADDICTION_CENTRE", "नशा मुक्ति केंद्र", 6, List.of()));
        exposureOptions.add(option("Shelter home", "आश्रय गृह", "SHELTER_HOME", "आश्रय गृह", 7, List.of()));
        exposureOptions.add(option("Yoga centre", "योग केंद्र", "YOGA_CENTRE", "योग केंद्र", 8, List.of()));
        exposureOptions.add(option("Gym", "जिम", "GYM", "जिम", 9, List.of()));
        exposureOptions.add(option("Clubs", "क्लब", "CLUBS", "क्लब", 10, List.of()));
        exposureOptions.add(option("Library", "पुस्तकालय", "LIBRARY", "पुस्तकालय", 11, List.of()));
        exposureOptions.add(option("Other Place", "अन्य स्थान", "OTHER_PLACE", "अन्य स्थान", 12,
                List.of(showQuestion("CCT_EXPOSURE_SETTING_OTHER"))));
        exposureSetting.setOptions(exposureOptions);
        exposureSetting.setValidations(List.of());
        qs.add(exposureSetting);

        // Other Place — hidden by default, mandatory + max 100 chars only when "Other Place" is selected
        qs.add(mandatoryIfQuestion("CCT_EXPOSURE_SETTING_OTHER", "Other Place", "अन्य स्थान", 12, 100,
                List.of("CCT_EXPOSURE_SETTING=OTHER_PLACE")));

        SectionQuestionDTO typeOfSpace = new SectionQuestionDTO();
        typeOfSpace.setQuestionUuid("CCT_TYPE_OF_SPACE");
        typeOfSpace.setQuestionText("Type of Space");
        typeOfSpace.setQuestionTextHindi("स्थान का प्रकार");
        typeOfSpace.setQuestionType(QuestionType.RADIO);
        typeOfSpace.setIsMandatory(true);
        typeOfSpace.setDisplayOrder(13);
        typeOfSpace.setVisibleByDefault(true);
        typeOfSpace.setOptions(List.of(
                option("Open space", "खुला स्थान", "OPEN_SPACE", "खुला स्थान", 1, List.of()),
                option("Closed space with ventilation", "वेंटिलेशन के साथ बंद स्थान",
                        "CLOSED_SPACE_VENTILATED", "वेंटिलेशन के साथ बंद स्थान", 2, List.of()),
                option("Closed space with no ventilation", "बिना वेंटिलेशन के बंद स्थान",
                        "CLOSED_SPACE_NOT_VENTILATED", "बिना वेंटिलेशन के बंद स्थान", 3, List.of())));
        typeOfSpace.setValidations(List.of());
        qs.add(typeOfSpace);

        qs.add(numberPickerQuestionRegex("CCT_DURATION_HOURS", "Daily duration of contact", "संपर्क की दैनिक अवधि", 14, true,
                "^([0-9]|1[0-9]|2[0-4])$", "Enter whole hours between 0 and 24", true));

        qs.add(textQuestionMaxLength("CCT_REMARKS", "Any other Significant Information/Notes/Remarks",
                "कोई अन्य महत्वपूर्ण जानकारी/टिप्पणी", 15, false, 3000, true));

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

    /** Hidden-by-default numeric question that becomes mandatory when {@code mandatoryIfParam}
     *  ("QUESTION_UUID=OPTION_VALUE") is satisfied. Carries a REGEX validation instead of MAX_LENGTH. */
    private SectionQuestionDTO mandatoryIfNumericQuestion(String uuid, String text, String textHindi, int order,
                                                           String regex, String errorMessage, String mandatoryIfParam) {
        SectionQuestionDTO q = new SectionQuestionDTO();
        q.setQuestionUuid(uuid);
        q.setQuestionText(text);
        q.setQuestionTextHindi(textHindi);
        q.setQuestionType(QuestionType.TEXT);
        q.setIsMandatory(true);
        q.setDisplayOrder(order);
        q.setVisibleByDefault(false);
        q.setOptions(List.of());

        QuestionValidationDTO regexValidation = new QuestionValidationDTO();
        regexValidation.setValidationType(ValidationType.REGEX);
        regexValidation.setValidationParam(regex);
        regexValidation.setErrorMessage(errorMessage);

        QuestionValidationDTO mandatoryIf = new QuestionValidationDTO();
        mandatoryIf.setValidationType(ValidationType.MANDATORY_IF);
        mandatoryIf.setValidationParam(mandatoryIfParam);
        mandatoryIf.setErrorMessage("This field is mandatory");

        q.setValidations(List.of(regexValidation, mandatoryIf));
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
