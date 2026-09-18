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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Per-beneficiary detail for a COMPLETE/REFUSED form response: outcome plus section-fill progress
 * against the exact form version that response was submitted against.
 *
 * @author Piramal Swasthya
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompletedBeneficiaryDetailDTO {
    private Long beneficiaryId;
    private boolean isRefused;
    private int sectionsFilled;
    private int totalSections;
}
