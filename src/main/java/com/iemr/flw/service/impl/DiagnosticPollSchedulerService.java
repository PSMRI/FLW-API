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

// Every order gets a primary poll window anchored on createdDate (XRAY_CHEST: xrayInitialDelayMinutes
// after creation; TrueNat: truenatInitialDelayMinutes after creation), running for giveUpMinutes before
// giving up (EXPIRED). Once an order has been retried via DiagnosticOrderService.retryPoll, retriedAt
// overrides that anchor: the order gets a fresh retryWindowMinutes window starting from retriedAt instead.
@Service
public class DiagnosticPollSchedulerService {

    private static final Logger logger = LoggerFactory.getLogger(DiagnosticPollSchedulerService.class);

    @Value("${diagnostic.poll.give-up-minutes}")
    private int giveUpMinutes;

    @Value("${diagnostic.poll.xray.initial-delay-minutes}")
    private int xrayInitialDelayMinutes;

    @Value("${diagnostic.poll.truenat.initial-delay-minutes}")
    private int truenatInitialDelayMinutes;

    @Value("${diagnostic.poll.retry.window-minutes}")
    private int retryWindowMinutes;

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
            PollWindow window = resolveWindow(order, xrayInitialDelayMinutes);
            if (window.start().isAfter(now)) {
                continue;
            }
            if (isExpired(window, now)) {
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
            PollWindow window = resolveWindow(order, truenatInitialDelayMinutes);
            if (window.start().isAfter(now)) {
                continue;
            }
            if (isExpired(window, now)) {
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

    private record PollWindow(Instant start, int durationMinutes) {}

    // retriedAt (set by the /order/retry endpoint) takes over as the window anchor once present,
    // overriding the original createdDate-anchored window regardless of whether that one already expired.
    private PollWindow resolveWindow(DiagnosticOrder order, int initialDelayMinutes) {
        if (order.getRetriedAt() != null) {
            return new PollWindow(order.getRetriedAt().toInstant(), retryWindowMinutes);
        }
        return new PollWindow(order.getCreatedDate().toInstant().plus(initialDelayMinutes, ChronoUnit.MINUTES), giveUpMinutes);
    }

    private boolean isExpired(PollWindow window, Instant now) {
        Instant deadline = window.start().plus(window.durationMinutes(), ChronoUnit.MINUTES);
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
