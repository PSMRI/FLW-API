package com.iemr.flw.integration.provider.emrlite.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmrLiteResultRequest {
    private String externalOrderId;
    // "base64" includes report/image file content in the response's assets array; "none" returns
    // status/summary only. Callers pass true only for the one follow-up call made once an order
    // resolves COMPLETED - every other poll is a cheap status-only check.
    private String includeAssets;

    public EmrLiteResultRequest(String externalOrderId, boolean includeAssets) {
        this.externalOrderId = externalOrderId;
        this.includeAssets = includeAssets ? "base64" : "none";
    }
}
