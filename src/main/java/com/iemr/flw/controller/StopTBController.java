package com.iemr.flw.controller;

import com.iemr.flw.dto.iemr.StopTBRegistrationDto;
import com.iemr.flw.service.StopTBService;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/stoptb")
public class StopTBController {

    private final Logger logger = LoggerFactory.getLogger(StopTBController.class);

    @Autowired
    private StopTBService stopTBService;

    @PostMapping("/registration/save")
    @Operation(summary = "Save Stop TB beneficiary registration")
    public ResponseEntity<Map<String, Object>> saveRegistration(
            @RequestBody String requestBody,
            @RequestHeader("JwtToken") String jwtToken) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            Map<String, Object> data = stopTBService.saveRegistration(requestBody, jwtToken);
            response.put("data", data);
            response.put("statusCode", 200);
            response.put("status", "Success");
        } catch (Exception e) {
            logger.error("Error in saveRegistration: " + e);
            response.put("statusCode", 5000);
            response.put("errorMessage", "Error in registration: " + e.getMessage());
            response.put("status", "FAILURE");
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/registration/get")
    @Operation(summary = "Get Stop TB beneficiary registration details")
    public ResponseEntity<Map<String, Object>> getRegistration(@RequestBody StopTBRegistrationDto dto) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            Map<String, Object> data = stopTBService.getRegistration(dto);
            response.put("data", data);
            response.put("statusCode", 200);
            response.put("status", "Success");
        } catch (Exception e) {
            logger.error("Error in getRegistration: " + e);
            response.put("statusCode", 5000);
            response.put("errorMessage", "Error fetching registration: " + e.getMessage());
            response.put("status", "FAILURE");
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/registrar/worklist")
    @Operation(summary = "Get registrar worklist for Stop TB")
    public ResponseEntity<Map<String, Object>> getRegistrarWorklist(@RequestBody StopTBRegistrationDto dto) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            Map<String, Object> data = stopTBService.getRegistrarWorklist(dto);
            response.put("data", data);
            response.put("statusCode", 200);
            response.put("status", "Success");
        } catch (Exception e) {
            logger.error("Error in getRegistrarWorklist: " + e);
            response.put("statusCode", 5000);
            response.put("errorMessage", "Error fetching worklist: " + e.getMessage());
            response.put("status", "FAILURE");
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/nurse/worklist")
    @Operation(summary = "Get nurse worklist for Stop TB")
    public ResponseEntity<Map<String, Object>> getNurseWorklist(@RequestBody StopTBRegistrationDto dto) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            Map<String, Object> data = stopTBService.getNurseWorklist(dto);
            response.put("data", data);
            response.put("statusCode", 200);
            response.put("status", "Success");
        } catch (Exception e) {
            logger.error("Error in getNurseWorklist: " + e);
            response.put("statusCode", 5000);
            response.put("errorMessage", "Error fetching nurse worklist: " + e.getMessage());
            response.put("status", "FAILURE");
        }
        return ResponseEntity.ok(response);
    }
}
