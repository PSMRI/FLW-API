package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "tb_tpt_follow_up",schema = "db_iemr")
public class TbTptFollowUp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ben_id")
    private Long benId;

    @Column(name = "household_id")
    private Long houseHoldId;
    @Column(name = "regimen_type")
    private String regimenType;

    @Column(name = "treatment_start_date")
    private Timestamp treatmentStartDate;

    @Column(name = "expected_treatment_completion_date")
    private Timestamp expectedTreatmentCompletionDate;

    @Column(name = "follow_up_date")
    private Timestamp followUpDate;

    @Column(name = "follow_up_month")
    private String followUpMonth;

    @Column(name = "adherence_to_medicines")
    private String adherenceToMedicines;

    @Column(name = "any_discomfort")
    private Boolean anyDiscomfort;

    @Column(name = "treatment_completed")
    private Boolean treatmentCompleted;

    @Column(name = "actual_treatment_completion_date")
    private Timestamp actualTreatmentCompletionDate;

    @Column(name = "tpt_outcome")
    private String tptOutcome;

    @Column(name = "date_of_death")
    private Timestamp dateOfDeath;

    @Column(name = "place_of_death")
    private String placeOfDeath;

    @Column(name = "reason_for_death")
    private String reasonForDeath;

    @CreationTimestamp
    @Column(name = "created_date")
    private Timestamp createdDate;

    @UpdateTimestamp
    @Column(name = "updated_date")
    private Timestamp updatedDate;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "synced_by")
    private String syncedBy;

    @Column(name = "user_id")
    private Integer userId;

}