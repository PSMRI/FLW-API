package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Table(name = "screening_kala_azar", schema = "db_iemr")
@Data
public class ScreeningKalaAzar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "ben_id")
    private Long benId;

    @Column(name = "house_hold_details_Id",nullable = false)
    private Long houseHoldDetailsId;

    @Column(name = "userID")
    private Integer userId;

    @Temporal(TemporalType.DATE)
    @Column(name = "visit_date")
    private Date visitDate;

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

    @Column(name = "kala_azar_case_status")
    private String kalaAzarCaseStatus;

    @Column(name = "kala_azar_case_count")
    private Integer kalaAzarCaseCount;

    @Column(name = "rapid_diagnostic_test")
    private String rapidDiagnosticTest;

    @Temporal(TemporalType.DATE)
    @Column(name = "date_of_rdt")
    private Date dateOfRdt;

    @Column(name = "follow_up_point")
    private Integer followUpPoint;

    @Column(name = "referred_to")
    private String referredTo;

    @Column(name = "other_referred_facility")
    private String otherReferredFacility;

    @Temporal(TemporalType.DATE)
    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "diseaseTypeID")
    private Integer diseaseTypeId;

    @Column(name = "refer_to_name")
    private String referToName;

    @Column(name = "beneficiary_statusId")
    private Integer beneficiaryStatusId;

    @Column(name = "beneficiary_status")
    private String beneficiaryStatus;
}