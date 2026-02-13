package com.iemr.flw.domain.iemr;

import lombok.Data;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "incentive_activity_record", schema = "db_iemr")
@Data
public class IncentiveActivityRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "activity_id")
    private Long activityId;

    @Column(name = "asha_id")
    private Integer ashaId;

    @Column(name = "ben_id")
    private Long benId;

    @Column(name = "amount")
    private Long amount;

    @Column(name = "name")
    private String name;

    @Column(name = "start_date")
    private Timestamp startDate;

    @Column(name = "end_date")
    private Timestamp endDate;

    @Column(name = "created_date")
    private Timestamp createdDate;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_date")
    private Timestamp updatedDate;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "is_eligible")
    private Boolean isEligible;

    @Column(name = "is_default_activity")
    private Boolean isDefaultActivity;
}
