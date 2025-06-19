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

import com.iemr.flw.dto.iemr.*;
import com.iemr.flw.service.VillageLevelFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/forms/villageLevel", headers = "Authorization")
public class VillageLevelFormController {

    @Autowired
    private VillageLevelFormService villageLevelFormService;

    @RequestMapping(value = "vhnd/saveAll", method = RequestMethod.POST, headers = "Authorization")
    public ResponseEntity<Map<String, Object>> saveVhndForm(@RequestBody VhndDto dto) {
        Map<String, Object> response = new HashMap<>();
        if (!dto.getEntries().isEmpty()) {
            Boolean isSaved = villageLevelFormService.saveForm(dto);
            boolean ok = isSaved;
            response.put("status", ok ? "Success" : "Fail");
            response.put("statusCode", ok ? 200 : 500);
            response.put("message", "Save Successfully");
            return new ResponseEntity<>(response, ok ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR);

        } else {
            response.put("status", "Fail");
            response.put("statusCode", 400);
            response.put("message", "Invalid Request Object");
            return ResponseEntity.ok(response);

        }


    }

    @RequestMapping(value = "vhnc/saveAll", method = RequestMethod.POST, headers = "Authorization")
    public ResponseEntity<Map<String, Object>> saveVhncForm(@RequestBody VhncDto dto) {
        Map<String, Object> response = new HashMap<>();

        if (!dto.getEntries().isEmpty()) {
            Boolean isSaved = villageLevelFormService.saveVhncForm(dto);
            boolean ok = isSaved;
            response.put("status", ok ? "Success" : "Fail");
            response.put("statusCode", ok ? 200 : 500);
            response.put("message", "Save Successfully");
            return new ResponseEntity<>(response, ok ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            response.put("status", "Fail");
            response.put("statusCode", 400);
            response.put("message", "Invalid Request Object");
            return ResponseEntity.ok(response);


        }


    }

    @RequestMapping(value = "phc/saveAll", method = RequestMethod.POST, headers = "Authorization")
    public ResponseEntity<Map<String, Object>> savePhcForm(@RequestBody PhcReviewMeetingDTO dto) {
        Map<String, Object> response = new HashMap<>();

        if (!dto.getEntries().isEmpty()) {
            Boolean isSaved = villageLevelFormService.savePhcForm(dto);
            boolean ok = isSaved;
            response.put("status", ok ? "Success" : "Fail");
            response.put("statusCode", ok ? 200 : 500);
            response.put("message", "Save Successfully");
            return new ResponseEntity<>(response, ok ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            response.put("status", "Fail");
            response.put("statusCode", 400);
            response.put("message", "Invalid Request Object");
            return ResponseEntity.ok(response);

        }


    }

    @RequestMapping(value = "ahd/saveAll", method = RequestMethod.POST, headers = "Authorization")
    public ResponseEntity<Map<String, Object>> saveAhdForm(@RequestBody AhdMeetingDto dto) {
        Map<String, Object> response = new HashMap<>();

        if(!dto.getEntries().isEmpty()){
            Boolean isSaved = villageLevelFormService.saveAhdForm(dto);
            boolean ok = isSaved;
            response.put("status", ok ? "Success" : "Fail");
            response.put("statusCode", ok ? 200 : 500);
            response.put("message", "Save Successfully");
            return new ResponseEntity<>(response, ok ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR);
        }else {
            response.put("status", "Fail");
            response.put("statusCode", 400);
            response.put("message", "Invalid Request Object");
            return ResponseEntity.ok(response);
        }

    }

    @RequestMapping(value = "deworming/saveAll", method = RequestMethod.POST, headers = "Authorization")
    public ResponseEntity<Map<String, Object>> saveDewormingForm(@RequestBody DewormingDto dto) {
        Map<String, Object> response = new HashMap<>();

        if(!dto.getEntries().isEmpty()){
            Boolean isSaved = villageLevelFormService.saveDewormingForm(dto);
            boolean ok = isSaved;
            response.put("status", ok ? "Success" : "Fail");
            response.put("statusCode", ok ? 200 : 500);
            response.put("message", "Save Successfully");
            return new ResponseEntity<>(response, ok ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR);
        }else {
            response.put("status", "Fail");
            response.put("statusCode", 400);
            response.put("message", "Invalid Request Object");
            return ResponseEntity.ok(response);
        }

    }


    @RequestMapping(value = "getAll", method = RequestMethod.POST, headers = "Authorization")
    public ResponseEntity<Map<String, Object>> getVillageLevelFormData(@RequestBody GetVillageLevelRequestHandler getVillageLevelRequestHandler) {
        Map<String, Object> response = new LinkedHashMap<>();

        try {
            Map<String, Object> data = new HashMap<>();
            data.put("userId", getVillageLevelRequestHandler.getUserId());
            data.put("entries", villageLevelFormService.getAll(getVillageLevelRequestHandler));
            response.put("data", data);
            response.put("statusCode", 200);
            response.put("message", "Success");
        } catch (Exception e) {
            response.put("statusCode", 500);
            response.put("errorMessage", e.getMessage());

        }

        return ResponseEntity.ok(response);
    }


}