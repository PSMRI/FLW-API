package com.iemr.flw.service.impl;

import com.iemr.flw.domain.iemr.DiagnosticOrder;
import com.iemr.flw.integration.provider.DiagnosticPollResult;
import com.iemr.flw.integration.provider.DiagnosticProviderFactory;
import com.iemr.flw.masterEnum.DiagnosticOrderStatus;
import com.iemr.flw.masterEnum.DiagnosticOrderType;
import com.iemr.flw.repo.iemr.DiagnosticOrderRepo;
import com.iemr.flw.service.DiagnosticOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

// Every due order (status PENDING/IN_PROGRESS) is polled on every tick from the moment it's created —
// there is no per-order initial delay or rolling give-up window anymore. A tick is skipped entirely
// for an order-type family if diagnostic.provider.xray / diagnostic.provider.truenat has no active
// vendor configured right now (checked live via DiagnosticProviderFactory, not any value stored on the
// order row). Once a day, expireOutstandingOrders sweeps up everything still PENDING/IN_PROGRESS (for
// order-type families that do have an active vendor) and marks it EXPIRED.
@Service
public class DiagnosticPollSchedulerService {

    private static final Logger logger = LoggerFactory.getLogger(DiagnosticPollSchedulerService.class);

    @Autowired
    private DiagnosticOrderRepo diagnosticOrderRepo;

    @Autowired
    private DiagnosticOrderService diagnosticOrderService;

    @Autowired
    private DiagnosticProviderFactory providerFactory;

    @Scheduled(fixedDelayString = "${diagnostic.poll.xray.tick-ms:15000}")
    public void pollXrayOrders() {
        if (noActiveVendor(DiagnosticOrderType.XRAY_CHEST)) {
            logger.debug("No active vendor configured for XRAY_CHEST — skipping poll tick");
            return;
        }
        List<DiagnosticOrder> candidates = diagnosticOrderRepo.findXrayDueForPoll();
        logger.info("XRAY poll tick: {} order(s) in due-for-poll queue", candidates.size());
        if (candidates.isEmpty()) return;

        for (DiagnosticOrder order : candidates) {
            pollSingle(order);
        }
        logger.info("Polled {} pending XRAY diagnostic orders", candidates.size());
    }

    @Scheduled(fixedDelayString = "${diagnostic.poll.truenat.tick-ms:60000}")
    public void pollTrueNatOrders() {
        if (noActiveVendor(DiagnosticOrderType.MTB)) {
            logger.debug("No active vendor configured for TrueNat — skipping poll tick");
            return;
        }
        List<DiagnosticOrder> candidates = diagnosticOrderRepo.findTrueNatDueForPoll();
        logger.info("TrueNat poll tick: {} order(s) in due-for-poll queue", candidates.size());
        if (candidates.isEmpty()) return;

        for (DiagnosticOrder order : candidates) {
            pollSingle(order);
        }
        logger.info("Polled {} pending TrueNat diagnostic orders", candidates.size());
    }

    @Scheduled(cron = "${diagnostic.poll.expiry-cron:0 0 18 * * *}")
    public void expireOutstandingOrders() {
        List<DiagnosticOrder> outstanding = diagnosticOrderRepo.findAllOutstandingOrders();
        int expired = 0;
        for (DiagnosticOrder order : outstanding) {
            if (noActiveVendor(DiagnosticOrderType.fromCode(order.getOrderType()))) {
                continue; // awaiting manual entry — not subject to the polling cutoff
            }
            expire(order);
            expired++;
        }
        if (expired > 0) {
            logger.warn("Daily polling cutoff reached: marked {} outstanding order(s) EXPIRED", expired);
        }
    }

    private boolean noActiveVendor(DiagnosticOrderType orderType) {
        String providerCode = providerFactory.getProviderCodeForOrderType(orderType);
        return providerCode == null || providerCode.isBlank();
    }

    private void expire(DiagnosticOrder order) {
        order.setStatus(DiagnosticOrderStatus.EXPIRED.name());
        order.setErrorMessage("Daily polling cutoff reached without a result");
        order.setLastPolledAt(new Timestamp(System.currentTimeMillis()));
        diagnosticOrderRepo.save(order);
    }

    private void pollSingle(DiagnosticOrder order) {
        try {
            DiagnosticPollResult result = diagnosticOrderService.pollOnce(order);
            diagnosticOrderService.processResult(order, result);
        } catch (Exception e) {
            logger.error("Poll failed for orderId={}: {}", order.getId(), e.getMessage());
            order.setRetryCount(order.getRetryCount() + 1);
            order.setLastPolledAt(new Timestamp(System.currentTimeMillis()));
            order.setErrorMessage(e.getMessage());
            diagnosticOrderRepo.save(order);
        }
    }
}