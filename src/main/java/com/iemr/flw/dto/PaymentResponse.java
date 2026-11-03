package com.iemr.flw.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PaymentResponse {

    @JsonProperty("submission_id")
    private String submissionId;

    private String status;

    private String receipt;
}