package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class PmsmaDTO {
    private Long id;
    private Long benId;
    private String rchNumber;
    private Boolean haveMCPCard;
    private Boolean givenMCPCard;
    private String husbandName;
    private String address;
    private Long mobileNumber;
    private Integer numANC;
    private Integer weight;
    private Integer systolicBloodPressure;
    private Integer diastolicBloodPressure;
    private String abdominalCheckUp;
    private Integer fetalHRPM;
    private Boolean twinPregnancy;
    private String urineAlbumin;
    private String haemoglobinAndBloodGroup;
    private String hiv;
    private String vdrl;
    private String hbsc;
    private String malaria;
    private Boolean hivTestDuringANC;
    private Boolean swollenCondition;
    private Boolean bloodSugarTest;
    private Boolean ultraSound;
    private Boolean ironFolicAcid;
    private Boolean calciumSupplementation;
    private String tetanusToxoid;
    private Timestamp lastMenstrualPeriod;
    private Timestamp expectedDateOfDelivery;
    private Boolean highriskSymbols;
    private String highRiskReason;
    private Boolean highRiskPregnant;
    private Boolean highRiskPregnancyReferred;
    private Boolean birthPrepNutriAndFamilyPlanning;
    private String medicalOfficerSign;
    private Boolean isActive;
    private String createdBy;
    private Timestamp createdDate;
    private Timestamp updatedDate;
    private String updatedBy;

    private Timestamp visitDate;
    private Integer visitNumber;
    private String anyOtherHighRiskCondition;
}
