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
package com.iemr.flw.controller.version;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iemr.flw.service.version.VersionService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
public class VersionController {

    private static final Logger logger = LoggerFactory.getLogger(VersionController.class);

    @Autowired
    private VersionService versionService;

    @Operation(summary = "Version information")
    @GetMapping(value = "/version", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> versionInformation() {
        logger.info("version Controller Start");
        Map<String, String> versionInfo = versionService.getVersionInfo();
        logger.info("version Controller End");
        return versionInfo;
    }
}
