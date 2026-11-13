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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    // A completed order's documents come from a second vendor call after the result is saved; if that
    // call fails the order stays COMPLETED with no documents and the regular poll never revisits it.
    // A result entered manually COMPLETES the order, so the poll stops visiting it: its documents (MTB
    // report, then the MDR RIF report) only arrive here, and the vendor's result overwrites the manual
    // one where they differ. This job runs at application startup and then every hour.
    @Scheduled(fixedDelayString = "${diagnostic.documents.backfill-interval-ms:3600000}")
    public void fetchMissingDocuments() {
        List<DiagnosticOrder> candidates = diagnosticOrderRepo.findOrdersNeedingDocuments();
        logger.info("Document back-fill tick: {} order(s) needing documents", candidates.size());
        if (candidates.isEmpty()) return;

        // X-ray and TrueNat are handled in this one job but are separate vendor endpoints: each is pinged
        // once, before its first order, and its orders are skipped this tick if it's down (or drops mid-tick),
        // without holding up the other.
        Map<String, Boolean> vendorUp = new HashMap<>();
        int received = 0;
        int skipped = 0;
        for (DiagnosticOrder order : candidates) {
            try {
                DiagnosticOrderType orderType = DiagnosticOrderType.fromCode(order.getOrderType());
                String vendorKey = order.getProviderCode() + ":" + vendorGroup(orderType);
                if (!vendorUp.computeIfAbsent(vendorKey, k -> isVendorUp(order.getProviderCode(), orderType))) {
                    skipped++;
                    continue;
                }
                try {
                    if (diagnosticOrderService.fetchMissingDocuments(order)) received++;
                } catch (ResourceAccessException e) {
                    logger.error("Vendor {} unreachable fetching documents for orderId={} — skipping its remaining orders this tick: {}",
                            vendorKey, order.getId(), e.getMessage());
                    vendorUp.put(vendorKey, false);
                }
            } catch (Exception e) {
                logger.error("Document fetch failed for orderId={}: {}", order.getId(), e.getMessage());
            }
        }
        logger.info("Document back-fill: documents received for {} of {} order(s), {} skipped (vendor down)",
                received, candidates.size(), skipped);
    }

    // MTB and MTB_PLUS share the TrueNat endpoint, so they share one health check.
    private static String vendorGroup(DiagnosticOrderType orderType) {
        return orderType == DiagnosticOrderType.XRAY_CHEST ? "XRAY" : "TRUENAT";
    }

    private boolean isVendorUp(String providerCode, DiagnosticOrderType orderType) {
        try {
            boolean up = providerFactory.getProvider(providerCode).checkHealth(orderType);
            if (!up) {
                logger.warn("{} {} vendor ping failed — skipping its document back-fill this tick",
                        providerCode, vendorGroup(orderType));
            }
            return up;
        } catch (Exception e) {
            logger.warn("No usable provider {} for {} document back-fill: {}", providerCode, orderType, e.getMessage());
            return false;
        }
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