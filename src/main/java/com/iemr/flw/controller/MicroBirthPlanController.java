package com.iemr.flw.controller;

import com.iemr.flw.domain.iemr.MicroBirthPlan;
import com.iemr.flw.dto.iemr.GetMicroPlanHandler;
import com.iemr.flw.dto.iemr.MicroBirthPlanDTO;
import com.iemr.flw.service.MicroBirthPlanService;
import com.iemr.flw.utils.JwtUtil;
import io.jsonwebtoken.Jwe;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/micro-birthPlan", headers = "Authorization", produces = "application/json")
public class MicroBirthPlanController {
    private final Logger logger = LoggerFactory.getLogger(MicroBirthPlanController.class);

    @Autowired
    private MicroBirthPlanService service;

    @Autowired
    JwtUtil jwtUtil;


    @RequestMapping(value = "saveAll", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createMicroBirthPlan(@RequestBody MicroBirthPlanDTO birthPlan) {
        logger.info("Micro birth plan request " + birthPlan.toString());
        Map<String, Object> response = new HashMap<>();

        Map<String, Object> data = new HashMap<>();
        try {
            data.put("userId", birthPlan.getUserId());
            data.put("entries", service.createMicroBirthPlan(birthPlan));
            response.put("data", data);
            response.put("statusCode", 200);
            response.put("status", "Success");
        } catch (Exception e) {
            response.put("statusCode", 500);
            response.put("errorMessage", e.getMessage());


        }


        return ResponseEntity.ok(response);
    }


    @RequestMapping(value = "getAll", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getAllMicroBirthPlans(@RequestHeader(value = "Authorization") String authorization) {

        Map<String, Object> response = new HashMap<>();


        Map<String, Object> data = new HashMap<>();
        try {
            Integer userID = jwtUtil.extractUserId(authorization);
            if (userID != null) {
                data.put("userId", userID);
                data.put("entries", service.getAllMicroBirthPlans(userID));
                response.put("data", data);
                response.put("statusCode", 200);
                response.put("status", "Success");
            }

        } catch (Exception e) {
            response.put("statusCode", 500);
            response.put("errorMessage", e.getMessage());


        }

        return ResponseEntity.ok(response);
    }


}