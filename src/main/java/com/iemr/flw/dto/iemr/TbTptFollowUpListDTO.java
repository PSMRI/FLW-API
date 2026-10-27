package com.iemr.flw.dto.iemr;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TbTptFollowUpListDTO {
    @JsonProperty("regimen_type")
    private String regimenType;

    @JsonProperty("treatment_start_date")
    private String treatmentStartDate;

    @JsonProperty("expected_treatment_completion_date")
    private String expectedTreatmentCompletionDate;

    @JsonProperty("follow_up_date")
    private String followUpDate;

    @JsonProperty("monthly_follow_up")
    private String monthlyFollowUp;

    @JsonProperty("medicine_adherence")
    private String medicineAdherence;

    @JsonProperty("any_discomfort")
    private String anyDiscomfort;

    @JsonProperty("treatment_completed")
    private String treatmentCompleted;

    @JsonProperty("actual_completion_date")
    private String actualCompletionDate;

    @JsonProperty("tpt_outcome")
    private String tptOutcome;

    @JsonProperty("date_of_death")
    private String dateOfDeath;

    @JsonProperty("place_of_death")
    private String placeOfDeath;

    @JsonProperty("reason_for_death")
    private String reasonForDeath;
}
