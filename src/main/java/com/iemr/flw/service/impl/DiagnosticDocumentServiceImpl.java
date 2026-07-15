package com.iemr.flw.service.impl;

import com.iemr.flw.domain.iemr.DiagnosticDocument;
import com.iemr.flw.dto.DiagnosticDocumentContent;
import com.iemr.flw.integration.provider.DiagnosticDocumentAsset;
import com.iemr.flw.masterEnum.DiagnosticDocumentType;
import com.iemr.flw.repo.iemr.DiagnosticDocumentRepo;
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

    // App-level encryption here protects against someone reading files directly off a
    // live/mounted filesystem. It does NOT replace OS-level full-disk encryption (e.g. LUKS),
    // which is what protects against physical theft of the drive - both should be in place.
    @Value("${diagnostic.documents.storage-root}")
    private String storageRoot;

    @Autowired
    private DiagnosticDocumentRepo diagnosticDocumentRepo;

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

        // Classified automatically from the provider's asset.type - callers never pass "CAD"
        // themselves. One DiagnosticDocument per (beneficiary, documentType) - always the latest
        // of that kind, regardless of which order produced it, mirroring the same "current state,
        // not history" approach used for DiagnosticResult. The provider resends the full current
        // asset list on every poll (e.g. the X-ray's PRIMARY_CAPTURE is included again once CAD
        // finishes, even though it's the same image), so skip re-writing when unchanged; a
        // differing hash overwrites that type's file with the newer content.
        DiagnosticDocumentType documentType = DiagnosticDocumentType.fromAssetType(asset.getType());
        Optional<DiagnosticDocument> existing =
                diagnosticDocumentRepo.findByBenRegIDAndDocumentTypeAndDeletedFalse(benRegID, documentType.name());
        if (existing.isPresent() && sha256Hash.equals(existing.get().getSha256Hash())) {
            logger.info("Diagnostic document already stored, skipping duplicate: benRegID={}, documentType={}",
                    benRegID, documentType);
            return;
        }

        long epochTime = Instant.now().toEpochMilli();
        String storedFileName = documentType.name() + ".enc";
        String relativeDir = String.valueOf(benRegID);

        // Documents must never touch disk unencrypted, not even briefly: encrypt in memory
        // first, then write only the ciphertext.
        String base64Original = Base64.getEncoder().encodeToString(originalBytes);
        // NOTE: reuses existing app-wide CryptoUtil (AES-128-ECB, hardcoded key) - known weaker-than-ideal
        // scheme, tracked as follow-up tech debt, not addressed in this change.
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
            // Lost an upsert race to a concurrent ingest for the same beneficiary+type (e.g. a
            // manual poll racing the scheduler) — the winner already persisted equivalent content.
            logger.warn("Lost document upsert race for benRegID={}, documentType={}", benRegID, documentType);
            return;
        }

        // Audit log: benRegID/orderType/timestamp only - never raw document content or the encryption key.
        logger.info("Diagnostic document ingested: benRegID={}, orderType={}, epochTime={}",
                benRegID, orderType, epochTime);
    }

    @Override
    public DiagnosticDocumentContent fetch(Long benRegID, DiagnosticDocumentType documentType) throws Exception {
        DiagnosticDocument document = diagnosticDocumentRepo
                .findByBenRegIDAndDocumentTypeAndDeletedFalse(benRegID, documentType.name())
                .orElseThrow(() -> new Exception(
                        "No document found for benRegID=" + benRegID + ", documentType=" + documentType));

        Path filePath = Paths.get(storageRoot, document.getStoredPath());
        String encryptedPayload = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);

        // NOTE: reuses existing app-wide CryptoUtil (AES-128-ECB, hardcoded key) - known weaker-than-ideal
        // scheme, tracked as follow-up tech debt, not addressed in this change.
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

        // Audit log: benRegID/documentType/timestamp only - never raw document content or the encryption key.
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
