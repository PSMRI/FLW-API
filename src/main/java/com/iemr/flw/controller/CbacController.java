package com.iemr.flw.controller;

import com.iemr.flw.dto.identity.CbacDTO;
import com.iemr.flw.service.CbacService;
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
@RequestMapping(value = "/cbac", headers = "Authorization")
public class CbacController {

    private final Logger logger = LoggerFactory.getLogger(CoupleController.class);

    @Autowired
    private CbacService cbacService;

    @CrossOrigin()
    @ApiOperation(value = "get cbac details of all beneficiaries registered with given user id", consumes = "application/json", produces = "application/json")
    @RequestMapping(value = { "/getAll" }, method = { RequestMethod.GET })
    public ResponseEntity<?> getAllScreeningByUserId(@RequestParam(value = "userId") Integer userId,
                                                     @RequestHeader(value = "Authorization") String Authorization) {
        try {
            logger.info("fetching All Cbac Details for user: " + userId);
            List<CbacDTO> result = cbacService.getByUserId(userId);
            return new ResponseEntity<>(
                    new ApiResponse(true, null, result), HttpStatus.ACCEPTED);
        } catch (Exception e) {
            logger.error("Error in fetching cbac details by user, " + e);
            return new ResponseEntity<>(
                    new ApiResponse(false, "Error in fetching cbac details by user, " + e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @CrossOrigin()
    @ApiOperation(value = "save cbac details of all beneficiaries registered with given user id", consumes = "application/json", produces = "application/json")
    @RequestMapping(value = { "/saveAll" }, method = { RequestMethod.POST })
    public ResponseEntity<?> saveAllCbacDetailsByUserId(@RequestBody List<CbacDTO> cbacList,
//                                                        @RequestParam(value = "userId") String userId,
                                                        @RequestHeader(value = "Authorization") String Authorization) {
        try {
            logger.info("Saving All Cbac Details for user: " + "parvathi");
            String result = cbacService.save(cbacList, "parvathi");
            return new ResponseEntity<>(
                    new ApiResponse(true, null, result), HttpStatus.ACCEPTED);
        } catch (Exception e) {
            logger.error("Error in saving cbac details by user, " + e);
            return new ResponseEntity<>(
                    new ApiResponse(false, "Error in saving cbac details by user, " + e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
