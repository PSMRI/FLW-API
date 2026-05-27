package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "tb_confirmed_cases",schema = "db_iemr")
public class TBConfirmedCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ben_id", nullable = false)
    private Long benId;

    @Column(name = "user_id",nullable = false)
    private Integer userId;


    @Column(name = "regimen_type")
    private String regimenType;

    @Column(name = "treatment_start_date")
    private LocalDate treatmentStartDate;

    @Column(name = "expected_treatment_completion_date")
    private LocalDate expectedTreatmentCompletionDate;

    @Column(name = "follow_up_date")
    private LocalDate followUpDate;

    @Column(name = "monthly_follow_up_done")
    private String monthlyFollowUpDone;

    @Column(name = "adherence_to_medicines")
    private String adherenceToMedicines;

    @Column(name = "any_discomfort")
    private Boolean anyDiscomfort;

    @Column(name = "treatment_completed")
    private Boolean treatmentCompleted;

    @Column(name = "actual_treatment_completion_date")
    private LocalDate actualTreatmentCompletionDate;

    @Column(name = "treatment_outcome")
    private String treatmentOutcome;

    @Column(name = "date_of_death")
    private LocalDate dateOfDeath;

    @Column(name = "place_of_death")
    private String placeOfDeath;

    @Column(name = "reason_for_death")
    private String reasonForDeath = "Tuberculosis";

    @Column(name = "reason_for_not_completing")
    private String reasonForNotCompleting;

    @Column(name = "created_at")
    private LocalDate createdAt = LocalDate.now();

    @Column(name = "updated_at")
    private LocalDate updatedAt;

}
