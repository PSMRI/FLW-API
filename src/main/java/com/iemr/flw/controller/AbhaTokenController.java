package com.iemr.flw.controller;

import com.iemr.flw.service.AbhaTokenService;
import com.iemr.flw.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/abha", headers = "Authorization")
public class AbhaTokenController {

    private final Logger logger = LoggerFactory.getLogger(AbhaTokenController.class);

    @Autowired
    private AbhaTokenService abhaTokenService;

    @Operation(summary = "Get ABHA access token")
    @PostMapping("/token")
    public ResponseEntity<?> getAbhaToken(
            @RequestHeader(value = "Authorization") String authorization) {
        try {
            String accessToken = abhaTokenService.getAbhaToken();
            Map<String, String> tokenResponse = new HashMap<>();
            tokenResponse.put("accessToken", accessToken);
            return new ResponseEntity<>(
                    new ApiResponse(true, null, tokenResponse), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error fetching ABHA token: " + e.getMessage());
            return new ResponseEntity<>(
                    new ApiResponse(false, "Error fetching ABHA token: " + e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
