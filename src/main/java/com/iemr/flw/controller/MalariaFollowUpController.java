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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/follow-up", headers = "Authorization")
public class MalariaFollowUpController {

    @Autowired
    private MalariaFollowUpService followUpService;

    @RequestMapping(value = "save", method = RequestMethod.POST, headers = "Authorization")
    public ResponseEntity<Map<String, Object>> save(@RequestBody MalariaFollowUpDTO dto) {
        Map<String, Object> response = new HashMap<>();

        try {
            Boolean result = followUpService.saveFollowUp(dto);
            if (result) {
                response.put("status", "Success");
                response.put("statusCode", 200);
                response.put("message", "Follow-up saved successfully");
            } else {
                response.put("status", "Failed");
                response.put("statusCode", 400);
            }
        } catch (Exception e) {
            response.put("status", "Error: " + e.getMessage());
            response.put("statusCode", 500);
        }
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "get", method = RequestMethod.POST, headers = "Authorization")
    public ResponseEntity<Map<String, Object>> getFollowUpsByUserId(@RequestBody GetDiseaseRequestHandler getDiseaseRequestHandler) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<MalariaFollowListUpDTO> data = followUpService.getByUserId(getDiseaseRequestHandler.getUserId());
            response.put("status", "Success");
            response.put("statusCode", 200);
            response.put("data", data);
            if (data.isEmpty()) {
                response.put("message", "No records found");
            }
        } catch (Exception e) {
            response.put("status", "Error: " + e.getMessage());
            response.put("statusCode", 500);
        }
        return ResponseEntity.ok(response);


    }
}
