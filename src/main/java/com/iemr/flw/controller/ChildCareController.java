package com.iemr.flw.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iemr.flw.domain.iemr.HbncVisit;
import com.iemr.flw.domain.iemr.IfaDistribution;
import com.iemr.flw.domain.iemr.SamVisitResponseDTO;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.*;
import com.iemr.flw.service.ChildCareService;
import com.iemr.flw.utils.response.OutputResponse;

import io.swagger.v3.oas.annotations.Operation;

import jakarta.mail.Multipart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/child-care", produces = "application/json")
public class ChildCareController {

    private final Logger logger = LoggerFactory.getLogger(DeathReportsController.class);

    @Autowired
    private ChildCareService childCareService;

    @Operation(summary = "save HBYC details")
    @RequestMapping(value = {"/hbycVisit/saveAll"}, method = {RequestMethod.POST})
    public String saveHbycRecords(@RequestBody List<HbycRequestDTO> hbycDTOs,
                                  @RequestHeader(value = "JwtToken") String token) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT); // Pretty print

        // Request log

        OutputResponse response = new OutputResponse();

        try {

            logger.info("Saving All HBYC Details");
            if (hbycDTOs != null) {
                String s = childCareService.registerHBYC(hbycDTOs);
                if (s != null)
                    response.setResponse(s);
                else
                    response.setError(5000, "No record found");
            } else
                response.setError(5000, "Invalid/NULL request obj");
        } catch (Exception e) {
            logger.error("Error in saving HBYC details, " + e);
            response.setError(5000, "Error in saving HBYC details" + e);
        }
        return response.toString();
    }

    @Operation(summary = "get List of HBYC details")
    @RequestMapping(value = {"/hbycVisit/getAll"}, method = {RequestMethod.POST})
    public String getHbycRecords(@RequestBody GetBenRequestHandler requestDTO) {
        OutputResponse response = new OutputResponse();
        try {
            logger.info("fetching All HBYC Details for user: " + requestDTO.getAshaId());
            if (requestDTO != null) {
                List<HbycVisitResponseDTO> result = childCareService.getHbycRecords(requestDTO);
                Gson gson = new GsonBuilder().setDateFormat("MMM dd, yyyy h:mm:ss a").create();
                String s = gson.toJson(result);
                if (s != null)
                    response.setResponse(s);
                else
                    response.setError(5000, "No record found");
            } else
                response.setError(5000, "Invalid/NULL request obj");

        } catch (Exception e) {
            logger.error("Error in fetching HBYC details, " + e);
            response.setError(5000, "Error in fetching HBYC details : " + e);
        }
        return response.toString();
    }

    @PostMapping("/hbncVisit/saveAll")
    public String saveHBNCVisit(@RequestBody List<HbncRequestDTO> hbncRequestDTOs,
                                @RequestHeader(value = "Authorization") String Authorization) {
        OutputResponse response = new OutputResponse();

        try {
            if (!hbncRequestDTOs.isEmpty()) {
                logger.info("Saving HBNC details at: " + new Timestamp(System.currentTimeMillis()));

                String result = childCareService.saveHBNCDetails(hbncRequestDTOs); // <-- actual save

                if (result != null)
                    response.setResponse(result);
                else
                    response.setError(500, "Failed to save HBNC visit data.");
            } else {
                response.setError(400, "Empty request list.");
            }
        } catch (Exception e) {
            logger.error("Error saving HBNC visit: ", e);
            response.setError(500, "Server error: " + e.getMessage());
        }
        return response.toString();
    }


    @Operation(summary = "get hbnc visit details")
    @RequestMapping(value = {"/hbncVisit/getAll"}, method = {RequestMethod.POST})
    public ResponseEntity<StandardResponse<List<HbncVisitResponseDTO>>> getHBNCVisitDetails(
            @RequestBody GetBenRequestHandler requestDTO) {

        StandardResponse<List<HbncVisitResponseDTO>> response = new StandardResponse<>();

        try {
            if (requestDTO != null) {
                logger.info("Request: " + requestDTO);

                List<HbncVisitResponseDTO> data = childCareService.getHBNCDetails(requestDTO);

                response.setStatusCode(200);
                response.setStatus("Success");
                response.setErrorMessage("Success");
                response.setData(data);

                return ResponseEntity.ok(response);

            } else {
                response.setStatusCode(400);
                response.setStatus("Failed");
                response.setErrorMessage("Invalid request object");
                response.setData(null);
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            logger.error("Exception in fetching HBNC visits", e);

            response.setStatusCode(500);
            response.setStatus("Failed");
            response.setErrorMessage("Internal Server Error: " + e.getMessage());
            response.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }



    @Operation(summary = "save child vaccination details")
    @RequestMapping(value = {"/vaccination/saveAll"}, method = {RequestMethod.POST})
    public String saveChildVaccinationDetails(@RequestBody List<ChildVaccinationDTO> childVaccinationDTOS,
                                              @RequestHeader(value = "Authorization") String Authorization) {
        OutputResponse response = new OutputResponse();
        try {
            if (childVaccinationDTOS.size() != 0) {
                logger.info("Saving Child Vaccination details with timestamp : "
                        + new Timestamp(System.currentTimeMillis()));
                String s = childCareService.saveChildVaccinationDetails(childVaccinationDTOS);
                if (s != null)
                    response.setResponse(s);
                else
                    response.setError(5000, "Saving Child vaccination data to db failed");
            } else
                response.setError(5000, "Invalid/NULL request obj");
        } catch (Exception e) {
            logger.error("Error in save child vaccination details : " + e);
            response.setError(5000, "Error in save child vaccination details : " + e);
        }
        return response.toString();
    }

    @Operation(summary = "get child vaccination details")
    @RequestMapping(value = {"/vaccination/getAll"}, method = {RequestMethod.POST})
    public String getChildVaccinationDetails(@RequestBody GetBenRequestHandler requestDTO,
                                             @RequestHeader(value = "Authorization") String Authorization) {
        OutputResponse response = new OutputResponse();
        try {
            if (requestDTO != null) {
                logger.info("request object for getting child vaccination with timestamp : "
                        + new Timestamp(System.currentTimeMillis()) + " "
                        + requestDTO);
                List<ChildVaccinationDTO> result = childCareService.getChildVaccinationDetails(requestDTO);
                Gson gson = new GsonBuilder().setDateFormat("MMM dd, yyyy h:mm:ss a").create();
                String s = gson.toJson(result);
                if (s != null)
                    response.setResponse(s);
                else
                    response.setError(5000, "No record found");
            } else
                response.setError(5000, "Invalid/NULL request obj");
        } catch (Exception e) {
            logger.error("Error in Child Vaccination get data : " + e);
            response.setError(5000, "Error in Child Vaccination get data : " + e);
        }
        return response.toString();
    }

    @Operation(summary = "get child vaccination details")
    @RequestMapping(value = {"/vaccine/getAll"}, method = {RequestMethod.GET})
    public String getChildVaccinationDetails(@RequestParam(value = "category") String category,
                                             @RequestHeader(value = "Authorization") String Authorization) {
        OutputResponse response = new OutputResponse();
        try {
            logger.info("request object for getting all vaccines with timestamp : "
                    + new Timestamp(System.currentTimeMillis()) +
                    " for category: " + category);
            List<VaccineDTO> result = childCareService.getAllChildVaccines(category);
            Gson gson = new GsonBuilder().setDateFormat("MMM dd, yyyy h:mm:ss a").create();
            String s = gson.toJson(result);
            if (s != null)
                response.setResponse(s);
            else
                response.setError(5000, "No record found");
        } catch (Exception e) {
            logger.error("Error in Vaccines get data : " + e);
            response.setError(5000, "Error in Vaccines get data : " + e);
        }
        return response.toString();
    }
    @RequestMapping(value = {"/sam/saveAll"}, method = RequestMethod.POST)
    public ResponseEntity<?> saveSevereAcuteMalnutrition(@RequestBody List<SamDTO> samRequest) {
        Map<String, Object> response = new LinkedHashMap<>();
        logger.info("SAM Request: {}", samRequest);

        try {
            if (samRequest == null || samRequest.isEmpty()) {
                response.put("statusCode", HttpStatus.BAD_REQUEST.value());
                response.put("message", "Request body cannot be empty");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }


            String responseObject = childCareService.saveSamDetails(samRequest);

            if (responseObject != null) {
                response.put("statusCode", HttpStatus.OK.value());
                response.put("message", responseObject);
                return ResponseEntity.ok(response);
            } else {
                response.put("statusCode", HttpStatus.BAD_REQUEST.value());
                response.put("message", "Failed to save SAM details");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

        } catch (Exception e) {
            logger.error("Error saving SAM details:", e);
            response.put("statusCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("errorMessage", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @RequestMapping(value = {"/sam/getAll"}, method = RequestMethod.POST)
    public ResponseEntity<?> getAllSevereAcuteMalnutrition(@RequestBody GetBenRequestHandler request) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            List<SAMResponseDTO> responseObject = childCareService.getSamVisitsByBeneficiary(request);

            if (responseObject != null && !responseObject.isEmpty()) {
                response.put("statusCode", HttpStatus.OK.value());
                response.put("data", responseObject);
                return ResponseEntity.ok(response);
            } else {
                response.put("statusCode", HttpStatus.NOT_FOUND.value());
                response.put("message", "No SAM records found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

        } catch (Exception e) {
            logger.error("Error fetching SAM records:", e);
            response.put("statusCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("errorMessage", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @RequestMapping(value = {"/ors/saveAll"}, method = RequestMethod.POST)
    public ResponseEntity<?> saveOrsDistribution(@RequestBody List<OrsDistributionDTO> orsDistributionDTOS) {
        Map<String, Object> response = new LinkedHashMap<>();
        logger.info("ORS Request: {}", orsDistributionDTOS);

        try {
            if (orsDistributionDTOS == null || orsDistributionDTOS.isEmpty()) {
                response.put("statusCode", HttpStatus.BAD_REQUEST.value());
                response.put("message", "Request body cannot be empty");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            String responseObject = childCareService.saveOrsDistributionDetails(orsDistributionDTOS);

            if (responseObject != null) {
                response.put("statusCode", HttpStatus.OK.value());
                response.put("message", responseObject);
                return ResponseEntity.ok(response);
            } else {
                response.put("statusCode", HttpStatus.BAD_REQUEST.value());
                response.put("message", "Failed to save ORS details");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

        } catch (Exception e) {
            logger.error("Error saving ORS:", e);
            response.put("statusCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("errorMessage", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @RequestMapping(value = {"/ors/getAll"}, method = RequestMethod.POST)
    public ResponseEntity<?> getAllOrDistribution(@RequestBody GetBenRequestHandler request) {
        Map<String, Object> response = new LinkedHashMap<>();

        try {
            List<OrsDistributionResponseDTO> responseObject = childCareService.getOrdDistrubtion(request);

            if (responseObject != null && !responseObject.isEmpty()) {
                response.put("statusCode", HttpStatus.OK.value());
                response.put("data", responseObject);
                return ResponseEntity.ok(response);
            } else {
                response.put("statusCode", HttpStatus.NOT_FOUND.value());
                response.put("message", "No ORS records found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

        } catch (Exception e) {
            logger.error("Error fetching ORS records:", e);
            response.put("statusCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("errorMessage", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @RequestMapping(value = {"/ifa/saveAll"}, method = RequestMethod.POST)
    public ResponseEntity<?> saveIfDistribution(@RequestBody List<IfaDistributionDTO> ifaDistributionDTOS) {
        Map<String, Object> response = new LinkedHashMap<>();
        logger.info("IFA Request: {}", ifaDistributionDTOS);

        try {
            if (ifaDistributionDTOS == null || ifaDistributionDTOS.isEmpty()) {
                response.put("statusCode", HttpStatus.BAD_REQUEST.value());
                response.put("message", "Request body cannot be empty");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            List<IfaDistribution> responseObject = childCareService.saveAllIfa(ifaDistributionDTOS);

            if (responseObject != null && !responseObject.isEmpty()) {
                response.put("statusCode", HttpStatus.OK.value());
                response.put("message", "IFA saved successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("statusCode", HttpStatus.BAD_REQUEST.value());
                response.put("message", "Failed to save IFA details");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

        } catch (Exception e) {
            logger.error("Error saving IFA:", e);
            response.put("statusCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("errorMessage", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @RequestMapping(value = {"/ifa/getAll"}, method = RequestMethod.POST)
    public ResponseEntity<?> getIfaDistribution(@RequestBody GetBenRequestHandler request) {
        Map<String, Object> response = new LinkedHashMap<>();

        try {
            List<IfaDistributionDTO> responseObject = childCareService.getByBeneficiaryId(request);

            if (responseObject != null && !responseObject.isEmpty()) {
                response.put("statusCode", HttpStatus.OK.value());
                response.put("data", responseObject);
                return ResponseEntity.ok(response);
            } else {
                response.put("statusCode", HttpStatus.OK.value());
                response.put("message", "No IFA records found");
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }

        } catch (Exception e) {
            logger.error("Error fetching IFA records:", e);
            response.put("statusCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("errorMessage", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
