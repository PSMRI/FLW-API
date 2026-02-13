package com.iemr.flw.dto.iemr;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ChronicDiseaseVisitDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("benId")
    private Long benId;

    @JsonProperty("hhId")
    private Long hhId;

    @JsonProperty("formId")
    private String formId;

    @JsonProperty("version")
    private Integer version;

    @JsonProperty("visitNo")
    private Integer visitNo;

    @JsonProperty("followUpNo")
    private Integer followUpNo;

    @Column(name = "followUpDate")
    private LocalDate followUpDate;

    @JsonProperty("diagnosisCodes")
    private String diagnosisCodes;

    @JsonProperty("treatmentStartDate")
    private String treatmentStartDate; // yyyy-MM-dd

    @JsonProperty("formDataJson")
    private String formDataJson;
}
