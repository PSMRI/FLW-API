package com.iemr.flw.service.impl;

import com.iemr.flw.repo.iemr.IncentiveRecordRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class IncentiveMonthlyScheduler {

    private static final ZoneId INDIA_ZONE =
            ZoneId.of("Asia/Kolkata");
      @Autowired
    private  IncentiveRecordRepo recordRepo;

    @Scheduled(cron = "0 5 0 1 * *", zone = "Asia/Kolkata")
    public void resetRejectedIncentivesForNewMonth() {

        LocalDate firstDayOfCurrentMonth =
                LocalDate.now(INDIA_ZONE).withDayOfMonth(1);

        Timestamp currentMonthStart =
                Timestamp.valueOf(firstDayOfCurrentMonth.atStartOfDay());

        Timestamp updatedDate =
                Timestamp.from(ZonedDateTime.now(INDIA_ZONE).toInstant());

        int updatedCount =
                recordRepo.resetRejectedIncentivesForNewMonth(
                        currentMonthStart,
                        updatedDate,
                        "SYSTEM"
                );

        log.info(
                "Rejected incentives reset successfully. Updated records: {}",
                updatedCount
        );
    }
}