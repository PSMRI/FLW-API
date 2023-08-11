package com.iemr.flw.controller;

import com.google.gson.Gson;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.CdrDTO;
import com.iemr.flw.dto.iemr.EligibleCoupleDTO;
import com.iemr.flw.dto.iemr.EligibleCoupleTrackingDTO;
import com.iemr.flw.dto.iemr.MdsrDTO;
import com.iemr.flw.service.CoupleService;
import com.iemr.flw.service.DeathReportsService;
import com.iemr.flw.utils.ApiResponse;
import com.iemr.flw.utils.response.OutputResponse;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/death-reports", headers = "Authorization")
public class DeathReportsController {

    private final Logger logger = LoggerFactory.getLogger(DeathReportsController.class);

    @Autowired
    private DeathReportsService deathReportsService;

    @CrossOrigin()
    @ApiOperation(value = "save CDR details", consumes = "application/json", produces = "application/json")
    @RequestMapping(value = { "/cdr/saveAll" }, method = { RequestMethod.POST })
    public String saveCdrRecords(@RequestBody List<CdrDTO> cdrDTOS,
                                     @RequestHeader(value = "Authorization") String Authorization) {
        OutputResponse response = new OutputResponse();
        try {
            logger.info("Saving All CDR Details");
            if (cdrDTOS != null) {
                String s = deathReportsService.registerCDR(cdrDTOS);
                if (s != null)
                    response.setResponse(s);
                else
                    response.setError(5000, "No record found");
            } else
                response.setError(5000, "Invalid/NULL request obj");
        } catch (Exception e) {
            logger.error("Error in saving CDR details, " + e);
            response.setError(5000, "Error in saving CDR details" + e);
        }
        return response.toString();
    }

    @CrossOrigin()
    @ApiOperation(value = "save MDSR details", consumes = "application/json", produces = "application/json")
    @RequestMapping(value = { "/mdsr/saveAll" }, method = { RequestMethod.POST })
    public String saveMdsrRecords(@RequestBody List<MdsrDTO> mdsrDTOS,
                                             @RequestHeader(value = "Authorization") String Authorization) {
        OutputResponse response = new OutputResponse();
        try {
            logger.info("Saving All MDSR Details");
            if (mdsrDTOS != null) {
                String s = deathReportsService.registerMDSR(mdsrDTOS);
                if (s != null)
                    response.setResponse(s);
                else
                    response.setError(5000, "No record found");
            } else
                response.setError(5000, "Invalid/NULL request obj");
        } catch (Exception e) {
            logger.error("Error in saving MDSR details, " + e);
            response.setError(5000, "Error in saving MDSR details" + e);
        }
        return response.toString();
    }

    @CrossOrigin()
    @ApiOperation(value = "get List of CDR details", consumes = "application/json", produces = "application/json")
    @RequestMapping(value = { "/cdr/getAll" }, method = { RequestMethod.POST })
    public String getCdrRecords(@RequestBody GetBenRequestHandler requestDTO,
                                    @RequestHeader(value = "Authorization") String Authorization) {
        OutputResponse response = new OutputResponse();
        try {
            logger.info("fetching All CDR Details for user: " + requestDTO.getAshaId());
            if (requestDTO != null) {
                List<CdrDTO> result = deathReportsService.getCdrRecords(requestDTO);
                String s = (new Gson()).toJson(result);
                if (s != null)
                    response.setResponse(s);
                else
                    response.setError(5000, "No record found");
            } else
                response.setError(5000, "Invalid/NULL request obj");

        } catch (Exception e) {
            logger.error("Error in fetching CDR details, " + e);
            response.setError(5000, "Error in fetching CDR details : " + e);
        }
        return response.toString();
    }

    @CrossOrigin()
    @ApiOperation(value = "get List of MDSR details", consumes = "application/json", produces = "application/json")
    @RequestMapping(value = { "/mdsr/getAll" }, method = { RequestMethod.POST })
    public String getMdsrRecords(@RequestBody GetBenRequestHandler requestDTO,
                                                       @RequestHeader(value = "Authorization") String Authorization) {
        OutputResponse response = new OutputResponse();
        try {
            logger.info("fetching All MDSR Details for user: " + requestDTO.getAshaId());
            if (requestDTO != null) {
                List<MdsrDTO> result = deathReportsService.getMdsrRecords(requestDTO);
                String s = (new Gson()).toJson(result);
                if (s != null)
                    response.setResponse(s);
                else
                    response.setError(5000, "No record found");
            } else
                response.setError(5000, "Invalid/NULL request obj");

        } catch (Exception e) {
            logger.error("Error in fetching MDSR details, " + e);
            response.setError(5000, "Error in fetching MDSR details : " + e);
        }
        return response.toString();
    }

}
