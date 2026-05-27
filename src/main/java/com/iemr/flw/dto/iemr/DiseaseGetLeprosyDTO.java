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

import java.sql.Timestamp;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class DiseaseGetLeprosyDTO {

    private Long id;
    private Long benId;
    private Long houseHoldDetailsId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMM d, yyyy h:mm:ss a", timezone = "Asia/Kolkata")
    private Date homeVisitDate;

    private String leprosyStatus;
    private String referredTo;
    private String otherReferredTo;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMM d, yyyy h:mm:ss a", timezone = "Asia/Kolkata")
    private Date leprosyStatusDate;

    private String typeOfLeprosy;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMM d, yyyy h:mm:ss a", timezone = "Asia/Kolkata")
    private Date followUpDate;

    private String beneficiaryStatus;
    private String remark;
    private Integer userId;
    private Integer diseaseTypeId;
    private String referToName;
    private Integer beneficiaryStatusId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMM d, yyyy h:mm:ss a", timezone = "Asia/Kolkata")
    private Date dateOfDeath;

    private String placeOfDeath;
    private String otherPlaceOfDeath;
    private String reasonForDeath;
    private String otherReasonForDeath;

    private String leprosySymptoms;
    private Integer leprosySymptomsPosition;
    private Integer lerosyStatusPosition;
    private Integer currentVisitNumber;
    private String visitLabel;
    private Integer visitNumber;
    private Boolean isConfirmed;
    private String leprosyState;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMM d, yyyy h:mm:ss a", timezone = "Asia/Kolkata")
    private Date treatmentStartDate;

    private Integer totalFollowUpMonthsRequired;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMM d, yyyy h:mm:ss a", timezone = "Asia/Kolkata")
    private Date treatmentEndDate;

    private String mdtBlisterPackRecived;
    private String treatmentStatus;
    private String createdBy;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMM d, yyyy h:mm:ss a", timezone = "Asia/Kolkata")
    private Timestamp createdDate;

    private String modifiedBy;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMM d, yyyy h:mm:ss a", timezone = "Asia/Kolkata")
    private Timestamp lastModDate;
}