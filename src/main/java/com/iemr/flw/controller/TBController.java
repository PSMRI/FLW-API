package com.iemr.flw.controller;

import com.iemr.flw.dto.iemr.TBScreeningDTO;
import com.iemr.flw.dto.iemr.TBScreeningRequestDTO;
import com.iemr.flw.service.TBScreeningService;
import com.iemr.flw.utils.ApiResponse;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/tb", headers = "Authorization")
public class TBController {

    private final Logger logger = LoggerFactory.getLogger(CoupleController.class);

    @Autowired
    private TBScreeningService tbScreeningService;

//    @Autowired
//    private TBSuspectedService tbSuspectedService;

    @CrossOrigin()
    @ApiOperation(value = "get tb screening data of all beneficiaries registered with given user id", consumes = "application/json", produces = "application/json")
    @RequestMapping(value = { "/screening/getAll" }, method = { RequestMethod.GET })
    public ResponseEntity<?> getAllScreeningByUserId(@RequestParam(value = "userId") Integer userId,
                                               @RequestHeader(value = "Authorization") String Authorization) {
        try {
            List<TBScreeningDTO> result = tbScreeningService.getByUserId(userId);
            return new ResponseEntity<>(
                    new ApiResponse(true, null, result), HttpStatus.ACCEPTED);
        } catch (Exception e) {
            logger.error("Error in fetching tb screening details by user, " + e);
            return new ResponseEntity<>(
                    new ApiResponse(false, "Error in fetching tb screening details by user, " + e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @CrossOrigin()
    @ApiOperation(value = "get tb screening data of all beneficiaries registered with given user id", consumes = "application/json", produces = "application/json")
    @RequestMapping(value = { "/screening/saveAll" }, method = { RequestMethod.GET })
    public ResponseEntity<?> saveAllScreeningByUserId(@RequestBody TBScreeningRequestDTO requestDTO,
                                                      @RequestHeader(value = "Authorization") String Authorization) {
        try {
            String result = tbScreeningService.save(requestDTO);
            return new ResponseEntity<>(
                    new ApiResponse(true, null, result), HttpStatus.ACCEPTED);
        } catch (Exception e) {
            logger.error("Error in fetching tb screening details by user, " + e);
            return new ResponseEntity<>(
                    new ApiResponse(false, "Error in fetching tb screening details by user, " + e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @CrossOrigin()
    @ApiOperation(value = "get tb suspected data of all beneficiaries registered with given user id", consumes = "application/json", produces = "application/json")
    @RequestMapping(value = { "/suspected/getAll" }, method = { RequestMethod.GET })
    public ResponseEntity<?> getAllSuspectedByUserId(@RequestParam(value = "userId") Integer userId,
                                            @RequestHeader(value = "Authorization") String Authorization) {
        try {
            List<TBScreeningDTO> result = tbScreeningService.getByUserId(userId);
            return new ResponseEntity<>(
                    new ApiResponse(true, null, result), HttpStatus.ACCEPTED);
        } catch (Exception e) {
            logger.error("Error in fetching tb screening details by user, " + e);
            return new ResponseEntity<>(
                    new ApiResponse(false, "Error in fetching tb screening details by user, " + e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
