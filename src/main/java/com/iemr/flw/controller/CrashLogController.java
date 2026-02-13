package com.iemr.flw.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iemr.flw.dto.crashlogs.CrashLogRequest;
import com.iemr.flw.service.CrashLogService;
import com.iemr.flw.utils.JwtUtil;
import com.iemr.flw.utils.response.OutputResponse;

@RestController
@RequestMapping(value = "/crash-logs", headers = "Authorization")
public class CrashLogController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    private CrashLogService crashLogService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public String uploadCrashLog(
            @RequestHeader(value = "JwtToken") String jwtToken, // Changed from Authorization
            @RequestParam("file") MultipartFile file,
            @RequestParam("metadata") String metadataJson) {

        OutputResponse response = new OutputResponse();

        try {
            // No need to remove "Bearer " prefix - JwtToken header contains raw JWT
            Integer userId = jwtUtil.extractUserId(jwtToken);

            // Parse metadata JSON
            CrashLogRequest request = objectMapper.readValue(metadataJson, CrashLogRequest.class);

            // Save crash log file
            String filePath = crashLogService.saveCrashLog(request, userId, file);

            // Build success response
            response.setResponse("Crash log saved successfully. File path: " + filePath);
            logger.info("Crash log uploaded successfully for userId: " + userId);

        } catch (Exception e) {
            logger.error("Error uploading crash log: " + e.getMessage(), e);
            response.setError(e);
        }

        return response.toString();
    }
}