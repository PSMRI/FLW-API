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

import com.iemr.flw.masterEnum.FormType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Transfer object for a full dynamic form definition.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DynamicFormDTO {

    private Long formId;

    @NotBlank(message = "formUuid is required")
    private String formUuid;

    @NotBlank(message = "formName is required")
    private String formName;

    @NotNull(message = "formType is required")
    private FormType formType;

    private Boolean isActive = true;

    /** Days after the most-recently-completed section before the next follow-up notification fires.
     *  Null means no follow-up notifications. */
    private Integer followUpDelayDays;

    /** Current version number — populated by service on read, ignored on write. */
    private Integer versionNumber;

    @Valid
    private List<FormSectionDTO> sections = new ArrayList<>();
}
