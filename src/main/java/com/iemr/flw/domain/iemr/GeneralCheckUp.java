package com.iemr.flw.domain.iemr;

import java.sql.Timestamp;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "general_check_up", schema = "db_iemr")
@Data
public class GeneralCheckUp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "ben_id")
    private Long benId;

    @Column(name = "household_id")
    private Long householdId;

    @Column(name = "asha_id", nullable = false)
    private Long ashaId;

    @Column(name = "checkup_date", nullable = false)
    private LocalDate checkupDate;

    @Column(name = "problem_type", length = 200)
    private String problemType;

    @Column(name = "problems", columnDefinition = "json")
    private String problems;

    @Column(name = "other_text", length = 250)
    private String otherText;

    @Column(name = "priority", length = 20)
    private String priority;

    @Column(name = "referral_reason", length = 500)
    private String referralReason;

    @Column(name = "referral_date")
    private LocalDate referralDate;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "created_date", insertable = false, updatable = false)
    private Timestamp createdDate;

    @Column(name = "synced_date")
    private Timestamp syncedDate;

    @Column(name = "updated_date", insertable = false, updatable = false)
    private Timestamp updatedDate;
}