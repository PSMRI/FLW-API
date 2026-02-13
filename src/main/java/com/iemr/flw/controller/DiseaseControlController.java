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
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/disease",headers = "Authorization")
public class DiseaseControlController {
    private final Logger logger = LoggerFactory.getLogger(DiseaseControlController.class);

    @Autowired
    private DiseaseControlService diseaseControlService;


    @RequestMapping(value = "malaria/saveAll", method = RequestMethod.POST, consumes = "application/json", produces = "application/json",headers = "Authorization")
    public ResponseEntity<Map<String, Object>> saveMalaria(@RequestBody MalariaDTO malariaDTO) {
        Map<String, Object> response = new HashMap<>();

        try {

            logger.info("Malaria DTO: {}", new ObjectMapper().writeValueAsString(malariaDTO));
             if(malariaDTO!=null){
                 response.put("status", "Success");
                 response.put("statusCode", 200);
                 response.put("data", diseaseControlService.saveMalaria(malariaDTO));
             }else {
                 response.put("message", "Invalid request");
                 response.put("statusCode", 400);
             }

        } catch (Exception e) {
            response.put("status", "Error" + e.getMessage());
            response.put("statusCode", 500);
        }
        return ResponseEntity.ok(response);

    }

    @RequestMapping(value = "kalaAzar/saveAll", method = RequestMethod.POST,consumes = "application/json", produces = "application/json",headers = "Authorization")
    public ResponseEntity<Map<String, Object>> saveKalaAzar(@RequestBody KalaAzarDTO kalaAzarDTO) {
        Map<String, Object> response = new HashMap<>();

        try {
            logger.info("KalaAzar DTO: {}", new ObjectMapper().writeValueAsString(kalaAzarDTO));
             if(kalaAzarDTO!=null){
                 response.put("status", "Success");
                 response.put("statusCode", 200);
                 response.put("data", diseaseControlService.saveKalaAzar(kalaAzarDTO));
             }else {
                 response.put("message", "Invalid request");
                 response.put("statusCode", 400);
             }

        } catch (Exception e) {
            response.put("status", "Error" + e.getMessage());
            response.put("statusCode", 500);
        }
        return ResponseEntity.ok(response);

    }

    @RequestMapping(value = "aesJe/saveAll", method = RequestMethod.POST, consumes = "application/json", produces = "application/json",headers = "Authorization")
    public ResponseEntity<Map<String, Object>> saveAESJE(@RequestBody AesJeDTO aesJeDTO) {
        Map<String, Object> response = new HashMap<>();

        try {
            if(aesJeDTO!=null){
                response.put("status", "Success");
                response.put("statusCode", 200);
                response.put("data", diseaseControlService.saveAES(aesJeDTO));
            }else {
                response.put("message", "Invalid request");
                response.put("statusCode", 400);
            }

        } catch (Exception e) {
            response.put("status", "Error" + e.getMessage());
            response.put("statusCode", 500);
        }
        return ResponseEntity.ok(response);

    }

    @RequestMapping(value = "filaria/saveAll", method = RequestMethod.POST, consumes = "application/json", produces = "application/json",headers = "Authorization")
    public ResponseEntity<Map<String, Object>> saveFilaria(@RequestBody FilariaDTO filariaDTO) {
        Map<String, Object> response = new HashMap<>();

        try {
            if(filariaDTO!=null){
                response.put("status", "Success");
                response.put("statusCode", 200);
                response.put("data", diseaseControlService.saveFilaria(filariaDTO));
            }else {
                response.put("message", "Invalid request");
                response.put("statusCode", 400);
            }

        } catch (Exception e) {
            response.put("status", "Error" + e.getMessage());
            response.put("statusCode", 500);
        }
        return ResponseEntity.ok(response);

    }

