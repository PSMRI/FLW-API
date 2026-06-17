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

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Transfer object for a selectable option on a RADIO or MCQ question.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionOptionDTO {

    private Long optionId;

    @NotBlank(message = "optionLabel is required")
    private String optionLabel;

    @NotBlank(message = "optionValue is required")
    private String optionValue;

    @NotNull(message = "displayOrder is required")
    private Integer displayOrder;

    @Valid
    private List<OptionConditionDTO> conditions = new ArrayList<>();
}
