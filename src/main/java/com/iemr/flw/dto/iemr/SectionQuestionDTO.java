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
package com.iemr.flw.dto.iemr;

import com.iemr.flw.masterEnum.QuestionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Transfer object for a question within a section.
 * visibleByDefault is computed by the service — not stored in DB.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SectionQuestionDTO {

    private Long questionId;

    @NotBlank(message = "questionUuid is required")
    private String questionUuid;

    @NotBlank(message = "questionText is required")
    private String questionText;

    @NotNull(message = "questionType is required")
    private QuestionType questionType;

    private Boolean isMandatory = true;

    @NotNull(message = "displayOrder is required")
    private Integer displayOrder;

    private Integer maxLength;

    private String defaultValue;

    private Boolean containsPii = false;

    /** Computed at read time: false if this question is a target of any OptionCondition. */
    private Boolean visibleByDefault = true;

    @Valid
    private List<QuestionOptionDTO> options = new ArrayList<>();

    @Valid
    private List<QuestionValidationDTO> validations = new ArrayList<>();
}