    @RequestMapping(value = "leprosy/saveAll", method = RequestMethod.POST, consumes = "application/json", produces = "application/json",headers = "Authorization")
    public ResponseEntity<Map<String, Object>> saveLeprosy(@RequestBody LeprosyDTO leprosyDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            if(leprosyDTO!=null){
                response.put("status", "Success");
                response.put("statusCode", 200);
                response.put("data", diseaseControlService.saveLeprosy(leprosyDTO));
            }else {
                response.put("message", "Invalid request");
                response.put("statusCode", 400);
            }

        } catch (Exception e) {
            response.put("status", "Error" + e.getMessage());
            response.put("statusCode", 500);
        }
        return ResponseEntity.ok(response);

    }

    @RequestMapping(value = "leprosy/followUp/saveAll", method = RequestMethod.POST, consumes = "application/json", produces = "application/json", headers = "Authorization")
    public ResponseEntity<Map<String, Object>> saveLeprosyFollowUP(@RequestBody List<LeprosyFollowUpDTO> followUps) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (followUps != null && !followUps.isEmpty()) {
                for (LeprosyFollowUpDTO dto : followUps) {
                    diseaseControlService.saveLeprosyFollowUp(dto);
                }
                response.put("status", "Success");
                response.put("statusCode", 200);
                response.put("message", "All follow-ups saved successfully");
            } else {
                response.put("message", "Invalid request - no follow-up data");
                response.put("statusCode", 400);
            }
        } catch (Exception e) {
            response.put("status", "Error: " + e.getMessage());
            response.put("statusCode", 500);
        }
        return ResponseEntity.ok(response);

    }


    @RequestMapping(value = "leprosy/getAll", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> getAllLeprosy(
            @RequestBody Map<String, String> request,
            @RequestHeader(value = "Authorization") String authorization) {

        Map<String, Object> response = new HashMap<>();
        try {
            String userName = request.get("userName");
            if (userName != null) {
                List<DiseaseGetLeprosyDTO> result = diseaseControlService.getAllLeprosyData(userName);

                if (result != null && !result.isEmpty()) {
                    response.put("status", "Success");
                    response.put("statusCode", 200);
                    response.put("data", result); // ← Put list directly, no gson.toJson()
                } else {
                    response.put("message", "No record found");
                    response.put("statusCode", 404);
                }
            } else {
                response.put("message", "Invalid request - userName required");
                response.put("statusCode", 400);
            }
        } catch (Exception e) {
            response.put("status", "Error: " + e.getMessage());
            response.put("statusCode", 500);
        }
        return ResponseEntity.ok(response); // ← Spring serializes the whole map
    }

        @RequestMapping(value = "leprosy/followUp/getAll", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> getAllLeprosyFollowUp(
            @RequestBody Map<String, String> request,
            @RequestHeader(value = "Authorization") String authorization) {

        Map<String, Object> response = new HashMap<>();
        try {
            String userName = request.get("userName");
            if (userName != null) {
                List<LeprosyGetFollowUpDTO> result = diseaseControlService.getAllLeprosyFollowUpData(userName);

                if (result != null && !result.isEmpty()) {
                    response.put("status", "Success");
                    response.put("statusCode", 200);
                    response.put("data", result); // ← Put list directly, no gson.toJson()
                } else {
                    response.put("message", "No record found");
                    response.put("statusCode", 404);
                }
            } else {
                response.put("message", "Invalid request - userName required");
                response.put("statusCode", 400);
            }
        } catch (Exception e) {
            response.put("status", "Error: " + e.getMessage());
            response.put("statusCode", 500);
        }
        return ResponseEntity.ok(response); // ← Spring serializes the whole map
    }

    @RequestMapping(value = "getAllDisease", method = RequestMethod.POST, produces = "application/json", headers = "Authorization")
    public ResponseEntity<Map<String, Object>> getAllData(
            @RequestBody GetDiseaseRequestHandler getDiseaseRequestHandler) {
        Map<String, Object> response = new HashMap<>();
        try {
            response.put("status", "Success");
            response.put("statusCode", 200);
            response.put("data", diseaseControlService.getAllScreeningData(getDiseaseRequestHandler));

        } catch (Exception e) {
            response.put("status", "Error" + e.getMessage());
            response.put("statusCode", 500);
        }
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "mobilizationMosquitoNet/saveAll",method = RequestMethod.POST)
    public  ResponseEntity<Map<String, Object>> saveMobilizationMosquitoNet(@RequestBody List<MosquitoNetDTO> mosquitoNetDTOS){

        Map<String, Object> response = new LinkedHashMap<>();
        logger.info("Eye Checkup Get Request: {}", mosquitoNetDTOS);

        try {
            List<MosquitoNetDTO> responseObject = diseaseControlService.saveMosquitoMobilizationNet(mosquitoNetDTOS);

            if (responseObject != null && !responseObject.isEmpty()) {
                response.put("statusCode", HttpStatus.OK.value());
                response.put("data", responseObject);
                return ResponseEntity.ok(response);
            } else {
                response.put("statusCode", HttpStatus.NOT_FOUND.value());
                response.put("message", "No records found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

        } catch (Exception e) {
            logger.error("Error mobilizationMosquitoNet :", e);
            response.put("statusCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("errorMessage", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }


    @RequestMapping(value = "mobilizationMosquitoNet/getAll", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> getAllMobilizationMosquitoNet(@RequestBody GetBenRequestHandler getBenRequestHandler) {

        Map<String, Object> response = new LinkedHashMap<>();

        try {
            List<MosquitoNetDTO> responseObject = diseaseControlService.getAllMosquitoMobilizationNet(getBenRequestHandler.getAshaId());

            response.put("statusCode", HttpStatus.OK.value());
            response.put("data", responseObject);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error getAllMobilizationMosquitoNet :", e);
            response.put("statusCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("errorMessage", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    @RequestMapping(value = "cdtfVisit/saveAll", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> saveVisit(
            @RequestBody List<ChronicDiseaseVisitDTO> requestList,@RequestHeader(value = "JwtToken") String token) {

        Map<String, Object> response = new LinkedHashMap<>();
        logger.info("Chronic Disease Visit Save Request: {}", requestList);

        try {
            List<ChronicDiseaseVisitDTO> savedList =
                    diseaseControlService.saveChronicDiseaseVisit(requestList,token);

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

    @RequestMapping(value = "cdtfVisit/getAll", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> getVisitDetails(
            @RequestBody GetBenRequestHandler getBenRequestHandler) {

        Map<String, Object> response = new LinkedHashMap<>();

        try {
            List<ChronicDiseaseVisitDTO> result =
                    diseaseControlService.getCdtfVisits(getBenRequestHandler);

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


}