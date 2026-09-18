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

import com.iemr.flw.dto.iemr.DynamicFormDTO;
import com.iemr.flw.service.DynamicFormDefinitionService;
import com.iemr.flw.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for dynamic form definition management.
 * All write operations use POST; all read operations use GET.
 * Exceptions propagate to GlobalExceptionHandler — no local try-catch.
 *
 * @author Piramal Swasthya
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/dynamicForm", headers = "Authorization")
public class DynamicFormController {

    private final DynamicFormDefinitionService formService;

    @Operation(summary = "Create a full dynamic form definition with sections, questions, options and validations")
    @RequestMapping(value = "/createForm", method = RequestMethod.POST)
    public ResponseEntity<ApiResponse> createForm(@RequestBody @Valid DynamicFormDTO dto) {
        return ResponseEntity.ok(new ApiResponse(true, null, formService.createForm(dto)));
    }

    @Operation(summary = "Replace form structure and bump version (unchanged sections reused)")
    @RequestMapping(value = "/updateForm", method = RequestMethod.POST)
    public ResponseEntity<ApiResponse> updateForm(
            @RequestParam Long formId,
            @RequestBody @Valid DynamicFormDTO dto) {
        return ResponseEntity.ok(new ApiResponse(true, null, formService.updateForm(formId, dto)));
    }

    @Operation(summary = "Get form definition — latest version or specific version if ?version= provided")
    @RequestMapping(value = "/getDefinition", method = RequestMethod.GET)
    public ResponseEntity<ApiResponse> getFormDefinition(
            @RequestParam Long formId,
            @RequestParam(required = false) Integer version) {
        Object result = version != null
                ? formService.getFormDefinitionByVersion(formId, version)
                : formService.getFormDefinition(formId);
        return ResponseEntity.ok(new ApiResponse(true, null, result));
    }

    @Operation(summary = "Get metadata list of all active forms")
    @RequestMapping(value = "/getAllForms", method = RequestMethod.GET)
    public ResponseEntity<ApiResponse> getAllForms() {
        return ResponseEntity.ok(new ApiResponse(true, null, formService.getAllForms()));
    }

    @Operation(summary = "Activate a form (isActive = true)")
    @RequestMapping(value = "/activateForm", method = RequestMethod.POST)
    public ResponseEntity<ApiResponse> activateForm(
            @RequestParam Long formId) {
        formService.activateForm(formId);
        return ResponseEntity.ok(new ApiResponse(true, "Form activated", null));
    }

    @Operation(summary = "Deactivate a form (isActive = false)")
    @RequestMapping(value = "/deactivateForm", method = RequestMethod.POST)
    public ResponseEntity<ApiResponse> deactivateForm(
            @RequestParam Long formId) {
        formService.deactivateForm(formId);
        return ResponseEntity.ok(new ApiResponse(true, "Form deactivated", null));
    }

}
