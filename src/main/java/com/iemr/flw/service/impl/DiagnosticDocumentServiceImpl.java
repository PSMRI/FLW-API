package com.iemr.flw.service.impl;

import com.iemr.flw.domain.iemr.DiagnosticDocument;
import com.iemr.flw.domain.iemr.DiagnosticOrder;
import com.iemr.flw.dto.DiagnosticDocumentContent;
import com.iemr.flw.integration.provider.DiagnosticDocumentAsset;
import com.iemr.flw.masterEnum.DiagnosticDocumentType;
import com.iemr.flw.repo.iemr.DiagnosticDocumentRepo;
import com.iemr.flw.repo.iemr.DiagnosticOrderRepo;
import com.iemr.flw.service.DiagnosticDocumentService;
import com.iemr.flw.utils.CryptoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.Locale;
import java.util.Optional;

@Service
public class DiagnosticDocumentServiceImpl implements DiagnosticDocumentService {

    private static final Logger logger = LoggerFactory.getLogger(DiagnosticDocumentServiceImpl.class);
    private static final String DEFAULT_CONTENT_TYPE = "application/pdf";

    @Value("${diagnostic.documents.storage-root}")
    private String storageRoot;

    @Autowired
    private DiagnosticDocumentRepo diagnosticDocumentRepo;

    @Autowired
    private DiagnosticOrderRepo diagnosticOrderRepo;

    @Autowired
    private CryptoUtil cryptoUtil;

    @Override
    public void ingestAsset(Long diagnosticOrderId, Long benRegID, String orderType, String externalOrderId,
            DiagnosticDocumentAsset asset) throws Exception {
        if (asset == null || asset.getBase64Content() == null) {
            logger.warn("Skipping document ingest for benRegID={}, orderType={}: empty asset content",
                    benRegID, orderType);
            return;
        }

        byte[] originalBytes = Base64.getDecoder().decode(asset.getBase64Content());
        String sha256Hash = sha256Hex(originalBytes);

        DiagnosticDocumentType documentType = DiagnosticDocumentType.from(orderType, asset.getType());
        Optional<DiagnosticDocument> existing = diagnosticDocumentRepo
                .findByDiagnosticOrderIdAndDocumentTypeAndDeletedFalse(diagnosticOrderId, documentType.name());
        if (existing.isPresent() && sha256Hash.equals(existing.get().getSha256Hash())) {
            logger.info("Diagnostic document already stored, skipping duplicate: diagnosticOrderId={}, documentType={}",
                    diagnosticOrderId, documentType);
            return;
        }

        long epochTime = Instant.now().toEpochMilli();
        String storedFileName = documentType.name() + ".enc";
        String relativeDir = benRegID + "/" + diagnosticOrderId;

        // Documents must never touch disk unencrypted: encrypt in memory first, write only ciphertext.
        String base64Original = Base64.getEncoder().encodeToString(originalBytes);
        String encryptedPayload = cryptoUtil.encrypt(base64Original);

        Path dir = Paths.get(storageRoot, relativeDir);
        Files.createDirectories(dir);
        Path filePath = dir.resolve(storedFileName);
        Files.write(filePath, encryptedPayload.getBytes(StandardCharsets.UTF_8));

        DiagnosticDocument document = existing.orElseGet(DiagnosticDocument::new);
        document.setDiagnosticOrderId(diagnosticOrderId);
        document.setBenRegID(benRegID);
        document.setOrderType(orderType);
        document.setAssetType(asset.getType());
        document.setDocumentType(documentType.name());
        document.setEpochTime(epochTime);
        document.setStoredFileName(storedFileName);
        document.setStoredPath(relativeDir + "/" + storedFileName);
        document.setSha256Hash(sha256Hash);
        document.setContentType(asset.getContentType() != null ? asset.getContentType() : DEFAULT_CONTENT_TYPE);
        document.setOriginalFileName(asset.getFileName());
        document.setCreatedBy("SYSTEM");
        try {
            diagnosticDocumentRepo.save(document);
        } catch (DataIntegrityViolationException dive) {
            logger.warn("Lost document upsert race for diagnosticOrderId={}, documentType={}", diagnosticOrderId, documentType);
            return;
        }

        logger.info("Diagnostic document ingested: benRegID={}, orderType={}, epochTime={}",
                benRegID, orderType, epochTime);
    }

    @Override
    public DiagnosticDocumentContent fetch(Long benRegID, DiagnosticDocumentType documentType, Long visitCode) throws Exception {
        String orderType = documentType.impliedOrderType().name();
        DiagnosticOrder order;
        if (visitCode != null) {
            order = diagnosticOrderRepo.findByBenRegIDAndVisitCodeAndOrderType(benRegID, visitCode, orderType)
                    .orElseThrow(() -> new Exception("No diagnostic order found for benRegID=" + benRegID
                            + ", visitCode=" + visitCode + ", orderType=" + orderType));
        } else {
            order = diagnosticOrderRepo
                    .findFirstByBenRegIDAndOrderTypeAndDeletedFalseOrderByCreatedDateDesc(benRegID, orderType)
                    .orElseThrow(() -> new Exception(
                            "No diagnostic order found for benRegID=" + benRegID + ", orderType=" + orderType));
        }

        DiagnosticDocument document = diagnosticDocumentRepo
                .findByDiagnosticOrderIdAndDocumentTypeAndDeletedFalse(order.getId(), documentType.name())
                .orElseThrow(() -> new Exception(
                        "No document found for diagnosticOrderId=" + order.getId() + ", documentType=" + documentType));

        Path filePath = Paths.get(storageRoot, document.getStoredPath());
        String encryptedPayload = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);

        String base64Original = cryptoUtil.decrypt(encryptedPayload);
        if (base64Original == null) {
            throw new Exception("Failed to decrypt document for benRegID=" + benRegID + ", documentType=" + documentType);
        }
        byte[] originalBytes = Base64.getDecoder().decode(base64Original);

        String recomputedHash = sha256Hex(originalBytes);
        if (!recomputedHash.equals(document.getSha256Hash())) {
            throw new Exception(
                    "Document integrity check failed for benRegID=" + benRegID + ", documentType=" + documentType);
        }

        logger.info("Diagnostic document fetched: benRegID={}, documentType={}, epochTime={}",
                benRegID, documentType, document.getEpochTime());

        return new DiagnosticDocumentContent(originalBytes, document.getContentType());
    }

    private static String sha256Hex(byte[] data) throws Exception {
        byte[] digest = MessageDigest.getInstance("SHA-256").digest(data);
        StringBuilder hex = new StringBuilder(digest.length * 2);
        for (byte b : digest) {
            hex.append(String.format(Locale.ROOT, "%02x", b));
        }
        return hex.toString();
    }
}
