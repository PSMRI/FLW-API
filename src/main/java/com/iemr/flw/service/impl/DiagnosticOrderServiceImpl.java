package com.iemr.flw.service.impl;

import com.iemr.flw.domain.iemr.BenVisitDetail;
import com.iemr.flw.domain.iemr.DiagnosticOrder;
import com.iemr.flw.domain.iemr.DiagnosticResult;
import com.iemr.flw.domain.iemr.TBSuspected;
import com.iemr.flw.domain.iemr.User;
import com.iemr.flw.dto.DiagnosticOrderRequestDto;
import com.iemr.flw.dto.DiagnosticOrderResultDto;
import com.iemr.flw.dto.DiagnosticOrderStatusSummaryDto;
import com.iemr.flw.dto.ManualDiagnosticResultRequestDto;
import com.iemr.flw.dto.VendorHealthDto;
import com.iemr.flw.integration.provider.DiagnosticCancelResult;
import com.iemr.flw.integration.provider.DiagnosticDocumentAsset;
import com.iemr.flw.integration.provider.DiagnosticPollResult;
import com.iemr.flw.integration.provider.DiagnosticProvider;
import com.iemr.flw.integration.provider.DiagnosticProviderFactory;
import com.iemr.flw.integration.provider.DiagnosticPushResult;
import com.iemr.flw.masterEnum.DiagnosticOrderStatus;
import com.iemr.flw.masterEnum.DiagnosticOrderType;
import com.iemr.flw.dto.iemr.UserServiceRoleDTO;
import com.iemr.flw.repo.identity.BeneficiaryRepo;
import com.iemr.flw.repo.iemr.DiagnosticOrderRepo;
import com.iemr.flw.repo.iemr.DiagnosticResultRepo;
import com.iemr.flw.repo.iemr.EmployeeMasterRepo;
import com.iemr.flw.repo.iemr.TBSuspectedRepo;
import com.iemr.flw.repo.iemr.UserServiceRoleRepo;
import com.iemr.flw.service.CampConfigService;
import com.iemr.flw.service.DiagnosticDocumentService;
import com.iemr.flw.service.DiagnosticOrderService;
import com.iemr.flw.service.TBStopVisitService;
import com.iemr.flw.utils.JwtUtil;
import com.google.common.util.concurrent.Striped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;

@Service
public class DiagnosticOrderServiceImpl implements DiagnosticOrderService {

    private static final Logger logger = LoggerFactory.getLogger(DiagnosticOrderServiceImpl.class);

    private static final Set<String> BLOCKING_STATUSES = Set.of(
            DiagnosticOrderStatus.PENDING.name(),
            DiagnosticOrderStatus.COMPLETED.name(),
            DiagnosticOrderStatus.MANUAL_ENTRY.name());

    private static final Set<String> NON_REUSABLE_ON_CLOSE_STATUSES = Set.of(
            DiagnosticOrderStatus.COMPLETED.name(),
            DiagnosticOrderStatus.FAILED.name(),
            DiagnosticOrderStatus.CLOSED.name());

    // Invalid test outcomes: the result is still recorded (result row, tb_suspected write-back, documents)
    // but the order is CLOSED instead of COMPLETED, so a retest can be pushed. The vendor reports an
    // invalid MTB/MDR_RIF run as "Error-2"; a manual entry uses "Invalid/Error".
    private static final String XRAY_INVALID_RESULT = "AI Invalid Result";
    private static final String SPUTUM_INVALID_POLLED_RESULT = "Error-2";
    private static final String SPUTUM_INVALID_MANUAL_RESULT = "Invalid/Error";

    @Autowired
    private DiagnosticOrderRepo diagnosticOrderRepo;

    @Autowired
    private DiagnosticResultRepo diagnosticResultRepo;

    @Autowired
    private TBSuspectedRepo tbSuspectedRepo;

    @Autowired
    private DiagnosticProviderFactory providerFactory;

    @Autowired
    private DiagnosticDocumentService diagnosticDocumentService;

    @Autowired
    private CampConfigService campConfigService;

    @Autowired
    private BeneficiaryRepo beneficiaryRepo;

    @Autowired
    private TBStopVisitService tbStopVisitService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmployeeMasterRepo employeeMasterRepo;

    @Override
    public DiagnosticOrder createAndPushOrderByUser(DiagnosticOrderRequestDto request, String jwtToken) throws Exception {
        return createAndPushOrder(request, resolveActingUserName(jwtToken));
    }

    private String resolveActingUserName(String jwtToken) {
        try {
            User user = employeeMasterRepo.findUserByUserID(jwtUtil.extractUserId(jwtToken));
            if (user != null && user.getUserName() != null && !user.getUserName().isBlank()) {
                return user.getUserName();
            }
        } catch (Exception e) {
            logger.warn("Could not resolve acting user's username from m_user: {}", e.getMessage());
        }
        return jwtUtil.extractUsername(jwtToken);
    }

