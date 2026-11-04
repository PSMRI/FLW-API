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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.*;
import com.iemr.flw.service.DiseaseControlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/generalCheckUpFollowUp",headers = "Authorization")
public class GeneralCheckUpFollowUpController {
    private final Logger logger = LoggerFactory.getLogger(GeneralCheckUpFollowUpController.class);

    @Autowired
    private DiseaseControlService diseaseControlService;

    @Autowired
    private ObjectMapper objectMapper;



        @RequestMapping(value = "getAll", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> getGeneralCheckUpFollowUp(@RequestBody GetBenRequestHandler getBenRequestHandler,@RequestHeader(value = "JwtToken") String token) {

        Map<String, Object> response = new LinkedHashMap<>();

        try {
            List<GeneralCheckUpFollowUpDto> result =
                    diseaseControlService.getGeneralCheckUpFollowUp(token);

            if (result != null && !result.isEmpty()) {
                response.put("statusCode", HttpStatus.OK.value());
                response.put("data", result);
                return ResponseEntity.ok(response);
            } else {
                response.put("statusCode", HttpStatus.NOT_FOUND.value());
                response.put("message", "No records found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

        } catch (Exception e) {
            logger.error("Error fetching Chronic Disease Visit :", e);
            response.put("statusCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("errorMessage", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @RequestMapping(value = "saveAll", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> savGeneralCheckUpFollowUp(
            @RequestBody List<GeneralCheckUpFollowUpDto> requestList,@RequestHeader(value = "JwtToken") String token) {

        Map<String, Object> response = new LinkedHashMap<>();
        logger.info("####################################################");
        try {
            logger.info("General Checkup Save Request: {}",
                    objectMapper.writerWithDefaultPrettyPrinter()
                            .writeValueAsString(requestList));
        } catch (Exception e) {
            logger.error("Failed to convert request to JSON", e);
        }
        logger.info("####################################################");

        try {
            List<GeneralCheckUpFollowUpDto> savedList =
                    diseaseControlService.saveGeneralCheckUpFollowUp(requestList,token);

            if (savedList != null && !savedList.isEmpty()) {
                response.put("statusCode", HttpStatus.OK.value());
                response.put("message", "Data saved successfully");
                response.put("data", savedList);
                return ResponseEntity.ok(response);
            } else {
                response.put("statusCode", HttpStatus.BAD_REQUEST.value());
                response.put("message", "No data saved");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

        } catch (Exception e) {
            logger.error("Error saving Chronic Disease Visit :", e);
            response.put("statusCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("errorMessage", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}