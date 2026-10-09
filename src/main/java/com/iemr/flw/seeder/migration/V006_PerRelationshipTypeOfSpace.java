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
import com.iemr.flw.service.DynamicFormReconciliationService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Replaces the Community Contact Tracing form's single, standalone "Type of Space" question
 * (CCT_TYPE_OF_SPACE) with one per "Relationship with community contacts" (CCT_RELATIONSHIP)
 * option, each followed by its own "Approximate Area of Shared Space", so the exposure setting is
 * captured per relationship type:
 *
 * <pre>
 * CCT_RELATIONSHIP=&lt;R&gt; ─ SHOW_QUESTION ─► CCT_TYPE_OF_SPACE_&lt;R&gt;
 *                                          └─ each option ─ SHOW_QUESTION ─► CCT_AREA_OF_SHARED_SPACE_&lt;R&gt;
 * </pre>
 *
 * The old CCT_TYPE_OF_SPACE is soft-removed (never hard-deleted — historical responses may
 * reference it). The 14 new questions (a Type of Space / Area pair per relationship) are inserted
 * into its slot, all hidden by default and mandatory only when revealed (MANDATORY_IF). Area
 * questions follow V006's conventions: helper text in {@code defaultValue}, unit as a
 * {@link ValidationType#UNIT} validation, whole square metres 50–5000.
 */
@Component
public class V006_PerRelationshipTypeOfSpace implements FormStructureMigration {

    private static final List<String> RELATIONSHIP_VALUES = List.of(
            "NEIGHBOUR",
            "FRIEND",
            "FELLOW_WORSHIPPER",
            "FELLOW_COMMUTER",
            "COMMUNITY_GROUP_MEMBER",
            "FELLOW_PATIENT",
            "OTHER");

    /** {label, labelHindi, value} — same options as the retired CCT_TYPE_OF_SPACE. */
    private static final List<String[]> TYPE_OF_SPACE_OPTIONS = List.of(
            new String[] {"Open space", "खुला स्थान", "OPEN_SPACE"},
            new String[] {"Closed space with ventilation", "वेंटिलेशन के साथ बंद स्थान", "CLOSED_SPACE_VENTILATED"},
            new String[] {"Closed space with no ventilation", "बिना वेंटिलेशन के बंद स्थान", "CLOSED_SPACE_NOT_VENTILATED"});

    private static final String AREA_REGEX = "^([5-9][0-9]|[1-9][0-9]{2}|[1-4][0-9]{3}|5000)$";
    private static final String MANDATORY_ERROR = "This field is mandatory";

    @Override
    public String migrationId() {
        return "V006";
    }

    @Override
    public void apply(DynamicFormReconciliationService svc) {
        FormVersion version = svc.latestVersion("COMMUNITY_CONTACT_TRACING")
                .orElseThrow(() -> new IllegalStateException(
                        "COMMUNITY_CONTACT_TRACING not found — must run after CommunityContactTracingFormSeeder"));
        FormSection section = svc.section(version, "CCT_SEC_1")
                .orElseThrow(() -> new IllegalStateException("Section CCT_SEC_1 not found"));
        SectionQuestion relationship = svc.question(section, "CCT_RELATIONSHIP")
                .orElseThrow(() -> new IllegalStateException("Question CCT_RELATIONSHIP not found"));
        SectionQuestion oldTypeOfSpace = svc.question(section, "CCT_TYPE_OF_SPACE")
                .orElseThrow(() -> new IllegalStateException("Question CCT_TYPE_OF_SPACE not found"));

        int base = oldTypeOfSpace.getDisplayOrder();
        if (Boolean.TRUE.equals(oldTypeOfSpace.getIsActive())) {
            svc.unlinkQuestion(oldTypeOfSpace.getQuestionId());
        }

        for (int i = 0; i < RELATIONSHIP_VALUES.size(); i++) {
            String r = RELATIONSHIP_VALUES.get(i);
            String typeOfSpaceUuid = "CCT_TYPE_OF_SPACE_" + r;
            String areaUuid = "CCT_AREA_OF_SHARED_SPACE_" + r;

            // Type of Space for this relationship — revealed by CCT_RELATIONSHIP=<r>
            SectionQuestion typeOfSpace = svc.ensureQuestion(section, question(typeOfSpaceUuid,
                    "Type of Space", "स्थान का प्रकार", QuestionType.RADIO, null, base + 1 + 2 * i));
            svc.ensureValidation(typeOfSpace, validation(ValidationType.MANDATORY_IF,
                    "CCT_RELATIONSHIP=" + r, MANDATORY_ERROR));
            QuestionOption relationshipOption = svc.option(relationship, r)
                    .orElseThrow(() -> new IllegalStateException("Option CCT_RELATIONSHIP=" + r + " not found"));
            svc.ensureCondition(relationshipOption, showQuestion(typeOfSpaceUuid), version);

            // Approximate Area of Shared Space for this relationship — revealed by any of its Type of Space options
            SectionQuestion area = svc.ensureQuestion(section, question(areaUuid,
                    "Approximate Area of Shared Space", "साझा स्थान का अनुमानित क्षेत्रफल", QuestionType.NUMBER_PICKER,
                    "Give estimated measurement of area", base + 2 + 2 * i));
            svc.ensureValidation(area, validation(ValidationType.REGEX, AREA_REGEX, "Enter a value between 50 and 5000."));
            svc.ensureValidation(area, validation(ValidationType.UNIT, "sq. m.", "Value is in sq. m."));

            for (int j = 0; j < TYPE_OF_SPACE_OPTIONS.size(); j++) {
                String[] o = TYPE_OF_SPACE_OPTIONS.get(j);
                QuestionOption spaceOption = svc.ensureOption(typeOfSpace, option(o[0], o[1], o[2], j + 1));
                svc.ensureValidation(area, validation(ValidationType.MANDATORY_IF,
                        typeOfSpaceUuid + "=" + o[2], MANDATORY_ERROR));
                svc.ensureCondition(spaceOption, showQuestion(areaUuid), version);
            }
        }

    }

    private SectionQuestionDTO question(String uuid, String text, String textHindi, QuestionType type,
            String defaultValue, int order) {
        SectionQuestionDTO q = new SectionQuestionDTO();
        q.setQuestionUuid(uuid);
        q.setQuestionText(text);
        q.setQuestionTextHindi(textHindi);
        q.setQuestionType(type);
        q.setDefaultValue(defaultValue);
        q.setIsMandatory(true);
        q.setDisplayOrder(order);
        q.setVisibleByDefault(false);
        q.setOptions(List.of());
        q.setValidations(List.of());
        return q;
    }

    private QuestionOptionDTO option(String label, String labelHindi, String value, int order) {
        QuestionOptionDTO o = new QuestionOptionDTO();
        o.setOptionLabel(label);
        o.setOptionLabelHindi(labelHindi);
        o.setOptionValue(value);
        o.setOptionValueHindi(labelHindi);
        o.setDisplayOrder(order);
        o.setConditions(List.of());
        return o;
    }

    private QuestionValidationDTO validation(ValidationType type, String param, String errorMessage) {
        QuestionValidationDTO v = new QuestionValidationDTO();
        v.setValidationType(type);
        v.setValidationParam(param);
        v.setErrorMessage(errorMessage);
        return v;
    }

    private OptionConditionDTO showQuestion(String targetQuestionUuid) {
        OptionConditionDTO c = new OptionConditionDTO();
        c.setActionType("SHOW_QUESTION");
        c.setTargetQuestionUuid(targetQuestionUuid);
        return c;
    }
}
