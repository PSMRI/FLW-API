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
@Table(name = "screening_filaria", schema = "db_iemr")
@Data
public class ScreeningFilariasis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "ben_id")
    private Long benId;

    @Column(name = "house_hold_details_Id",nullable = false)
    private Long houseHoldDetailsId;

    @Column(name = "suffering_from_filariasis")
    private Boolean sufferingFromFilariasis;

    @Column(name = "affected_body_part", length = 50)
    private String affectedBodyPart;

    @Temporal(TemporalType.DATE)
    @Column(name = "mda_home_visit_date")
    private Date mdaHomeVisitDate;

    @Column(name = "dose_status", length = 5)
    private String doseStatus;

    @Column(name = "filariasis_case_count")
    private Integer filariasisCaseCount;

    @Column(name = "other_dose_status_details", columnDefinition = "TEXT")
    private String otherDoseStatusDetails;

    @Column(name = "medicine_side_effect", length = 5)
    private String medicineSideEffect;

    @Column(name = "other_side_effect_details", columnDefinition = "TEXT")
    private String otherSideEffectDetails;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "disease_type_id")
    private Integer diseaseTypeId;

    @Column(name = "user_id")
    private Integer userId;

}