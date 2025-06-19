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
import com.iemr.flw.dto.iemr.AesJeDTO;
import com.iemr.flw.dto.iemr.FilariaDTO;
import com.iemr.flw.dto.iemr.KalaAzarDTO;
import com.iemr.flw.dto.iemr.MalariaDTO;
import com.iemr.flw.dto.iemr.LeprosyDTO;
import com.iemr.flw.dto.iemr.GetDiseaseRequestHandler;
import com.iemr.flw.service.DiseaseControlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
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

    @RequestMapping(value = "getAllDisease", method = RequestMethod.GET, produces = "application/json",headers = "Authorization")
    public ResponseEntity<Map<String, Object>> getAllData(@RequestBody GetDiseaseRequestHandler getDiseaseRequestHandler) {
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


}