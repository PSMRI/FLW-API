package com.iemr.flw.integration.provider;

import com.iemr.flw.masterEnum.DiagnosticOrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiagnosticPollResult {
    private DiagnosticOrderStatus status;
    private String providerOrderId;
    private String resultSummary;
    private String rawResponseJson;
    private List<DiagnosticDocumentAsset> assets;
    private String errorMessage;
    private Boolean tbPresence;
    private Double tbConfidence;
    private Boolean drugResistancePresence;
}
