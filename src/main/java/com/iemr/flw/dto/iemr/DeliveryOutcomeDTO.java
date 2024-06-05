package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class DeliveryOutcomeDTO {
    private Long id;
    private Long benId;
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
    private Timestamp dateOfDischarge;
    private String timeOfDischarge;
    private Boolean isJSYBenificiary;
    private Boolean isActive;
    private Timestamp createdDate;
    private String createdBy;
    private Timestamp updatedDate;
    private String updatedBy;
}
