/*
* AMRIT – Accessible Medical Records via Integrated Technology 
* Integrated EHR (Electronic Health Records) Solution 
*
* Copyright (C) "Piramal Swasthya Management and Research Institute" 
*
* This file is part of AMRIT.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see https://www.gnu.org/licenses/.
*/
package com.iemr.flw.controller.health;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iemr.flw.service.health.HealthService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
public class HealthController {

    private static final Logger logger = LoggerFactory.getLogger(HealthController.class);

    @Autowired
    private HealthService healthService;

    @Operation(summary = "Health check endpoint")
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        logger.info("Health check endpoint called");
        
        Map<String, Object> healthStatus = healthService.checkHealth();
        
        // Return 503 if any service is down, 200 if all are up
        String status = (String) healthStatus.get("status");
        HttpStatus httpStatus = "UP".equals(status) ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE;
        
        logger.info("Health check completed with status: {}", status);
        return ResponseEntity.status(httpStatus).body(healthStatus);
    }
}
