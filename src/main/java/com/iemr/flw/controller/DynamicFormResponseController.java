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
package com.iemr.flw.controller;

import com.iemr.flw.dto.iemr.CompletedBeneficiaryDetailDTO;
import com.iemr.flw.dto.iemr.FormResponseDTO;
import com.iemr.flw.dto.iemr.FormResponseRequest;
import com.iemr.flw.masterEnum.FormType;
import com.iemr.flw.service.DynamicFormResponseService;
import com.iemr.flw.utils.ApiResponse;
import com.iemr.flw.utils.exception.IEMRException;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for saving and retrieving dynamic form responses (beneficiary answers).
 * All exceptions propagate to GlobalExceptionHandler — no local try-catch.
 *
 * @author Piramal Swasthya
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/dynamicForm/response", headers = "Authorization")
public class DynamicFormResponseController {

    private final DynamicFormResponseService responseService;

    @Operation(summary = "Submit GENERAL_INFO and PRE_SUBMIT section answers, status → SUBMITTED. " +
            "Pass responseId in body to re-submit an existing SUBMITTED response.")
    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    public ResponseEntity<ApiResponse> submitForm(
            @Valid @RequestBody FormResponseRequest request) {
        FormResponseDTO dto = responseService.submitForm(request);
        return ResponseEntity.ok(new ApiResponse(true, "Form submitted successfully", dto));
    }

    @Operation(summary = "Save POST_SUBMIT section answers, status → COMPLETE")
    @RequestMapping(value = "/complete", method = RequestMethod.POST)
    public ResponseEntity<ApiResponse> completeForm(
            @Valid @RequestBody FormResponseRequest request,
            @RequestHeader("JwtToken") String jwtToken) throws IEMRException {
        FormResponseDTO dto = responseService.completeForm(request, jwtToken);        
        return ResponseEntity.ok(new ApiResponse(true, "Form completed successfully", dto));
    }

    @Operation(summary = "Submit multiple form responses in one bulk transaction. " +
            "All-or-nothing: any failure rolls back all. responseId is populated for every saved item.")
    @RequestMapping(value = "/submitBulk", method = RequestMethod.POST)
    public ResponseEntity<ApiResponse> submitBulk(
            @Valid @RequestBody List<FormResponseRequest> requests,
            @RequestHeader("JwtToken") String jwtToken) {
        List<FormResponseDTO> saved = responseService.submitBulk(requests, jwtToken);
        return ResponseEntity.ok(
                new ApiResponse(true, "Bulk submit complete: " + saved.size() + " saved", saved));
    }

    @Operation(summary = "Get all form responses for a beneficiary filtered by form UUID")
    @RequestMapping(value = "/getByBeneficiary", method = RequestMethod.GET)
    public ResponseEntity<ApiResponse> getByBeneficiary(
            @RequestParam Long beneficiaryId,
            @RequestParam String formUuid) {
        List<FormResponseDTO> dtos =
                responseService.getResponsesByBeneficiary(beneficiaryId, formUuid);
        return ResponseEntity.ok(new ApiResponse(true, "Responses fetched successfully", dtos));
    }

    @Operation(summary = "Get a single form response with all nested answers")
    @RequestMapping(value = "/getById", method = RequestMethod.GET)
    public ResponseEntity<ApiResponse> getById(@RequestParam Long responseId) {
        FormResponseDTO dto = responseService.getResponseById(responseId);
        return ResponseEntity.ok(new ApiResponse(true, "Response fetched successfully", dto));
    }
    @Operation(summary = "Get COMPLETE/REFUSED form-response details (beneficiaryId, refusal flag, sections filled vs total) for the given form type, optionally filtered by village and/or provider service map")
    @RequestMapping(value = "/getCompletedBeneficiaries", method = RequestMethod.GET)
    public ResponseEntity<ApiResponse> getCompletedBeneficiaries(
            @RequestParam FormType formType,
            @RequestParam(required = false) Integer villageId,
            @RequestParam(required = false) Integer providerServiceMapId) {
        List<CompletedBeneficiaryDetailDTO> result =
                responseService.getCompletedBeneficiaries(formType, villageId, providerServiceMapId);
        return ResponseEntity.ok(new ApiResponse(true, "Completed beneficiaries fetched successfully", result));
    }
}
