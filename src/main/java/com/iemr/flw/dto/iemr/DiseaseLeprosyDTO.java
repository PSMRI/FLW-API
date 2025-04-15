package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.util.Date;
@Data
public class DiseaseLeprosyDTO {

    private Long id;
    private Long benId;
    private Long houseHoldDetailsId;
    private Date homeVisitDate;
    private String leprosyStatus;
    private String referredTo;
    private String otherReferredTo;
    private Date leprosyStatusDate;
    private String typeOfLeprosy;
    private Date followUpDate;
    private String diseaseStatus;
    private String remark;
    private Integer userId;
    private Integer diseaseTypeId;

    // Getters and Setters
}