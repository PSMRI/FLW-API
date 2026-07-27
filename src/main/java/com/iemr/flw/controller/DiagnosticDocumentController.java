package com.iemr.flw.controller;

import com.iemr.flw.dto.DiagnosticDocumentContent;
import com.iemr.flw.masterEnum.DiagnosticDocumentType;
import com.iemr.flw.service.DiagnosticDocumentService;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * SECURITY: this endpoint carries decrypted document bytes and MUST sit behind proper
 * authentication/authorization in the real deployment. Today that's this app's existing
 * JwtUserIdValidationFilter (utils/JwtUserIdValidationFilter.java), which protects every
 * non-whitelisted path by default - do NOT add "/documents" to security.public-paths.
 */
@RestController
@RequestMapping("/documents")
public class DiagnosticDocumentController {

    private final Logger logger = LoggerFactory.getLogger(DiagnosticDocumentController.class);

    private final DiagnosticDocumentService diagnosticDocumentService;

    public DiagnosticDocumentController(DiagnosticDocumentService diagnosticDocumentService) {
        this.diagnosticDocumentService = diagnosticDocumentService;
    }

    @GetMapping
    @Operation(summary = "Fetch the most recent stored document for a beneficiary + document type "
            + "(XRAY_CHEST, XRAY_CHEST_ANNOTATED, CAD, MTB_REPORT, MTB_PLUS_REPORT, or MDR_RIF_REPORT)")
    public ResponseEntity<byte[]> getDocument(@RequestParam Long benId, @RequestParam DiagnosticDocumentType documentType) {
        try {
            DiagnosticDocumentContent content = diagnosticDocumentService.fetch(benId, documentType);
            MediaType mediaType;
            try {
                mediaType = MediaType.parseMediaType(content.getContentType());
            } catch (Exception e) {
                mediaType = MediaType.APPLICATION_PDF;
            }
            return ResponseEntity.ok().contentType(mediaType).body(content.getContent());
        } catch (Exception e) {
            logger.error("Error fetching document: benId={}, documentType={}: {}", benId, documentType, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
