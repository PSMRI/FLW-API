package com.iemr.flw.dto.iemr;

import java.sql.Timestamp;

public class BenVisitDetailsDTO {
    private Long benVisitID;
    private Long beneficiaryRegID;
    private Long visitCode;
    private Integer providerServiceMapID;
    private Timestamp visitDateTime;
    private Short visitNo;
    private Short visitReasonID;
    private String visitReason;
    private Integer visitCategoryID;
    private String visitCategory;
    private String pcregnancyStatus;
    private String rCHID;
    private String healthFailityType;
    private String healthFacilityLocation;
    private String reportFilePath;
    private Boolean deleted;
    private String processed;
    private String createdBy;
    private Timestamp createdDate;
    private String modifiedBy;
    private Timestamp lastModDate;
    private String visitFlowStatusFlag;
    private Long vanSerialNo;
    private String vehicalNo;
    private Integer vanID;
    private Integer parkingPlaceID;
    private String syncedBy;
    private Timestamp syncedDate;
    private String reservedForChange;
    private String fpSideeffects;
    private String otherSideEffects;
    private String fpMethodFollowup;
    private String otherFollowUpForFpMethod;
    private String subVisitCategory;
}
