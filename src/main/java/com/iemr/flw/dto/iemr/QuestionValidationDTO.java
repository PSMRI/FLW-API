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

import com.iemr.flw.masterEnum.ValidationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Transfer object for a server-side validation rule on a question.
 * validationType: MAX_LENGTH | MIN_DATE | MAX_DATE | REGEX | MANDATORY_IF
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionValidationDTO {

    private Long validationId;

    @NotNull(message = "validationType is required")
    private ValidationType validationType;

    private String validationParam;

    @NotBlank(message = "errorMessage is required")
    private String errorMessage;
}
