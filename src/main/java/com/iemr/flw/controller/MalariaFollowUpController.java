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
*
/*
* AMRIT – Accessible Medical Records via Integrated Technology
*/
package com.iemr.flw.controller;

import com.iemr.flw.dto.iemr.GetDiseaseRequestHandler;
import com.iemr.flw.dto.iemr.MalariaFollowListUpDTO;
import com.iemr.flw.dto.iemr.MalariaFollowUpDTO;
import com.iemr.flw.service.MalariaFollowUpService;
import com.iemr.flw.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/follow-up", headers = "Authorization")
public class MalariaFollowUpController {
    private final Logger logger = LoggerFactory.getLogger(MalariaFollowUpController.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MalariaFollowUpService followUpService;

    @RequestMapping(value = "save", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> save(@RequestBody MalariaFollowUpDTO dto,@RequestHeader(value = "JwtToken") String token) {
        Map<String, Object> response = new HashMap<>();

        try {
            if(token!=null){

                Boolean result = followUpService.saveFollowUp(dto,token);
                if (result) {
                    response.put("status", "Success");
                    response.put("statusCode", 200);
                    response.put("message", "Follow-up saved successfully");
                } else {
                    response.put("status", "Failed");
                    response.put("statusCode", 5000);
                }
            }

        } catch (Exception e) {
            response.put("status", "Error: " + e.getMessage());
            response.put("statusCode", 5000);
        }
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "get", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> getFollowUpsByUserId(@RequestBody GetDiseaseRequestHandler getDiseaseRequestHandler,@RequestHeader(value = "JwtToken") String token) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if(token!=null){
                List<MalariaFollowListUpDTO> data = followUpService.getByUserId(jwtUtil.extractUserId(token));
                response.put("status", "Success");
                response.put("statusCode", 200);
                response.put("data", data);
                if (data.isEmpty()) {
                    response.put("message", "No records found");
                }
            }

        } catch (Exception e) {
            logger.info("Fail Malaria followUp: "+e.getMessage());
            logger.info("Fail Malaria  full error: "+e);
            response.put("status", "Error: " + e.getMessage());
            response.put("statusCode", 5000);
        }
        return ResponseEntity.ok(response);


    }
}
