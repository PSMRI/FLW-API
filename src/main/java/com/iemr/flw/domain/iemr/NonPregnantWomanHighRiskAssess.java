package com.iemr.flw.domain.iemr;

import lombok.Data;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "non_pregnant_high_risk_assess", schema = "db_iemr")
@Data
public class NonPregnantWomanHighRiskAssess {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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

    @Column(name = "mis_carriage_check")
    private String misCarriage;

    @Column(name = "home_delivery_check")
    private String homeDelivery;

    @Column(name = "medical_issues_check")
    private String medicalIssues;

    @Column(name = "past_csection_check")
    private String pastCSection;

    @Column(name = "is_high_risk")
    private Boolean isHighRisk;

    @Column(name = "visit_date")
    private Timestamp visitDate;

}
