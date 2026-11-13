package com.iemr.flw.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/** One test's result on an order: the order's own test, or MDR_RIF on an MTB/MTB_PLUS order. */
@Data
@NoArgsConstructor
public class DiagnosticResultItemDto {

    // XRAY_CHEST, MTB, MTB_PLUS or MDR_RIF
    private String resultType;
    private String providerStatus;
    private String resultSummary;
    private Boolean tbPresence;
    private Double tbConfidence;
    // MDR_RIF only: true = DR TB, false = Non DR TB, null = not done / indeterminate
    private Boolean drugResistancePresence;
}
