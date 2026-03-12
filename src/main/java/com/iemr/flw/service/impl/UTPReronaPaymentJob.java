package com.iemr.flw.service.impl;

import com.google.gson.Gson;
import com.iemr.flw.domain.iemr.IncentiveActivity;
import com.iemr.flw.domain.iemr.IncentiveActivityRecord;
import com.iemr.flw.dto.iemr.PaymentItem;
import com.iemr.flw.dto.iemr.PaymentRequest;
import com.iemr.flw.dto.iemr.Period;
import com.iemr.flw.dto.iemr.VerifiedBy;
import com.iemr.flw.repo.iemr.IncentiveRecordRepo;
import com.iemr.flw.repo.iemr.IncentivesRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class UTPReronaPaymentJob {

    @Autowired
    private IncentiveRecordRepo recordRepo;

    @Autowired
    private IncentivesRepo incentivesRepo;

    @Autowired
    private UtpreronaPaymentIntegrationImpl paymentService;

    // Runs automatically on 1st of every month at midnight
    @Scheduled(cron = "0 0 0 1 * *")
    public void sendMonthlyPayments() {
        triggerPayment();
    }

    // ✅ Separate method — call this for immediate testing
    public void triggerPayment() {

        LocalDate today = LocalDate.now();

// ✅ Current month ka data — testing ke liye
        LocalDate firstDay = today.withDayOfMonth(1);        // Mar 1, 2026
        LocalDate lastDay = today;                            // Mar 11, 2026 (aaj)

        String periodStart = firstDay.format(DateTimeFormatter.ISO_DATE);
        String periodEnd = lastDay.format(DateTimeFormatter.ISO_DATE);

        Timestamp startTs = Timestamp.valueOf(firstDay.atStartOfDay());           // 2026-03-01 00:00:00
        Timestamp endTs = Timestamp.valueOf(lastDay.plusDays(1).atStartOfDay());  // 2026-03-12 00:00:00

        log.info("========================================");
        log.info("UTP Rerona Payment Job Started");
        log.info("Period: {} to {}", periodStart, periodEnd);
        log.info("Start Timestamp: {}", startTs);
        log.info("End Timestamp: {}", endTs);
        log.info("========================================");

        List<Integer> allAshaIds = recordRepo.findDistinctAshaIdsByDateRange(startTs, endTs);
        log.info("Total ASHAs found: {}", allAshaIds.size());

        int success = 0, failed = 0, skipped = 0;

        for (Integer ashaId : allAshaIds) {
            try {
                log.info("----------------------------------------");
                log.info("Processing ASHA ID: {}", ashaId);

                // Fetch claimed and approved records for this ASHA
                List<IncentiveActivityRecord> entities = recordRepo
                        .findClaimedApprovedRecordsByAshaAndDateRange(ashaId, startTs, endTs);

                if (entities == null || entities.isEmpty()) {
                    log.warn("ASHA {} — No records found, skipping", ashaId);
                    skipped++;
                    continue;
                }

                log.info("ASHA {} — Total records fetched: {}", ashaId, entities.size());

                // Collect all unique activity IDs
                Set<Long> activityIds = entities.stream()
                        .filter(e -> e.getActivityId() != null)
                        .map(IncentiveActivityRecord::getActivityId)
                        .collect(Collectors.toSet());

                log.info("ASHA {} — Unique activity IDs: {}", ashaId, activityIds);

                // Fetch activity master data for all activity IDs at once
                Map<Long, IncentiveActivity> activityMap = incentivesRepo.findAllById(activityIds)
                        .stream()
                        .filter(a -> a.getIsDeleted() == null || !a.getIsDeleted())
                        .collect(Collectors.toMap(IncentiveActivity::getId, a -> a));

                // Group records by activity ID
                Map<Long, List<IncentiveActivityRecord>> groupedByActivity = entities.stream()
                        .filter(e -> e.getActivityId() != null)
                        .collect(Collectors.groupingBy(IncentiveActivityRecord::getActivityId));

                // Build items list from grouped records
                List<PaymentItem> items = new ArrayList<>();

                for (Map.Entry<Long, List<IncentiveActivityRecord>> entry : groupedByActivity.entrySet()) {
                    Long activityId = entry.getKey();
                    List<IncentiveActivityRecord> records = entry.getValue();

                    IncentiveActivity activity = activityMap.get(activityId);
                    if (activity == null) {
                        log.warn("ASHA {} — Activity ID {} not found in master, skipping", ashaId, activityId);
                        continue;
                    }

                    // Calculate total incentive amount for this activity
                    Long totalAmount = records.stream()
                            .mapToLong(r -> r.getAmount() != null ? r.getAmount() : 0L)
                            .sum();


                    String stateActivityCode = activity.getStateActivityCode() != null
                            ? String.valueOf(activity.getStateActivityCode())
                            : null;

                    if (stateActivityCode == null) {
                        log.warn("ASHA {} — ActivityId {} stateActivityCode null, skipping", ashaId, activityId);
                        continue;  // ✅ Is item ko request mein add hi mat karo
                    }

                    PaymentItem item = new PaymentItem();
                    item.setActivityCode(stateActivityCode);
                    item.setCount(String.valueOf(records.size()));
                    item.setIncentiveAmount(String.valueOf(totalAmount));
                    items.add(item);



                    log.info("ASHA {} — Activity: {} | Count: {} | Total Amount: {}",
                            ashaId, activityId, records.size(), totalAmount);
                }

                if (items.isEmpty()) {
                    log.warn("ASHA {} — No valid items found, skipping", ashaId);
                    skipped++;
                    continue;
                }

                // Get verified by details from first record
                IncentiveActivityRecord firstRecord = entities.get(0);
                VerifiedBy verifiedBy = new VerifiedBy();
                verifiedBy.setEmployeeId(firstRecord.getVerifiedByUserId() != null
                        ? String.valueOf(firstRecord.getVerifiedByUserId()) : "");
                verifiedBy.setName(firstRecord.getVerifiedByUserName() != null
                        ? firstRecord.getVerifiedByUserName() : "");

                // Build period object
                Period period = new Period();
                period.setStart(periodStart);
                period.setEnd(periodEnd);

                // Build final payment request
                PaymentRequest paymentRequest = new PaymentRequest(
                        UUID.randomUUID().toString(),
                        "AMRIT",
                        period,
                        ashaId.toString(),
                        OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                        verifiedBy,
                        items
                );

                // Log full request before sending
                log.info("ASHA {} — Request Payload: {}", ashaId, new Gson().toJson(paymentRequest));

                // Send payment request to API
                paymentService.sendPaymentRequest(paymentRequest);

                log.info("ASHA {} — Payment sent successfully", ashaId);
                success++;

            } catch (Exception e) {
                log.error("ASHA {} — Payment failed: {}", ashaId, e.getMessage(), e);
                failed++;
            }
        }

        log.info("========================================");
        log.info("Job Complete | Success: {} | Failed: {} | Skipped: {}", success, failed, skipped);
        log.info("========================================");
    }
}