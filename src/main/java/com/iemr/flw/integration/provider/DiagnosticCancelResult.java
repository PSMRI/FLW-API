package com.iemr.flw.integration.provider;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiagnosticCancelResult {
    private boolean success;
    private String rawResponseJson;
    private String errorMessage;
}