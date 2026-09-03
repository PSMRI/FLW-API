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

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Transfer object for a conditional action triggered when an option is selected.
 * Exactly one of targetQuestionId / targetSectionId should be non-null.
 *
 * On create/update: use targetQuestionUuid or targetSectionUuid to reference target.
 * On read: targetQuestionId + targetQuestionUuid (or targetSectionId + targetSectionUuid) are
 * returned as flat references — the full target object appears in the form's questions/sections list.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OptionConditionDTO {

    private Long conditionId;

    @NotBlank(message = "actionType is required")
    private String actionType;

    /** DB ID of the target question. */
    private Long targetQuestionId;

    /** DB ID of the target section. */
    private Long targetSectionId;

    /** UUID of the target question. */
    private String targetQuestionUuid;

    /** UUID of the target section. */
    private String targetSectionUuid;
}
