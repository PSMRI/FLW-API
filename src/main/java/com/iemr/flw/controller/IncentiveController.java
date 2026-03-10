package com.iemr.flw.controller;

import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.*;
import com.iemr.flw.service.IncentiveService;
import com.iemr.flw.utils.JwtUtil;
import com.iemr.flw.utils.response.OutputResponse;

import io.swagger.v3.oas.annotations.Operation;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/incentive", consumes = "application/json", produces = "application/json")
public class IncentiveController {

    private final Logger logger = LoggerFactory.getLogger(IncentiveController.class);


    @Autowired
    IncentiveService incentiveService;

    @Autowired
    private JwtUtil jwtUtil;

    @Operation(summary = "save incentive master")
    @RequestMapping(value = {"/masterData/saveAll"}, method = {RequestMethod.POST})
    public String saveIncentiveMasterData(@RequestBody List<IncentiveActivityDTO> activityDTOS, @RequestHeader(value = "Authorization") String authorization, HttpServletRequest request) {
        OutputResponse response = new OutputResponse();
        try {
            logger.info("Saving All incentives");
            if (activityDTOS != null) {
                String s = incentiveService.saveIncentivesMaster(activityDTOS);
                if (s != null)
                    response.setResponse(s);
                else
                    response.setError(5000, "No record found");
            } else
                response.setError(5000, "Invalid/NULL request obj");
        } catch (Exception e) {
            logger.error("Error in incentive master data " + e);
            response.setError(5000, "Error in incentive master data" + e);
        }
        return response.toString();
    }

    @Operation(summary = "get incentive master")
    @RequestMapping(value = {"/masterData/getAll"}, method = {RequestMethod.POST})
    public String saveIncentiveMasterData(@RequestBody IncentiveRequestDTO incentiveRequestDTO,
                                          @RequestHeader(value = "Authorization") String Authorization) {
        OutputResponse response = new OutputResponse();
        try {

            logger.info("get All incentives");

            // add logic for different state or district
            if (incentiveRequestDTO != null) {
                String s = incentiveService.getIncentiveMaster(incentiveRequestDTO);
                logger.info("All incentives" + s);

                if (s != null)
                    response.setResponse(s);
                else
                    response.setError(5000, "No record found");
            } else
                response.setError(5000, "Invalid/NULL request obj");
        } catch (Exception e) {
            logger.error("Error in incentive master data " + e);
            response.setError(5000, "Error in incentive master data" + e);
        }
        return response.toString();
    }


    @Operation(summary = "get high risk assessment data of all beneficiaries registered with given user id")
    @RequestMapping(value = {"/fetchUserData"}, method = {RequestMethod.POST})
    public String getAllIncentivesByUserId(@RequestBody GetBenRequestHandler requestDTO,
                                           @RequestHeader(value = "Authorization") String Authorization) {
        OutputResponse response = new OutputResponse();
        try {

            if (requestDTO != null) {
                logger.info("request object with timestamp : " + new Timestamp(System.currentTimeMillis()) + " "
                        + requestDTO);
                String s = incentiveService.getAllIncentivesByUserId(requestDTO);
                logger.info("User Incentive:" + s);
                if (s != null)
                    response.setResponse(s);
                else
                    response.setError(5000, "No record found");
            } else
                response.setError(5000, "Invalid/NULL request obj");
        } catch (Exception e) {
            logger.error("Error in high risk assessment data : " + e);
            response.setError(5000, "Error in high risk assessment data : " + e);
        }
        return response.toString();
    }

    @RequestMapping(value = {"/update"}, method = RequestMethod.POST, consumes = {"multipart/form-data"})
    public String updateIncentive(@ModelAttribute  PendingActivityDTO requestDTO) {
        OutputResponse response = new OutputResponse();
        try {

            if (requestDTO != null) {
                logger.info("request object with timestamp : " + new Timestamp(System.currentTimeMillis()) + " "
                        + requestDTO);
                String s = incentiveService.updateIncentive(requestDTO);
                logger.info("User Incentive:" + s);
                if (s != null)
                    response.setResponse(s);
                else
                    response.setError(500, "No record found");
            } else
                response.setError(500, "Invalid/NULL request obj");
        } catch (Exception e) {
            logger.error("Error in high risk assessment data : " + e);
            response.setError(500, "Error in high risk assessment data : " + e);
        }
        return response.toString();


    }

    @PostMapping("/updateClaim")
    public ResponseEntity<?> updateApprovalStatus(
            @RequestBody UpdateClaimedStatusRequestDTO request,
            @RequestHeader(value = "JwtToken") String token) {

        try {
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("UNAUTHORIZED");
            }

            String result = incentiveService.updateClaimStatus(
                    jwtUtil.extractUserId(token),
                    request.getMonth(),
                    request.getYear(),
                    request.isClaimed(),
                    token
            );

            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", 200);
            response.put("message", result);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Server error: " + e.getMessage());
        }
    }


}
