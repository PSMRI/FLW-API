package com.iemr.flw.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DiagnosticOrderResultDto {

    private Long diagnosticOrderId;
    private String orderType;
    private String status;
    private String providerStatus;
    private String resultSummary;
    private String errorMessage;
    private Boolean tbPresence;
    private Double tbConfidence;
}