    @Override
    public DiagnosticOrder createAndPushOrderAsSystem(DiagnosticOrderRequestDto request) throws Exception {
        return createAndPushOrder(request, "SYSTEM");
    }

    // One lock per beneficiary, held across visit lookup → dedup check → insert → vendor push. Without it,
    // two simultaneous pushes for the same beneficiary could both pass the dedup check (or both create a
    // visit for today) and send the same patient to the vendor twice — shown as duplicate names on the
    // TrueNat machine. Keyed by beneficiary, not orderType, because the visit is shared across order types.
    // In-memory, so it only serialises within this JVM: correct while orders are created only by the
    // single van server that can reach the vendor. Striped keeps the lock registry a fixed size for the
    // JVM's lifetime: the same beneficiary always maps to the same stripe, while unrelated beneficiaries
    // that happen to share a stripe merely wait on each other briefly. Stripes are reentrant, and no path
    // ever holds two beneficiaries' locks at once, so sharing a stripe can't deadlock.
    private static final int BENEFICIARY_LOCK_STRIPES = 1024;
    private final Striped<Lock> beneficiaryOrderLocks = Striped.lock(BENEFICIARY_LOCK_STRIPES);

    private <T> T withBeneficiaryLock(Long beneficiaryId, Callable<T> action) throws Exception {
        if (beneficiaryId == null) {
            throw new IllegalArgumentException("beneficiaryId is required");
        }
        Lock lock = beneficiaryOrderLocks.get(beneficiaryId);
        lock.lock();
        try {
            return action.call();
        } finally {
            lock.unlock();
        }
    }

    private DiagnosticOrder createAndPushOrder(DiagnosticOrderRequestDto request, String createdBy) throws Exception {
        return withBeneficiaryLock(request.getBeneficiaryId(), () -> createAndPushOrderLocked(request, createdBy));
    }

    private DiagnosticOrder createAndPushOrderLocked(DiagnosticOrderRequestDto request, String createdBy) throws Exception {
        Long beneficiaryId            = request.getBeneficiaryId();
        DiagnosticOrderType orderType = DiagnosticOrderType.fromCode(request.getOrderType());
        String orderEvent            = request.getOrderEvent();
        String patientFirstName      = request.getPatient().getFirstName();
        String patientLastName       = request.getPatient().getLastName();
        String patientDateOfBirth    = request.getPatient().getDateOfBirth();
        String patientSex            = request.getPatient().getSex();

        String reasonToClose = request.getReasonToClose();
        if (reasonToClose != null) {
            return closeOrder(beneficiaryId, orderType, orderEvent, patientFirstName, patientLastName,
                    patientDateOfBirth, patientSex, reasonToClose, createdBy);
        }

        Integer vanID = campConfigService.getVanID();
        Integer parkingPlaceID = campConfigService.getParkingPlaceID();

        Long beneficiaryRegID = beneficiaryRepo.getRegIDFromBenId(beneficiaryId);
        if (beneficiaryRegID == null) {
            throw new Exception("No beneficiaryRegID found for beneficiaryId=" + beneficiaryId);
        }
        BenVisitDetail visit = tbStopVisitService.getOrCreateVisitForToday(beneficiaryRegID, null, createdBy, vanID,
                parkingPlaceID);
        Long visitCode = visit.getVisitCode();

        String providerCode = providerFactory.getProviderCodeForOrderType(orderType);
        String externalOrderId = String.format("%s-%d-%s", UUID.randomUUID(), visitCode, orderType.name());

        Optional<DiagnosticOrder> latestForType = diagnosticOrderRepo
                .findFirstByBeneficiaryIdAndOrderTypeAndDeletedFalseOrderByCreatedDateDesc(beneficiaryId, orderType.name());

        Optional<DiagnosticOrder> existing = (latestForType.isPresent() && visitCode.equals(latestForType.get().getVisitCode()))
                ? latestForType
                : diagnosticOrderRepo.findFirstByBeneficiaryIdAndVisitCodeAndOrderTypeAndDeletedFalseOrderByCreatedDateDesc(beneficiaryId, visitCode, orderType.name());

        if (existing.isPresent()) {
            String existingStatus = existing.get().getStatus();
            if (!DiagnosticOrderStatus.FAILED.name().equals(existingStatus)
                    && !DiagnosticOrderStatus.CLOSED.name().equals(existingStatus)) {
                // Still active or already resolved (PENDING/COMPLETED/legacy IN_PROGRESS/MANUAL_ENTRY)
                // — returned as-is.
                return existing.get();
            }
            // FAILED and CLOSED are never reused, even for the same visit — both stay as-is for
            // history, and a fresh push always creates a brand new row instead of overwriting either.
        }

        if (latestForType.isPresent()
                && BLOCKING_STATUSES.contains(latestForType.get().getStatus())
                && !visitCode.equals(latestForType.get().getVisitCode())) {
            DiagnosticOrder blocker = latestForType.get();
            logger.info("Duplicate order push suppressed for beneficiaryId={}, orderType={}: unresolved order id={} "
                            + "(visitCode={}, status={}) already exists — returning it instead of pushing a new order for visitCode={}",
                    beneficiaryId, orderType, blocker.getId(), blocker.getVisitCode(), blocker.getStatus(), visitCode);
            return blocker;
        }

        // Neither FAILED nor CLOSED is ever reused above, so this is always a brand new row — a
        // beneficiary's diagnostic order history is a sequence of rows, not one row overwritten in place.
        DiagnosticOrder order = new DiagnosticOrder();
        order.setVanID(vanID);
        order.setParkingPlaceID(parkingPlaceID);
        order.setOrderEvent(orderEvent);
        order.setBeneficiaryId(beneficiaryId);
        order.setVisitCode(visitCode);
        order.setProviderServiceName(providerCode);
        order.setProviderCode(providerCode);
        order.setOrderType(orderType.name());
        order.setExternalOrderId(externalOrderId);
        boolean noVendor = providerCode == null || providerCode.isBlank();
        order.setStatus(noVendor ? DiagnosticOrderStatus.MANUAL_ENTRY.name() : DiagnosticOrderStatus.PENDING.name());
        order.setPatientFirstName(patientFirstName);
        order.setPatientLastName(patientLastName);
        order.setPatientDateOfBirth(patientDateOfBirth);
        order.setPatientSex(patientSex);
        order.setCreatedBy(createdBy);
        order.setModifiedBy(createdBy);

        try {
            order = diagnosticOrderRepo.save(order);
        } catch (DataIntegrityViolationException dive) {
            Optional<DiagnosticOrder> winner = diagnosticOrderRepo
                    .findFirstByBeneficiaryIdAndVisitCodeAndOrderTypeAndDeletedFalseOrderByCreatedDateDesc(beneficiaryId, visitCode, orderType.name());
            if (winner.isPresent()) {
                logger.warn("Lost create race for beneficiaryId={}, visitCode={}, orderType={} — returning existing order id={}",
                        beneficiaryId, visitCode, orderType, winner.get().getId());
                return winner.get();
            }
            throw dive;
        }

        if (noVendor) {
            logger.info("No active vendor configured for orderType={}, beneficiaryId={} — order saved for manual entry",
                    orderType, beneficiaryId);
            // Status is MANUAL_ENTRY (set above) — awaiting manual entry via submitManualResult.
            order = diagnosticOrderRepo.save(order);
            if (order.getVanSerialNo() == null) diagnosticOrderRepo.updateVanSerialNo(order.getId());
            return order;
        }

        return pushToProvider(order, providerCode);
    }

