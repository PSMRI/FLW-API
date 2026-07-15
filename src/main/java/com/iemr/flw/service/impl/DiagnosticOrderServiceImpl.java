package com.iemr.flw.service.impl;

import com.iemr.flw.domain.iemr.DiagnosticOrder;
import com.iemr.flw.domain.iemr.DiagnosticResult;
import com.iemr.flw.dto.DiagnosticOrderRequestDto;
import com.iemr.flw.dto.DiagnosticOrderResultDto;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class DiagnosticOrderServiceImpl implements DiagnosticOrderService {

    private static final Logger logger = LoggerFactory.getLogger(DiagnosticOrderServiceImpl.class);

    @Value("${diagnostic.active-provider}")
    private String activeProvider;

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
        Long benRegID                = request.getBenRegID();
        Long visitCode               = request.getVisitCode();
        Integer providerServiceMapID = request.getProviderServiceMapID();
        DiagnosticOrderType orderType = DiagnosticOrderType.fromCode(request.getOrderType());
        String orderEvent            = request.getOrderEvent();
        String patientFirstName      = request.getPatient().getFirstName();
        String patientLastName       = request.getPatient().getLastName();
        String patientDateOfBirth    = request.getPatient().getDateOfBirth();
        String patientSex            = request.getPatient().getSex();

        String providerCode = activeProvider;
        String externalOrderId = String.format("%d-%d-%s", benRegID, visitCode, orderType.name());

        Optional<DiagnosticOrder> existing =
                diagnosticOrderRepo.findByBenRegIDAndVisitCodeAndOrderType(benRegID, visitCode, orderType.name());

        if (existing.isPresent() && !DiagnosticOrderStatus.FAILED.name().equals(existing.get().getStatus())) {
            // Idempotent: already pushed (or in flight / completed) for this visit+orderType — don't push again.
            return existing.get();
        }

        DiagnosticOrder order = existing.orElseGet(DiagnosticOrder::new);
        order.setOrderEvent(orderEvent);
        order.setBenRegID(benRegID);
        order.setVisitCode(visitCode);
        order.setProviderServiceMapID(providerServiceMapID);
        order.setProviderCode(providerCode);
        order.setOrderType(orderType.name());
        order.setExternalOrderId(externalOrderId);
        // Explicit reset required when reusing a previously-FAILED row: without this, a successful
        // retry would leave status=FAILED (the push-success branch below only sets providerOrderId),
        // and findDueForPoll only selects PENDING/IN_PROGRESS — so the order would be silently pushed
        // to the real device queue but never polled again.
        order.setStatus(DiagnosticOrderStatus.PENDING.name());
        order.setErrorMessage(null);
        order.setPatientFirstName(patientFirstName);
        order.setPatientLastName(patientLastName);
        order.setPatientDateOfBirth(patientDateOfBirth);
        order.setPatientSex(patientSex);
//        order.setCreatedBy(createdBy);
        try {
            order = diagnosticOrderRepo.save(order);
        } catch (DataIntegrityViolationException dive) {
            // Lost a create race to a concurrent identical request — return the winner idempotently
            // instead of surfacing a 5000 error for what is, from the caller's perspective, a success.
            Optional<DiagnosticOrder> winner = diagnosticOrderRepo
                    .findByBenRegIDAndVisitCodeAndOrderType(benRegID, visitCode, orderType.name());
            if (winner.isPresent()) {
                logger.warn("Lost create race for benRegID={}, visitCode={}, orderType={} — returning existing order id={}",
                        benRegID, visitCode, orderType, winner.get().getId());
                return winner.get();
            }
            throw dive; // not our natural-key constraint — genuinely unexpected, don't swallow
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

    @Override
    public DiagnosticOrderResultDto processResult(DiagnosticOrder order, DiagnosticPollResult pollResult) throws Exception {
        // One DiagnosticResult per DiagnosticOrder — each poll updates the same row in place
        // rather than accumulating a new row per attempt, since most intermediate polls before
        // completion carry nothing new and DiagnosticOrder.lastPolledAt/status already tracks
        // poll progress over time. The unique constraint on diagnostic_order_id also means a
        // concurrent poll (e.g. this manual trigger racing the scheduler) safely loses to a
        // DataIntegrityViolationException instead of creating a duplicate row.
        Optional<DiagnosticResult> existingResult = diagnosticResultRepo.findByDiagnosticOrderIdAndDeletedFalse(order.getId());
        DiagnosticResult result = existingResult.orElseGet(DiagnosticResult::new);
        result.setDiagnosticOrderId(order.getId());
        result.setBenRegID(order.getBenRegID());
        result.setProviderStatus(pollResult.getStatus().name());
        result.setResultSummary(pollResult.getResultSummary());
        result.setRawResponseJson(pollResult.getRawResponseJson());
        result.setTbPresence(pollResult.getTbPresence());
        result.setTbConfidence(pollResult.getTbConfidence());
        result.setCreatedBy("SYSTEM");
        try {
            diagnosticResultRepo.save(result);
        } catch (DataIntegrityViolationException dive) {
            // Lost an upsert race to a concurrent poll for the same order — the winner already
            // persisted an equivalent result; nothing further to do.
            logger.warn("Lost result upsert race for diagnosticOrderId={}", order.getId());
            result = diagnosticResultRepo.findByDiagnosticOrderIdAndDeletedFalse(order.getId()).orElse(result);
        }

        // Assets can arrive before the order is fully complete (e.g. the X-ray's PRIMARY_CAPTURE
        // while CAD is still pending), so ingestion runs on every poll, not gated on status.
        // ingestAsset() itself de-dupes re-sent assets, so this is safe to call repeatedly.
        if (pollResult.getAssets() != null) {
            for (DiagnosticDocumentAsset asset : pollResult.getAssets()) {
                try {
                    diagnosticDocumentService.ingestAsset(order.getId(), order.getBenRegID(), order.getOrderType(),
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
        dto.setDiagnosticOrderId(order.getId());
        dto.setOrderType(order.getOrderType());
        dto.setStatus(order.getStatus());
        dto.setErrorMessage(order.getErrorMessage());
        dto.setProviderStatus(result.getProviderStatus());
        dto.setResultSummary(result.getResultSummary());
        dto.setTbPresence(result.getTbPresence());
        dto.setTbConfidence(result.getTbConfidence());
        return dto;
    }

    @Override
    public DiagnosticPollResult pollOnce(DiagnosticOrder order) throws Exception {
        DiagnosticProvider provider = providerFactory.getProvider(order.getProviderCode());
        DiagnosticPollResult result = provider.pollResult(order, false);
        if (DiagnosticOrderStatus.COMPLETED.equals(result.getStatus())) {
            // Order just resolved COMPLETED on a status-only check — make the one follow-up call
            // that actually fetches the report/image content.
            result = provider.pollResult(order, true);
        }
        return result;
    }

    private DiagnosticOrder findLatestOrder(Long benRegID, String orderType) throws Exception {
        DiagnosticOrderType type = DiagnosticOrderType.fromCode(orderType);
        return diagnosticOrderRepo
                .findFirstByBenRegIDAndOrderTypeAndDeletedFalseOrderByCreatedDateDesc(benRegID, type.name())
                .orElseThrow(() -> new Exception(
                        "DiagnosticOrder not found for benRegID=" + benRegID + ", orderType=" + orderType));
    }

    @Override
    public DiagnosticOrderResultDto triggerManualPoll(Long benRegID, String orderType) throws Exception {
        DiagnosticOrder order = findLatestOrder(benRegID, orderType);
        DiagnosticProvider provider = providerFactory.getProvider(order.getProviderCode());
        // Ops-triggered manual poll: always fetch whatever is currently available, skip the
        // status-then-assets optimization used by the scheduler.
        DiagnosticPollResult pollResult = provider.pollResult(order, true);
        return processResult(order, pollResult);
    }

    @Override
    public DiagnosticOrder markTestCompleted(Long benRegID, String orderType) throws Exception {
        DiagnosticOrder order = findLatestOrder(benRegID, orderType);
        if (order.getTestCompletedAt() != null) {
            return order; // idempotent — already flagged, don't reset the poll clock
        }
        String status = order.getStatus();
        if (DiagnosticOrderStatus.COMPLETED.name().equals(status)
                || DiagnosticOrderStatus.FAILED.name().equals(status)
                || DiagnosticOrderStatus.CANCELLED.name().equals(status)) {
            throw new IllegalStateException("Cannot mark test completed for order in terminal status " + status);
        }
        order.setTestCompletedAt(new Timestamp(System.currentTimeMillis()));
        order.setStatus(DiagnosticOrderStatus.IN_PROGRESS.name());
        return diagnosticOrderRepo.save(order);
    }

    @Override
    public DiagnosticOrder getOrder(Long benRegID, String orderType) throws Exception {
        return findLatestOrder(benRegID, orderType);
    }

    @Override
    public List<DiagnosticOrder> getOrdersByBenRegId(Long benRegID) throws Exception {
        return diagnosticOrderRepo.findByBenRegID(benRegID);
    }

    @Override
    public DiagnosticOrderResultDto getOrderResult(Long benRegID, String orderType) {
        DiagnosticOrderResultDto dto = new DiagnosticOrderResultDto();
        dto.setOrderType(orderType);

        Optional<DiagnosticOrder> orderOpt =
                diagnosticOrderRepo.findFirstByBenRegIDAndOrderTypeAndDeletedFalseOrderByCreatedDateDesc(benRegID, orderType);
        if (orderOpt.isEmpty()) {
            dto.setStatus("NOT_FOUND");
            return dto;
        }

        DiagnosticOrder order = orderOpt.get();
        dto.setDiagnosticOrderId(order.getId());
        dto.setStatus(order.getStatus());
        dto.setErrorMessage(order.getErrorMessage());

        diagnosticResultRepo.findByDiagnosticOrderIdAndDeletedFalse(order.getId()).ifPresent(result -> {
            dto.setProviderStatus(result.getProviderStatus());
            dto.setResultSummary(result.getResultSummary());
            dto.setTbPresence(result.getTbPresence());
            dto.setTbConfidence(result.getTbConfidence());
        });
        return dto;
    }
}
