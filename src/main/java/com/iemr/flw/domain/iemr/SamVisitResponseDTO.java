package com.iemr.flw.domain.iemr;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class SamVisitResponseDTO {

    @JsonProperty("beneficiary_id")
    private Long beneficiaryId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @JsonProperty("visit_date")
    private LocalDate visitDate;

    @JsonProperty("visit_label")
    private String visitLabel;

    @JsonProperty("muac")
    private Double muac;

    @JsonProperty("weight_for_height_status")
    private String weightForHeightStatus;

    @JsonProperty("is_child_referred_nrc")
    private String isChildReferredNrc;

    @JsonProperty("is_child_admitted_nrc")
    private String isChildAdmittedNrc;

    @JsonProperty("nrc_admission_date")
    private String nrcAdmissionDate;

    @JsonProperty("is_child_discharged_nrc")
    private String isChildDischargedNrc;

    @JsonProperty("nrc_discharge_date")
    private String nrcDischargeDate;

    @JsonProperty("follow_up_visit_date")
    private List<String> followUpVisitDate;

    @JsonProperty("sam_status")
    private String samStatus;

    @JsonProperty("discharge_summary")
    private String dischargeSummary;

    @JsonProperty("view_discharge_docs")
    private List<String> viewDischargeDocs; // Base64 array or JSON array
}
