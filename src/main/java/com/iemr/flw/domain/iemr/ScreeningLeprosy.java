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
@Table(name = "screening_leprosy", schema = "db_iemr")
@Data
public class ScreeningLeprosy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "ben_id")
    private Long benId;

    @Column(name = "house_hold_details_Id",nullable = false)
    private Long houseHoldDetailsId;

    @Temporal(TemporalType.DATE)
    @Column(name = "home_visit_date")
    private Date homeVisitDate;

    @Column(name = "leprosy_status", length = 225)
    private String leprosyStatus;

    @Column(name = "referred_to", length = 225)
    private String referredTo;

    @Column(name = "other_referred_to", columnDefinition = "TEXT")
    private String otherReferredTo;

    @Temporal(TemporalType.DATE)
    @Column(name = "leprosy_status_date")
    private Date leprosyStatusDate;

    @Column(name = "type_of_leprosy", length = 225)
    private String typeOfLeprosy;

    @Temporal(TemporalType.DATE)
    @Column(name = "follow_up_date")
    private Date followUpDate;

    @Column(name = "remark", length = 225)
    private String remark;

    @Column(name = "disease_type_id")
    private Integer diseaseTypeId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "refer_to_name")
    private String referToName;

    @Column(name = "beneficiary_statusId")
    private Integer beneficiaryStatusId;

    @Column(name = "beneficiary_status", length = 50)
    private String beneficiaryStatus;

    @Column(name = "date_of_death")
    private Date dateOfDeath;

    @Column(name = "place_of_death", length = 50)
    private String placeOfDeath;

    @Column(name = "other_place_of_death", columnDefinition = "TEXT")
    private String otherPlaceOfDeath;

    @Column(name = "reason_for_death", length = 50)
    private String reasonForDeath;

    @Column(name = "other_reason_for_death", columnDefinition = "TEXT")
    private String otherReasonForDeath;
}