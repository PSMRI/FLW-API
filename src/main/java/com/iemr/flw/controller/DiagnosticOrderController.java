package com.iemr.flw.controller;

import com.google.gson.Gson;
import com.iemr.flw.domain.iemr.DiagnosticOrder;
import com.iemr.flw.dto.DiagnosticOrderRequestDto;
import com.iemr.flw.dto.DiagnosticOrderResultDto;
import com.iemr.flw.service.DiagnosticOrderService;
import com.iemr.flw.utils.response.OutputResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @Operation(summary = "Get the diagnostic order for a beneficiary+orderType (latest, if more than one exists)")
    public String getOrder(@RequestParam Long benRegID, @RequestParam String orderType) {
        OutputResponse response = new OutputResponse();
        try {
            DiagnosticOrder order = diagnosticOrderService.getOrder(benRegID, orderType);
            response.setResponse(new Gson().toJson(order));
        } catch (Exception e) {
            logger.error("Error in getOrder: {}", e.getMessage());
            response.setError(5000, "Error fetching diagnostic order: " + e.getMessage());
        }
        return response.toString();
    }

    @PostMapping("/order/getByBen/{benRegID}")
    @Operation(summary = "Get all diagnostic orders for a beneficiary")
    public String getOrdersByBen(@PathVariable Long benRegID) {
        OutputResponse response = new OutputResponse();
        try {
            List<DiagnosticOrder> orders = diagnosticOrderService.getOrdersByBenRegId(benRegID);
            response.setResponse(new Gson().toJson(orders));
        } catch (Exception e) {
            logger.error("Error in getOrdersByBen: {}", e.getMessage());
            response.setError(5000, "Error fetching diagnostic orders: " + e.getMessage());
        }
        return response.toString();
    }

    @PostMapping("/order/testCompleted")
    @Operation(summary = "Mark that the physical test has completed for this beneficiary+orderType, starting the poll cadence")
    public String markTestCompleted(@RequestParam Long benRegID, @RequestParam String orderType) {
        OutputResponse response = new OutputResponse();
        try {
            DiagnosticOrder order = diagnosticOrderService.markTestCompleted(benRegID, orderType);
            response.setResponse(new Gson().toJson(order));
        } catch (Exception e) {
            logger.error("Error in markTestCompleted: {}", e.getMessage());
            response.setError(5000, "Error marking test completed: " + e.getMessage());
        }
        return response.toString();
    }

    @PostMapping("/order/result")
    @Operation(summary = "Get the diagnostic result for benId+orderType, at whatever stage it's currently in")
    public String getOrderResult(@RequestParam Long benId, @RequestParam String orderType) {
        OutputResponse response = new OutputResponse();
        try {
            DiagnosticOrderResultDto result = diagnosticOrderService.getOrderResult(benId, orderType);
            response.setResponse(new Gson().toJson(result));
        } catch (Exception e) {
            logger.error("Error in getOrderResult: {}", e.getMessage());
            response.setError(5000, "Error fetching diagnostic result: " + e.getMessage());
        }
        return response.toString();
    }

    @PostMapping("/order/poll")
    @Operation(summary = "Trigger an immediate poll for one beneficiary+orderType's diagnostic order and return the result (ops use)")
    public String pollOrder(@RequestParam Long benRegID, @RequestParam String orderType) {
        OutputResponse response = new OutputResponse();
        try {
            DiagnosticOrderResultDto result = diagnosticOrderService.triggerManualPoll(benRegID, orderType);
            response.setResponse(new Gson().toJson(result));
        } catch (Exception e) {
            logger.error("Error in pollOrder: {}", e.getMessage());
            response.setError(5000, "Error triggering poll: " + e.getMessage());
        }
        return response.toString();
    }
}
