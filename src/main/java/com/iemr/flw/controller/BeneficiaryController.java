package com.iemr.flw.controller;

import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.EyeCheckupRequestDTO;
import com.iemr.flw.dto.iemr.IFAFormSubmissionRequest;
import com.iemr.flw.dto.iemr.MdaFormSubmissionRequest;
import com.iemr.flw.dto.iemr.SAMResponseDTO;
import com.iemr.flw.dto.iemr.SamDTO;
import com.iemr.flw.service.BeneficiaryService;
import com.iemr.flw.service.IFAFormSubmissionService;
import com.iemr.flw.service.impl.MdaFormSubmissionService;
import com.iemr.flw.utils.JwtUtil;
import com.iemr.flw.utils.response.OutputResponse;
import io.swagger.v3.oas.annotations.Operation;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/beneficiary", consumes = "application/json")
public class BeneficiaryController {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(BeneficiaryController.class);

    @Autowired
    BeneficiaryService beneficiaryService;

    @Autowired
    private IFAFormSubmissionService ifaFormSubmissionService;

    @Autowired
    private MdaFormSubmissionService mdaFormSubmissionService;

    @RequestMapping(value = "/getBeneficiaryData", method = RequestMethod.POST)
    @Operation(summary = "get beneficiary data for given user ")
    public String getBeneficiaryDataByAsha(@RequestBody GetBenRequestHandler requestDTO,
            @RequestHeader(value = "Authorization") String authorization) {
        OutputResponse response = new OutputResponse();

        try {

            if (requestDTO != null) {
                logger.info("request object with timestamp : " + new Timestamp(System.currentTimeMillis()) + " "
                        + requestDTO);
                String s = beneficiaryService.getBenData(requestDTO, authorization);
                if (s != null)
                    response.setResponse(s);
                else
                    response.setError(5000, "No record found");
            } else
                response.setError(5000, "Invalid/NULL request obj");
        } catch (Exception e) {
            logger.error("Error in get data : " + e);
            response.setError(5000, "Error in get data : " + e);
        }
        return response.toString();

    }
    @RequestMapping(value = {"/eye_surgery/saveAll"}, method = RequestMethod.POST)
    public ResponseEntity<?> saveEyeSurgery(@RequestBody List<EyeCheckupRequestDTO> eyeCheckupRequestDTOS) {
        Map<String, Object> response = new LinkedHashMap<>();
        logger.info("Eye Checkup Save Request: {}", eyeCheckupRequestDTOS);

        try {
            if (eyeCheckupRequestDTOS == null || eyeCheckupRequestDTOS.isEmpty()) {
                response.put("statusCode", HttpStatus.BAD_REQUEST.value());
                response.put("message", "Request body cannot be empty");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            String responseObject = beneficiaryService.saveEyeCheckupVsit(eyeCheckupRequestDTOS);

            if (responseObject != null) {
                response.put("statusCode", HttpStatus.OK.value());
                response.put("message", responseObject);
                return ResponseEntity.ok(response);
            } else {
                response.put("statusCode", HttpStatus.BAD_REQUEST.value());
                response.put("message", "Failed to save records");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

        } catch (Exception e) {
            logger.error("Error saving eye checkup visit:", e);
            response.put("statusCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("errorMessage", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }



    @RequestMapping(value = {"/eye_surgery/getAll"}, method = RequestMethod.POST)
    public ResponseEntity<?> getAllEyeSurgery(@RequestBody GetBenRequestHandler request) {
        Map<String, Object> response = new LinkedHashMap<>();
        logger.info("Eye Checkup Get Request: {}", request);

        try {
            List<EyeCheckupRequestDTO> responseObject = beneficiaryService.getEyeCheckUpVisit(request);

            if (responseObject != null && !responseObject.isEmpty()) {
                response.put("statusCode", HttpStatus.OK.value());
                response.put("data", responseObject);
                return ResponseEntity.ok(response);
            } else {
                response.put("statusCode", HttpStatus.NOT_FOUND.value());
                response.put("message", "No records found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

        } catch (Exception e) {
            logger.error("Error fetching eye checkup visit:", e);
            response.put("statusCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("errorMessage", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @RequestMapping(value = "/ifa/saveAll",method = RequestMethod.POST)
    public ResponseEntity<?> saveFormData(@RequestBody List<IFAFormSubmissionRequest> requests) {
        String message = ifaFormSubmissionService.saveFormData(requests);
        return ResponseEntity.ok(Map.of("success", true, "message", message));
    }

    @RequestMapping(value = "ifa/getAll",method = RequestMethod.POST)
    public ResponseEntity<?> getFormData(@RequestBody GetBenRequestHandler getBenRequestHandler) {
        var data = ifaFormSubmissionService.getFormData(getBenRequestHandler);
        return ResponseEntity.ok(Map.of("success", true, "message", "Data fetched successfully", "data", data));
    }

    @RequestMapping(value = "/mda/saveAll", method = RequestMethod.POST)
    public ResponseEntity<?> saveFormData(@RequestBody List<MdaFormSubmissionRequest> requests,
            @RequestHeader(value = "Authorization") String authorization) {
        String message = mdaFormSubmissionService.saveFormData(requests);
        return ResponseEntity.ok(Map.of("success", true, "message", message));
    }

    @RequestMapping(value = "/mda/getAll", method = RequestMethod.POST)
    public ResponseEntity<?> getFormDataByCreatedBy(@RequestBody Map<String, String> request,
            @RequestHeader(value = "Authorization") String authorization) {
        String userName = request.get("userName") ;
        var data = mdaFormSubmissionService.getFormDataByUserName(userName);
        return ResponseEntity.ok(Map.of("success", true, "message", "Data fetched successfully", "data", data));
    }

}
