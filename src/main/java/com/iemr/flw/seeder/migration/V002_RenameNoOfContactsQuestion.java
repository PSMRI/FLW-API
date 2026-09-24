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
import com.iemr.flw.service.DynamicFormReconciliationService;
import org.springframework.stereotype.Component;

/**
 * Renames the "No. of contacts" question to "Total No. of contacts" (and its Hindi translation)
 * in both the Community Contact Tracing (CCT_NO_OF_CONTACTS) and Occupational Contact Tracing
 * (OCT_NO_OF_CONTACTS) forms, per the revised field-label wording supplied for these forms.
 *
 * Only the label text changes — type, mandatoriness and position are read off the existing row
 * and passed back unchanged, since {@code updateQuestion} overwrites all of its scalar fields
 * unconditionally.
 */
@Component
public class V002_RenameNoOfContactsQuestion implements FormStructureMigration {

    private static final String NEW_TEXT = "Total No. of contacts";
    private static final String NEW_TEXT_HINDI = "कुल संपर्कों की संख्या";

    @Override
    public String migrationId() {
        return "V002";
    }

    @Override
    public void apply(DynamicFormReconciliationService svc) {
        renameQuestion(svc, "COMMUNITY_CONTACT_TRACING", "CCT_SEC_1", "CCT_NO_OF_CONTACTS");
        renameQuestion(svc, "OCCUPATION_CONTACT_TRACING", "OCT_SEC_1", "OCT_NO_OF_CONTACTS");
    }

    private void renameQuestion(DynamicFormReconciliationService svc, String formUuid, String sectionUuid,
            String questionUuid) {
        FormVersion version = svc.latestVersion(formUuid)
                .orElseThrow(() -> new IllegalStateException(
                        formUuid + " not found — must run after its seeder"));
        FormSection section = svc.section(version, sectionUuid)
                .orElseThrow(() -> new IllegalStateException("Section " + sectionUuid + " not found"));
        SectionQuestion question = svc.question(section, questionUuid)
                .orElseThrow(() -> new IllegalStateException("Question " + questionUuid + " not found"));

        if (NEW_TEXT.equals(question.getQuestionText()) && NEW_TEXT_HINDI.equals(question.getQuestionTextHindi())) {
            return;
        }

        SectionQuestionDTO dto = new SectionQuestionDTO();
        dto.setQuestionText(NEW_TEXT);
        dto.setQuestionTextHindi(NEW_TEXT_HINDI);
        dto.setQuestionType(question.getQuestionType());
        dto.setIsMandatory(question.getIsMandatory());
        dto.setMaxLength(question.getMaxLength());
        dto.setDefaultValue(question.getDefaultValue());
        dto.setContainsPii(question.getContainsPii());
        dto.setDisplayOrder(question.getDisplayOrder());

        svc.updateQuestion(question.getQuestionId(), dto);
    }
}
