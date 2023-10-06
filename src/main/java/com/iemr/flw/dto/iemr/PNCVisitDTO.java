package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class PNCVisitDTO {
    private Long id;
    private Long benId;
//    private Integer pncVisit;
    private Integer pncPeriod;
    private Timestamp pncDate;
    private Integer ifaTabsGiven;
    private Boolean anyContraceptionMethod;
    private String contraceptionMethod;
    private String otherPpcMethod;
    private String motherDangerSign;
    private String otherDangerSign;
    private String referralFacility;
    private Boolean motherDeath;
    private Timestamp deathDate;
    private String causeOfDeath;
    private String otherDeathCause;
    private String placeOfDeath;
    private String remarks;
    private Boolean isActive;
    private Timestamp createdDate;
    private String createdBy;
    private Timestamp updatedDate;
    private String updatedBy;
}
