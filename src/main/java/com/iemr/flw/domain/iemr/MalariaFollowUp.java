package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "malaria_follow_up", schema = "db_iemr")
@Data
public class MalariaFollowUp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ben_id", nullable = false)
    private Long benId;

    @Column(name = "house_hold_details_id", nullable = false)
    private Long houseHoldDetailsId;

    @Column(name = "user_Id")
    private Integer userId;

    @Column(name = "disease_Id", nullable = false)
    private Long diseaseId;

    @Column(name = "date_of_diagnosis", nullable = false)
    private LocalDate dateOfDiagnosis;

    @Column(name = "treatment_start_date", nullable = false)
    private LocalDate treatmentStartDate;

    @Column(name = "treatment_given", nullable = false)
    private String treatmentGiven;

    @Column(name = "days", nullable = false)
    private String day;

    @Column(name = "treatment_completion_date")
    private LocalDate treatmentCompletionDate;

    @Column(name = "referral_date")
    private LocalDate referralDate;
}
