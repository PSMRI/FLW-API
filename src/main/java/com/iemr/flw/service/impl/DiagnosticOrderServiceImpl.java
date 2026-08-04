package com.iemr.flw.service.impl;

import com.iemr.flw.domain.iemr.DiagnosticOrder;
import com.iemr.flw.domain.iemr.DiagnosticResult;
import com.iemr.flw.dto.DiagnosticOrderRequestDto;
import com.iemr.flw.dto.DiagnosticOrderResultDto;
import com.iemr.flw.dto.DiagnosticOrderStatusSummaryDto;
import com.iemr.flw.dto.ManualDiagnosticResultRequestDto;
import com.iemr.flw.dto.VendorHealthDto;
import com.iemr.flw.integration.provider.DiagnosticDocumentAsset;
import com.iemr.flw.integration.provider.DiagnosticPollResult;
import com.iemr.flw.integration.provider.DiagnosticProvider;
import com.iemr.flw.integration.provider.DiagnosticProviderFactory;
import com.iemr.flw.integration.provider.DiagnosticPushResult;
import com.iemr.flw.masterEnum.DiagnosticOrderStatus;
import com.iemr.flw.masterEnum.DiagnosticOrderType;
import com.iemr.flw.repo.iemr.DiagnosticOrderRepo;
import com.iemr.flw.repo.iemr.DiagnosticResultRepo;
import com.iemr.flw.service.DiagnosticDocumentService;
import com.iemr.flw.service.DiagnosticOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class DiagnosticOrderServiceImpl implements DiagnosticOrderService {

    private static final Logger logger = LoggerFactory.getLogger(DiagnosticOrderServiceImpl.class);

    @Autowired
    private DiagnosticOrderRepo diagnosticOrderRepo;

    @Autowired
    private DiagnosticResultRepo diagnosticResultRepo;

    @Autowired
    private DiagnosticProviderFactory providerFactory;

    @Autowired
    private DiagnosticDocumentService diagnosticDocumentService;

    @Override
    public DiagnosticOrder createAndPushOrder(DiagnosticOrderRequestDto request) throws Exception {
        Long beneficiaryId            = request.getBeneficiaryId();
        Long visitCode               = request.getVisitCode();
        DiagnosticOrderType orderType = DiagnosticOrderType.fromCode(request.getOrderType());
        String orderEvent            = request.getOrderEvent();
        String patientFirstName      = request.getPatient().getFirstName();
        String patientLastName       = request.getPatient().getLastName();
        String patientDateOfBirth    = request.getPatient().getDateOfBirth();
        String patientSex            = request.getPatient().getSex();

        String providerCode = providerFactory.getProviderCodeForOrderType(orderType);
        String externalOrderId = String.format("%d-%d-%s", beneficiaryId, visitCode, orderType.name());

        String reasonForRefusal = request.getReasonForRefusal();
        if (reasonForRefusal != null) {
            return saveRefusedOrder(beneficiaryId, visitCode, orderType, orderEvent, providerCode, externalOrderId,
                    patientFirstName, patientLastName, patientDateOfBirth, patientSex, reasonForRefusal);
        }

        Optional<DiagnosticOrder> existing =
                diagnosticOrderRepo.findByBeneficiaryIdAndVisitCodeAndOrderType(beneficiaryId, visitCode, orderType.name());

        if (existing.isPresent() && !DiagnosticOrderStatus.FAILED.name().equals(existing.get().getStatus())) {
            return existing.get();
        }

        DiagnosticOrder order = existing.orElseGet(DiagnosticOrder::new);
        order.setOrderEvent(orderEvent);
        order.setBeneficiaryId(beneficiaryId);
        order.setVisitCode(visitCode);
        order.setProviderServiceName(providerCode);
        order.setProviderCode(providerCode);
        order.setOrderType(orderType.name());
        order.setExternalOrderId(externalOrderId);
        // Reset required when reusing a previously-FAILED row, otherwise a successful retry would
        // leave status=FAILED and findDueForPoll (PENDING/IN_PROGRESS only) would never poll it.
        order.setStatus(DiagnosticOrderStatus.PENDING.name());
        order.setErrorMessage(null);
        order.setPatientFirstName(patientFirstName);
        order.setPatientLastName(patientLastName);
        order.setPatientDateOfBirth(patientDateOfBirth);
        order.setPatientSex(patientSex);

        try {
            order = diagnosticOrderRepo.save(order);
        } catch (DataIntegrityViolationException dive) {
            Optional<DiagnosticOrder> winner = diagnosticOrderRepo
                    .findByBeneficiaryIdAndVisitCodeAndOrderType(beneficiaryId, visitCode, orderType.name());
            if (winner.isPresent()) {
                logger.warn("Lost create race for beneficiaryId={}, visitCode={}, orderType={} — returning existing order id={}",
                        beneficiaryId, visitCode, orderType, winner.get().getId());
                return winner.get();
            }
            throw dive;
        }

        if (providerCode == null || providerCode.isBlank()) {
            logger.info("No active vendor configured for orderType={}, beneficiaryId={} — order saved for manual entry",
                    orderType, beneficiaryId);
            return order;
        }

        try {
            DiagnosticProvider provider = providerFactory.getProvider(providerCode);
            DiagnosticPushResult pushResult = provider.pushOrder(order);
            order.setPushResponseJson(pushResult.getRawResponseJson());
            if (pushResult.isSuccess()) {
                order.setProviderOrderId(pushResult.getProviderOrderId());
            } else {
                order.setStatus(DiagnosticOrderStatus.FAILED.name());
                order.setErrorMessage(pushResult.getErrorMessage());
            }
        } catch (Exception e) {
            logger.error("Failed to push order to provider, orderId={}: {}", order.getId(), e.getMessage());
            order.setStatus(DiagnosticOrderStatus.FAILED.name());
            order.setErrorMessage(e.getMessage());
        }
        order = diagnosticOrderRepo.save(order);

        return order;
    }

    // Refusals are keyed to the latest order for this beneficiary+orderType (not the exact visitCode
    // of this request), since a refusal can be recorded outside the visit that originally created the
    // order. A COMPLETED latest order is left untouched (treated as "not found") and a new REFUSED row
    // is created instead. Refused orders are saved as-is and never pushed to the vendor.
    private DiagnosticOrder saveRefusedOrder(Long beneficiaryId, Long visitCode, DiagnosticOrderType orderType,
            String orderEvent, String providerCode, String externalOrderId, String patientFirstName,
            String patientLastName, String patientDateOfBirth, String patientSex, String reasonForRefusal) {
        Optional<DiagnosticOrder> latest = diagnosticOrderRepo
                .findFirstByBeneficiaryIdAndOrderTypeAndDeletedFalseOrderByCreatedDateDesc(beneficiaryId, orderType.name());
        if (latest.isPresent() && DiagnosticOrderStatus.COMPLETED.name().equals(latest.get().getStatus())) {
            latest = Optional.empty();
        }

        DiagnosticOrder order = latest.orElseGet(DiagnosticOrder::new);
        if (latest.isEmpty()) {
            order.setOrderEvent(orderEvent);
            order.setBeneficiaryId(beneficiaryId);
            order.setVisitCode(visitCode);
            order.setProviderServiceName(providerCode);
            order.setProviderCode(providerCode);
            order.setOrderType(orderType.name());
            order.setExternalOrderId(externalOrderId);
            order.setPatientFirstName(patientFirstName);
            order.setPatientLastName(patientLastName);
            order.setPatientDateOfBirth(patientDateOfBirth);
            order.setPatientSex(patientSex);
        }
        order.setStatus(DiagnosticOrderStatus.REFUSED.name());
        order.setReasonForRefusal(reasonForRefusal);
        order.setErrorMessage(null);

        try {
            return diagnosticOrderRepo.save(order);
        } catch (DataIntegrityViolationException dive) {
            Optional<DiagnosticOrder> winner = diagnosticOrderRepo
                    .findByBeneficiaryIdAndVisitCodeAndOrderType(beneficiaryId, visitCode, orderType.name());
            if (winner.isPresent()) {
                logger.warn("Lost create race for beneficiaryId={}, visitCode={}, orderType={} — returning existing order id={}",
                        beneficiaryId, visitCode, orderType, winner.get().getId());
                return winner.get();
            }
            throw dive;
        }
    }

    @Override
    public DiagnosticOrderResultDto processResult(DiagnosticOrder order, DiagnosticPollResult pollResult) throws Exception {
        Optional<DiagnosticResult> existingResult = diagnosticResultRepo.findByDiagnosticOrderIdAndDeletedFalse(order.getId());
        DiagnosticResult result = existingResult.orElseGet(DiagnosticResult::new);
        result.setDiagnosticOrderId(order.getId());
        result.setBeneficiaryId(order.getBeneficiaryId());
        result.setProviderStatus(pollResult.getStatus().name());
        result.setResultSummary(pollResult.getResultSummary());
        result.setRawResponseJson(pollResult.getRawResponseJson());
        result.setTbPresence(pollResult.getTbPresence());
        result.setTbConfidence(pollResult.getTbConfidence());
        result.setDrugResistancePresence(pollResult.getDrugResistancePresence());
        result.setCreatedBy("SYSTEM");
        try {
            diagnosticResultRepo.save(result);
        } catch (DataIntegrityViolationException dive) {
            logger.warn("Lost result upsert race for diagnosticOrderId={}", order.getId());
            result = diagnosticResultRepo.findByDiagnosticOrderIdAndDeletedFalse(order.getId()).orElse(result);
        }

        if (pollResult.getAssets() != null) {
            for (DiagnosticDocumentAsset asset : pollResult.getAssets()) {
                try {
                    diagnosticDocumentService.ingestAsset(order.getId(), order.getBeneficiaryId(), order.getOrderType(),
                            order.getExternalOrderId(), asset);
                } catch (Exception e) {
                    logger.error("Failed to ingest document asset for orderId={}, assetType={}, fileName={}: {}",
                            order.getId(), asset.getType(), asset.getFileName(), e.getMessage());
                }
            }
        }

        order.setStatus(pollResult.getStatus().name());
        order.setErrorMessage(pollResult.getErrorMessage());
        if (pollResult.getProviderOrderId() != null) {
            order.setProviderOrderId(pollResult.getProviderOrderId());
        }
        order.setLastPolledAt(new Timestamp(System.currentTimeMillis()));
        diagnosticOrderRepo.save(order);

        DiagnosticOrderResultDto dto = new DiagnosticOrderResultDto();
        dto.setExternalOrderId(order.getExternalOrderId());
        dto.setOrderType(order.getOrderType());
        dto.setStatus(order.getStatus());
        dto.setErrorMessage(order.getErrorMessage());
        dto.setReasonForRefusal(order.getReasonForRefusal());
        dto.setProviderStatus(result.getProviderStatus());
        dto.setResultSummary(result.getResultSummary());
        dto.setTbPresence(result.getTbPresence());
        dto.setTbConfidence(result.getTbConfidence());
        dto.setDrugResistancePresence(result.getDrugResistancePresence());
        return dto;
    }

    @Override
    public DiagnosticPollResult pollOnce(DiagnosticOrder order) throws Exception {
        DiagnosticProvider provider = providerFactory.getProvider(order.getProviderCode());
        DiagnosticPollResult result = provider.pollResult(order, false);
        if (DiagnosticOrderStatus.COMPLETED.equals(result.getStatus())) {
            // Save as COMPLETED now, without assets, so a failure fetching/ingesting assets below
            // doesn't also lose the already-confirmed completed status (see pollSingle's catch).
            processResult(order, result);
            result = provider.pollResult(order, true);
        }
        return result;
    }

    private DiagnosticOrder findLatestOrder(Long beneficiaryId, String orderType) throws Exception {
        DiagnosticOrderType type = DiagnosticOrderType.fromCode(orderType);
        return diagnosticOrderRepo
                .findFirstByBeneficiaryIdAndOrderTypeAndDeletedFalseOrderByCreatedDateDesc(beneficiaryId, type.name())
                .orElseThrow(() -> new Exception(
                        "DiagnosticOrder not found for beneficiaryId=" + beneficiaryId + ", orderType=" + orderType));
    }

    // Resolves a specific order by visitCode when given (retest disambiguation), otherwise
    // falls back to "latest" - matching the pre-multi-order default callers already rely on.
    private DiagnosticOrder resolveOrder(Long beneficiaryId, String orderType, Long visitCode) throws Exception {
        if (visitCode == null) {
            return findLatestOrder(beneficiaryId, orderType);
        }
        DiagnosticOrderType type = DiagnosticOrderType.fromCode(orderType);
        return diagnosticOrderRepo.findByBeneficiaryIdAndVisitCodeAndOrderType(beneficiaryId, visitCode, type.name())
                .orElseThrow(() -> new Exception("DiagnosticOrder not found for beneficiaryId=" + beneficiaryId
                        + ", visitCode=" + visitCode + ", orderType=" + orderType));
    }

    @Override
    public DiagnosticOrderResultDto triggerManualPoll(Long beneficiaryId, String orderType, Long visitCode) throws Exception {
        DiagnosticOrder order = resolveOrder(beneficiaryId, orderType, visitCode);
        DiagnosticProvider provider = providerFactory.getProvider(order.getProviderCode());
        DiagnosticPollResult pollResult = provider.pollResult(order, true);
        return processResult(order, pollResult);
    }

    @Override
    public DiagnosticOrder retryPoll(Long beneficiaryId, String orderType, Long visitCode) throws Exception {
        DiagnosticOrder order = resolveOrder(beneficiaryId, orderType, visitCode);
        String status = order.getStatus();

        if (DiagnosticOrderStatus.COMPLETED.name().equals(status)
                || DiagnosticOrderStatus.CANCELLED.name().equals(status)
                || DiagnosticOrderStatus.REFUSED.name().equals(status)) {
            throw new IllegalStateException("Cannot retry polling for order in terminal status " + status);
        }

        // retriedAt is kept as an audit timestamp only — the scheduler no longer uses it to anchor a
        // poll window; a retried order is simply picked up on the next regular tick like any other
        // PENDING order (see DiagnosticPollSchedulerService).
        order.setRetriedAt(new Timestamp(System.currentTimeMillis()));
        order.setStatus(DiagnosticOrderStatus.PENDING.name());
        order.setErrorMessage(null);
        return diagnosticOrderRepo.save(order);
    }

    @Override
    public DiagnosticOrder getOrder(Long beneficiaryId, String orderType, Long visitCode) throws Exception {
        return resolveOrder(beneficiaryId, orderType, visitCode);
    }

    @Override
    public List<DiagnosticOrder> getOrdersByBeneficiaryId(Long beneficiaryId) throws Exception {
        return diagnosticOrderRepo.findByBeneficiaryId(beneficiaryId);
    }

    @Override
    public DiagnosticOrderResultDto getOrderResult(Long beneficiaryId, String orderType, Long visitCode) {
        DiagnosticOrderResultDto dto = new DiagnosticOrderResultDto();
        dto.setOrderType(orderType);

        Optional<DiagnosticOrder> orderOpt = visitCode != null
                ? diagnosticOrderRepo.findByBeneficiaryIdAndVisitCodeAndOrderType(beneficiaryId, visitCode, orderType)
                : diagnosticOrderRepo.findFirstByBeneficiaryIdAndOrderTypeAndDeletedFalseOrderByCreatedDateDesc(beneficiaryId, orderType);
        if (orderOpt.isEmpty()) {
            dto.setStatus("NOT_FOUND");
            return dto;
        }

        DiagnosticOrder order = orderOpt.get();
        dto.setExternalOrderId(order.getExternalOrderId());
        dto.setStatus(order.getStatus());
        dto.setErrorMessage(order.getErrorMessage());
        dto.setReasonForRefusal(order.getReasonForRefusal());

        diagnosticResultRepo.findByDiagnosticOrderIdAndDeletedFalse(order.getId()).ifPresent(result -> {
            dto.setProviderStatus(result.getProviderStatus());
            dto.setResultSummary(result.getResultSummary());
            dto.setTbPresence(result.getTbPresence());
            dto.setTbConfidence(result.getTbConfidence());
            dto.setDrugResistancePresence(result.getDrugResistancePresence());
        });
        return dto;
    }

    @Override
    public DiagnosticOrderStatusSummaryDto getOrderStatusSummary(String orderType, Integer villageId,
            Integer providerServiceMapId) {
        DiagnosticOrderType type = DiagnosticOrderType.fromCode(orderType);
        List<Long> awaitingProviderResult = diagnosticOrderRepo
                .findBeneficiaryIdsAwaitingProviderResult(type.name(), villageId, providerServiceMapId);
        List<Long> completed = diagnosticOrderRepo
                .findBeneficiaryIdsCompleted(type.name(), villageId, providerServiceMapId);
        List<Long> pollingTimedOut = diagnosticOrderRepo
                .findBeneficiaryIdsPollingTimedOut(type.name(), villageId, providerServiceMapId);
        List<Long> failed = diagnosticOrderRepo
                .findBeneficiaryIdsFailed(type.name(), villageId, providerServiceMapId);
        List<Long> refused = diagnosticOrderRepo
                .findBeneficiaryIdsRefused(type.name(), villageId, providerServiceMapId);
        return new DiagnosticOrderStatusSummaryDto(awaitingProviderResult, completed, pollingTimedOut, failed, refused);
    }

    @Override
    public VendorHealthDto checkVendorHealth(String orderType) throws Exception {
        DiagnosticOrderType type = DiagnosticOrderType.fromCode(orderType);
        String providerCode = providerFactory.getProviderCodeForOrderType(type);
        boolean isDeviceIntegrated = providerCode != null && !providerCode.isBlank();
        boolean isConnected = false;
        if (isDeviceIntegrated) {
            try {
                isConnected = providerFactory.getProvider(providerCode).checkHealth(type);
            } catch (Exception e) {
                logger.warn("Vendor health check failed for providerCode={}, orderType={}: {}",
                        providerCode, orderType, e.getMessage());
            }
        }
        return new VendorHealthDto(isConnected, isDeviceIntegrated);
    }

    @Override
    public DiagnosticOrderResultDto submitManualResult(ManualDiagnosticResultRequestDto request) throws Exception {
        DiagnosticOrder order = findLatestOrder(request.getBeneficiaryId(), request.getOrderType());
        if (DiagnosticOrderStatus.COMPLETED.name().equals(order.getStatus())) {
            throw new IllegalStateException("Cannot submit manual result: order is already COMPLETED (beneficiaryId="
                    + request.getBeneficiaryId() + ", orderType=" + request.getOrderType() + ")");
        }
        DiagnosticPollResult pollResult = new DiagnosticPollResult(
                DiagnosticOrderStatus.COMPLETED, null, request.getResultSummary(), null, null, null, null, null, null);
        return processResult(order, pollResult);
    }
}
