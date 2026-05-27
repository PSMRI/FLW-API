package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Data
@Entity
@Table(name = "t_eye_checkup",schema = "db_iemr")
public class EyeCheckupVisit {
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

    @Column(name = "symptoms_observed")
    private String symptomsObserved;

    @Column(name = "eye_affected")
    private String eyeAffected;

    @Column(name = "referred_to")
    private String referredTo;

    @Column(name = "follow_up_status")
    private String followUpStatus;

    @Column(name = "date_of_surgery")
    private String dateOfSurgery;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "discharge_summary_upload")
    private String dischargeSummaryUpload;

    @Column(name = "created_date")
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(name = "updated_date")
    private LocalDateTime updatedDate = LocalDateTime.now();
}
