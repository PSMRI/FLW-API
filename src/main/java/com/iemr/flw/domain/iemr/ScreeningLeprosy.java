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
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @Column(name = "house_hold_details_Id", nullable = false)
    private Long houseHoldDetailsId;

    @Column(name = "home_visit_date")
    private LocalDate homeVisitDate;

    @Column(name = "leprosy_status", length = 225)
    private String leprosyStatus;

    @Column(name = "referred_to", length = 225)
    private String referredTo;

    @Column(name = "other_referred_to", columnDefinition = "TEXT")
    private String otherReferredTo;

    @Column(name = "leprosy_status_date")
    private LocalDate leprosyStatusDate;

    @Column(name = "type_of_leprosy", length = 225)
    private String typeOfLeprosy;

    @Column(name = "follow_up_date")
    private LocalDate followUpDate;

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

    @Column(name = "beneficiary_status")
    private String beneficiaryStatus;

    @Column(name = "date_of_death")
    private LocalDate dateOfDeath;

    @Column(name = "place_of_death")
    private String placeOfDeath;

    @Column(name = "other_place_of_death", columnDefinition = "TEXT")
    private String otherPlaceOfDeath;

    @Column(name = "reason_for_death")
    private String reasonForDeath;

    @Column(name = "other_reason_for_death", columnDefinition = "TEXT")
    private String otherReasonForDeath;

    @Column(name = "leprosy_symptoms")
    private String leprosySymptoms;

    @Column(name = "leprosy_symptoms_position")
    private Integer leprosySymptomsPosition;

    @Column(name = "leprosy_status_position")
    private Integer lerosyStatusPosition;

    @Column(name = "current_visit_number")
    private Integer currentVisitNumber;

    @Column(name = "visit_label")
    private String visitLabel;

    @Column(name = "visit_number")
    private Integer visitNumber;

    @Column(name = "is_confirmed")
    private Boolean isConfirmed;

    @Column(name = "leprosy_state", length = 255)
    private String leprosyState;

    @Column(name = "treatment_start_date")
    private LocalDate treatmentStartDate;

    @Column(name = "total_followup_months_required")
    private Integer totalFollowUpMonthsRequired;

    @Column(name = "treatment_end_date")
    private LocalDate treatmentEndDate;

    @Column(name = "mdt_blister_pack_recived", length = 100)
    private String mdtBlisterPackRecived;

    @Column(name = "treatment_status", length = 100)
    private String treatmentStatus;

    @Column(name = "recurrent_ulceration_id")
    private Integer recurrentUlcerationId;

    @Column(name = "recurrent_tingling_id")
    private Integer recurrentTinglingId;

    @Column(name = "hypopigmented_patch_id")
    private Integer hypopigmentedPatchId;

    @Column(name = "thickened_skin_id")
    private Integer thickenedSkinId;

    @Column(name = "skin_nodules_id")
    private Integer skinNodulesId;

    @Column(name = "skin_patch_discoloration_id")
    private Integer skinPatchDiscolorationId;

    @Column(name = "recurrent_numbness_id")
    private Integer recurrentNumbnessId;

    @Column(name = "clawing_fingers_id")
    private Integer clawingFingersId;

    @Column(name = "tingling_numbness_extremities_id")
    private Integer tinglingNumbnessExtremitiesId;

    @Column(name = "inability_close_eyelid_id")
    private Integer inabilityCloseEyelidId;

    @Column(name = "difficulty_holding_objects_id")
    private Integer difficultyHoldingObjectsId;

    @Column(name = "weakness_feet_id")
    private Integer weaknessFeetId;

    @Column(name = "recurrent_ulceration")
    private String recurrentUlceration;

    @Column(name = "recurrent_tingling")
    private String recurrentTingling;

    @Column(name = "hypopigmented_patch")
    private String hypopigmentedPatch;

    @Column(name = "thickened_skin")
    private String thickenedSkin;

    @Column(name = "skin_nodules")
    private String skinNodules;

    @Column(name = "skin_patch_discoloration")
    private String skinPatchDiscoloration;

    @Column(name = "recurrent_numbness")
    private String recurrentNumbness;

    @Column(name = "clawing_fingers")
    private String clawingFingers;

    @Column(name = "tingling_numbness_extremities")
    private String tinglingNumbnessExtremities;

    @Column(name = "inability_close_eyelid")
    private String inabilityCloseEyelid;

    @Column(name = "difficulty_holding_objects")
    private String difficultyHoldingObjects;

    @Column(name = "weakness_feet")
    private String weaknessFeet;

    @Column(name = "CreatedBy", length = 100)
    private String createdBy;

    @Column(name = "CreatedDate")
    private LocalDateTime createdDate;

    @Column(name = "ModifiedBy", length = 100)
    private String modifiedBy;

    @Column(name = "LastModDate")
    private LocalDateTime lastModDate;
}