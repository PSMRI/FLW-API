package com.iemr.flw.controller;

import com.iemr.flw.dto.iemr.EligibleCoupleDTO;
import com.iemr.flw.dto.iemr.EligibleCoupleTrackingDTO;
import com.iemr.flw.service.CoupleService;
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
@RequestMapping(value = "/couple", headers = "Authorization")
public class CoupleController {


    private final Logger logger = LoggerFactory.getLogger(CoupleController.class);

    @Autowired
    private CoupleService coupleService;

    @CrossOrigin()
    @ApiOperation(value = "save eligible couple registration details", consumes = "application/json", produces = "application/json")
    @RequestMapping(value = { "/eligibleCoupleRegister" }, method = { RequestMethod.POST })
    public ResponseEntity<?> saveEligibleCouple(@RequestBody List<EligibleCoupleDTO> eligibleCoupleDTOs,
                                  @RequestHeader(value = "Authorization") String Authorization) {
        try {
            String result = coupleService.registerEligibleCouple(eligibleCoupleDTOs);
            return new ResponseEntity<>(
                    new ApiResponse(true, null, result), HttpStatus.ACCEPTED);
        } catch (Exception e) {
            logger.error("Error in saving eligible couple registration details, " + e);
            return new ResponseEntity<>(
                    new ApiResponse(false, e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin()
    @ApiOperation(value = "get List of eligible couple registration details", consumes = "application/json", produces = "application/json")
    @RequestMapping(value = { "/eligibleCoupleRegister" }, method = { RequestMethod.GET })
    public ResponseEntity<?> getEligibleCouple(@RequestParam(value = "benId") Long benId,
                                     @RequestHeader(value = "Authorization") String Authorization) {
        try {
            EligibleCoupleDTO result = coupleService.getEligibleCouple(benId);
            return new ResponseEntity<>(
                    new ApiResponse(true, null, result), HttpStatus.ACCEPTED);
        } catch (Exception e) {
            logger.error("Error in fetching eligible couple registration details, " + e);
            return new ResponseEntity<>(
                    new ApiResponse(false, "Error in fetching eligible couple registration details, " + e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin()
    @ApiOperation(value = "save eligible couple tracking details", consumes = "application/json", produces = "application/json")
    @RequestMapping(value = { "/eligibleCoupleTrackingRegister" }, method = { RequestMethod.POST })
    public ResponseEntity<?> saveEligibleCoupleTracking(@RequestBody List<EligibleCoupleTrackingDTO> eligibleCoupleTrackingDTOSDTOs,
                                     @RequestHeader(value = "Authorization") String Authorization) {
        try {
            String serviceResponse = coupleService.registerEligibleCoupleTracking(eligibleCoupleTrackingDTOSDTOs);
            return new ResponseEntity<>(
                    new ApiResponse(true, null, serviceResponse), HttpStatus.ACCEPTED);
        } catch (Exception e) {
            logger.error("Error in saving eligible couple registration details, " + e);
            return new ResponseEntity<>(
                    new ApiResponse(false, "Error in saving eligible couple registration details, " + e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @CrossOrigin()
    @ApiOperation(value = "get List of eligible couple tracking details", consumes = "application/json", produces = "application/json")
    @RequestMapping(value = { "/eligibleCoupleTrackingRegister" }, method = { RequestMethod.GET })
    public ResponseEntity<?> getEligibleCoupleTracking(@RequestParam(value = "ecrId") Long ecrId,
                                               @RequestHeader(value = "Authorization") String Authorization) {
        try {
            List<EligibleCoupleTrackingDTO> result = coupleService.getEligibleCoupleTracking(ecrId);
            return new ResponseEntity<>(
                    new ApiResponse(true, null, result), HttpStatus.ACCEPTED);
        } catch (Exception e) {
            logger.error("Error in fetching eligible couple tracking details, " + e);
            return new ResponseEntity<>(
                    new ApiResponse(false, "Error in fetching eligible couple tracking details, " + e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
