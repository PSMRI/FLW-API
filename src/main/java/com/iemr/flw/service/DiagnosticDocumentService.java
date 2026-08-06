package com.iemr.flw.service;

import com.iemr.flw.dto.DiagnosticDocumentContent;
import com.iemr.flw.integration.provider.DiagnosticDocumentAsset;
import com.iemr.flw.masterEnum.DiagnosticDocumentType;

public interface DiagnosticDocumentService {

    void ingestAsset(Long diagnosticOrderId, Long beneficiaryId, String orderType, String externalOrderId,
            DiagnosticDocumentAsset asset) throws Exception;

    DiagnosticDocumentContent fetch(Long beneficiaryId, DiagnosticDocumentType documentType, Long visitCode) throws Exception;
}
