package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "t_sam_visit", schema = "db_iemr")
public class SamVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "beneficiary_id")
    private Long beneficiaryId;

    @Column(name = "household_id")
    private Long householdId;

    @Column(name = "visit_date")
    private LocalDate visitDate;

    @Column(name = "visit_label")
    private String visitLabel;

    @Column(name = "muac")
    private String muac;

    @Column(name = "weight_for_height_status")
    private String weightForHeightStatus;

    @Column(name = "is_child_referred_nrc")
    private String isChildReferredNrc;

    @Column(name = "is_child_admitted_nrc")
    private String isChildAdmittedNrc;

    @Column(name = "nrc_admission_date")
    private String nrcAdmissionDate;

    @Column(name = "is_child_discharged_nrc")
    private String isChildDischargedNrc;

    @Column(name = "nrc_discharge_date")
    private String nrcDischargeDate;

    @Column(name = "follow_up_visit_date")
    private String followUpVisitDate;

    @Column(name = "sam_status")
    private String samStatus;

    @Column(name = "discharge_summary")
    private String dischargeSummary;

    @Column(name = "view_discharge_docs")
    private String viewDischargeDocs;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_date")
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(name = "updated_date")
    private LocalDateTime updatedDate = LocalDateTime.now();
}
