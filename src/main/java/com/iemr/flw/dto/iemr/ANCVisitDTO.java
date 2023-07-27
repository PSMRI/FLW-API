package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class ANCVisitDTO {

    private Long id;

    private Long pwrId;

    private Timestamp ancDate;

    private String ancPeriod;

    private Boolean isAborted;

    private String abortionType;

    private String abortionFacility;

    private Timestamp abortionDate;

    private Integer weightOfPW;

    private Integer bpSystolic;

    private Integer bpDiastolic;

    private Integer pulseRate;

    private Integer hb;

    private Integer fundalHeight;

    private Boolean urineAlbuminPresent;

    private Boolean bloodSugarTestDone;

    private Timestamp tdDose1Date;

    private Timestamp tdDose2Date;

    private Timestamp tdDoseBoosterDate;

    private Integer folicAcidTabs;

    private Integer ifaTabs;

    private Boolean isHighRisk;

    private String highRiskCondition;

    private String referralFacility;

    private Boolean isHrpConfirmed;

    private String hrpIdentifiedBy;

    private Boolean isMaternalDeath;

    private String probableCauseOfDeath;

    private Timestamp deathDate;

    private Boolean isBabyDelivered;

    private Timestamp createdDate;

    private String createdBy;
}

