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
package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Table(name = "screening_aesje", schema = "db_iemr")
@Data
public class ScreeningAesje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "ben_id")
    private Long benId;

    @Column(name = "house_hold_details_Id",nullable = false)
    private Long houseHoldDetailsId;

    @Temporal(TemporalType.DATE)
    @Column(name = "visit_date")
    private Date visitDate;

    @Column(name = "beneficiary_status")
    private String beneficiaryStatus = "Not Applicable";

    @Temporal(TemporalType.DATE)
    @Column(name = "date_of_death")
    private Date dateOfDeath;

    @Column(name = "place_of_death")
    private String placeOfDeath;

    @Column(name = "other_place_of_death")
    private String otherPlaceOfDeath;

    @Column(name = "reason_for_death")
    private String reasonForDeath;

    @Column(name = "other_reason_for_death")
    private String otherReasonForDeath;

    @Column(name = "aes_je_case_status")
    private String aesJeCaseStatus;

    @Column(name = "aes_je_case_count")
    private Integer aesJeCaseCount = 0;

    @Column(name = "follow_up_point")
    private Integer followUpPoint;

    @Column(name = "referred_to")
    private String referredTo;

    @Column(name = "other_referred_facility")
    private String otherReferredFacility;

    @Column(name = "created_date")
    private Date createdDate = new Date();

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "disease_type_id")
    private Integer diseaseTypeId;

    @Column(name = "refer_to_name")
    private String referToName;

    @Column(name = "beneficiary_status_id")
    private Integer beneficiaryStatusId;

    // Getters and Setters
}