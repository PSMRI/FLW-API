package com.iemr.flw.controller;

import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.TBScreeningRequestDTO;
import com.iemr.flw.dto.iemr.TBSuspectedRequestDTO;
import com.iemr.flw.service.TBScreeningService;
import com.iemr.flw.service.TBSuspectedService;
import com.iemr.flw.utils.response.OutputResponse;

import io.swagger.v3.oas.annotations.Operation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;

@RestController
@RequestMapping(value = "/tb", headers = "Authorization", consumes = "application/json", produces = "application/json")
public class TBController {

    private final Logger logger = LoggerFactory.getLogger(CoupleController.class);

    @Autowired
    private TBScreeningService tbScreeningService;

    @Autowired
    private TBSuspectedService tbSuspectedService;

    @CrossOrigin()
    @Operation(summary = "get tb screening data of all beneficiaries registered with given user id")
    @RequestMapping(value = {"/screening/getAll"}, method = {RequestMethod.POST})
    public String getAllScreeningByUserId(@RequestBody GetBenRequestHandler requestDTO,
                                          @RequestHeader(value = "Authorization") String Authorization) {
        OutputResponse response = new OutputResponse();
        try {

            if (requestDTO != null) {
                logger.info("request object with timestamp : " + new Timestamp(System.currentTimeMillis()) + " "
                        + requestDTO);
                String s = tbScreeningService.getByUserId(requestDTO);
                if (s != null)
                    response.setResponse(s);
                else
                    response.setError(5000, "No record found");
            } else
                response.setError(5000, "Invalid/NULL request obj");
        } catch (Exception e) {
            logger.error("Error in tb screening get data : " + e);
            response.setError(5000, "Error in tb screening get data : " + e);
        }
        return response.toString();
    }


    @CrossOrigin()
    @Operation(summary = "save tb screening data of all beneficiaries registered with given user id")
    @RequestMapping(value = {"/screening/saveAll"}, method = {RequestMethod.POST})
    public String saveAllScreeningByUserId(@RequestBody TBScreeningRequestDTO requestDTO,
                                           @RequestHeader(value = "Authorization") String Authorization) {
        OutputResponse response = new OutputResponse();
        try {

            if (requestDTO != null) {
                logger.info("request object with timestamp : " + new Timestamp(System.currentTimeMillis()) + " "
                        + requestDTO);
                String s = tbScreeningService.save(requestDTO);
                if (s != null)
                    response.setResponse(s);
                else
                    response.setError(5000, "No record found");
            } else
                response.setError(5000, "Invalid/NULL request obj");
        } catch (Exception e) {
            logger.error("Error in save tb screening details : " + e);
            response.setError(5000, "Error in save tb suspected details : " + e);
        }
        return response.toString();
    }

    @CrossOrigin()
    @Operation(summary = "get tb suspected data of all beneficiaries registered with given user id")
    @RequestMapping(value = {"/suspected/getAll"}, method = {RequestMethod.POST})
    public String getAllSuspectedByUserId(@RequestBody GetBenRequestHandler requestDTO,
                                          @RequestHeader(value = "Authorization") String Authorization) {

        OutputResponse response = new OutputResponse();
        try {

            if (requestDTO != null) {
                logger.info("request object with timestamp : " + new Timestamp(System.currentTimeMillis()) + " "
                        + requestDTO);
                String s = tbSuspectedService.getByUserId(requestDTO);
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

    @CrossOrigin()
    @Operation(summary = "save tb suspected data of all beneficiaries registered with given user id")
    @RequestMapping(value = {"/suspected/saveAll"}, method = {RequestMethod.POST})
    public String saveAllSuspectedByUserId(@RequestBody TBSuspectedRequestDTO requestDTO,
                                           @RequestHeader(value = "Authorization") String Authorization) {
        OutputResponse response = new OutputResponse();
        try {

            if (requestDTO != null) {
                logger.info("request object with timestamp : " + new Timestamp(System.currentTimeMillis()) + " "
                        + requestDTO);
                String s = tbSuspectedService.save(requestDTO);
                if (s != null)
                    response.setResponse(s);
                else
                    response.setError(5000, "No record found");
            } else
                response.setError(5000, "Invalid/NULL request obj");
        } catch (Exception e) {
            logger.error("Error in save tb suspected details : " + e);
            response.setError(5000, "Error in save tb suspected details : " + e);
        }
        return response.toString();
    }
}
