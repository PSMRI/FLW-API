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

import com.iemr.flw.domain.iemr.FormVersion;
import com.iemr.flw.dto.iemr.DynamicFormDTO;
import com.iemr.flw.dto.iemr.FormSectionDTO;
import com.iemr.flw.dto.iemr.OptionConditionDTO;
import com.iemr.flw.dto.iemr.QuestionOptionDTO;
import com.iemr.flw.dto.iemr.QuestionValidationDTO;
import com.iemr.flw.dto.iemr.SectionQuestionDTO;
import com.iemr.flw.service.DynamicFormDefinitionService;
import com.iemr.flw.service.DynamicFormReconciliationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Clones the seeded version 1 of both contact tracing forms — Community Contact Tracing and
 * Occupational Contact Tracing — into a new version 2, so the later structural changes to them are
 * applied to version 2 only and version 1 stays exactly as the seeders created it:
 * COMMUNITY_CONTACT_TRACING (V002, V003, V006, V007, V008) and OCCUPATION_CONTACT_TRACING (V002, V004,
 * V005). Responses keep the versionId they were submitted against; new responses and the app use the
 * latest version.
 *
 * Id "V001_1" sorts between V001 and V002, so this runs before the first migration that touches
 * either form — every migration resolves a form via {@code latestVersion}, which is version 2 from
 * here on.
 *
 * Uses {@link DynamicFormDefinitionService#updateForm}, which copies every section, question, option,
 * validation and condition into a new version and marks the current one isLatest=false. All ids are
 * cleared from the version-1 DTO first: the mapper copies them onto the new entities, and saving an
 * entity with an existing id would move that version-1 row into version 2 instead of copying it.
 * Conditions are re-wired by targetQuestionUuid/targetSectionUuid, which are kept.
 */
@Component
@RequiredArgsConstructor
public class V001_1_CreateContactTracingVersion2 implements FormStructureMigration {

    private static final List<String> FORM_UUIDS = List.of(
            "COMMUNITY_CONTACT_TRACING",
            "OCCUPATION_CONTACT_TRACING");

    private final DynamicFormDefinitionService definitionService;

    @Override
    public String migrationId() {
        return "V001_1";
    }

    @Override
    public void apply(DynamicFormReconciliationService svc) {
        FORM_UUIDS.forEach(formUuid -> createVersion2(svc, formUuid));
    }

    private void createVersion2(DynamicFormReconciliationService svc, String formUuid) {
        FormVersion latest = svc.latestVersion(formUuid)
                .orElseThrow(() -> new IllegalStateException(formUuid + " not found — must run after its seeder"));
        if (latest.getVersionNumber() > 1) {
            return;
        }

        Long formId = latest.getDynamicForm().getFormId();
        DynamicFormDTO version1 = definitionService.getFormDefinition(formId);
        clearIds(version1);
        definitionService.updateForm(formId, version1);
    }

    private void clearIds(DynamicFormDTO form) {
        for (FormSectionDTO section : form.getSections()) {
            section.setSectionId(null);
            for (SectionQuestionDTO question : section.getQuestions()) {
                question.setQuestionId(null);
                for (QuestionValidationDTO validation : question.getValidations()) {
                    validation.setValidationId(null);
                }
                for (QuestionOptionDTO option : question.getOptions()) {
                    option.setOptionId(null);
                    for (OptionConditionDTO condition : option.getConditions()) {
                        condition.setConditionId(null);
                        condition.setTargetQuestionId(null);
                        condition.setTargetSectionId(null);
                    }
                }
            }
        }
    }
}
