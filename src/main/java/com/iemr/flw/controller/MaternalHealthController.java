package com.iemr.flw.controller;

import com.iemr.flw.dto.identity.GetBenRequestHandler;
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
    @RequestMapping(value = { "/pregnantWoman/saveAll" }, method = { RequestMethod.POST })
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
    @RequestMapping(value = { "/pregnantWoman/getAll" }, method = { RequestMethod.POST })
    public ResponseEntity<?> getPregnantWomanList(@RequestBody GetBenRequestHandler requestDTO,
                                               @RequestHeader(value = "Authorization") String Authorization) {
        try {
            List<PregnantWomanDTO> result = pregnantWomanService.getPregnantWoman(requestDTO);
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
    @RequestMapping(value = { "/ancVisit/saveAll" }, method = { RequestMethod.POST })
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
    @RequestMapping(value = { "/ancVisit/getAll" }, method = { RequestMethod.POST })
    public ResponseEntity<?> getANCVisitDetails(@RequestBody GetBenRequestHandler requestDTO,
                                                @RequestHeader(value = "Authorization") String Authorization) {
        try {
            List<ANCVisitDTO> serviceResponse = pregnantWomanService.getANCVisits(requestDTO);
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
    @RequestMapping(value = { "/deliveryOutcome/saveAll" }, method = { RequestMethod.POST })
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
    @RequestMapping(value = { "/deliveryOutcome/getAll" }, method = { RequestMethod.POST })
    public ResponseEntity<?> getDeliveryOutcome(@RequestBody GetBenRequestHandler requestDTO,
                                                @RequestHeader(value = "Authorization") String Authorization) {
        try {
            List<DeliveryOutcomeDTO> serviceResponse = deliveryOutcomeService.getDeliveryOutcome(requestDTO);
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
    @RequestMapping(value = { "/infant/saveAll" }, method = { RequestMethod.POST })
    public ResponseEntity<?> saveInfantList(@RequestBody List<InfantRegisterDTO> infantRegisterDTOs,
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
    @RequestMapping(value = { "/infant/getAll" }, method = { RequestMethod.POST })
    public ResponseEntity<?> getInfantList(@RequestBody GetBenRequestHandler requestDTO,
                                               @RequestHeader(value = "Authorization") String Authorization) {
        try {
            List<InfantRegisterDTO> result = infantService.getInfantDetails(requestDTO);
            return new ResponseEntity<>(
                    new ApiResponse(true, null, result), HttpStatus.ACCEPTED);
        } catch (Exception e) {
            logger.error("Error in fetching infant registration details, " + e);
            return new ResponseEntity<>(
                    new ApiResponse(false, "Error in fetching infant registration details, " + e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
