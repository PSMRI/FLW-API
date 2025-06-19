package com.iemr.flw.domain.iemr;

import lombok.Data;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "t_anc_visit", schema = "db_iemr")
@Data
public class ANCVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ben_id")
    private Long benId;

    @Column(name = "anc_date")
    private Timestamp ancDate;

    @Column(name = "anc_visit")
    private Integer ancVisit;

    @Column(name = "is_aborted")
    private Boolean isAborted;

    @Column(name = "abortion_type")
    private String abortionType;

    @Column(name = "abortion_facility")
    private String abortionFacility;

    @Column(name = "abortion_date")
    private Timestamp abortionDate;

    @Column(name = "weight_of_pw")
    private Integer weightOfPW;

    @Column(name = "bp_systolic")
    private Integer bpSystolic;

    @Column(name = "bp_diastolic")
    private Integer bpDiastolic;

    @Column(name = "pulse_rate")
    private Integer pulseRate;

    @Column(name = "hb")
    private Double hb;

    @Column(name = "fundal_height")
    private Integer fundalHeight;

    @Column(name = "urine_albumin_present")
    private Boolean urineAlbuminPresent;

    @Column(name = "blood_sugar_test_done")
    private Boolean bloodSugarTestDone;

    @Column(name = "folic_acid_tabs")
    private Integer folicAcidTabs;

    @Column(name = "ifa_tabs")
    private Integer ifaTabs;

    @Column(name = "is_high_risk")
    private Boolean isHighRisk;

    @Column(name = "high_risk_condition")
    private String highRiskCondition;

    @Column(name = "other_high_risk_condition")
    private String otherHighRiskCondition;

    @Column(name = "referral_facility")
    private String referralFacility;

    @Column(name = "is_hrp_confirmed")
    private Boolean isHrpConfirmed;

    @Column(name = "hrp_identified_by")
    private String hrpIdentifiedBy;

    @Column(name = "is_maternal_death")
    private Boolean isMaternalDeath;

    @Column(name = "probable_cause_of_death")
    private String probableCauseOfDeath;

    @Column(name = "other_cause_of_death")
    private String otherCauseOfDeath;

    @Column(name = "death_date")
    private Timestamp deathDate;

    @Column(name = "is_baby_delivered")
    private Boolean isBabyDelivered;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "created_date")
    private Timestamp createdDate;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_date")
    private Timestamp updatedDate;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "file_path")
    private String filePath;

}
