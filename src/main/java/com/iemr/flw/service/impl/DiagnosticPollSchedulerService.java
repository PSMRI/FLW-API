package com.iemr.flw.service.impl;

import com.iemr.flw.domain.iemr.DiagnosticOrder;
import com.iemr.flw.integration.provider.DiagnosticPollResult;
import com.iemr.flw.masterEnum.DiagnosticOrderStatus;
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

// Two independent schedulers, one per order family: XRAY_CHEST polls immediately on its own
// cadence, while TrueNat waits truenatInitialDelayMinutes after test completion before polling
// on its own (slower) cadence.
@Service
public class DiagnosticPollSchedulerService {

    private static final Logger logger = LoggerFactory.getLogger(DiagnosticPollSchedulerService.class);

    @Value("${diagnostic.poll.give-up-minutes}")
    private int giveUpMinutes;

    @Value("${diagnostic.poll.truenat.initial-delay-minutes}")
    private int truenatInitialDelayMinutes;

    @Autowired
    private DiagnosticOrderRepo diagnosticOrderRepo;

    @Autowired
    private DiagnosticOrderService diagnosticOrderService;

    @Scheduled(fixedDelayString = "${diagnostic.poll.xray.tick-ms:15000}")
    public void pollXrayOrders() {
        List<DiagnosticOrder> candidates = diagnosticOrderRepo.findXrayDueForPoll();
        logger.info("XRAY poll tick: {} order(s) in due-for-poll queue", candidates.size());
        if (candidates.isEmpty()) return;

        Instant now = Instant.now();
        int polled = 0;
        for (DiagnosticOrder order : candidates) {
            Instant testCompletedAt = order.getTestCompletedAt().toInstant();
            if (isExpired(testCompletedAt, now)) {
                giveUp(order);
            } else {
                pollSingle(order);
                polled++;
            }
        }
        if (polled > 0) {
            logger.info("Polled {} pending XRAY diagnostic orders", polled);
        }
    }

    @Scheduled(fixedDelayString = "${diagnostic.poll.truenat.tick-ms:60000}")
    public void pollTrueNatOrders() {
        List<DiagnosticOrder> candidates = diagnosticOrderRepo.findTrueNatDueForPoll();
        logger.info("TrueNat poll tick: {} order(s) in due-for-poll queue", candidates.size());
        if (candidates.isEmpty()) return;

        Instant now = Instant.now();
        int polled = 0;
        for (DiagnosticOrder order : candidates) {
            Instant pollingStartedAt = order.getTestCompletedAt().toInstant()
                    .plus(truenatInitialDelayMinutes, ChronoUnit.MINUTES);
            if (pollingStartedAt.isAfter(now)) {
                continue;
            }
            if (isExpired(pollingStartedAt, now)) {
                giveUp(order);
            } else {
                pollSingle(order);
                polled++;
            }
        }
        if (polled > 0) {
            logger.info("Polled {} pending TrueNat diagnostic orders", polled);
        }
    }

    // Measured from when polling actually starts for this order (immediately for X-ray, after the
    // initial delay for TrueNat), so one give-up-minutes value works for both families.
    private boolean isExpired(Instant pollingStartedAt, Instant now) {
        Instant deadline = pollingStartedAt.plus(giveUpMinutes, ChronoUnit.MINUTES);
        return !deadline.isAfter(now);
    }

    private void giveUp(DiagnosticOrder order) {
        order.setStatus(DiagnosticOrderStatus.EXPIRED.name());
        order.setErrorMessage("Polling window exceeded (" + giveUpMinutes + " min) without a result");
        order.setLastPolledAt(new Timestamp(System.currentTimeMillis()));
        diagnosticOrderRepo.save(order);
        logger.warn("Order {} exceeded give-up window of {} min, marked EXPIRED", order.getId(), giveUpMinutes);
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
