package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class HbncDTO {
    private Long beneficiaryId;
    private String hbncVisitDay;
    private LocalDate hbncDueDate;
    private LocalDate visitDate;
    private Boolean babyAlive;
    private LocalDate dateOfDeath;
    private String reasonForDeath;
    private String placeOfDeath;
    private String otherPlaceOfDeath;
    private Double babyWeight;
    private Boolean urinePassed;
    private Boolean stoolPassed;
    private Boolean diarrhoea;
    private Boolean vomiting;
    private Boolean convulsions;
    private String activity;
    private String sucking;
    private String breathing;
    private String chestIndrawing;
    private String temperature;
    private Boolean jaundice;
    private String umbilicalStumpCondition;
    private Boolean babyDischargedFromSNCU;
    private List<String> dischargeSummaryImages;
}
