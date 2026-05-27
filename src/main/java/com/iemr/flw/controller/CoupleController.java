package com.iemr.flw.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.EligibleCoupleDTO;
import com.iemr.flw.dto.iemr.EligibleCoupleTrackingDTO;
import com.iemr.flw.service.CoupleService;
import com.iemr.flw.utils.response.OutputResponse;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/couple", headers = "Authorization")
public class CoupleController {

    private final Logger logger = LoggerFactory.getLogger(CoupleController.class);

    @Autowired
    private CoupleService coupleService;

    private String kitPhoto1;

    private String kitPhoto2;

    @Operation(summary = "save eligible couple registration details")
    @RequestMapping(value = { "/register/saveAll" }, method = { RequestMethod.POST },consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String saveEligibleCouple(@RequestPart("data") List<EligibleCoupleDTO> eligibleCoupleDTOs,
                                     @RequestPart(value = "kitPhoto1", required = false) MultipartFile kitPhoto1,
                                     @RequestPart(value = "kitPhoto2", required = false) MultipartFile kitPhoto2,
                                     @RequestHeader(value = "Authorization") String Authorization) {
        OutputResponse response = new OutputResponse();
        try {
            logger.info("Saving All Eligible Couple Details");
            if (eligibleCoupleDTOs != null) {
                String s = coupleService.registerEligibleCouple(eligibleCoupleDTOs,kitPhoto1,kitPhoto2);
                if (s != null)
                    response.setResponse(s);
                else
                    response.setError(5000, "No record found");
            } else
                response.setError(5000, "Invalid/NULL request obj");
        } catch (Exception e) {
            logger.error("Error in saving eligible couple registration details, " + e);
            response.setError(5000, "Error in saving eligible couple registration details" + e);
        }
        return response.toString();
    }



    @Operation(summary = "save eligible couple registration details")
    @RequestMapping(value = { "/register/saveAll" }, method = { RequestMethod.POST })
    public String saveEligibleCouple(@RequestBody List<EligibleCoupleDTO> eligibleCoupleDTOs,
                                     @RequestHeader(value = "Authorization") String Authorization) {
        OutputResponse response = new OutputResponse();
        try {
            logger.info("Saving All Eligible Couple Details");
            if (eligibleCoupleDTOs != null) {
                String s = coupleService.registerEligibleCouple(eligibleCoupleDTOs);
                if (s != null)
                    response.setResponse(s);
                else
                    response.setError(5000, "No record found");
            } else
                response.setError(5000, "Invalid/NULL request obj");
        } catch (Exception e) {
            logger.error("Error in saving eligible couple registration details, " + e);
            response.setError(5000, "Error in saving eligible couple registration details" + e);
        }
        return response.toString();
    }

    @Operation(summary = "save eligible couple tracking details")
    @RequestMapping(value = { "/tracking/saveAll" }, method = { RequestMethod.POST })
    public String saveEligibleCoupleTracking(@RequestBody List<EligibleCoupleTrackingDTO> eligibleCoupleTrackingDTOs,
            @RequestHeader(value = "Authorization") String Authorization) {
        OutputResponse response = new OutputResponse();
        try {
            logger.info("Saving All Eligible Couple Tracking Details");
            if (eligibleCoupleTrackingDTOs != null) {
                String s = coupleService.registerEligibleCoupleTracking(eligibleCoupleTrackingDTOs);
                if (s != null)
                    response.setResponse(s);
                else
                    response.setError(5000, "No record found");
            } else
                response.setError(5000, "Invalid/NULL request obj");
        } catch (Exception e) {
            logger.error("Error in saving eligible couple Tracking details, " + e);
            response.setError(5000, "Error in saving eligible couple tracking details" + e);
        }
        return response.toString();
    }

    @Operation(summary = "get List of eligible couple registration details")
    @RequestMapping(value = { "/register/getAll" }, method = { RequestMethod.POST })
    public String getEligibleCouple(@RequestBody GetBenRequestHandler requestDTO,
            @RequestHeader(value = "Authorization") String Authorization) {
        OutputResponse response = new OutputResponse();
        try {
            if (requestDTO != null) {
                logger.info("fetching All Eligible Couple Details for user: " + requestDTO.getAshaId());
                String s = coupleService.getEligibleCoupleRegRecords(requestDTO);

                if (s != null)
                    response.setResponse(s);
                else
                    response.setError(5000, "No record found");
            } else
                response.setError(5000, "Invalid/NULL request obj");

        } catch (Exception e) {
            logger.error("Error in fetching eligible couple registration details, " + e);
            response.setError(5000, "Error in fetching eligible couple registration details : " + e);
        }
        return response.toString();
    }

    @Operation(summary = "get List of eligible couple tracking details")
    @RequestMapping(value = { "/tracking/getAll" }, method = { RequestMethod.POST })
    public String getEligibleCoupleTracking(@RequestBody GetBenRequestHandler requestDTO,
            @RequestHeader(value = "Authorization") String Authorization) {
        OutputResponse response = new OutputResponse();
        try {
            if (requestDTO != null) {
                logger.info("fetching All Eligible Couple Tracking Details for user: " + requestDTO.getAshaId());
                List<EligibleCoupleTrackingDTO> result = coupleService.getEligibleCoupleTracking(requestDTO);
                Gson gson = new GsonBuilder()
                        .setDateFormat("MMM dd, yyyy h:mm:ss a") // Set the desired date format
                        .create();
                String s = gson.toJson(result);
                logger.info("tracking data:"+s);
                if (s != null)
                    response.setResponse(s);
                else
                    response.setError(5000, "No record found");
            } else
                response.setError(5000, "Invalid/NULL request obj");

        } catch (Exception e) {
            logger.error("Error in fetching eligible couple tracking details, " + e);
            response.setError(5000, "Error in fetching eligible couple tracking details : " + e);
        }
        return response.toString();
    }

}
