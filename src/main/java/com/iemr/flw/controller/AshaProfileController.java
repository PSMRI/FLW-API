package com.iemr.flw.controller;

import com.iemr.flw.domain.iemr.AshaWorker;
import com.iemr.flw.domain.iemr.M_User;
import com.iemr.flw.dto.iemr.UserServiceRoleDTO;
import com.iemr.flw.repo.iemr.UserServiceRoleRepo;
import com.iemr.flw.service.AshaProfileService;
import com.iemr.flw.service.EmployeeMasterInter;
import com.iemr.flw.service.UserService;
import com.iemr.flw.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import com.iemr.flw.utils.JwtAuthenticationUtil;
import com.iemr.flw.utils.JwtUtil;
import com.iemr.flw.utils.exception.IEMRException;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping(value = "/asha", headers = "Authorization", produces = "application/json")
public class AshaProfileController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
    @Autowired
    AshaProfileService ashaProfileService;
    private Map<String, Object> response = new HashMap<>();

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired

    private EmployeeMasterInter employeeMasterInter;

    @RequestMapping(value = "editProfile", method = {RequestMethod.POST}, produces = {
            "application/json"}, consumes = "application/json")

    public ResponseEntity<Map<String, Object>> editEmployee(@RequestBody AshaWorker editEmployee,@RequestHeader(value = "Authorization") String authorization) {

        try {
            System.out.println(editEmployee.toString());

            AshaWorker ashaWorker = ashaProfileService.saveEditData(editEmployee);
            response.put("data", ashaWorker);
            response.put("statusCode", 200);
            response.put("status", "Success");

        } catch (Exception e) {
            logger.error("Unexpected error:", e);
            ResponseEntity.status(500).body(e.getMessage());

        }

        return ResponseEntity.ok().body(response);

    }

    @RequestMapping(value = "getProfile", method = RequestMethod.GET, headers = "Authorization")
    public ResponseEntity<Map<String, Object>> getProfile(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            String jwtFromHeader = request.getHeader("JwtToken");

            // Validate JWT header presence
            if (jwtFromHeader == null || jwtFromHeader.trim().isEmpty()) {
                response.put("statusCode", 401);
                response.put("status", "Unauthorized");
                response.put("errorMessage", "JWT token is missing");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Extract and validate user ID from JWT
            int userId = jwtUtil.extractUserId(jwtFromHeader); // Make sure this returns 0 or throws for invalid token

            if (userId == 0) {
                response.put("statusCode", 401);
                response.put("status", "Unauthorized");
                response.put("errorMessage", "Invalid JWT token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Business logic
            AshaWorker ashaWorker = ashaProfileService.getProfileData(userId);

            response.put("statusCode", 200);
            response.put("status", "Success");
            response.put("data", ashaWorker);

            if (ashaWorker == null) {
                response.put("errorMessage", "Asha profile not found");
            }

            return ResponseEntity.ok().body(response);

        } catch (Exception e) {
            logger.error("Unexpected error:", e);
            response.put("statusCode", 500);
            response.put("status", "Error");
            response.put("errorMessage", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
