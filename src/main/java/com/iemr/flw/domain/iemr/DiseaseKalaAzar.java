package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Table(name = "disease_kala_azar", schema = "db_iemr")
@Data
public class DiseaseKalaAzar {

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

    @Column(name = "beneficiary_status", length = 50)
    private String beneficiaryStatus;

    @Temporal(TemporalType.DATE)
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

    @Column(name = "kala_azar_case_status", length = 50)
    private String kalaAzarCaseStatus;

    @Column(name = "kala_azar_case_count")
    private Integer kalaAzarCaseCount;

    @Column(name = "rapid_diagnostic_test", length = 20)
    private String rapidDiagnosticTest;

    @Temporal(TemporalType.DATE)
    @Column(name = "date_of_rdt")
    private Date dateOfRdt;

    @Column(name = "follow_up_point")
    private Integer followUpPoint;

    @Column(name = "referred_to", length = 100)
    private String referredTo;

    @Column(name = "other_referred_facility", columnDefinition = "TEXT")
    private String otherReferredFacility;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "diseaseTypeID")
    private Integer diseaseTypeId;
    // Getters and Setters
}