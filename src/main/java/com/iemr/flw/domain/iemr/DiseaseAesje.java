package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Table(name = "disease_aesje", schema = "db_iemr")
@Data
public class DiseaseAesje {

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

    @Column(name = "follow_up_point", columnDefinition = "INT CHECK (follow_up_point between 1 and 6)")
    private Integer followUpPoint;

    @Column(name = "referred_to")
    private String referredTo;

    @Column(name = "other_referred_facility")
    private String otherReferredFacility;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Date createdDate = new Date();

    @Column(name = "created_by")
    private String createdBy;



    @Column(name = "diseaseTypeID")
    private Integer diseaseTypeId;

    // Getters and Setters
}