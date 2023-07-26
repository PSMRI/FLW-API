package com.iemr.flw.controller;

import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.TBScreeningRequestDTO;
import com.iemr.flw.dto.iemr.TBSuspectedRequestDTO;
import com.iemr.flw.service.TBScreeningService;
import com.iemr.flw.service.TBSuspectedService;
import com.iemr.flw.utils.ApiResponse;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/tb", headers = "Authorization")
public class TBController {

    private final Logger logger = LoggerFactory.getLogger(CoupleController.class);

    @Autowired
    private TBScreeningService tbScreeningService;

    @Autowired
    private TBSuspectedService tbSuspectedService;

    @CrossOrigin()
    @ApiOperation(value = "get tb screening data of all beneficiaries registered with given user id", consumes = "application/json", produces = "application/json")
    @RequestMapping(value = { "/screening/getAll" }, method = { RequestMethod.POST })
    public ResponseEntity<?> getAllScreeningByUserId(@RequestBody GetBenRequestHandler request,
                                               @RequestHeader(value = "Authorization") String Authorization) {
        try {
            TBScreeningRequestDTO result = tbScreeningService.getByUserId(request);
            return new ResponseEntity<>(
                    new ApiResponse(true, null, result), HttpStatus.ACCEPTED);
        } catch (Exception e) {
            logger.error("Error in fetching tb screening details by user, " + e);
            return new ResponseEntity<>(
                    new ApiResponse(false, "Error in fetching tb screening details by user, " + e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @CrossOrigin()
    @ApiOperation(value = "save tb screening data of all beneficiaries registered with given user id", consumes = "application/json", produces = "application/json")
    @RequestMapping(value = { "/screening/saveAll" }, method = { RequestMethod.POST })
    public ResponseEntity<?> saveAllScreeningByUserId(@RequestBody TBScreeningRequestDTO requestDTO,
                                                      @RequestHeader(value = "Authorization") String Authorization) {
        try {
            String result = tbScreeningService.save(requestDTO);
            return new ResponseEntity<>(
                    new ApiResponse(true, null, result), HttpStatus.ACCEPTED);
        } catch (Exception e) {
            logger.error("Error in saving tb screening details by user, " + e);
            return new ResponseEntity<>(
                    new ApiResponse(false, "Error in saving tb screening details by user, " + e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @CrossOrigin()
    @ApiOperation(value = "get tb suspected data of all beneficiaries registered with given user id", consumes = "application/json", produces = "application/json")
    @RequestMapping(value = { "/suspected/getAll" }, method = { RequestMethod.POST })
    public ResponseEntity<?> getAllSuspectedByUserId(@RequestBody GetBenRequestHandler request,
                                            @RequestHeader(value = "Authorization") String Authorization) {
        try {
            TBSuspectedRequestDTO result = tbSuspectedService.getByUserId(request);
            return new ResponseEntity<>(
                    new ApiResponse(true, null, result), HttpStatus.ACCEPTED);
        } catch (Exception e) {
            logger.error("Error in fetching tb suspected details by user, " + e);
            return new ResponseEntity<>(
                    new ApiResponse(false, "Error in fetching tb suspected details by user, " + e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin()
    @ApiOperation(value = "save tb suspected data of all beneficiaries registered with given user id", consumes = "application/json", produces = "application/json")
    @RequestMapping(value = { "/suspected/saveAll" }, method = { RequestMethod.POST })
    public ResponseEntity<?> saveAllSuspectedByUserId(@RequestBody TBSuspectedRequestDTO requestDTO,
                                                      @RequestHeader(value = "Authorization") String Authorization) {
        try {
            String result = tbSuspectedService.save(requestDTO);
            return new ResponseEntity<>(
                    new ApiResponse(true, null, result), HttpStatus.ACCEPTED);
        } catch (Exception e) {
            logger.error("Error in saving tb suspected details by user, " + e);
            return new ResponseEntity<>(
                    new ApiResponse(false, "Error in saving tb suspected details by user, " + e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
