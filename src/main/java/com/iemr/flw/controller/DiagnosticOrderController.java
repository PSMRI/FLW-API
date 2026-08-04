package com.iemr.flw.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iemr.flw.domain.iemr.DiagnosticOrder;
import com.iemr.flw.dto.DiagnosticOrderRequestDto;
import com.iemr.flw.dto.DiagnosticOrderResultDto;
import com.iemr.flw.dto.DiagnosticOrderStatusSummaryDto;
import com.iemr.flw.dto.VendorHealthDto;
import com.iemr.flw.masterEnum.DiagnosticOrderType;
import com.iemr.flw.service.DiagnosticOrderService;
import com.iemr.flw.utils.ApiResponse;
import com.iemr.flw.utils.response.OutputResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/diagnostic")
public class DiagnosticOrderController {

    private final Logger logger = LoggerFactory.getLogger(DiagnosticOrderController.class);

    private final DiagnosticOrderService diagnosticOrderService;

    public DiagnosticOrderController(DiagnosticOrderService diagnosticOrderService) {
        this.diagnosticOrderService = diagnosticOrderService;
    }

    @PostMapping("/order/push")
    @Operation(summary = "Push a diagnostic order to the configured provider")
    public String pushOrder(@RequestBody @Valid DiagnosticOrderRequestDto request) {
        OutputResponse response = new OutputResponse();
        try {
            DiagnosticOrder order = diagnosticOrderService.createAndPushOrder(request);
            response.setResponse(new Gson().toJson(order));
        } catch (Exception e) {
            logger.error("Error in pushOrder: {}", e.getMessage());
            response.setError(5000, "Error pushing diagnostic order: " + e.getMessage());
        }
        return response.toString();
    }

    @PostMapping("/order/get")
    @Operation(summary = "Get the diagnostic order for a beneficiary+orderType (latest, if more than one exists; "
            + "pass visitCode to target a specific order/retest)")
    public String getOrder(@RequestParam Long beneficiaryId, @RequestParam String orderType,
            @RequestParam(required = false) Long visitCode) {
        OutputResponse response = new OutputResponse();
        try {
            DiagnosticOrder order = diagnosticOrderService.getOrder(beneficiaryId, orderType, visitCode);
            response.setResponse(new Gson().toJson(order));
        } catch (Exception e) {
            logger.error("Error in getOrder: {}", e.getMessage());
            response.setError(5000, "Error fetching diagnostic order: " + e.getMessage());
        }
        return response.toString();
    }

    @PostMapping("/order/getByBen/{beneficiaryId}")
    @Operation(summary = "Get all diagnostic orders for a beneficiary")
    public String getOrdersByBen(@PathVariable Long beneficiaryId) {
        OutputResponse response = new OutputResponse();
        try {
            List<DiagnosticOrder> orders = diagnosticOrderService.getOrdersByBeneficiaryId(beneficiaryId);
            response.setResponse(new Gson().toJson(orders));
        } catch (Exception e) {
            logger.error("Error in getOrdersByBen: {}", e.getMessage());
            response.setError(5000, "Error fetching diagnostic orders: " + e.getMessage());
        }
        return response.toString();
    }

    @PostMapping("/order/retry")
    @Operation(summary = "Restart polling for this beneficiary+orderType's diagnostic order for another "
            + "retry window (targets the latest order unless visitCode is given)")
    public String retryOrder(@RequestParam Long beneficiaryId, @RequestParam String orderType,
            @RequestParam(required = false) Long visitCode) {
        OutputResponse response = new OutputResponse();
        try {
            DiagnosticOrder order = diagnosticOrderService.retryPoll(beneficiaryId, orderType, visitCode);
            response.setResponse(new Gson().toJson(order));
        } catch (Exception e) {
            logger.error("Error in retryOrder: {}", e.getMessage());
            response.setError(5000, "Error retrying diagnostic order poll: " + e.getMessage());
        }
        return response.toString();
    }

    @PostMapping("/order/result")
    @Operation(summary = "Get the diagnostic result for beneficiaryId+orderType, at whatever stage it's currently in "
            + "(targets the latest order unless visitCode is given)")
    public String getOrderResult(@RequestParam Long beneficiaryId, @RequestParam String orderType,
            @RequestParam(required = false) Long visitCode) {
        OutputResponse response = new OutputResponse();
        try {
            DiagnosticOrderResultDto result = diagnosticOrderService.getOrderResult(beneficiaryId, orderType, visitCode);
            response.setResponse(new GsonBuilder().serializeNulls().create().toJson(result));
        } catch (Exception e) {
            logger.error("Error in getOrderResult: {}", e.getMessage());
            response.setError(5000, "Error fetching diagnostic result: " + e.getMessage());
        }
        return response.toString();
    }

    @PostMapping("/order/poll")
    @Operation(summary = "Trigger an immediate poll for one beneficiary+orderType's diagnostic order and return the result (ops use). "
            + "Targets the latest order unless visitCode is given")
    public String pollOrder(@RequestParam Long beneficiaryId, @RequestParam String orderType,
            @RequestParam(required = false) Long visitCode) {
        OutputResponse response = new OutputResponse();
        try {
            DiagnosticOrderResultDto result = diagnosticOrderService.triggerManualPoll(beneficiaryId, orderType, visitCode);
            response.setResponse(new GsonBuilder().serializeNulls().create().toJson(result));
        } catch (Exception e) {
            logger.error("Error in pollOrder: {}", e.getMessage());
            response.setError(5000, "Error triggering poll: " + e.getMessage());
        }
        return response.toString();
    }

    @GetMapping("/order/getBeneficiariesByStatus")
    @Operation(summary = "Get beneficiary IDs bucketed by diagnostic order status for the given order type, "
            + "optionally filtered by village and/or provider service map")
    public ResponseEntity<ApiResponse> getBeneficiariesByStatus(
            @RequestParam DiagnosticOrderType orderType,
            @RequestParam(required = false) Integer villageId,
            @RequestParam(required = false) Integer providerServiceMapId) {
        DiagnosticOrderStatusSummaryDto result =
                diagnosticOrderService.getOrderStatusSummary(orderType.name(), villageId, providerServiceMapId);
        return ResponseEntity.ok(new ApiResponse(true, "Diagnostic order status summary fetched successfully", result));
    }

    @GetMapping("/vendor/health")
    @Operation(summary = "Check whether the diagnostic vendor responsible for the given orderType is running, "
            + "by hitting its unauthenticated ping endpoint.")
    public ResponseEntity<ApiResponse> vendorHealth(@RequestParam String orderType) {
        Map<String, Object> data = new LinkedHashMap<>();
        try {
            VendorHealthDto health = diagnosticOrderService.checkVendorHealth(orderType);
            data.put("isConnected", health.isConnected());
            data.put("isDeviceIntegrated", health.isDeviceIntegrated());
            return ResponseEntity.ok(new ApiResponse(true, "Vendor health check for orderType " + orderType + " completed", data));
        } catch (Exception e) {
            logger.error("Vendor health check failed for orderType {}: {}", orderType, e.getMessage());
            data.put("isConnected", false);
            data.put("isDeviceIntegrated", false);
            return ResponseEntity.ok(new ApiResponse(true,
                    "Vendor health check failed for orderType " + orderType + ": " + e.getMessage(), data));
        }
    }
}