    // Pushes an already-saved PENDING order to the vendor and saves the outcome — FAILED (with the
    // error) if the push is rejected or throws. Shared by createAndPushOrder and pushRetestOrder.
    private DiagnosticOrder pushToProvider(DiagnosticOrder order, String providerCode) {
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
        // Check the field itself, not a point-in-time "isNew" flag — a flag computed before save()
        // goes stale forever once the row exists (e.g. this row was the "winner" of a lost create
        // race below, or a prior attempt crashed between save() and this line). Re-checking the
        // real value self-heals any row still stuck at NULL, on whichever call next touches it.
        if (order.getVanSerialNo() == null) diagnosticOrderRepo.updateVanSerialNo(order.getId());

        return order;
    }

    // Shared by createAndPushOrder (push with reasonToClose) and submitManualResult (manualResult
    // with reasonToClose) — resolves the same visit/provider/externalOrderId a normal push would, then
    // closes the order via saveRefusedOrder. Identical outcome regardless of which endpoint triggered it.
    private DiagnosticOrder closeOrder(Long beneficiaryId, DiagnosticOrderType orderType, String orderEvent,
                                       String patientFirstName, String patientLastName, String patientDateOfBirth, String patientSex,
                                       String reasonToClose, String actingUserId) throws Exception {
        Integer vanID = campConfigService.getVanID();
        Integer parkingPlaceID = campConfigService.getParkingPlaceID();

        Long beneficiaryRegID = beneficiaryRepo.getRegIDFromBenId(beneficiaryId);
        if (beneficiaryRegID == null) {
            throw new Exception("No beneficiaryRegID found for beneficiaryId=" + beneficiaryId);
        }
        BenVisitDetail visit = tbStopVisitService.getOrCreateVisitForToday(beneficiaryRegID, null, actingUserId, vanID,
                parkingPlaceID);
        Long visitCode = visit.getVisitCode();

        String providerCode = providerFactory.getProviderCodeForOrderType(orderType);
        String externalOrderId = String.format("%s-%d-%s", UUID.randomUUID(), visitCode, orderType.name());
        return saveRefusedOrder(beneficiaryId, visitCode, orderType, orderEvent, providerCode, externalOrderId,
                patientFirstName, patientLastName, patientDateOfBirth, patientSex, reasonToClose, actingUserId);
    }

