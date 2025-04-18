package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.util.Date;
@Data
public class DiseaseAesjeDto {
    private Long id;
    private Long benId;
    private Long houseHoldDetailsId;
    private Date visitDate;
    private String beneficiaryStatus;
    private Date dateOfDeath;
    private String placeOfDeath;
    private String otherPlaceOfDeath;
    private String reasonForDeath;
    private String otherReasonForDeath;
    private String aesJeCaseStatus;
    private Integer aesJeCaseCount;
    private Integer followUpPoint;
    private String referredTo;
    private String otherReferredFacility;
    private Date createdDate;
    private String createdBy;
    private Integer userId;
    private Integer diseaseTypeId;
    private String referToName;
    private Integer beneficiaryStatusId;
}