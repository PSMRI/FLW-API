package com.iemr.flw.controller;

import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.IncentiveActivityDTO;
import com.iemr.flw.dto.iemr.IncentiveRequestDTO;
import com.iemr.flw.service.IncentiveService;
import com.iemr.flw.utils.response.OutputResponse;

import io.swagger.v3.oas.annotations.Operation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;

@RestController
@RequestMapping(value = "/incentive", headers = "Authorization", consumes = "application/json", produces = "application/json")
public class IncentiveController {

    private final Logger logger = LoggerFactory.getLogger(IncentiveController.class);

    @Autowired
    IncentiveService incentiveService;


    
    @Operation(summary = "save incentive master")
    @RequestMapping(value = {"/masterData/saveAll"}, method = {RequestMethod.POST})
    public String saveIncentiveMasterData(@RequestBody List<IncentiveActivityDTO> activityDTOS,
                                     @RequestHeader(value = "Authorization") String Authorization) {
        OutputResponse response = new OutputResponse();
        try {
            logger.info("Saving All incentives");
            if (activityDTOS != null) {
                String s = incentiveService.saveIncentivesMaster(activityDTOS);
                if (s != null)
                    response.setResponse(s);
                else
                    response.setError(5000, "No record found");
            } else
                response.setError(5000, "Invalid/NULL request obj");
        } catch (Exception e) {
            logger.error("Error in incentive master data " + e);
            response.setError(5000, "Error in incentive master data" + e);
        }
        return response.toString();
    }


    
    @Operation(summary = "get incentive master")
    @RequestMapping(value = {"/masterData/getAll"}, method = {RequestMethod.POST})
    public String saveIncentiveMasterData(@RequestBody IncentiveRequestDTO incentiveRequestDTO,
                                          @RequestHeader(value = "Authorization") String Authorization) {
        OutputResponse response = new OutputResponse();
        try {
            logger.info("get All incentives");

            // add logic for different state or district
            if (incentiveRequestDTO != null) {
                String s = incentiveService.getIncentiveMaster(incentiveRequestDTO);
                if (s != null)
                    response.setResponse(s);
                else
                    response.setError(5000, "No record found");
            } else
                response.setError(5000, "Invalid/NULL request obj");
        } catch (Exception e) {
            logger.error("Error in incentive master data " + e);
            response.setError(5000, "Error in incentive master data" + e);
        }
        return response.toString();
    }


    
    @Operation(summary = "get high risk assessment data of all beneficiaries registered with given user id")
    @RequestMapping(value = {"/fetchUserData"}, method = {RequestMethod.POST})
    public String getAllIncentivesByUserId(@RequestBody GetBenRequestHandler requestDTO,
                                       @RequestHeader(value = "Authorization") String Authorization) {
        OutputResponse response = new OutputResponse();
        try {

            if (requestDTO != null) {
                logger.info("request object with timestamp : " + new Timestamp(System.currentTimeMillis()) + " "
                        + requestDTO);
                String s = incentiveService.getAllIncentivesByUserId(requestDTO);
                if (s != null)
                    response.setResponse(s);
                else
                    response.setError(5000, "No record found");
            } else
                response.setError(5000, "Invalid/NULL request obj");
        } catch (Exception e) {
            logger.error("Error in high risk assessment data : " + e);
            response.setError(5000, "Error in high risk assessment data : " + e);
        }
        return response.toString();
    }


}
