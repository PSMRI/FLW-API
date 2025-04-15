package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.util.Date;
@Data
public class DiseaseKalaAzarDTO {

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
    private String kalaAzarCaseStatus;
    private Integer kalaAzarCaseCount;
    private String rapidDiagnosticTest;
    private Date dateOfRdt;
    private Integer followUpPoint;
    private String referredTo;
    private String otherReferredFacility;
    private Date createdDate;
    private String createdBy;
    private Integer userId;
    private Integer diseaseTypeId;

    // Getters and Setters
}