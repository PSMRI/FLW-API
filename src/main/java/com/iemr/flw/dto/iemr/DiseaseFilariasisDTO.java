package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.util.Date;
@Data
public class DiseaseFilariasisDTO {

    private Long id;
    private Long benId;
    private Long houseHoldDetailsId;
    private Boolean sufferingFromFilariasis;
    private String affectedBodyPart;
    private Date mdaHomeVisitDate;
    private String doseStatus;
    private Integer filariasisCaseCount;
    private String otherDoseStatusDetails;
    private String medicineSideEffect;
    private String otherSideEffectDetails;
    private Date createdDate;
    private String createdBy;
    private Integer userId;
    private Integer diseaseTypeId;


}