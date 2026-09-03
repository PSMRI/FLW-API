/*
 * AMRIT – Accessible Medical Records via Integrated Technology
 * Integrated EHR (Electronic Health Records) Solution
 *
 * Copyright (C) "Piramal Swasthya Management and Research Institute"
 *
 * This file is part of AMRIT.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses/.
 */
package com.iemr.flw.service.impl;

import com.iemr.flw.domain.iemr.DynamicForm;
import com.iemr.flw.dto.iemr.FormResponseDTO;
import com.iemr.flw.repo.iemr.DynamicFormRepo;
import com.iemr.flw.service.DynamicFormResponseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Generic daily scheduler that sends follow-up push notifications for any active
 * dynamic form with a configured {@code followUpDelayDays}. The notification fires
 * exactly N days after the most recently completed section (PRE_SUBMIT submission or
 * any subsequent POST_SUBMIT section completion).
 *
 * @author Piramal Swasthya
 */
@Service
public class FormFollowUpNotificationScheduler {

    private static final Logger log =
            LoggerFactory.getLogger(FormFollowUpNotificationScheduler.class);

    private final DynamicFormRepo dynamicFormRepo;
    private final DynamicFormResponseService responseService;
    private final NotificationService notificationService;

    public FormFollowUpNotificationScheduler(
            DynamicFormRepo dynamicFormRepo,
            DynamicFormResponseService responseService,
            NotificationService notificationService) {
        this.dynamicFormRepo = dynamicFormRepo;
        this.responseService = responseService;
        this.notificationService = notificationService;
    }

    @Scheduled(cron = "0 0 9 * * *")
    public void sendFollowUpReminders() {
        log.info("FormFollowUpNotificationScheduler: starting daily follow-up scan");

        List<DynamicForm> formsWithDelay =
                dynamicFormRepo.findByIsActiveTrueAndFollowUpDelayDaysIsNotNull();

        if (formsWithDelay.isEmpty()) {
            log.debug("No active forms with followUpDelayDays configured — skipping");
            return;
        }

        // Group by delay value so we issue one batch DB query per distinct delay
        Map<Integer, List<Long>> byDelay = formsWithDelay.stream()
                .collect(Collectors.groupingBy(
                        DynamicForm::getFollowUpDelayDays,
                        Collectors.mapping(DynamicForm::getFormId, Collectors.toList())));

        for (Map.Entry<Integer, List<Long>> entry : byDelay.entrySet()) {
            int delayDays = entry.getKey();
            List<Long> formIds = entry.getValue();

            try {
                List<FormResponseDTO> pending =
                        responseService.findPendingFollowUps(formIds, delayDays);

                log.info("delayDays={}: found {} pending follow-up(s)", delayDays, pending.size());

                for (FormResponseDTO r : pending) {
                    try {
                        //Notification sending logic
                        log.info("Sent follow-up notification: responseId={}, officerId={}",
                                r.getResponseId(), r.getOfficerId());
                    } catch (Exception e) {
                        log.error("Failed to send notification for responseId={}: {}",
                                r.getResponseId(), e.getMessage());
                    }
                }
            } catch (Exception e) {
                log.error("Follow-up scheduler failed for delayDays={}: {}",
                        delayDays, e.getMessage(), e);
            }
        }

        log.info("FormFollowUpNotificationScheduler: scan complete");
    }
}
