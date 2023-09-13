package com.iemr.flw.domain.iemr;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "non_pregnant_high_risk_track", schema = "db_iemr")
@Data
public class NonPregnantWomanHighRiskTrack {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "ben_id")
    private Long benId;

    @Column(name = "visit_date")
    private Timestamp visitDate;

    @Column(name = "anemia_check")
    private String anemia;

    @Column(name = "hypertension_check")
    private String hypertension;

    @Column(name = "diabetes_check")
    private String diabetes;

    @Column(name = "severe_anemia_check")
    private String severeAnemia;

    @Column(name = "fp_check")
    private String fp;

    @Column(name = "lmp_date")
    private Timestamp lmp;

    @Column(name = "missed_period_check")
    private String missedPeriod;

    @Column(name = "is_pregnant")
    private String isPregnant;

    @Column(name = "is_deleted")
    private Boolean isDeleted;
}
