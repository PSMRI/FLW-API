package com.iemr.flw.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iemr.flw.domain.iemr.DiagnosticOrder;
import com.iemr.flw.dto.DiagnosticOrderRequestDto;
import com.iemr.flw.dto.DiagnosticOrderResultDto;
import com.iemr.flw.dto.DiagnosticOrderStatusSummaryDto;
import com.iemr.flw.dto.ManualDiagnosticResultRequestDto;
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
    public String pushOrder(@RequestBody @Valid DiagnosticOrderRequestDto request,
            @RequestHeader("JwtToken") String jwtToken) {
        OutputResponse response = new OutputResponse();
        try {
            DiagnosticOrder order = diagnosticOrderService.createAndPushOrderByUser(request, jwtToken);
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
            + "(targets the latest order unless visitCode is given). results[] has one entry per test: the order's "
            + "own (XRAY_CHEST / MTB / MTB_PLUS), then MDR_RIF on an MTB order once its RIF test exists. status INVALID: the MTB/RIF test came back "
            + "invalid (e.g. Error-2) and the machine is retesting the same order — it is still polled and "
            + "moves to COMPLETED when the retest result arrives.")
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
            + "Targets the latest order unless visitCode is given. An invalid MTB/RIF result gives status INVALID "
            + "(still polled, no retest order); an invalid X-ray closes the order and pushes a retest.")
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

    @PostMapping("/order/manualResult")
    @Operation(summary = "Send either a result (resultSummary and/or mdrRifResult) or one reasonToClose. resultSummary: "
            + "manually submit a diagnostic result for a beneficiary's latest order of the given type, for use when "
            + "no vendor device is integrated. Stored like a vendor result, one row per test in results[]. "
            + "MTB/MTB_PLUS orders carry two results: resultSummary is the MTB result; mdrRifResult is the MDR RIF "
            + "result, stored as the order's MDR_RIF result (and tb_suspected's MDR RIF result), only allowed on a positive MTB. "
            + "A positive MTB without mdrRifResult leaves the order open until RIF is in: PENDING if it was pushed "
            + "to a vendor (polling continues, and the vendor's result replaces a differing manual one), "
            + "MANUAL_ENTRY if there is no vendor (send mdrRifResult or reasonToCloseRif). Otherwise the order "
            + "is COMPLETED; a vendor's differing result still replaces it when its documents are fetched. "
            + "Rejected if that order is already COMPLETED. Closing (same as on /order/push): reasonToCloseXray for "
            + "XRAY_CHEST; reasonToCloseMTB when the MTB test wasn't done; reasonToCloseRif when MTB was positive but "
            + "MDR RIF wasn't done — closes that same order, keeping its MTB result. Patient/orderEvent are sourced "
            + "from the beneficiary's existing order for this type.")
    public String submitManualResult(@RequestBody @Valid ManualDiagnosticResultRequestDto request,
            @RequestHeader("JwtToken") String jwtToken) {
        OutputResponse response = new OutputResponse();
        try {
            DiagnosticOrderResultDto result = diagnosticOrderService.submitManualResult(request, jwtToken);
            response.setResponse(new GsonBuilder().serializeNulls().create().toJson(result));
        } catch (Exception e) {
            logger.error("Error in submitManualResult: {}", e.getMessage());
            response.setError(5000, "Error submitting manual diagnostic result: " + e.getMessage());
        }
        return response.toString();
    }

    @GetMapping("/order/getBeneficiariesByStatus")
    @Operation(summary = "Get beneficiary IDs bucketed by diagnostic order status for the given order type, "
            + "optionally filtered by village and/or provider service map. invalid: MTB/RIF invalid, waiting for "
            + "the machine's own retest")
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
