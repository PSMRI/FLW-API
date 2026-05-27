package com.iemr.flw.dto.iemr;

import lombok.Data;
import java.sql.Timestamp;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class LeprosyGetFollowUpDTO {

    private Long id;
    private Long benId;
    private Integer visitNumber;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMM d, yyyy h:mm:ss a", timezone = "Asia/Kolkata")
    private Date followUpDate;

    private String treatmentStatus;
    private String mdtBlisterPackReceived;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMM d, yyyy h:mm:ss a", timezone = "Asia/Kolkata")
    private Date treatmentCompleteDate;

    private String remarks;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMM d, yyyy h:mm:ss a", timezone = "Asia/Kolkata")
    private Date homeVisitDate;

    private String leprosySymptoms;
    private String typeOfLeprosy;
    private Integer leprosySymptomsPosition;
    private String visitLabel;
    private String leprosyStatus;
    private String referredTo;
    private String referToName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMM d, yyyy h:mm:ss a", timezone = "Asia/Kolkata")
    private Date treatmentEndDate;

    private String mdtBlisterPackRecived;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMM d, yyyy h:mm:ss a", timezone = "Asia/Kolkata")
    private Date treatmentStartDate;

    private String createdBy;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMM d, yyyy h:mm:ss a", timezone = "Asia/Kolkata")
    private Timestamp createdDate;

    private String modifiedBy;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMM d, yyyy h:mm:ss a", timezone = "Asia/Kolkata")
    private Timestamp lastModDate;
}