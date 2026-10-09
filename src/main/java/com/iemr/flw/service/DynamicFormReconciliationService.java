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
package com.iemr.flw.service;

import com.iemr.flw.domain.iemr.DynamicForm;
import com.iemr.flw.domain.iemr.FormSection;
import com.iemr.flw.domain.iemr.FormVersion;
import com.iemr.flw.domain.iemr.OptionCondition;
import com.iemr.flw.domain.iemr.QuestionOption;
import com.iemr.flw.domain.iemr.QuestionValidation;
import com.iemr.flw.domain.iemr.SectionQuestion;
import com.iemr.flw.dto.iemr.DynamicFormDTO;
import com.iemr.flw.dto.iemr.FormSectionDTO;
import com.iemr.flw.dto.iemr.OptionConditionDTO;
import com.iemr.flw.dto.iemr.QuestionOptionDTO;
import com.iemr.flw.dto.iemr.QuestionValidationDTO;
import com.iemr.flw.dto.iemr.SectionQuestionDTO;

import java.util.Optional;

/**
 * Idempotent, in-place reconciliation for dynamic form structure — inserts whatever is missing
 * from the current latest {@link FormVersion} and leaves existing rows untouched. Never creates a
 * new version (unlike {@link DynamicFormDefinitionService#updateForm}, which always clones one).
 *
 * Used both by the startup seeders (walking their full hardcoded DTO tree every run) and by the
 * granular structure endpoints (a single section/question/option at a time).
 *
 * Each {@code ensureX} method operates on exactly one level — callers walk the tree themselves and
 * call the next level down, since only the caller knows whether it's safe yet to wire cross-sibling
 * references (see {@link #ensureCondition}).
 */
public interface DynamicFormReconciliationService {

    /** Finds the form by formUuid, or creates it. If found inactive, reactivates it (touches no other field). */
    DynamicForm ensureForm(DynamicFormDTO dto);

    /** Finds the form's isLatest=true version, or creates version 1 if none exists yet. */
    FormVersion ensureLatestVersion(DynamicForm form);

    /** Finds a section by sectionUuid within the version, or creates it. */
    FormSection ensureSection(FormVersion version, FormSectionDTO dto);

    /** Finds a question by questionUuid within the section, or creates it (a section's question is "linked" the moment it's created — no separate link step exists). */
    SectionQuestion ensureQuestion(FormSection section, SectionQuestionDTO dto);

    /** Finds an option by optionValue within the question, or creates it. */
    QuestionOption ensureOption(SectionQuestion question, QuestionOptionDTO dto);

    /** Finds a validation by (validationType, validationParam) within the question, or creates it — validations carry no natural key of their own. */
    QuestionValidation ensureValidation(SectionQuestion question, QuestionValidationDTO dto);

    /**
     * Finds a condition by (actionType, target) within the option, or creates it, resolving
     * targetQuestionUuid/targetSectionUuid against the given version. Call only once every sibling
     * section/question this condition might target already exists as a row.
     */
    OptionCondition ensureCondition(QuestionOption option, OptionConditionDTO dto, FormVersion version);

    /** Updates a question's scalar fields only (options/validations are managed by their own endpoints). A displayOrder change moves the question among its siblings, shifting them accordingly. */
    SectionQuestion updateQuestion(Long questionId, SectionQuestionDTO dto);

    /** Soft-deletes (isActive=false) a question — never hard-deleted; historical responses may reference it. */
    void unlinkQuestion(Long questionId);

    /** Soft-deletes (isActive=false) an option — never hard-deleted; historical responses may reference it. */
    void removeOption(Long optionId);

    // ── Read-only lookups, for FormStructureMigration classes to locate what they're changing ──

    /** Finds the given form's current latest version, if the form exists at all. */
    Optional<FormVersion> latestVersion(String formUuid);

    /** Finds a section by sectionUuid within the given version. */
    Optional<FormSection> section(FormVersion version, String sectionUuid);

    /** Finds a question by questionUuid within the given section. */
    Optional<SectionQuestion> question(FormSection section, String questionUuid);

    /** Finds an option by optionValue within the given question. */
    Optional<QuestionOption> option(SectionQuestion question, String optionValue);
}