    // manualResult's reasonToClose variant has no patient/orderEvent in its request — it sources them
    // from the beneficiary's own most recent order for this orderType (whatever its status), since that
    // information already exists there. No prior order at all means there's nothing to source it from.
    private DiagnosticOrder closeOrderManually(Long beneficiaryId, DiagnosticOrderType orderType,
                                               String reasonToClose, String actingUserId) throws Exception {
        return withBeneficiaryLock(beneficiaryId, () -> {
            DiagnosticOrder source = diagnosticOrderRepo
                    .findFirstByBeneficiaryIdAndOrderTypeAndDeletedFalseOrderByCreatedDateDesc(beneficiaryId, orderType.name())
                    .orElseThrow(() -> new Exception("No diagnostic order found for beneficiaryId=" + beneficiaryId
                            + ", orderType=" + orderType.name() + " — cannot close a record that was never created"));
            return closeOrder(beneficiaryId, orderType, source.getOrderEvent(), source.getPatientFirstName(),
                    source.getPatientLastName(), source.getPatientDateOfBirth(), source.getPatientSex(), reasonToClose,
                    actingUserId);
        });
    }

    // Refusals are keyed to the latest order for this beneficiary+orderType (not the exact visitCode
    // of this request), since a refusal can be recorded outside the visit that originally created the
    // order. A COMPLETED, FAILED or already-CLOSED latest order is left untouched (treated as "not found")
    // and a new CLOSED row is created instead, so its history (e.g. a FAILED row's errorMessage) survives.
    // Refused orders are saved as-is and never pushed to the vendor.
    private DiagnosticOrder saveRefusedOrder(Long beneficiaryId, Long visitCode, DiagnosticOrderType orderType,
                                             String orderEvent, String providerCode, String externalOrderId, String patientFirstName,
                                             String patientLastName, String patientDateOfBirth, String patientSex, String reasonToClose,
                                             String actingUserId) {
        Optional<DiagnosticOrder> latest = diagnosticOrderRepo
                .findFirstByBeneficiaryIdAndOrderTypeAndDeletedFalseOrderByCreatedDateDesc(beneficiaryId, orderType.name());
        if (latest.isPresent() && NON_REUSABLE_ON_CLOSE_STATUSES.contains(latest.get().getStatus())) {
            latest = Optional.empty();
        }

        DiagnosticOrder order = latest.orElseGet(DiagnosticOrder::new);
        if (order.getVanID() == null) {
            order.setVanID(campConfigService.getVanID());
            order.setParkingPlaceID(campConfigService.getParkingPlaceID());
        }
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
            order.setCreatedBy(actingUserId);
        }
        order.setStatus(DiagnosticOrderStatus.CLOSED.name());
        order.setReasonToClose(reasonToClose);
        order.setErrorMessage(null);
        order.setModifiedBy(actingUserId);
        order.setManuallyEnteredBy(actingUserId);
        order.setProcessed("N");

