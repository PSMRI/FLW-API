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
*
/*
* AMRIT – Accessible Medical Records via Integrated Technology
*/
package com.iemr.flw.dto.iemr;

import lombok.Data;
import java.util.Date;

@Data
public class MalariaFollowListUpDTO {
    private Long id;
    private Long benId;
    private Long houseHoldDetailsId;
    private Integer userId ;
    private Long diseaseId;
    private Date dateOfDiagnosis;
    private Date treatmentStartDate;
    private String treatmentGiven;

    private Boolean pfDay1;
    private Boolean pfDay2;
    private Boolean pfDay3;

    private Boolean pvDay1;
    private Boolean pvDay2;
    private Boolean pvDay3;
    private Boolean pvDay4;

    private Date treatmentCompletionDate;
    private Date referralDate;
}
