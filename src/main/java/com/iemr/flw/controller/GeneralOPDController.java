package com.iemr.flw.controller;

import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.GeneralOpdDto;
import com.iemr.flw.service.BeneficiaryService;
import com.iemr.flw.service.GeneralOpdService;
import com.iemr.flw.utils.response.OutputResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;

@RestController
@RequestMapping("/api/general-opd")
public class GeneralOPDController {
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(BeneficiaryController.class);

    @Autowired
    private GeneralOpdService generalOpdService;




    @RequestMapping(value = "/getData", method = RequestMethod.POST)
    @Operation(summary = "get beneficiary data for given user ")
    public String getBeneficiaryDataByAsha(@RequestBody GetBenRequestHandler requestDTO,
                                           @RequestHeader(value = "Authorization") String authorization) {
        OutputResponse response = new OutputResponse();
        try {
            if (requestDTO != null) {
                logger.info("request object with timestamp : " + new Timestamp(System.currentTimeMillis()) + " "
                        + requestDTO);
                String s = generalOpdService.getOpdListForAsha(requestDTO, authorization);
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
} 