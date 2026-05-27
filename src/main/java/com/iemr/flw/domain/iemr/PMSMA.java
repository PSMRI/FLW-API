package com.iemr.flw.domain.iemr;

import lombok.Data;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "t_pmsma", schema = "db_iemr")
@Data
public class PMSMA {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ben_id")
    private Long benId;

    @Column(name = "rch_number")
    private String rchNumber;

    @Column(name = "have_mcp_card")
    private Boolean haveMCPCard;

    @Column(name = "given_mcp_card")
    private Boolean givenMCPCard;

    @Column(name = "husband_name")
    private String husbandName;

    @Column(name = "address")
    private String address;

    @Column(name = "mobile_number")
    private Long mobileNumber;

    @Column(name = "num_anc")
    private Integer numANC;

    @Column(name = "weight")
    private Integer weight;

    @Column(name = "systolic_bp")
    private Integer systolicBloodPressure;

    @Column(name = "diatolic_bp")
    private Integer diastolicBloodPressure;

    @Column(name = "abdominal_check_up")
    private String abdominalCheckUp;

    @Column(name = "fetal_hrpm")
    private Integer fetalHRPM;

    @Column(name = "twin_pregnancy")
    private Boolean twinPregnancy;

    @Column(name = "urine_albumin")
    private String urineAlbumin;

    @Column(name = "haemoglobin_and_blood_group")
    private String haemoglobinAndBloodGroup;

    @Column(name = "hiv")
    private String hiv;

    @Column(name = "vdrl")
    private String vdrl;

    @Column(name = "hbsc")
    private String hbsc;

    @Column(name = "malaria")
    private String malaria;

    @Column(name = "hiv_test_during_anc")
    private Boolean hivTestDuringANC;

    @Column(name = "swollen_condition")
    private Boolean swollenCondition;

    @Column(name = "blood_sugar_test")
    private Boolean bloodSugarTest;

    @Column(name = "ultra_sound")
    private Boolean ultraSound;

    @Column(name = "iron_folic_acid")
    private Boolean ironFolicAcid;

    @Column(name = "calcium_supplementation")
    private Boolean calciumSupplementation;

    @Column(name = "tetanus_toxoid")
    private String tetanusToxoid;

    @Column(name = "lmp")
    private Timestamp lastMenstrualPeriod;

    @Column(name = "edd")
    private Timestamp expectedDateOfDelivery;

    @Column(name = "high_risk_symbols")
    private Boolean highriskSymbols;

    @Column(name = "high_risk_reason")
    private String highRiskReason;

    @Column(name = "high_risk_pregnant")
    private Boolean highRiskPregnant;

    @Column(name = "high_risk_pregnancy_referred")
    private Boolean highRiskPregnancyReferred;

    @Column(name = "birth_prep_nutri_and_planning")
    private Boolean birthPrepNutriAndFamilyPlanning;

    @Column(name = "medical_officer_sign")
    private String medicalOfficerSign;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_date")
    private Timestamp createdDate;

    @Column(name = "updated_date")
    private Timestamp updatedDate;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "visitDate")
    private Timestamp visitDate;

    @Column(name = "visitNo")
    private Integer visitNumber;

    @Column(name = "anyOtherHighRiskCondition")
    private String anyOtherHighRiskCondition;
}
