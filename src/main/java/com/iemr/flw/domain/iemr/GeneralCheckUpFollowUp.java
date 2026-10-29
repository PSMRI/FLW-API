package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDate;

@Entity
@Table(name = "general_checkup_followup", schema = "db_iemr")
@Data
public class GeneralCheckUpFollowUp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "ben_id")
    private Long benId;

    @Column(name = "household_id")
    private Long householdId;

    @Column(name = "asha_id", nullable = false)
    private Integer ashaId;

    @Column(name = "follow_up_date", nullable = false)
    private LocalDate followUpDate;

    @Column(name = "followup_status", length = 200)
    private String followupStatus;

    @Column(name = "referred_to", columnDefinition = "json")
    private String referredTo;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "created_date", insertable = false, updatable = false)
    private Timestamp createdDate;

    @Column(name = "synced_date")
    private Timestamp syncedDate;

    @Column(name = "updated_date", insertable = false, updatable = false)
    private Timestamp updatedDate;

    @Column(name = "referralReason")
    private String referralReason;

    @Column(name = "referredOnDate")
    private LocalDate referredOnDate;
}