package com.iemr.flw.controller;

import com.google.gson.Gson;
import com.iemr.flw.domain.iemr.AdolescentHealth;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.AdolescentHealthDTO;
import com.iemr.flw.service.AdolescentHealthService;
import com.iemr.flw.utils.response.OutputResponse;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping(value = "/adolescentHealth", headers = "Authorization")
public class AdolescentHealthController {
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(AdolescentHealthController.class);

    @Autowired
    private AdolescentHealthService adolescentHealthService;

    @RequestMapping(value = "/saveAll", method = RequestMethod.POST, headers = "Authorization")
    public ResponseEntity<Map<String,Object>>  saveAdolescentHealth(@RequestBody AdolescentHealthDTO adolescentHealthDTO,@RequestHeader(value = "JwtToken") String token) {
        Map<String,Object> response = new HashMap<>();

        try {
            if (adolescentHealthDTO.getAdolescentHealths().size() != 0) {
                String result = adolescentHealthService.saveAll(adolescentHealthDTO);
                if (result != null) {
                    response.put("statusCode",200);
                    response.put("message",result);

                }

            } else {
                response.put("statusCode", 500);
                response.put("error", "Invalid/NULL request obj");
            }
        } catch (Exception e) {
            logger.error("Error in saving adolescent health data : " + e);
            response.put("statusCode",500);
            response.put("error","Error in saving adolescent health data : " + e);
        }
        return ResponseEntity.ok(response);

    }

    @RequestMapping(
            value = "/getAll",
            method = RequestMethod.POST,
            headers = "Authorization"
    )
    public ResponseEntity<Map<String, Object>> getAllAdolescentHealth(
            @RequestBody GetBenRequestHandler getBenRequestHandler) {

        Map<String, Object> response = new HashMap<>();
        long startTime = System.currentTimeMillis();
        logger.info("========================================");

        logger.info("getAllAdolescentHealth API called");
        logger.info("========================================");

        try {

            logger.info("getAllAdolescentHealth request: {}",
                    new Gson().toJson(getBenRequestHandler));

            logger.debug("Fetching adolescent health records");

            List<AdolescentHealth> resultList =
                    adolescentHealthService.getAllAdolescentHealth(getBenRequestHandler);

            int recordCount = resultList == null ? 0 : resultList.size();

            logger.info(
                    "getAllAdolescentHealth: fetched {} records",
                    recordCount
            );

            if (resultList != null && !resultList.isEmpty()) {
                response.put("statusCode", 200);
                response.put("data", resultList);
            } else {
                logger.warn("getAllAdolescentHealth: no records returned");

                response.put("statusCode", 500);
                response.put("error", "Invalid/NULL request obj");
            }
        } catch (Exception e) {
            // Logs the exception along with its full stack trace.
            logger.error("getAllAdolescentHealth: failed to fetch records", e);

            response.put("statusCode", 500);
            response.put("error", "Unable to fetch adolescent health records");
        } finally {
            logger.info(
                    "getAllAdolescentHealth completed: statusCode={}, durationMs={}",
                    response.get("statusCode"),
                    System.currentTimeMillis() - startTime
            );
        }

        return ResponseEntity.ok(response);
    }
}
