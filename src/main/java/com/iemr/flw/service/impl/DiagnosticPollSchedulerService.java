package com.iemr.flw.service.impl;

import com.iemr.flw.domain.iemr.DiagnosticOrder;
import com.iemr.flw.integration.provider.DiagnosticPollResult;
import com.iemr.flw.masterEnum.DiagnosticOrderStatus;
import com.iemr.flw.masterEnum.DiagnosticOrderType;
import com.iemr.flw.repo.iemr.DiagnosticOrderRepo;
import com.iemr.flw.service.DiagnosticOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class DiagnosticPollSchedulerService {

    private static final Logger logger = LoggerFactory.getLogger(DiagnosticPollSchedulerService.class);

    @Value("${diagnostic.poll.give-up-minutes}")
    private int giveUpMinutes;

    @Value("${diagnostic.poll.interval-seconds}")
    private int pollIntervalSeconds;

    @Value("${diagnostic.poll.truenat.initial-delay-minutes}")
    private int truenatInitialDelayMinutes;

    @Autowired
    private DiagnosticOrderRepo diagnosticOrderRepo;

    @Autowired
    private DiagnosticOrderService diagnosticOrderService;

    @Scheduled(fixedDelayString = "${diagnostic.poll.tick-ms:15000}")
    public void pollPendingOrders() {
        List<DiagnosticOrder> candidates = diagnosticOrderRepo.findDueForPoll();
        logger.info("Diagnostic poll tick: {} order(s) in due-for-poll queue", candidates.size());
        if (candidates.isEmpty()) return;

        Instant now = Instant.now();
        int polled = 0;
        for (DiagnosticOrder order : candidates) {
            if (isExpired(order, now)) {
                giveUp(order);
            } else if (isDue(order, now)) {
                pollSingle(order);
                polled++;
            }
        }
        if (polled > 0) {
            logger.info("Polled {} pending diagnostic orders", polled);
        }
    }

    // XRAY_CHEST: polls from the moment the test is marked completed. TrueNat: waits
    // truenatInitialDelayMinutes after that (the machine's own processing time) before polling
    // starts. Both then poll at the same pollIntervalSeconds cadence.
    private boolean isDue(DiagnosticOrder order, Instant now) {
        Instant pollingStartedAt = pollingStartedAt(order);
        if (pollingStartedAt.isAfter(now)) {
            return false; // still inside TrueNat's initial delay window
        }
        Timestamp lastPolledAt = order.getLastPolledAt();
        if (lastPolledAt == null) {
            return true;
        }
        return !lastPolledAt.toInstant().plusSeconds(pollIntervalSeconds).isAfter(now);
    }

    // Give-up window is measured from when polling actually starts for this order (immediately
    // for X-ray, after the initial delay for TrueNat) - not from testCompletedAt directly for
    // TrueNat - so a shared give-up-minutes value is meaningful for both types instead of
    // auto-failing TrueNat orders before they're ever polled.
    private boolean isExpired(DiagnosticOrder order, Instant now) {
        Instant deadline = pollingStartedAt(order).plus(giveUpMinutes, ChronoUnit.MINUTES);
        return !deadline.isAfter(now);
    }

    private Instant pollingStartedAt(DiagnosticOrder order) {
        Instant testCompletedAt = order.getTestCompletedAt().toInstant();
        DiagnosticOrderType type = DiagnosticOrderType.fromCode(order.getOrderType());
        if (type == DiagnosticOrderType.XRAY_CHEST) {
            return testCompletedAt;
        }
        return testCompletedAt.plus(truenatInitialDelayMinutes, ChronoUnit.MINUTES);
    }

    private void giveUp(DiagnosticOrder order) {
        order.setStatus(DiagnosticOrderStatus.FAILED.name());
        order.setErrorMessage("Polling window exceeded (" + giveUpMinutes + " min) without a result");
        order.setLastPolledAt(new Timestamp(System.currentTimeMillis()));
        diagnosticOrderRepo.save(order);
        logger.warn("Order {} exceeded give-up window of {} min, marked FAILED", order.getId(), giveUpMinutes);
    }

    private void pollSingle(DiagnosticOrder order) {
        try {
            DiagnosticPollResult result = diagnosticOrderService.pollOnce(order);
            diagnosticOrderService.processResult(order, result);
        } catch (Exception e) {
            logger.error("Poll failed for orderId={}: {}", order.getId(), e.getMessage());
            // Purely an observability counter - does not gate future polling or mark the order
            // FAILED; the give-up window above is what eventually closes out a stuck order.
            order.setRetryCount(order.getRetryCount() + 1);
            order.setLastPolledAt(new Timestamp(System.currentTimeMillis()));
            order.setErrorMessage(e.getMessage());
            diagnosticOrderRepo.save(order);
        }
    }
}
