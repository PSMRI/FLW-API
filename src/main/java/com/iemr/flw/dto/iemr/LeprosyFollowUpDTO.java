package com.iemr.flw.dto.iemr;

import lombok.Data;
import java.sql.Timestamp;
import java.util.Date;

@Data
public class LeprosyFollowUpDTO {

    private Long id;
    private Long benId;
    private Integer visitNumber;
    private Date followUpDate;

    private String treatmentStatus;
    private String mdtBlisterPackReceived;
    private Date treatmentCompleteDate;

    private String remarks;
    private Date homeVisitDate;

    private String leprosySymptoms;
    private String typeOfLeprosy;
    private Integer leprosySymptomsPosition;
    private String visitLabel;
    private String leprosyStatus;
    private String referredTo;
    private String referToName;
    private Date treatmentEndDate;

    private String mdtBlisterPackRecived;

    private Date treatmentStartDate;

    private String createdBy;

    private Timestamp createdDate;

    private String modifiedBy;

    private Timestamp lastModDate;
}