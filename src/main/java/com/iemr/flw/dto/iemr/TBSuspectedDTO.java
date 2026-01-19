package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class TBSuspectedDTO {

    private Long id;

    private Long benId;
    private String visitLabel;
    private Timestamp visitDate;
    private String typeOfTBCase;
    private Timestamp visitDate;
    private String reasonForSuspicion;
    private Boolean isSputumCollected;
    private String sputumSubmittedAt;
    private String nikshayId;
    private String sputumTestResult;
    private Boolean isChestXRayDone;
    private String chestXRayResult;
    private String referralFacility;
    private Boolean isTBConfirmed;
    private Boolean isConfirmed;
    private Boolean isDRTBConfirmed;
    private Boolean referred;
    private String followUps;
}
