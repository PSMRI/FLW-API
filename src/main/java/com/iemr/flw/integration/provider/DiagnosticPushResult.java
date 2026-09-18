package com.iemr.flw.integration.provider;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiagnosticPushResult {
    private boolean success;
    private String providerOrderId;
    private String rawResponseJson;
    private String errorMessage;
}