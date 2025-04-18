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

    @Column(name = "houseHoldDetailsId")
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

    @Column(name = "userID")
    private Integer userId;

    @Column(name = "diseaseTypeID")
    private Integer diseaseTypeId;

    @Column(name = "refer_to_name")
    private String referToName;

    @Column(name = "beneficiary_statusId")
    private Integer beneficiaryStatusId;

    // Getters and Setters
}