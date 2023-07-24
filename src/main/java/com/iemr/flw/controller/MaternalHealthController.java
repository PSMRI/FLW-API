package com.iemr.flw.controller;

import com.iemr.flw.dto.iemr.ANCVisitDTO;
import com.iemr.flw.dto.iemr.DeliveryOutcomeDTO;
import com.iemr.flw.dto.iemr.InfantRegisterDTO;
import com.iemr.flw.dto.iemr.PregnantWomanDTO;
import com.iemr.flw.service.DeliveryOutcomeService;
import com.iemr.flw.service.InfantService;
import com.iemr.flw.service.PregnantWomanService;
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
@RequestMapping(value = "/maternalCare", headers = "Authorization")
public class MaternalHealthController {

    private final Logger logger = LoggerFactory.getLogger(CoupleController.class);

    @Autowired
    private PregnantWomanService pregnantWomanService;

    @Autowired
    private DeliveryOutcomeService deliveryOutcomeService;

    @Autowired
    private InfantService infantService;

    @CrossOrigin()
    @ApiOperation(value = "save pregnant woman registration details", consumes = "application/json", produces = "application/json")
    @RequestMapping(value = { "/pregnantWomanRegister" }, method = { RequestMethod.POST })
    public ResponseEntity<?> savePregnantWomanRegistrations(@RequestBody List<PregnantWomanDTO> pregnantWomanDTOs,
                                                @RequestHeader(value = "Authorization") String Authorization) {
        try {
            String result = pregnantWomanService.registerPregnantWoman(pregnantWomanDTOs);
            return new ResponseEntity<>(
                    new ApiResponse(true, null, result), HttpStatus.ACCEPTED);
        } catch (Exception e) {
            logger.error("Error in saving pregnant woman registration details, " + e);
            return new ResponseEntity<>(
                    new ApiResponse(false, e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin()
    @ApiOperation(value = "get List of pregnant woman registration details", consumes = "application/json", produces = "application/json")
    @RequestMapping(value = { "/pregnantWoman" }, method = { RequestMethod.GET })
    public ResponseEntity<?> getPregnantWoman(@RequestParam(value = "benId") Long benId,
                                               @RequestHeader(value = "Authorization") String Authorization) {
        try {
            PregnantWomanDTO result = pregnantWomanService.getPregnantWoman(benId);
            return new ResponseEntity<>(
                    new ApiResponse(true, null, result), HttpStatus.ACCEPTED);
        } catch (Exception e) {
            logger.error("Error in fetching pregnant woman registration details, " + e);
            return new ResponseEntity<>(
                    new ApiResponse(false, "Error in fetching pregnant woman registration details, " + e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin()
    @ApiOperation(value = "save anc visit details", consumes = "application/json", produces = "application/json")
    @RequestMapping(value = { "/saveANCVisit" }, method = { RequestMethod.POST })
    public ResponseEntity<?> saveANCVisit(@RequestBody List<ANCVisitDTO> ancVisitDTOs,
                                          @RequestHeader(value = "Authorization") String Authorization) {
        try {
            String result = pregnantWomanService.saveANCVisit(ancVisitDTOs);
            return new ResponseEntity<>(
                    new ApiResponse(true, null, result), HttpStatus.ACCEPTED);
        } catch (Exception e) {
            logger.error("Error in saving anc details, " + e);
            return new ResponseEntity<>(
                    new ApiResponse(false, e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin()
    @ApiOperation(value = "get anc visit details", consumes = "application/json", produces = "application/json")
    @RequestMapping(value = { "/getANCVisitsForPW" }, method = { RequestMethod.GET })
    public ResponseEntity<?> getANCVisitDetails(@RequestParam("pwrId") Long pwrId,
                                                @RequestHeader(value = "Authorization") String Authorization) {
        try {
            List<ANCVisitDTO> serviceResponse = pregnantWomanService.getANCVisits(pwrId);
            return new ResponseEntity<>(
                    new ApiResponse(true, null, serviceResponse), HttpStatus.ACCEPTED);
        } catch (Exception e) {
            logger.error("Error in fetching anc visit details, " + e);
            return new ResponseEntity<>(
                    new ApiResponse(false, "Error in fetching anc visit details, " + e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin()
    @ApiOperation(value = "save Delivery Outcome details", consumes = "application/json", produces = "application/json")
    @RequestMapping(value = { "/saveDeliveryOutcome" }, method = { RequestMethod.POST })
    public ResponseEntity<?> saveDeliveryOutcome(@RequestBody List<DeliveryOutcomeDTO> deliveryOutcomeDTOS,
                                          @RequestHeader(value = "Authorization") String Authorization) {
        try {
            String result = deliveryOutcomeService.registerDeliveryOutcome(deliveryOutcomeDTOS);
            return new ResponseEntity<>(
                    new ApiResponse(true, null, result), HttpStatus.ACCEPTED);
        } catch (Exception e) {
            logger.error("Error in saving Delivery Outcome details, " + e);
            return new ResponseEntity<>(
                    new ApiResponse(false, e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin()
    @ApiOperation(value = "get Delivery Outcome details", consumes = "application/json", produces = "application/json")
    @RequestMapping(value = { "/getDeliveryOutcome" }, method = { RequestMethod.GET })
    public ResponseEntity<?> getDeliveryOutcome(@RequestParam("benId") Long benId,
                                                @RequestHeader(value = "Authorization") String Authorization) {
        try {
            DeliveryOutcomeDTO serviceResponse = deliveryOutcomeService.getDeliveryOutcome(benId);
            return new ResponseEntity<>(
                    new ApiResponse(true, null, serviceResponse), HttpStatus.ACCEPTED);
        } catch (Exception e) {
            logger.error("Error in fetching Delivery Outcome details, " + e);
            return new ResponseEntity<>(
                    new ApiResponse(false, "Error in fetching Delivery Outcome details, " + e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin()
    @ApiOperation(value = "save Infant registration details", consumes = "application/json", produces = "application/json")
    @RequestMapping(value = { "/registerInfant" }, method = { RequestMethod.POST })
    public ResponseEntity<?> saveEligibleCouple(@RequestBody List<InfantRegisterDTO> infantRegisterDTOs,
                                                @RequestHeader(value = "Authorization") String Authorization) {
        try {
            String result = infantService.registerInfant(infantRegisterDTOs);
            return new ResponseEntity<>(
                    new ApiResponse(true, null, result), HttpStatus.ACCEPTED);
        } catch (Exception e) {
            logger.error("Error in saving infant registration details, " + e);
            return new ResponseEntity<>(
                    new ApiResponse(false, e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin()
    @ApiOperation(value = "get infant registration details", consumes = "application/json", produces = "application/json")
    @RequestMapping(value = { "/infantDetails" }, method = { RequestMethod.GET })
    public ResponseEntity<?> getEligibleCouple(@RequestParam(value = "benId") Long benId,
                                               @RequestHeader(value = "Authorization") String Authorization) {
        try {
            InfantRegisterDTO result = infantService.getInfantDetails(benId);
            return new ResponseEntity<>(
                    new ApiResponse(true, null, result), HttpStatus.ACCEPTED);
        } catch (Exception e) {
            logger.error("Error in fetching infant registration details, " + e);
            return new ResponseEntity<>(
                    new ApiResponse(false, "Error in fetching infant registration details, " + e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
