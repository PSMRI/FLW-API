package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class PregnantWomanDTO {

    private Long id;
    private Long benId;
    private Timestamp registrationDate;
    private Long rchId;
    private Long mcpCardId;
    private Timestamp lmpDate;
    private String bloodGroup;
    private Integer weight;
    private Integer height;
    private String rprTestResult;
    private Timestamp dateOfRprTest;
    private String hivTestResult;
    private String hbsAgTestResult;
    private Timestamp dateOfHivTest;
    private Timestamp dateOfHbsAgTest;
    private String pastIllness;
    private String otherPastIllness;
    private Boolean isFirstPregnancyTest;
    private Integer numPrevPregnancy;
    private String pregComplication;
    private String otherComplication;
    private Boolean isActive;

    private String rhNegative;

    private String homeDelivery;

    private String badObstetric;

    private Timestamp createdDate;
    private String createdBy;
    private Timestamp updatedDate;
    private String updatedBy;
}
