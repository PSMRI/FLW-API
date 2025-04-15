package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Table(name = "disease_filariasis", schema = "db_iemr")
@Data
public class DiseaseFilariasis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "ben_id")
    private Long benId;

    @Column(name = "houseHoldDetailsId")
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

    @Column(name = "diseaseTypeID")
    private Integer diseaseTypeId;

    // Getters and Setters
}