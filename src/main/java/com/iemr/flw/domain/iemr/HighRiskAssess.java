package com.iemr.flw.domain.iemr;

import lombok.Data;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "high_risk_assess", schema = "db_iemr")
@Data

public class HighRiskAssess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "ben_id")
    private Long benId;

    @Column(name = "delivery_count_check")
    private String noOfDeliveries;

    @Column(name = "delivery_gap_check")
    private String timeLessThan18m;

    @Column(name = "height_short_check")
    private String heightShort;

    @Column(name = "age_check")
    private String age;

    @Column(name = "created_date")
    private Timestamp createdDate;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_date")
    private Timestamp updatedDate;

    @Column(name = "updated_by")
    private String updatedBy;

}
