package com.iemr.flw.service;

import com.iemr.flw.domain.iemr.DiagnosticOrder;
import com.iemr.flw.dto.DiagnosticOrderRequestDto;
import com.iemr.flw.dto.DiagnosticOrderResultDto;
import com.iemr.flw.dto.DiagnosticOrderStatusSummaryDto;
import com.iemr.flw.dto.ManualDiagnosticResultRequestDto;
import com.iemr.flw.dto.VendorHealthDto;
import com.iemr.flw.integration.provider.DiagnosticPollResult;

import java.util.List;

public interface DiagnosticOrderService {

    DiagnosticOrder createAndPushOrderByUser(DiagnosticOrderRequestDto request, String jwtToken) throws Exception;

    /**
     * Same create/push logic as createAndPushOrder, attributed to "SYSTEM" instead of a JWT-derived
     * user. For callers with no authenticated user in context, e.g. the poll scheduler auto-recreating
     * an order after an invalid result.
     */
    DiagnosticOrder createAndPushOrderAsSystem(DiagnosticOrderRequestDto request) throws Exception;

    DiagnosticOrderResultDto processResult(DiagnosticOrder order, DiagnosticPollResult result) throws Exception;

    /**
     * Polls the vendor once for this order. The order is re-read first and skipped (returns null) if it
     * is no longer PENDING/IN_PROGRESS, e.g. a manual result landed after the scheduler loaded it.
     */
    DiagnosticPollResult pollOnce(DiagnosticOrder order) throws Exception;

    /**
     * Daily cutoff: marks this order CLOSED with the given reason and notifies the vendor, unless it was
     * resolved (e.g. by a manual result) after the scheduler loaded it.
     */
    void expireOrder(DiagnosticOrder order, String reason) throws Exception;

    DiagnosticOrderResultDto triggerManualPoll(Long beneficiaryId, String orderType, Long visitCode) throws Exception;

    DiagnosticOrder retryPoll(Long beneficiaryId, String orderType, Long visitCode) throws Exception;

    /**
     * Best-effort: tells the vendor this order is closed out (no result coming), so they stop
     * tracking it. Never throws — a provider failure is logged and swallowed, since the order's own
     * CLOSED status is what actually matters locally. No-op if the order was never pushed to a vendor.
     */
    void notifyProviderOrderClosed(DiagnosticOrder order, String reason);

    DiagnosticOrderResultDto getOrderResult(Long beneficiaryId, String orderType, Long visitCode);

    DiagnosticOrder getOrder(Long beneficiaryId, String orderType, Long visitCode) throws Exception;

    List<DiagnosticOrder> getOrdersByBeneficiaryId(Long beneficiaryId) throws Exception;

    DiagnosticOrderStatusSummaryDto getOrderStatusSummary(String orderType, Integer villageId, Integer providerServiceMapId);

    VendorHealthDto checkVendorHealth(String orderType) throws Exception;

    DiagnosticOrderResultDto submitManualResult(ManualDiagnosticResultRequestDto request, String jwtToken) throws Exception;
}
