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
import com.iemr.flw.domain.iemr.SectionQuestion;
import com.iemr.flw.dto.iemr.SectionQuestionDTO;
import com.iemr.flw.masterEnum.QuestionType;
import com.iemr.flw.service.DynamicFormReconciliationService;
import org.springframework.stereotype.Component;

/**
 * Changes the Community Contact Tracing form's "Place of exposure" question (CCT_EXPOSURE_SETTING)
 * from CHECKBOX_MULTI to DROPDOWN_MULTI, so the 12 place options render as a multi-select dropdown
 * instead of a long checkbox list.
 *
 * Both types submit {@code optionValues} and store one response row per selected option, so
 * existing responses remain valid. Options and the OTHER_PLACE → CCT_EXPOSURE_SETTING_OTHER
 * conditional rule are untouched. All other scalar fields are read off the existing row and passed
 * back unchanged, since {@code updateQuestion} overwrites all of them unconditionally.
 */
@Component
public class V003_ChangeExposureSettingToDropdownMulti implements FormStructureMigration {

    @Override
    public String migrationId() {
        return "V003";
    }

    @Override
    public void apply(DynamicFormReconciliationService svc) {
        FormVersion version = svc.latestVersion("COMMUNITY_CONTACT_TRACING")
                .orElseThrow(() -> new IllegalStateException(
                        "COMMUNITY_CONTACT_TRACING not found — must run after CommunityContactTracingFormSeeder"));
        FormSection section = svc.section(version, "CCT_SEC_1")
                .orElseThrow(() -> new IllegalStateException("Section CCT_SEC_1 not found"));
        SectionQuestion question = svc.question(section, "CCT_EXPOSURE_SETTING")
                .orElseThrow(() -> new IllegalStateException("Question CCT_EXPOSURE_SETTING not found"));

        if (question.getQuestionType() == QuestionType.DROPDOWN_MULTI) {
            return;
        }

        SectionQuestionDTO dto = new SectionQuestionDTO();
        dto.setQuestionText(question.getQuestionText());
        dto.setQuestionTextHindi(question.getQuestionTextHindi());
        dto.setQuestionType(QuestionType.DROPDOWN_MULTI);
        dto.setIsMandatory(question.getIsMandatory());
        dto.setMaxLength(question.getMaxLength());
        dto.setDefaultValue(question.getDefaultValue());
        dto.setContainsPii(question.getContainsPii());
        dto.setDisplayOrder(question.getDisplayOrder());

        svc.updateQuestion(question.getQuestionId(), dto);
    }
}
