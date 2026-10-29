package com.iemr.flw.service.impl;

import com.iemr.flw.domain.iemr.DiagnosticOrder;
import com.iemr.flw.integration.provider.DiagnosticProviderFactory;
import com.iemr.flw.masterEnum.DiagnosticOrderType;
import com.iemr.flw.repo.iemr.DiagnosticOrderRepo;
import com.iemr.flw.service.DiagnosticOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Supplier;

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
        pollDueOrders("XRAY", DiagnosticOrderType.XRAY_CHEST,
                diagnosticOrderRepo::findXrayDueForPoll);
    }

    @Scheduled(fixedDelayString = "${diagnostic.poll.truenat.tick-ms:60000}")
    public void pollTrueNatOrders() {
        pollDueOrders("TrueNat", DiagnosticOrderType.MTB,
                diagnosticOrderRepo::findTrueNatDueForPoll);
    }

    private void pollDueOrders(String label, DiagnosticOrderType orderType, Supplier<List<DiagnosticOrder>> dueOrders) {
        String providerCode = providerFactory.getProviderCodeForOrderType(orderType);
        if (providerCode == null || providerCode.isBlank()) {
            logger.debug("No active vendor configured for {} — skipping poll tick", label);
            return;
        }
        List<DiagnosticOrder> candidates = dueOrders.get();
        logger.info("{} poll tick: {} order(s) in due-for-poll queue", label, candidates.size());
        if (candidates.isEmpty()) return;

        if (!providerFactory.getProvider(providerCode).checkHealth(orderType)) {
            logger.warn("{} vendor ping failed — skipping poll tick, {} order(s) left for the next tick",
                    label, candidates.size());
            return;
        }

        int polled = 0;
        for (DiagnosticOrder order : candidates) {
            polled++;
            if (!pollSingle(order)) {
                logger.warn("{} vendor unreachable mid-tick — stopping, {} order(s) left for the next tick",
                        label, candidates.size() - polled);
                break;
            }
        }
        logger.info("Polled {} of {} pending {} diagnostic orders", polled, candidates.size(), label);
    }

    // Auto-closure of outstanding orders at the daily cutoff is disabled for now — orders are no longer
    // closed automatically. Uncomment this job (and expire() below) to re-enable it.
    // @Scheduled(cron = "${diagnostic.poll.expiry-cron:0 50 23 * * *}")
    // public void expireOutstandingOrders() {
    //     List<DiagnosticOrder> outstanding = diagnosticOrderRepo.findAllOutstandingOrders(startOfToday(), startOfTomorrow());
    //     int expired = 0;
    //     for (DiagnosticOrder order : outstanding) {
    //         if (noActiveVendor(DiagnosticOrderType.fromCode(order.getOrderType()))) {
    //             continue; // awaiting manual entry — not subject to the polling cutoff
    //         }
    //         expire(order);
    //         expired++;
    //     }
    //     if (expired > 0) {
    //         logger.warn("Daily polling cutoff reached: marked {} outstanding order(s) CLOSED", expired);
    //     }
    // }

    // The (disabled) daily close only handles orders created today. JVM default zone on purpose: it's the
    // same clock created_date is stamped with (@CreationTimestamp), so the day boundaries line up.
    private static Timestamp startOfToday() {
        return Timestamp.valueOf(LocalDate.now().atStartOfDay());
    }

    private static Timestamp startOfTomorrow() {
        return Timestamp.valueOf(LocalDate.now().plusDays(1).atStartOfDay());
    }

    private boolean noActiveVendor(DiagnosticOrderType orderType) {
        String providerCode = providerFactory.getProviderCodeForOrderType(orderType);
        return providerCode == null || providerCode.isBlank();
    }

    // Used only by the (disabled) auto-closure job above. expireOrder re-reads the order under the
    // beneficiary lock, so an order completed manually after this batch was loaded is left alone.
    // private void expire(DiagnosticOrder order) {
    //     String reason = "Order validity expired for the day " + LocalDate.now(ZoneId.of("Asia/Kolkata"));
    //     try {
    //         diagnosticOrderService.expireOrder(order, reason);
    //     } catch (Exception e) {
    //         logger.error("Expiry failed for orderId={}: {}", order.getId(), e.getMessage());
    //     }
    // }

    // Returns false only when the vendor couldn't be reached at all (connect/read timeout, refused
    // connection) — the caller stops the tick then. Any other failure is specific to this order
    // (bad data, vendor-side rejection), so it's recorded and the tick carries on.
    private boolean pollSingle(DiagnosticOrder order) {
        try {
            diagnosticOrderService.pollOnce(order);
            return true;
        } catch (ResourceAccessException e) {
            logger.error("Vendor unreachable polling orderId={}: {}", order.getId(), e.getMessage());
            return false;
        } catch (Exception e) {
            logger.error("Poll failed for orderId={}: {}", order.getId(), e.getMessage());
            return true;
        }
    }

}