package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TBConfirmedCaseDTO {
    
    private Long benId;
    private Integer userId;
    private Integer suspectedTbId;
    private String regimenType;
    private LocalDate treatmentStartDate;
    private LocalDate expectedTreatmentCompletionDate;
    private LocalDate followUpDate;
    private String monthlyFollowUpDone;
    private String adherenceToMedicines;
    private Boolean anyDiscomfort;
    private Boolean treatmentCompleted;
    private LocalDate actualTreatmentCompletionDate;
    private String treatmentOutcome;
    private LocalDate dateOfDeath;
    private String placeOfDeath;
    private String reasonForDeath ;
    private String reasonForNotCompleting;

}
