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
public class DiseaseKalaAzarDTO {

    private Long id;
    private Long benId;
    private Long houseHoldDetailsId;
    private Date visitDate;
    private String beneficiaryStatus;
    private Date dateOfDeath;
    private String placeOfDeath;
    private String otherPlaceOfDeath;
    private String reasonForDeath;
    private String otherReasonForDeath;
    private String kalaAzarCaseStatus;
    private Integer kalaAzarCaseCount;
    private String rapidDiagnosticTest;
    private Date dateOfRdt;
    private Integer followUpPoint;
    private String referredTo;
    private String otherReferredFacility;
    private Date createdDate;
    private String createdBy;
    private Integer userId;
    private Integer diseaseTypeId;
    private String referToName;
    private Integer beneficiaryStatusId;

}