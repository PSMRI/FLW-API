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
import com.iemr.flw.dto.iemr.QuestionValidationDTO;
import com.iemr.flw.dto.iemr.SectionQuestionDTO;
import com.iemr.flw.masterEnum.QuestionType;
import com.iemr.flw.masterEnum.ValidationType;
import com.iemr.flw.service.DynamicFormReconciliationService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Adds an "Approximate Area of Shared Space" question to the Occupational Contact Tracing form,
 * directly after "Type of Space" (OCT_TYPE_OF_SPACE), per the revised criteria for this form:
 * enabled once a Type of Space is selected, whole square metres only, accepted range 50–5000.
 *
 * Mapped to Type of Space as a condition: hidden by default, and each of the 3 OCT_TYPE_OF_SPACE
 * options reveals it (SHOW_QUESTION) and makes it mandatory (MANDATORY_IF) — conditions attach to
 * options, so "any Type of Space selected" is one condition per option, all targeting this single
 * question. Conventions used, since questions carry no dedicated fields for these:
 * the helper text is stored in {@code defaultValue}, and the unit as a {@link ValidationType#UNIT}
 * validation whose {@code validationParam} is the unit label.
 *
 * Inserted at display order 7 — {@code ensureQuestion} shifts "Daily duration of contact" to 8 and
 * "Remarks" to 9.
 */
@Component
public class V005_AddAreaOfSharedSpaceQuestion implements FormStructureMigration {

    private static final String QUESTION_UUID = "OCT_AREA_OF_SHARED_SPACE";

    private static final List<String> TYPE_OF_SPACE_VALUES = List.of(
            "OPEN_SPACE",
            "CLOSED_SPACE_VENTILATED",
            "CLOSED_SPACE_NOT_VENTILATED");

    @Override
    public String migrationId() {
        return "V005";
    }

    @Override
    public void apply(DynamicFormReconciliationService svc) {
        FormVersion version = svc.latestVersion("OCCUPATION_CONTACT_TRACING")
                .orElseThrow(() -> new IllegalStateException(
                        "OCCUPATION_CONTACT_TRACING not found — must run after OccupationalContactTracingFormSeeder"));
        FormSection section = svc.section(version, "OCT_SEC_1")
                .orElseThrow(() -> new IllegalStateException("Section OCT_SEC_1 not found"));
        SectionQuestion typeOfSpace = svc.question(section, "OCT_TYPE_OF_SPACE")
                .orElseThrow(() -> new IllegalStateException("Question OCT_TYPE_OF_SPACE not found"));

        SectionQuestionDTO dto = new SectionQuestionDTO();
        dto.setQuestionUuid(QUESTION_UUID);
        dto.setQuestionText("Approximate Area of Shared Space");
        dto.setQuestionTextHindi("साझा स्थान का अनुमानित क्षेत्रफल");
        dto.setQuestionType(QuestionType.NUMBER_PICKER);
        dto.setDefaultValue("Give estimated measurement of area");
        dto.setIsMandatory(true);
        dto.setDisplayOrder(7);
        dto.setVisibleByDefault(false);
        dto.setOptions(List.of());
        dto.setValidations(List.of());
        SectionQuestion area = svc.ensureQuestion(section, dto);

        svc.ensureValidation(area, validation(ValidationType.REGEX,
                "^([5-9][0-9]|[1-9][0-9]{2}|[1-4][0-9]{3}|5000)$", "Enter a value between 50 and 5000."));
        svc.ensureValidation(area, validation(ValidationType.UNIT, "sq. m.", "Value is in sq. m."));

        for (String value : TYPE_OF_SPACE_VALUES) {
            svc.ensureValidation(area, validation(ValidationType.MANDATORY_IF,
                    "OCT_TYPE_OF_SPACE=" + value, "This field is mandatory"));

            QuestionOption option = svc.option(typeOfSpace, value)
                    .orElseThrow(() -> new IllegalStateException("Option OCT_TYPE_OF_SPACE=" + value + " not found"));
            OptionConditionDTO show = new OptionConditionDTO();
            show.setActionType("SHOW_QUESTION");
            show.setTargetQuestionUuid(QUESTION_UUID);
            svc.ensureCondition(option, show, version);
        }
    }

    private QuestionValidationDTO validation(ValidationType type, String param, String errorMessage) {
        QuestionValidationDTO v = new QuestionValidationDTO();
        v.setValidationType(type);
        v.setValidationParam(param);
        v.setErrorMessage(errorMessage);
        return v;
    }
}
