package com.iemr.flw.domain.iemr;

import lombok.Data;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "pregnant_high_risk_assess", schema = "db_iemr")
@Data
public class PregnantWomanHighRiskAssess {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "ben_id")
    private Long benId;

    @Column(name = "visit_date")
    private Timestamp visitDate;

    @Column(name = "delivery_count_check")
    private String noOfDeliveries;

    @Column(name = "delivery_gap_check")
    private String timeLessThan18m;

    @Column(name = "height_short_check")
    private String heightShort;

    @Column(name = "age_check")
    private String age;

    @Column(name = "rh_negative_check")
    private String rhNegative;

    @Column(name = "home_delivery_check")
    private String homeDelivery;

    @Column(name = "bad_obstetric_check")
    private String badObstetric;

    @Column(name = "multiple_pregnancy_check")
    private String multiplePregnancy;

    @Column(name = "lmp_date")
    private Timestamp lmpDate;

    @Column(name = "edd_date")
    private Timestamp edd;

    @Column(name = "is_high_risk")
    private Boolean isHighRisk;
}
