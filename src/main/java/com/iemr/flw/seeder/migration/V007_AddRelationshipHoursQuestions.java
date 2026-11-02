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
 * Adds a mandatory "Time spent with contact in hours" number picker to the Community Contact
 * Tracing form for each "Relationship with community contacts" (CCT_RELATIONSHIP) option, so the
 * exposure duration is captured per relationship type alongside its existing contact count.
 *
 * One question per option (CCT_RELATIONSHIP_HOURS_&lt;OPTION_VALUE&gt;) rather than a shared one,
 * since CCT_RELATIONSHIP is multi-select and a shared field couldn't disambiguate which duration
 * belongs to which relationship. Each is hidden by default, revealed by a SHOW_QUESTION condition
 * on its option, and mandatory only when that option is selected (MANDATORY_IF).
 *
 * Each question is inserted directly after its relationship's "Approximate Area of Shared Space"
 * (CCT_AREA_OF_SHARED_SPACE_&lt;OPTION_VALUE&gt;), so every relationship's revealed questions read
 * Type of Space, Area, Time spent; {@code ensureQuestion} shifts everything after it down by one.
 * Must run after V006_PerRelationshipTypeOfSpace, which creates those Area questions — and since it
 * runs later, each option's SHOW_QUESTION conditions are also created in that same order.
 */
@Component
public class V007_AddRelationshipHoursQuestions implements FormStructureMigration {

    private static final List<String> RELATIONSHIP_VALUES = List.of(
            "NEIGHBOUR",
            "FRIEND",
            "FELLOW_WORSHIPPER",
            "FELLOW_COMMUTER",
            "COMMUNITY_GROUP_MEMBER",
            "FELLOW_PATIENT",
            "OTHER");

    private static final String QUESTION_TEXT = "Time spent with contact in hours";
    private static final String QUESTION_TEXT_HINDI = "संपर्क के साथ बिताया गया समय (घंटों में)";
    private static final String HOURS_REGEX = "^([0-9]|1[0-9]|2[0-4])$";
    private static final String HOURS_ERROR = "Enter whole hours between 0 and 24";

    @Override
    public String migrationId() {
        return "V007";
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

        for (String value : RELATIONSHIP_VALUES) {
            String questionUuid = "CCT_RELATIONSHIP_HOURS_" + value;
            String areaUuid = "CCT_AREA_OF_SHARED_SPACE_" + value;

            SectionQuestion area = svc.question(section, areaUuid)
                    .orElseThrow(() -> new IllegalStateException(
                            "Question " + areaUuid + " not found — must run after V006_PerRelationshipTypeOfSpace"));
            SectionQuestion hours = svc.ensureQuestion(section,
                    hoursQuestion(questionUuid, area.getDisplayOrder() + 1));
            svc.ensureValidation(hours, validation(ValidationType.REGEX, HOURS_REGEX, HOURS_ERROR));
            svc.ensureValidation(hours, validation(ValidationType.MANDATORY_IF,
                    "CCT_RELATIONSHIP=" + value, "This field is mandatory"));

            QuestionOption option = svc.option(relationship, value)
                    .orElseThrow(() -> new IllegalStateException("Option CCT_RELATIONSHIP=" + value + " not found"));
            OptionConditionDTO show = new OptionConditionDTO();
            show.setActionType("SHOW_QUESTION");
            show.setTargetQuestionUuid(questionUuid);
            svc.ensureCondition(option, show, version);
        }
    }

    private SectionQuestionDTO hoursQuestion(String uuid, int order) {
        SectionQuestionDTO q = new SectionQuestionDTO();
        q.setQuestionUuid(uuid);
        q.setQuestionText(QUESTION_TEXT);
        q.setQuestionTextHindi(QUESTION_TEXT_HINDI);
        q.setQuestionType(QuestionType.NUMBER_PICKER);
        q.setIsMandatory(true);
        q.setDisplayOrder(order);
        q.setVisibleByDefault(false);
        q.setOptions(List.of());
        q.setValidations(List.of());
        return q;
    }

    private QuestionValidationDTO validation(ValidationType type, String param, String errorMessage) {
        QuestionValidationDTO v = new QuestionValidationDTO();
        v.setValidationType(type);
        v.setValidationParam(param);
        v.setErrorMessage(errorMessage);
        return v;
    }
}
