package com.iemr.flw.dto.iemr;

import java.sql.Date;
import java.sql.Timestamp;

public class ANCCareDTO {
    private Long ID;
    private Long beneficiaryRegID;
    private Long benVisitID;
    private Integer providerServiceMapID;
    private Long visitCode;
    private Short visitNo;
    private String comolaintType;
    private String duration;
    private String description;
    private Date lastMenstrualPeriod_LMP;
    private Short gestationalAgeOrPeriodofAmenorrhea_POA;
    private Short trimesterNumber;
    private Date expectedDateofDelivery;
    private Boolean primiGravida;
    private Short gravida_G;
    private Short termDeliveries_T;
    private Short pretermDeliveries_P;
    private Short abortions_A;
    private Short livebirths_L;
    private String bloodGroup;
    private Boolean deleted;
    private String processed;
    private Long vanSerialNo;
    private Integer vanID;
    private String vehicalNo;
    private Integer parkingPlaceID;
    private String syncedBy;
    private Timestamp syncedDate;
    private String reservedForChange;
    private Integer stillBirth;
    private Short para;
    private String createdBy;
    private Timestamp createdDate;
    private String modifiedBy;
    private Timestamp lastModDate;
}
