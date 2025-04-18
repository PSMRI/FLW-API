package com.iemr.flw.dto.iemr;

import jakarta.persistence.Column;
import lombok.Data;

import java.util.Date;
@Data
public class DiseaseMalariaDTO {

    private Long id;
    private Long benId;
    private Long houseHoldDetailsId;
    private Date screeningDate;
    private String beneficiaryStatus;
    private Date dateOfDeath;
    private String placeOfDeath;
    private String otherPlaceOfDeath;
    private String reasonForDeath;
    private String otherReasonForDeath;
    private String symptoms;
    private String caseStatus;
    private String rapidDiagnosticTest;
    private Date dateOfRdt;
    private String slideTestPf;
    private String slideTestPv;
    private Date dateOfSlideTest;
    private String slideNo;
    private Integer referredTo;
    private String otherReferredFacility;
    private String remarks;
    private Date dateOfVisitBySupervisor;
    private Integer diseaseTypeId;
    private Integer userId;
    private Date createdDate;
    private String createdBy;
    private Date dateOfVisitSupervisor;
    private boolean feverMoreThanTwoWeeks;
    private boolean fluLikeIllness;
    private boolean shakingChills;
    private boolean headache;
    private boolean muscleAches;
    private boolean tiredness;
    private boolean nausea;
    private boolean vomiting;
    private boolean diarrhea;
    private String referToName;
    private Integer caseStatusId;

}