package com.iemr.flw.dto.iemr;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class DeliveryOutcomeDTO {
    private Long id;
    private Long benId;

    @JsonFormat(pattern = "MMM d yyyy h:mm:ss a")
    private Timestamp dateOfDelivery;
    private String timeOfDelivery;
    private String placeOfDelivery;
    private String typeOfDelivery;
    private Boolean hadComplications;
    private String complication;
    private String causeOfDeath;
    private String otherCauseOfDeath;
    private String otherComplication;
    private Integer deliveryOutcome;
    private Integer liveBirth;
    private Integer stillBirth;
    @JsonFormat(pattern = "MMM d yyyy h:mm:ss a")
    private Timestamp dateOfDischarge;
    private String timeOfDischarge;
    private Boolean isJSYBenificiary;
    private Boolean isActive;
    @JsonFormat(pattern = "MMM d yyyy h:mm:ss a")
    private Timestamp createdDate;
    private String createdBy;
    @JsonFormat(pattern = "MMM d yyyy h:mm:ss a")
    private Timestamp updatedDate;
    private String updatedBy;
}
