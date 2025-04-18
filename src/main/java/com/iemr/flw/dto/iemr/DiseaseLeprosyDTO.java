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
    private String beneficiaryStatus;
    private String remark;
    private Integer userId;
    private Integer diseaseTypeId;
    private String referToName;
    private Integer beneficiaryStatusId;
    private Date dateOfDeath;
    private String placeOfDeath;
    private String otherPlaceOfDeath;
    private String reasonForDeath;
    private String otherReasonForDeath;

    // Getters and Setters
}