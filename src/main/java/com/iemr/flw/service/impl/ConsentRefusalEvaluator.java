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

import com.iemr.flw.domain.iemr.FormSection;
import com.iemr.flw.dto.iemr.FormResponseRequest;
import com.iemr.flw.dto.iemr.QuestionAnswerRequest;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * TB2_SEC_GENERAL_INFO's consent question (TB2_GI_Q1) gates the rest of the TB Counselling
 * form; a "NO" answer means the beneficiary refused counselling. Shared by /complete and
 * /submitBulk so both apply the same refusal rule.
 *
 * @author Piramal Swasthya
 */
@Component
public class ConsentRefusalEvaluator {

    private static final String SECTION_UUID_GENERAL_INFO = "TB2_SEC_GENERAL_INFO";
    private static final String QUESTION_UUID_CONSENT = "TB2_GI_Q1";
    private static final String OPTION_VALUE_CONSENT_REFUSED = "NO";

    public boolean isRefusalAnswer(QuestionAnswerRequest answer) {
        return QUESTION_UUID_CONSENT.equals(answer.getQuestionUuid())
                && OPTION_VALUE_CONSENT_REFUSED.equals(answer.getOptionValue());
    }

    // Scanned directly off the raw request (before any FormSection is resolved) so the
    // top-level FormResponse status can be decided. Absence-tolerant: no TB2_SEC_GENERAL_INFO
    // section, no answers, or no TB2_GI_Q1 answer at all just means "not refused".
    public boolean isConsentRefused(FormResponseRequest request) {
        if (request.getSections() == null) {
            return false;
        }
        return request.getSections().stream()
                .filter(s -> SECTION_UUID_GENERAL_INFO.equals(s.getSectionUuid()))
                .filter(s -> s.getAnswers() != null)
                .flatMap(s -> s.getAnswers().stream())
                .anyMatch(this::isRefusalAnswer);
    }

    // Same TB2_GI_Q1="NO" signal as isConsentRefused, but resolved per-section so callers can
    // set a section's own status. Absence-tolerant by design: any other section, a missing
    // answers list, or no answer for the consent question at all just falls through to
    // doneStatus.
    public String determineSectionStatus(FormSection section, List<QuestionAnswerRequest> answers,
            String doneStatus, String refusedStatus) {
        if (!SECTION_UUID_GENERAL_INFO.equals(section.getSectionUuid()) || answers == null) {
            return doneStatus;
        }
        boolean refused = answers.stream().anyMatch(this::isRefusalAnswer);
        return refused ? refusedStatus : doneStatus;
    }
}