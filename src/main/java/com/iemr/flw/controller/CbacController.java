package com.iemr.flw.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iemr.flw.dto.identity.CbacDTO;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.service.CbacService;
import com.iemr.flw.utils.response.OutputResponse;

import io.swagger.v3.oas.annotations.Operation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;

@RestController
@RequestMapping(value = "/cbac", headers = "Authorization", consumes = "application/json", produces = "application/json")
public class CbacController {

    private final Logger logger = LoggerFactory.getLogger(CbacController.class);

    @Autowired
    private CbacService cbacService;

    @Operation(summary = "get cbac details of all beneficiaries registered with given user id")
    @RequestMapping(value = { "/getAll" }, method = { RequestMethod.POST })
    public String getAllCbacDetailsByUserId(@RequestBody GetBenRequestHandler requestDTO,
            @RequestHeader(value = "Authorization") String Authorization) {
        OutputResponse response = new OutputResponse();
        try {
            logger.info("fetching All Cbac Details for user: " + requestDTO.getAshaId());
            if (requestDTO != null) {
                String result = cbacService.getByUserId(requestDTO);

                if (result != null)
                    response.setResponse(result);
                else
                    response.setError(5000, "No record found");
            } else
                response.setError(5000, "Invalid/NULL request obj");
        } catch (Exception e) {
            logger.error("Error in fetching cbac details by user, " + e);
            response.setError(5000, "Error in fetching cbac details : " + e);
        }
        return response.toString();
    }

    @Operation(summary = "save cbac details of all beneficiaries registered with given user name")
    @RequestMapping(value = { "/saveAll" }, method = { RequestMethod.POST })
    public String saveAllCbacDetailsByUserId(@RequestBody List<CbacDTO> cbacDTOS,
            @RequestHeader(value = "Authorization") String Authorization) {
        OutputResponse response = new OutputResponse();
        try {
            logger.info("Saving Cbac Details with timestamp : " + new Timestamp(System.currentTimeMillis()));
            if (cbacDTOS.size() != 0) {
                String s = cbacService.save(cbacDTOS);
                if (s != null)
                    response.setResponse(s);
                else
                    response.setError(5000, "No record found");
            } else
                response.setError(5000, "Invalid/NULL request obj");
        } catch (Exception e) {
            logger.error("Error in saving cbac details by user, " + e);
            response.setError(5000, "Error in saving cbac details : " + e);
        }
        return response.toString();
    }
}