        try {
            order = diagnosticOrderRepo.save(order);
            if (order.getVanSerialNo() == null) diagnosticOrderRepo.updateVanSerialNo(order.getId());
            notifyProviderOrderClosed(order, reasonToClose);
            return order;
        } catch (DataIntegrityViolationException dive) {
            Optional<DiagnosticOrder> winner = diagnosticOrderRepo
                    .findFirstByBeneficiaryIdAndVisitCodeAndOrderTypeAndDeletedFalseOrderByCreatedDateDesc(beneficiaryId, visitCode, orderType.name());
            if (winner.isPresent()) {
                logger.warn("Lost create race for beneficiaryId={}, visitCode={}, orderType={} — returning existing order id={}",
                        beneficiaryId, visitCode, orderType, winner.get().getId());
                return winner.get();
            }
            throw dive;
        }
    }

    @Override
    public void notifyProviderOrderClosed(DiagnosticOrder order, String reason) {
        String providerCode = order.getProviderCode();
        if (providerCode == null || providerCode.isBlank()) {
            return; // no vendor was ever involved (manual-entry order)
        }
        if (order.getPushResponseJson() == null || order.getPushResponseJson().isEmpty()) {
            return; // never actually confirmed as pushed to the vendor — nothing to cancel there
        }
        try {
            DiagnosticCancelResult result = providerFactory.getProvider(providerCode).cancelOrder(order, reason);
            order.setCancelResponseJson(result.getRawResponseJson());
            order.setProcessed("N");
            diagnosticOrderRepo.save(order);
        } catch (Exception e) {
            // Transport-level failure only (unreachable, timeout) — a provider-level rejection is
            // already captured in the returned result above, not thrown.
            logger.warn("Failed to notify provider of order cancellation for externalOrderId={}: {}",
                    order.getExternalOrderId(), e.getMessage());
        }
    }

    @Override
    public DiagnosticOrderResultDto processResult(DiagnosticOrder order, DiagnosticPollResult pollResult) throws Exception {
        return processResult(order, pollResult, false, "SYSTEM");
    }

    private DiagnosticOrderResultDto processResult(DiagnosticOrder order, DiagnosticPollResult pollResult,
                                                   boolean writeBackWhenClosed, String actingUser) throws Exception {
        Optional<DiagnosticResult> existingResult = diagnosticResultRepo.findByExternalOrderIdAndDeletedFalse(order.getExternalOrderId());
        DiagnosticResult result = existingResult.orElseGet(DiagnosticResult::new);
        result.setExternalOrderId(order.getExternalOrderId());
        result.setBeneficiaryId(order.getBeneficiaryId());
        result.setProviderStatus(pollResult.getStatus().name());
        result.setResultSummary(pollResult.getResultSummary());
        // The vendor embeds each asset's base64 file content directly inline in its response body
        // when assets are requested — never persist that raw JSON verbatim (it's unencrypted, unlike
        // the same content on local disk). Only store rawResponseJson from the asset-free call.
        if (pollResult.getAssets() == null || pollResult.getAssets().isEmpty()) {
            result.setRawResponseJson(pollResult.getRawResponseJson());
        }
        result.setTbPresence(pollResult.getTbPresence());
        result.setTbConfidence(pollResult.getTbConfidence());
        result.setDrugResistancePresence(pollResult.getDrugResistancePresence());
        result.setCreatedBy(actingUser);
        result.setModifiedBy(actingUser);
        if (result.getVanID() == null) {
            // Inherit from the parent order rather than re-reading Redis — the result belongs
            // to whichever van originated the order, not whichever van happens to be polling now.
            result.setVanID(order.getVanID());
            result.setParkingPlaceID(order.getParkingPlaceID());
        }
        try {
            diagnosticResultRepo.save(result);
            if (result.getVanSerialNo() == null) diagnosticResultRepo.updateVanSerialNo(result.getId());
        } catch (DataIntegrityViolationException dive) {
            logger.warn("Lost result upsert race for externalOrderId={}", order.getExternalOrderId());
            result = diagnosticResultRepo.findByExternalOrderIdAndDeletedFalse(order.getExternalOrderId()).orElse(result);
        }

        ingestAssets(order, pollResult);

        order.setStatus(pollResult.getStatus().name());
        order.setErrorMessage(pollResult.getErrorMessage());
        if (pollResult.getProviderOrderId() != null) {
            order.setProviderOrderId(pollResult.getProviderOrderId());
        }
        order.setLastPolledAt(new Timestamp(System.currentTimeMillis()));
        order.setModifiedBy(actingUser);
        order.setProcessed("N");
        diagnosticOrderRepo.save(order);

        if (DiagnosticOrderStatus.COMPLETED.name().equals(order.getStatus())
                || (writeBackWhenClosed && DiagnosticOrderStatus.CLOSED.name().equals(order.getStatus()))) {
            recordTbSuspectedResult(order, result);
        }

        DiagnosticOrderResultDto dto = new DiagnosticOrderResultDto();
        dto.setExternalOrderId(order.getExternalOrderId());
        dto.setOrderType(order.getOrderType());
        dto.setStatus(order.getStatus());
        dto.setErrorMessage(order.getErrorMessage());
        dto.setReasonToClose(order.getReasonToClose());
        dto.setProviderStatus(result.getProviderStatus());
        dto.setResultSummary(result.getResultSummary());
        dto.setTbPresence(result.getTbPresence());
        dto.setTbConfidence(result.getTbConfidence());
        dto.setDrugResistancePresence(result.getDrugResistancePresence());
        return dto;
    }

    // Best-effort link from a diagnostic order back to the tb_suspected referral that presumably
    // prompted it. Not every diagnostic order originates from a TB referral, so a miss here is
    // expected and must never fail/block the push or result flow — just log and move on.
    private TBSuspected findTbSuspected(Long beneficiaryId) {
        TBSuspected tbSuspected = tbSuspectedRepo.findFirstByBenIdOrderByCreatedDateDesc(beneficiaryId);
        if (tbSuspected == null) {
            logger.warn("No tb_suspected row for beneficiaryId={} — skipping write-back", beneficiaryId);
        }
        return tbSuspected;
    }

    private void recordTbSuspectedResult(DiagnosticOrder order, DiagnosticResult result) {
        TBSuspected tbSuspected = findTbSuspected(order.getBeneficiaryId());
        if (tbSuspected == null) return;
        DiagnosticOrderType type = DiagnosticOrderType.fromCode(order.getOrderType());
        if (type == DiagnosticOrderType.XRAY_CHEST) {
            tbSuspected.setIsChestXRayDone(true);
            tbSuspected.setChestXRayResult(result.getResultSummary());
        } else if (type == DiagnosticOrderType.MDR_RIF) {
            tbSuspected.setIsSputumCollected(true);
            tbSuspected.setMdrRifResult(result.getResultSummary());
        } else if (type == DiagnosticOrderType.MTB){
            tbSuspected.setIsSputumCollected(true);
            tbSuspected.setSputumTestResult(result.getResultSummary());
        }
        tbSuspected.setProcessed("N");
        tbSuspectedRepo.save(tbSuspected);
    }

    // Shared by processResult (single combined save+ingest call, e.g. submitManualResult) and
    // pollOnce's second, asset-only call (which must NOT re-save the already-saved result/order
    // fields a second time).
    private void ingestAssets(DiagnosticOrder order, DiagnosticPollResult pollResult) {
        if (pollResult.getAssets() == null) {
            return;
        }
        for (DiagnosticDocumentAsset asset : pollResult.getAssets()) {
            try {
                diagnosticDocumentService.ingestAsset(order.getBeneficiaryId(), order.getOrderType(),
                        order.getExternalOrderId(), asset);
            } catch (Exception e) {
                logger.error("Failed to ingest document asset for orderId={}, assetType={}, fileName={}: {}",
                        order.getId(), asset.getType(), asset.getFileName(), e.getMessage());
            }
        }
    }

    @Override
    public DiagnosticPollResult pollOnce(DiagnosticOrder order) throws Exception {
        // A null pushResponseJson means the order was never actually confirmed as pushed to the
        // vendor (e.g. a stale/legacy row) — polling the vendor for it would be meaningless, so
        // fail it outright instead of burning a vendor call every tick.
        if (order.getPushResponseJson() == null || order.getPushResponseJson().isEmpty()) {
            order.setStatus(DiagnosticOrderStatus.FAILED.name());
            order.setErrorMessage("No push response recorded for this order — cannot poll");
            order.setLastPolledAt(new Timestamp(System.currentTimeMillis()));
            order.setModifiedBy("SYSTEM");
            order.setProcessed("N");
            diagnosticOrderRepo.save(order);
            return null;
        }
        DiagnosticProvider provider = providerFactory.getProvider(order.getProviderCode());
        DiagnosticPollResult result = provider.pollResult(order, false);
        if (DiagnosticOrderStatus.COMPLETED.equals(result.getStatus())) {
            // Save as COMPLETED (or CLOSED for an invalid outcome) now, without assets, so a failure
            // fetching/ingesting assets below doesn't also lose the already-confirmed status (see
            // pollSingle's catch).
            boolean invalid = closeIfInvalidPolledResult(order, result);
            processResult(order, result, invalid, "SYSTEM");
            if (invalid) {
                // Before the asset fetch below: if that throws, this CLOSED order is never polled
                // again, so the retest must already exist by then.
                pushRetestOrder(order);
            }
            result = provider.pollResult(order, true);
            // Only ingest the documents this time — the result/order fields were already saved above
            // and this second, asset-bearing response carries the same status/summary, so re-saving
            // them again would just be a redundant duplicate write.
            ingestAssets(order, result);
        } else {
            processResult(order, result);
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
        return diagnosticOrderRepo.findFirstByBeneficiaryIdAndVisitCodeAndOrderTypeAndDeletedFalseOrderByCreatedDateDesc(beneficiaryId, visitCode, type.name())
                .orElseThrow(() -> new Exception("DiagnosticOrder not found for beneficiaryId=" + beneficiaryId
                        + ", visitCode=" + visitCode + ", orderType=" + orderType));
    }

    @Override
    public DiagnosticOrderResultDto triggerManualPoll(Long beneficiaryId, String orderType, Long visitCode) throws Exception {
        DiagnosticOrder order = resolveOrder(beneficiaryId, orderType, visitCode);
        DiagnosticProvider provider = providerFactory.getProvider(order.getProviderCode());
        DiagnosticPollResult pollResult = provider.pollResult(order, true);
        boolean invalid = closeIfInvalidPolledResult(order, pollResult);
        return processResult(order, pollResult, invalid, "SYSTEM");
    }

    @Override
    public DiagnosticOrder retryPoll(Long beneficiaryId, String orderType, Long visitCode) throws Exception {
        DiagnosticOrder order = resolveOrder(beneficiaryId, orderType, visitCode);
        String status = order.getStatus();

        if (DiagnosticOrderStatus.COMPLETED.name().equals(status)
                || DiagnosticOrderStatus.CLOSED.name().equals(status)) {
            throw new IllegalStateException("Cannot retry polling for order in terminal status " + status
                    + " — create a new order instead");
        }
        if (DiagnosticOrderStatus.MANUAL_ENTRY.name().equals(status)) {
            throw new IllegalStateException("Cannot retry polling for a MANUAL_ENTRY order — no vendor is involved, "
                    + "submit the result via manualResult instead");
        }

        // retriedAt is kept as an audit timestamp only — the scheduler no longer uses it to anchor a
        // poll window; a retried order is simply picked up on the next regular tick like any other
        // PENDING order (see DiagnosticPollSchedulerService).
        order.setRetriedAt(new Timestamp(System.currentTimeMillis()));
        order.setStatus(DiagnosticOrderStatus.PENDING.name());
        order.setErrorMessage(null);
        order.setProcessed("N");
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
        Optional<DiagnosticOrder> orderOpt = visitCode != null
                ? diagnosticOrderRepo.findFirstByBeneficiaryIdAndVisitCodeAndOrderTypeAndDeletedFalseOrderByCreatedDateDesc(beneficiaryId, visitCode, orderType)
                : diagnosticOrderRepo.findFirstByBeneficiaryIdAndOrderTypeAndDeletedFalseOrderByCreatedDateDesc(beneficiaryId, orderType);
        if (orderOpt.isEmpty()) {
            DiagnosticOrderResultDto dto = new DiagnosticOrderResultDto();
            dto.setOrderType(orderType);
            dto.setStatus("NOT_FOUND");
            return dto;
        }

        DiagnosticOrder order = orderOpt.get();
        DiagnosticOrderResultDto dto = toResultDto(order);

        diagnosticResultRepo.findByExternalOrderIdAndDeletedFalse(order.getExternalOrderId()).ifPresent(result -> {
            dto.setProviderStatus(result.getProviderStatus());
            dto.setResultSummary(result.getResultSummary());
            dto.setTbPresence(result.getTbPresence());
            dto.setTbConfidence(result.getTbConfidence());
            dto.setDrugResistancePresence(result.getDrugResistancePresence());
        });
        return dto;
    }

    private DiagnosticOrderResultDto toResultDto(DiagnosticOrder order) {
        DiagnosticOrderResultDto dto = new DiagnosticOrderResultDto();
        dto.setExternalOrderId(order.getExternalOrderId());
        dto.setOrderType(order.getOrderType());
        dto.setStatus(order.getStatus());
        dto.setErrorMessage(order.getErrorMessage());
        dto.setReasonToClose(order.getReasonToClose());
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
        List<Long> failed = diagnosticOrderRepo
                .findBeneficiaryIdsFailed(type.name(), villageId, providerServiceMapId);
        List<Long> closed = diagnosticOrderRepo
                .findBeneficiaryIdsClosed(type.name(), villageId, providerServiceMapId);
        List<Long> awaitingManualEntry = diagnosticOrderRepo
                .findBeneficiaryIdsAwaitingManualEntry(type.name(), villageId, providerServiceMapId);
        return new DiagnosticOrderStatusSummaryDto(awaitingProviderResult, completed, failed, closed, awaitingManualEntry);
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
    public DiagnosticOrderResultDto submitManualResult(ManualDiagnosticResultRequestDto request, String jwtToken)
            throws Exception {
        boolean hasResult = request.getResultSummary() != null && !request.getResultSummary().isBlank();
        boolean hasReasonToClose = request.getReasonToClose() != null && !request.getReasonToClose().isBlank();
        if (hasResult == hasReasonToClose) {
            throw new IllegalArgumentException("Exactly one of resultSummary or reasonToClose must be provided");
        }

        String actingUserName = resolveActingUserName(jwtToken);

        if (hasReasonToClose) {
            DiagnosticOrderType orderType = DiagnosticOrderType.fromCode(request.getOrderType());
            DiagnosticOrder closed = closeOrderManually(request.getBeneficiaryId(), orderType,
                    request.getReasonToClose(), actingUserName);
            return toResultDto(closed);
        }

        DiagnosticOrder order = findLatestOrder(request.getBeneficiaryId(), request.getOrderType());
        if (DiagnosticOrderStatus.COMPLETED.name().equals(order.getStatus())) {
            throw new IllegalStateException("Cannot submit manual result: order is already COMPLETED (beneficiaryId="
                    + request.getBeneficiaryId() + ", orderType=" + request.getOrderType() + ")");
        }
        order.setManuallyEnteredBy(actingUserName);
        // An invalid test outcome is still recorded exactly like a normal result (result row +
        // tb_suspected write-back), but the order is CLOSED rather than COMPLETED.
        DiagnosticOrderStatus status = isInvalidResult(order.getOrderType(), request.getResultSummary(),
                SPUTUM_INVALID_MANUAL_RESULT)
                ? DiagnosticOrderStatus.CLOSED
                : DiagnosticOrderStatus.COMPLETED;
        DiagnosticPollResult pollResult = new DiagnosticPollResult(
                status, null, request.getResultSummary(), null, null, null, null, null, null);
        return processResult(order, pollResult, true, actingUserName);
    }

    // sputumInvalidResult differs by source: SPUTUM_INVALID_POLLED_RESULT for vendor polls,
    // SPUTUM_INVALID_MANUAL_RESULT for manual entry. The X-ray value is the same for both.
    private static boolean isInvalidResult(String orderTypeCode, String resultSummary, String sputumInvalidResult) {
        if (resultSummary == null) {
            return false;
        }
        String summary = resultSummary.trim();
        DiagnosticOrderType type = DiagnosticOrderType.fromCode(orderTypeCode);
        if (type == DiagnosticOrderType.XRAY_CHEST) {
            return XRAY_INVALID_RESULT.equalsIgnoreCase(summary);
        }
        if (type == DiagnosticOrderType.MTB || type == DiagnosticOrderType.MDR_RIF) {
            return sputumInvalidResult.equalsIgnoreCase(summary);
        }
        return false;
    }

    // Automatic retest after a polled invalid outcome: a brand new PENDING row copying the closed
    // order's beneficiary, visit, vendor and patient details, with only a fresh externalOrderId,
    // pushed to the vendor straight away. Never throws — a failure here must not undo the close.
    private void pushRetestOrder(DiagnosticOrder closed) {
        try {
            withBeneficiaryLock(closed.getBeneficiaryId(), () -> {
                pushRetestOrderLocked(closed);
                return null;
            });
        } catch (Exception e) {
            logger.error("Failed to create retest order after invalid result, closedOrderId={}: {}",
                    closed.getId(), e.getMessage());
        }
    }

    private void pushRetestOrderLocked(DiagnosticOrder closed) {
        // A user push may have created a fresh order for this beneficiary+orderType after this one was
        // closed — the retest would then put the same patient on the vendor twice, so skip it.
        Optional<DiagnosticOrder> latest = diagnosticOrderRepo
                .findFirstByBeneficiaryIdAndOrderTypeAndDeletedFalseOrderByCreatedDateDesc(
                        closed.getBeneficiaryId(), closed.getOrderType());
        if (latest.isPresent() && !latest.get().getId().equals(closed.getId())
                && BLOCKING_STATUSES.contains(latest.get().getStatus())) {
            logger.info("Retest skipped for closedOrderId={}: newer order id={} (status={}) already exists",
                    closed.getId(), latest.get().getId(), latest.get().getStatus());
            return;
        }
        try {
            DiagnosticOrder retest = new DiagnosticOrder();
            retest.setVanID(closed.getVanID());
            retest.setParkingPlaceID(closed.getParkingPlaceID());
            retest.setOrderEvent(closed.getOrderEvent());
            retest.setBeneficiaryId(closed.getBeneficiaryId());
            retest.setVisitCode(closed.getVisitCode());
            retest.setProviderServiceName(closed.getProviderServiceName());
            retest.setProviderCode(closed.getProviderCode());
            retest.setOrderType(closed.getOrderType());
            retest.setExternalOrderId(String.format("%s-%d-%s", UUID.randomUUID(), closed.getVisitCode(),
                    closed.getOrderType()));
            retest.setStatus(DiagnosticOrderStatus.PENDING.name());
            retest.setPatientFirstName(closed.getPatientFirstName());
            retest.setPatientLastName(closed.getPatientLastName());
            retest.setPatientDateOfBirth(closed.getPatientDateOfBirth());
            retest.setPatientSex(closed.getPatientSex());
            retest.setCreatedBy("SYSTEM");
            retest.setModifiedBy("SYSTEM");
            retest = diagnosticOrderRepo.save(retest);
            retest = pushToProvider(retest, closed.getProviderCode());
            logger.info("Retest order created after invalid result: closedOrderId={}, retestOrderId={}, "
                            + "externalOrderId={}, status={}", closed.getId(), retest.getId(), retest.getExternalOrderId(),
                    retest.getStatus());
        } catch (Exception e) {
            logger.error("Failed to create retest order after invalid result, closedOrderId={}: {}",
                    closed.getId(), e.getMessage());
        }
    }

    // A vendor poll that COMPLETED with an invalid outcome is downgraded to CLOSED in place.
    // Returns true when that happened, so the caller still writes the result back to tb_suspected.
    private static boolean closeIfInvalidPolledResult(DiagnosticOrder order, DiagnosticPollResult result) {
        if (DiagnosticOrderStatus.COMPLETED.equals(result.getStatus())
                && isInvalidResult(order.getOrderType(), result.getResultSummary(), SPUTUM_INVALID_POLLED_RESULT)) {
            result.setStatus(DiagnosticOrderStatus.CLOSED);
            return true;
        }
        return false;
    }
}
