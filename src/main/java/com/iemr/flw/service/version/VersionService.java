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
package com.iemr.flw.service.version;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class VersionService {

    private static final Logger logger = LoggerFactory.getLogger(VersionService.class);
    
    @Value("${app.version:unknown}")
    private String appVersion;
    
    @Value("${maven.properties.path:META-INF/maven/com.iemr.common.flw/flw-api/pom.properties}")
    private String mavenPropertiesPath;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Get version information as a Map for direct JSON serialization
     * @return Map containing version information
     */
    public Map<String, String> getVersionInfo() {
        return buildVersionInfo();
    }

    /**
     * Get version information as JSON string (deprecated - use getVersionInfo() instead)
     * @return JSON string containing version information
     */
    @Deprecated
    public String getVersionInformation() {
        try {
            Map<String, String> versionInfo = buildVersionInfo();
            return objectMapper.writeValueAsString(versionInfo);
        } catch (Exception e) {
            logger.error("Error building version information", e);
            return createErrorResponse();
        }
    }

    private Map<String, String> buildVersionInfo() {
        Map<String, String> versionInfo = new LinkedHashMap<>();

        // Add Git information
        addGitInformation(versionInfo);
        
        // Add build information
        addBuildInformation(versionInfo);
        
        // Add current time
        versionInfo.put("current.time", getCurrentIstTimeFormatted());

        return versionInfo;
    }

    private void addGitInformation(Map<String, String> versionInfo) {
        Properties gitProps = loadPropertiesFile("git.properties");
        if (gitProps != null) {
            String commitId = gitProps.getProperty("git.commit.id",
                gitProps.getProperty("git.commit.id.abbrev", "unknown"));
            versionInfo.put("git.commit.id", commitId);

            String buildTime = gitProps.getProperty("git.build.time",
                gitProps.getProperty("git.commit.time",
                    gitProps.getProperty("git.commit.timestamp", "unknown")));
            versionInfo.put("git.build.time", buildTime);
        } else {
            logger.warn("git.properties file not found. Git information will be unavailable.");
            versionInfo.put("git.commit.id", "information unavailable");
            versionInfo.put("git.build.time", "information unavailable");
        }
    }

    private void addBuildInformation(Map<String, String> versionInfo) {
        Properties buildProps = loadPropertiesFile("META-INF/build-info.properties");
        if (buildProps != null) {
            String version = buildProps.getProperty("build.version",
                buildProps.getProperty("build.version.number",
                    buildProps.getProperty("version", appVersion)));
            versionInfo.put("build.version", version);

            String time = buildProps.getProperty("build.time",
                buildProps.getProperty("build.timestamp",
                    buildProps.getProperty("timestamp", getCurrentIstTimeFormatted())));
            versionInfo.put("build.time", time);
        } else {
            logger.info("build-info.properties not found, trying Maven properties");
            Properties mavenProps = loadPropertiesFile(mavenPropertiesPath);
            if (mavenProps != null) {
                String version = mavenProps.getProperty("version", appVersion);
                versionInfo.put("build.version", version);
                versionInfo.put("build.time", getCurrentIstTimeFormatted());
            } else {
                logger.warn("Neither build-info.properties nor Maven properties found.");
                versionInfo.put("build.version", appVersion);
                versionInfo.put("build.time", getCurrentIstTimeFormatted());
            }
        }
    }

    private String getCurrentIstTimeFormatted() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        return sdf.format(new Date());
    }

    private Properties loadPropertiesFile(String resourceName) {
        ClassLoader classLoader = getClass().getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(resourceName)) {
            if (inputStream != null) {
                Properties props = new Properties();
                props.load(inputStream);
                return props;
            }
        } catch (IOException e) {
            logger.warn("Could not load properties file: " + resourceName, e);
        }
        return null;
    }

    private String createErrorResponse() {
        try {
            Map<String, String> errorInfo = new LinkedHashMap<>();
            errorInfo.put("git.commit.id", "error retrieving information");
            errorInfo.put("git.build.time", "error retrieving information");
            errorInfo.put("build.version", appVersion);
            errorInfo.put("build.time", getCurrentIstTimeFormatted());
            errorInfo.put("current.time", getCurrentIstTimeFormatted());
            return objectMapper.writeValueAsString(errorInfo);
        } catch (Exception e) {
            logger.error("Error creating error response", e);
            return "{\"error\": \"Unable to retrieve version information\"}";
        }
    }
}
