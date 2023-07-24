package com.iemr.flw.domain.iemr;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "ANC_VISIT")
@Data
public class ANCVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "pwr_id")
    private Long pwrId;

    @Column(name = "anc_date")
    private Timestamp ancDate;

    @Column(name = "anc_period")
    private String ancPeriod;

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
    private Integer hb;

    @Column(name = "fundal_height")
    private Integer fundalHeight;

    @Column(name = "urine_albumin_present")
    private Boolean urineAlbuminPresent;

    @Column(name = "blood_sugar_test_done")
    private Boolean bloodSugarTestDone;

    @Column(name = "td_dose1_date")
    private Timestamp tdDose1Date;

    @Column(name = "td_dose2_date")
    private Timestamp tdDose2Date;

    @Column(name = "td_dose_booster_date")
    private Timestamp tdDoseBoosterDate;

    @Column(name = "folic_acid_tabs")
    private Integer folicAcidTabs;

    @Column(name = "ifa_tabs")
    private Integer ifaTabs;

    @Column(name = "is_high_risk")
    private Boolean isHighRisk;

    @Column(name = "high_risk_condition")
    private String highRiskCondition;

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

    @Column(name = "death_date")
    private Timestamp deathDate;

    @Column(name = "is_baby_delivered")
    private Boolean isBabyDelivered;

}
