package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.io.File;
import java.sql.Timestamp;

@Data
public class ANCVisitDTO {

    private Long id;
    private Long benId;
    private Timestamp ancDate;
    private Integer ancVisit;
    private Boolean isAborted;
    private String abortionType;
    private String abortionFacility;
    private Timestamp abortionDate;
    private Integer weightOfPW;
    private Integer bpSystolic;
    private Integer bpDiastolic;
    private Integer pulseRate;
    private Double hb;
    private Integer fundalHeight;
    private Boolean urineAlbuminPresent;
    private Boolean bloodSugarTestDone;
    private Integer folicAcidTabs;
    private Integer ifaTabs;
    private Boolean isHighRisk;
    private String highRiskCondition;
    private String otherHighRiskCondition;
    private String referralFacility;
    private Boolean isHrpConfirmed;
    private String hrpIdentifiedBy;
    private Boolean isMaternalDeath;
    private String probableCauseOfDeath;
    private String otherCauseOfDeath;
    private Timestamp deathDate;
    private Boolean isBabyDelivered;
    private Boolean isActive;
    private Timestamp createdDate;
    private String createdBy;
    private Timestamp updatedDate;
    private String updatedBy;
    private Integer providerServiceMapID;
    private String filePath;


}

