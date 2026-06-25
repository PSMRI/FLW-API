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
package com.iemr.flw.service;

import com.iemr.flw.dto.iemr.FormResponseDTO;
import com.iemr.flw.dto.iemr.FormResponseRequest;
import com.iemr.flw.masterEnum.FormType;
import com.iemr.flw.utils.exception.IEMRException;

import java.util.List;

/**
 * Contract for saving and retrieving dynamic form responses.
 *
 * @author Piramal Swasthya
 */
public interface DynamicFormResponseService {

    /** Save PRE_SUBMIT section answers and advance status to SUBMITTED. */
    FormResponseDTO submitForm(FormResponseRequest request);

    /** Save POST_SUBMIT section answers and advance status to COMPLETE. */
    FormResponseDTO completeForm(FormResponseRequest request, String jwtToken) throws IEMRException;

    /** All responses for a beneficiary filtered by form UUID. */
    List<FormResponseDTO> getResponsesByBeneficiary(Long beneficiaryId, String formUuid);

    /** Single response with all nested section and question answers. */
    FormResponseDTO getResponseById(Long responseId);

    /**
     * Submit multiple form responses in one bulk transaction.
     * All FormResponse rows are batch-inserted via saveAll() so responseIds are available
     * before section/question processing begins. All-or-nothing: any failure rolls back all.
     */
    List<FormResponseDTO> submitBulk(List<FormResponseRequest> requests, String jwtToken);

    /**
     * Returns SUBMITTED responses for the given form IDs whose {@code lastFollowUpAt}
     * falls within the 24-hour window that started exactly {@code delayDays} days ago.
     * Used by the follow-up notification scheduler.
     */
    List<FormResponseDTO> findPendingFollowUps(List<Long> formIds, int delayDays);

    /** Returns beneficiary IDs with COMPLETE status for the given form type. */
    List<Long> getCompletedBeneficiaries(FormType formType);
}
