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

import jakarta.persistence.Column;
import lombok.Data;

import java.util.Date;
@Data
public class DiseaseMalariaDTO {

    private Long id;
    private Long benId;
    private Long houseHoldDetailsId;
    private Date screeningDate;
    private String beneficiaryStatus;
    private Date dateOfDeath;
    private String placeOfDeath;
    private String otherPlaceOfDeath;
    private String reasonForDeath;
    private String otherReasonForDeath;
    private String symptoms;
    private String caseStatus;
    private String rapidDiagnosticTest;
    private Date dateOfRdt;
    private String slideTestPf;
    private String slideTestPv;
    private Date dateOfSlideTest;
    private String slideNo;
    private Integer referredTo;
    private String otherReferredFacility;
    private String remarks;
    private Date dateOfVisitBySupervisor;
    private Integer diseaseTypeId;
    private Integer userId;
    private Date createdDate;
    private String createdBy;
    private boolean feverMoreThanTwoWeeks;
    private boolean fluLikeIllness;
    private boolean shakingChills;
    private boolean headache;
    private boolean muscleAches;
    private boolean tiredness;
    private boolean nausea;
    private boolean vomiting;
    private boolean diarrhea;
    private String referToName;
    private Integer caseStatusId;
    private  String  malariaTestType;
    private String malariaSlideTestType;
    private Long visitId;
    private Date visitDate;

}