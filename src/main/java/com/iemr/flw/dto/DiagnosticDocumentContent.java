package com.iemr.flw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DiagnosticDocumentContent {
    private byte[] content;
    private String contentType;
}
