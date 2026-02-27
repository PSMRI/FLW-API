package com.iemr.flw.controller;

import com.iemr.flw.dto.iemr.AshaByFacilityRequestDTO;
import com.iemr.flw.service.SupervisorDashboardService;
import com.iemr.flw.service.impl.AshaSupervisorLoginService;
import com.iemr.flw.utils.JwtUtil;
import com.iemr.flw.utils.response.OutputResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/ashaSupervisor")
public class AshaSupervisorIncentiveApproval {

    @Autowired
    private AshaSupervisorLoginService ashaSupervisorLoginService;

    @Autowired
    private SupervisorDashboardService supervisorDashboardService;

    @Autowired
    private JwtUtil jwtUtil;



    @PostMapping("/getAshaListByFacility")
    public ResponseEntity<?> getAshaListByFacility(@RequestBody AshaByFacilityRequestDTO request,@RequestHeader(value = "JwtToken") String token) {
        try {
            if(token==null){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("UNAUTHORIZED");

            }
            List<Map<String, Object>> result = ashaSupervisorLoginService.getAshasAtFacility(
                    jwtUtil.extractUserId(token),
                    request.getFacilityId(),
                    request.getMonth(),
                    request.getYear(),
                    request.getApprovalStatus()
            );
            Map<String, Object> approvalStatus = new HashMap<>();

            approvalStatus.put("pending", 0);
            approvalStatus.put("verified", 0);
            approvalStatus.put("rejected", 0);
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", 200);
            response.put("approvalStatus", approvalStatus);
            response.put("data", result);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Server error: " + e.getMessage());
        }
    }

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());



    @Operation(summary = "Get comprehensive ASHA Supervisor dashboard with facilities, ASHAs, villages and incentive data")
    @PostMapping("/dashboard")
    public String getSupervisorDashboard(@RequestBody AshaByFacilityRequestDTO request,@RequestHeader(value = "JwtToken") String token ) {
        OutputResponse response = new OutputResponse();

        try {
            if(token!=null){
                String result = supervisorDashboardService.getSupervisorDashboard(jwtUtil.extractUserId(token),request.getMonth(),request.getYear());
                response.setResponse(result);
            }

        } catch (Exception e) {
            logger.error("getSupervisorDashboard failed: " + e.getMessage(), e);
            response.setError(e);
        }
        return response.toString();
    }

}
