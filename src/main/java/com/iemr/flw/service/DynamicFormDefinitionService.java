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

import com.iemr.flw.dto.iemr.DynamicFormDTO;
import com.iemr.flw.dto.iemr.FormSectionDTO;
import com.iemr.flw.dto.iemr.OptionConditionDTO;
import com.iemr.flw.dto.iemr.QuestionOptionDTO;
import com.iemr.flw.dto.iemr.QuestionValidationDTO;
import com.iemr.flw.dto.iemr.SectionQuestionDTO;

import java.util.List;

/**
 * Service for managing dynamic form definitions (structure only — not responses).
 */
public interface DynamicFormDefinitionService {

    /** Creates a full form definition (form + v1 + sections + questions + options + validations). */
    DynamicFormDTO createForm(DynamicFormDTO formDTO);

    /** Replaces the form structure with a new version, bumping versionNumber. */
    DynamicFormDTO updateForm(Long formId, DynamicFormDTO formDTO);

    /** Returns the latest version's full definition tree, Redis-cached by formId. */
    DynamicFormDTO getFormDefinition(Long formId);

    /** Returns a specific version's full definition tree (no cache). */
    DynamicFormDTO getFormDefinitionByVersion(Long formId, Integer versionNumber);

    /** Returns metadata for all active forms (without full tree). */
    List<DynamicFormDTO> getAllForms();

    /** Sets the form's isActive flag to true. */
    void activateForm(Long formId);

    /** Sets the form's isActive flag to false and invalidates cache. */
    void deactivateForm(Long formId);

}
