package com.iemr.flw.dto.iemr;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class MalariaSymptomsDTO {
    private boolean nausea;
    private boolean diarrhea;
    private boolean tiredness;
    private boolean vomiting;
    private boolean headache;
    private boolean feverMoreThanTwoWeeks;
    private boolean fluLikeIllness;
    private boolean shakingChills;
    private boolean muscleAches;

    // Getters & Setters
}