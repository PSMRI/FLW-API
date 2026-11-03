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

/**
 * Clones a form's latest version into a new one for migrations that must leave the earlier version
 * untouched — same mechanism as {@link V001_1_CreateContactTracingVersion2}. Lets several migrations
 * in one batch share a single new version: the first to call {@link #ensureVersion} for a form
 * creates it, the rest find it already there.
 *
 * Uses {@link DynamicFormDefinitionService#updateForm}, which copies every active section, question,
 * option, validation and condition into a new version and marks the current one isLatest=false. All
 * ids are cleared from the copied DTO first: the mapper copies them onto the new entities, and saving
 * an entity with an existing id would move that row into the new version instead of copying it.
 * Conditions are re-wired by targetQuestionUuid/targetSectionUuid, which are kept.
 */
@Component
@RequiredArgsConstructor
public class FormVersionCloner {

    private final DynamicFormDefinitionService definitionService;

    /**
     * Ensures {@code formUuid}'s latest version is {@code targetVersion}, cloning the previous one into
     * it if needed, and returns it. Fails if the latest is neither the target nor the one just before it.
     */
    public FormVersion ensureVersion(DynamicFormReconciliationService svc, String formUuid, int targetVersion) {
        FormVersion latest = svc.latestVersion(formUuid)
                .orElseThrow(() -> new IllegalStateException(formUuid + " not found — must run after its seeder"));
        if (latest.getVersionNumber() == targetVersion) {
            return latest;
        }
        if (latest.getVersionNumber() != targetVersion - 1) {
            throw new IllegalStateException(formUuid + " is at version " + latest.getVersionNumber()
                    + ", expected " + (targetVersion - 1) + " or " + targetVersion);
        }

        Long formId = latest.getDynamicForm().getFormId();
        DynamicFormDTO current = definitionService.getFormDefinition(formId);
        clearIds(current);
        definitionService.updateForm(formId, current);
        return svc.latestVersion(formUuid)
                .orElseThrow(() -> new IllegalStateException(formUuid + " version " + targetVersion + " not created"));
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
