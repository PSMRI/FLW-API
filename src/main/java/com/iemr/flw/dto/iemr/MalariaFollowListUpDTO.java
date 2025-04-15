package com.iemr.flw.dto.iemr;

import lombok.Data;
import java.util.Date;

@Data
public class MalariaFollowListUpDTO {
    private Long id;
    private Long benId;
    private Long houseHoldDetailsId;
    private Integer userId ;
    private Long diseaseId;
    private Date dateOfDiagnosis;
    private Date treatmentStartDate;
    private String treatmentGiven;

    private Boolean pfDay1;
    private Boolean pfDay2;
    private Boolean pfDay3;

    private Boolean pvDay1;
    private Boolean pvDay2;
    private Boolean pvDay3;
    private Boolean pvDay4;

    private Date treatmentCompletionDate;
    private Date referralDate;
}
