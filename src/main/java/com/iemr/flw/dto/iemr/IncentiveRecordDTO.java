package com.iemr.flw.dto.iemr;

import jakarta.persistence.Column;
import jakarta.persistence.Transient;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class IncentiveRecordDTO {

    private Long id;

    private Long activityId;

    private Long ashaId;

    private Long benId;

    private Long amount;

    private String name;

    private Timestamp startDate;

    private Timestamp endDate;

    private Timestamp createdDate;

    private String createdBy;

    private Timestamp updatedDate;

    private String updatedBy;

    private Boolean isEligible;

    private Boolean isDefaultActivity;

   private Integer approvalStatus;

    private Integer verifiedByUserId;

    private String  verifiedByUserName;

    private String activityDec;

    private String groupName;

    private String reason;

    private String otherReason;

    private Boolean isClaimed;

}

