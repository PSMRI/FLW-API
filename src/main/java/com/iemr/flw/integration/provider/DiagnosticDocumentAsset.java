package com.iemr.flw.integration.provider;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Source-agnostic document payload accepted by DiagnosticDocumentService.ingestAsset(...).
 * Not tied to any specific provider's DTO shape (e.g. EMR Lite's AssetDto) or to any
 * particular polling flow — deliberately decoupled so it can be fed from wherever the
 * caller ends up being wired in.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiagnosticDocumentAsset {
    private String type;
    private String contentType;
    private String fileName;
    private String base64Content;
}
