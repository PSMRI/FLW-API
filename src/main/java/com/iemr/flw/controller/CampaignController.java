package com.iemr.flw.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.iemr.flw.domain.iemr.CampaignOrs;
import com.iemr.flw.domain.iemr.FilariasisCampaign;
import com.iemr.flw.domain.iemr.PulsePolioCampaign;
import com.iemr.flw.dto.iemr.*;
import com.iemr.flw.service.CampaignService;
import com.iemr.flw.utils.exception.IEMRException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.*;

@RestController
@RequestMapping(value = "/campaign")
public class CampaignController {
    private final Logger logger = LoggerFactory.getLogger(CampaignController.class);

    @Autowired
    private CampaignService campaignService;

    @PostMapping("/ors/distribution/saveAll")
    public ResponseEntity<Map<String, Object>> saveOrsDistribution(
            @RequestPart("formDataJson") String fields,
            @RequestHeader("jwtToken") String token) {

        Map<String, Object> response = new LinkedHashMap<>();

        try {

            // Validate input
            if (fields == null || fields.trim().isEmpty()) {
                response.put("statusCode", HttpStatus.BAD_REQUEST.value());
                response.put("message", "Form data is required");
                return ResponseEntity.badRequest().body(response);
            }

            // Parse JSON to DTO
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            OrsCampaignDTO requestDTO = objectMapper.readValue(fields, OrsCampaignDTO.class);

            // Validate fields
            if (requestDTO.getFields() == null) {
                response.put("statusCode", HttpStatus.BAD_REQUEST.value());
                response.put("message", "Campaign fields are required");
                return ResponseEntity.badRequest().body(response);
            }

            logger.info("Parsed DTO - Start Date: {}, End Date: {}, Families: {}, Photos: {}",
                    requestDTO.getFields().getStartDate(),
                    requestDTO.getFields().getEndDate(),
                    requestDTO.getFields().getNumberOfFamilies(),
                    requestDTO.getFields().getCampaignPhotos()
            );

            // Create DTO
            OrsCampaignDTO campaignDTO = new OrsCampaignDTO();
            campaignDTO.setFields(requestDTO.getFields());

            List<OrsCampaignDTO> orsCampaignDTOList = Collections.singletonList(campaignDTO);

            // Save to database
            List<CampaignOrs> result = campaignService.saveOrsCampaign(orsCampaignDTOList, token);

            if (result != null && !result.isEmpty()) {
                response.put("statusCode", HttpStatus.OK.value());
                response.put("message", "Campaign saved successfully");
                response.put("data", result);
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } else {
                response.put("statusCode", HttpStatus.BAD_REQUEST.value());
                response.put("message", "Failed to save campaign");
                return ResponseEntity.badRequest().body(response);
            }

        } catch (JsonProcessingException e) {
            logger.error("JSON parsing error: {}", e.getMessage(), e);
            response.put("statusCode", HttpStatus.BAD_REQUEST.value());
            response.put("message", "Invalid JSON format");
            response.put("errorMessage", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (IEMRException e) {
            logger.error("Business logic error: {}", e.getMessage(), e);
            response.put("statusCode", HttpStatus.BAD_REQUEST.value());
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            logger.error("Error saving ORS distribution: {}", e.getMessage(), e);
            response.put("statusCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("message", "Internal server error occurred");
            response.put("errorMessage", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @RequestMapping(value = "ors/distribution/getAll", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> getAllOrsDistribution(@RequestHeader(value = "jwtToken") String token) {

        Map<String, Object> response = new LinkedHashMap<>();

        try {
            List<OrsCampaignResponseDTO> result  = campaignService.getOrsCampaign(token);


            if (result != null) {
                response.put("statusCode", HttpStatus.OK.value());
                response.put("data", result);
                return ResponseEntity.ok(response);
            } else {
                response.put("statusCode", HttpStatus.NOT_FOUND.value());
                response.put("message", "No records found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

        } catch (Exception e) {
            logger.error("Error save ors distribution :", e.getMessage());
            response.put("statusCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("errorMessage", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @PostMapping("/polio/campaign/saveAll")
    public ResponseEntity<Map<String, Object>> savePolioCampaign(
            @RequestPart("formDataJson") String fields,
            @RequestHeader("jwtToken") String token) {

        Map<String, Object> response = new LinkedHashMap<>();

        try {
            logger.info("Received polio campaign data");

            // Validate input
            if (fields == null || fields.trim().isEmpty()) {
                response.put("statusCode", HttpStatus.BAD_REQUEST.value());
                response.put("message", "Form data is required");
                return ResponseEntity.badRequest().body(response);
            }

            // Parse JSON to DTO
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            PolioCampaignDTO requestDTO = objectMapper.readValue(fields, PolioCampaignDTO.class);

            // Validate fields
            if (requestDTO.getFields() == null) {
                response.put("statusCode", HttpStatus.BAD_REQUEST.value());
                response.put("message", "Campaign fields are required");
                return ResponseEntity.badRequest().body(response);
            }

            logger.info("Parsed DTO - Start Date: {}, End Date: {}, Families: {}, Individuals: {}, Photos: {}",
                    requestDTO.getFields().getStartDate(),
                    requestDTO.getFields().getEndDate(),
                    requestDTO.getFields().getNumberOfChildren(),

                    requestDTO.getFields().getCampaignPhotos()
            );

            // Create DTO
            PolioCampaignDTO campaignDTO = new PolioCampaignDTO();
            campaignDTO.setFields(requestDTO.getFields());

            List<PolioCampaignDTO> polioCampaignDTOList = Collections.singletonList(campaignDTO);

            // Save to database
            List<PulsePolioCampaign> result = campaignService.savePolioCampaign(polioCampaignDTOList, token);

            if (result != null && !result.isEmpty()) {
                response.put("statusCode", HttpStatus.OK.value());
                response.put("message", "polio  campaign saved successfully");
                response.put("data", result);
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } else {
                response.put("statusCode", HttpStatus.BAD_REQUEST.value());
                response.put("message", "Failed to save Polio campaign");
                return ResponseEntity.badRequest().body(response);
            }

        } catch (JsonProcessingException e) {
            logger.error("JSON parsing error: {}", e.getMessage(), e);
            response.put("statusCode", HttpStatus.BAD_REQUEST.value());
            response.put("message", "Invalid JSON format");
            response.put("errorMessage", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (IEMRException e) {
            logger.error("Business logic error: {}", e.getMessage(), e);
            response.put("statusCode", HttpStatus.BAD_REQUEST.value());
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            logger.error("Error saving filariasis campaign: {}", e.getMessage(), e);
            response.put("statusCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("message", "Internal server error occurred");
            response.put("errorMessage", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @RequestMapping(value = "polio/campaign/getAll", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> getAllPolioCampaign(@RequestHeader(value = "jwtToken") String token) {

        Map<String, Object> response = new LinkedHashMap<>();

        try {
            List<PolioCampaignResponseDTO> result  = campaignService.getPolioCampaign(token);


            if (result != null) {
                response.put("statusCode", HttpStatus.OK.value());
                response.put("data", result);
                return ResponseEntity.ok(response);
            } else {
                response.put("statusCode", HttpStatus.NOT_FOUND.value());
                response.put("message", "No records found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

        } catch (Exception e) {
            logger.error("Error save ors distribution :", e.getMessage());
            response.put("statusCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("errorMessage", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @PostMapping("/filariasis/campaign/saveAll")
    public ResponseEntity<Map<String, Object>> saveFilariasisCampaign(
            @RequestPart("formDataJson") String fields,
            @RequestHeader("jwtToken") String token) {

        Map<String, Object> response = new LinkedHashMap<>();

        try {
            logger.info("Received polio campaign data");

            // Validate input
            if (fields == null || fields.trim().isEmpty()) {
                response.put("statusCode", HttpStatus.BAD_REQUEST.value());
                response.put("message", "Form data is required");
                return ResponseEntity.badRequest().body(response);
            }

            // Parse JSON to DTO
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            FilariasisCampaignDTO requestDTO = objectMapper.readValue(fields, FilariasisCampaignDTO.class);

            // Validate fields
            if (requestDTO.getFields() == null) {
                response.put("statusCode", HttpStatus.BAD_REQUEST.value());
                response.put("message", "Campaign fields are required");
                return ResponseEntity.badRequest().body(response);
            }

            logger.info("Parsed DTO - Start Date: {}, End Date: {}, Families: {}, Individuals: {}, Photos: {}",
                    requestDTO.getFields().getStartDate(),
                    requestDTO.getFields().getEndDate(),
                    requestDTO.getFields().getNumberOfIndividuals(),
                    requestDTO.getFields().getNumberOfIndividuals(),

                    requestDTO.getFields().getMdaPhotos()
            );

            // Create DTO
            FilariasisCampaignDTO campaignDTO = new FilariasisCampaignDTO();
            campaignDTO.setFields(requestDTO.getFields());

            List<FilariasisCampaignDTO> filariasisCampaignDTOS = Collections.singletonList(campaignDTO);


            // Save to database
            List<FilariasisCampaign> result = campaignService.saveFilariasisCampaign(filariasisCampaignDTOS, token);

            if (result != null && !result.isEmpty()) {
                response.put("statusCode", HttpStatus.OK.value());
                response.put("message", "filariasis campaign saved successfully");
                response.put("data", result);
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } else {
                response.put("statusCode", HttpStatus.BAD_REQUEST.value());
                response.put("message", "Failed to save filariasis campaign");
                return ResponseEntity.badRequest().body(response);
            }

        } catch (JsonProcessingException e) {
            logger.error("JSON parsing error: {}", e.getMessage(), e);
            response.put("statusCode", HttpStatus.BAD_REQUEST.value());
            response.put("message", "Invalid JSON format");
            response.put("errorMessage", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (IEMRException e) {
            logger.error("Business logic error: {}", e.getMessage(), e);
            response.put("statusCode", HttpStatus.BAD_REQUEST.value());
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            logger.error("Error saving filariasis campaign: {}", e.getMessage(), e);
            response.put("statusCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("message", "Internal server error occurred");
            response.put("errorMessage", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @RequestMapping(value = "filariasis/campaign/getAll", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> getAllFilariasisCampaign(@RequestHeader(value = "jwtToken") String token) {

        Map<String, Object> response = new LinkedHashMap<>();

        try {
            List<FilariasisResponseDTO> result  = campaignService.getAllFilariasisCampaign(token);


            if (result != null) {
                response.put("statusCode", HttpStatus.OK.value());
                response.put("data", result);
                return ResponseEntity.ok(response);
            } else {
                response.put("statusCode", HttpStatus.NOT_FOUND.value());
                response.put("message", "No records found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

        } catch (Exception e) {
            logger.error("Error save ors distribution :", e.getMessage());
            response.put("statusCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("errorMessage", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
