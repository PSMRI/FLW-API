package com.iemr.flw.controller;

import com.iemr.flw.domain.iemr.AshaWorker;
import com.iemr.flw.service.AshaProfileService;
import com.iemr.flw.service.EmployeeMasterInter;
import com.iemr.flw.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/asha", produces = "application/json")
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

    public ResponseEntity<Map<String, Object>> editEmployee(@RequestBody AshaWorker editEmployee) {

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

   @RequestMapping(value = "getProfile", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getProfile(
            @RequestHeader(value = "JwtToken") String jwtToken) {
        Map<String, Object> response = new HashMap<>();

        try {
            Integer employeeId = jwtUtil.extractUserId(jwtToken);

            AshaWorker ashaWorker = ashaProfileService.getProfileData(employeeId);
            logger.info("Asha Profile" + ashaWorker);

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
