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
* @@ -0,0 +1,212 @@
/*
* AMRIT – Accessible Medical Records via Integrated Technology
*/
package com.iemr.flw.controller;

import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.GeneralOpdDto;
import com.iemr.flw.service.BeneficiaryService;
import com.iemr.flw.service.GeneralOpdService;
import com.iemr.flw.utils.CookieUtil;
import com.iemr.flw.utils.JwtUtil;
import com.iemr.flw.utils.response.OutputResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/generalOpd")
public class GeneralOPDController {
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(GeneralOPDController.class);

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private GeneralOpdService generalOpdService;

    @RequestMapping(value = "getBeneficiaries", method = RequestMethod.POST, headers = "Authorization")
    @Operation(summary = "get beneficiary data for given user ")
    public ResponseEntity<Map<String, Object>> getBeneficiaryDataByAsha(@RequestBody GetBenRequestHandler requestDTO, @RequestHeader(value = "Authorization") String authorization) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {

            Map<String, Object> data = new HashMap<>();
            data.put("userId", requestDTO.getUserId());
            data.put("entries", generalOpdService.getOpdListForAsha(requestDTO, authorization));
            response.put("data", data);
            response.put("statusCode", 200);
            response.put("errorMessage", "Success");
            response.put("status", "Success");
        } catch (Exception e) {
            logger.error("Error in get data : " + e);
            response.put("status", 500);
            response.put("message", "Error in get data : " + e);
        }
        return ResponseEntity.ok(response);
    }
} 