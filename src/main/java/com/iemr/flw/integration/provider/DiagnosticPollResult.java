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
    // The MDR RIF test's own summary (e.g. "DR TB", "Non DR TB", "Indeterminate"), run on the same
    // order when MTB is positive. Null when no RIF test ran or it isn't finished.
    private String drugResistanceSummary;
    // The MDR RIF test's own status from the vendor (PENDING / COMPLETED / FAILED). Null when there is
    // no RIF test.
    private String drugResistanceStatus;

    public DiagnosticPollResult(DiagnosticOrderStatus status, String providerOrderId, String resultSummary,
                                String rawResponseJson, List<DiagnosticDocumentAsset> assets, String errorMessage,
                                Boolean tbPresence, Double tbConfidence, Boolean drugResistancePresence,
                                String drugResistanceSummary) {
        this(status, providerOrderId, resultSummary, rawResponseJson, assets, errorMessage, tbPresence,
                tbConfidence, drugResistancePresence, drugResistanceSummary, null);
    }
}
