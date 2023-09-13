package com.iemr.flw.controller;

import com.google.gson.Gson;
import com.iemr.flw.domain.iemr.PMSMA;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.*;
import com.iemr.flw.service.ChildService;
import com.iemr.flw.service.DeliveryOutcomeService;
import com.iemr.flw.service.InfantService;
import com.iemr.flw.service.PregnantWomanService;
import com.iemr.flw.utils.response.OutputResponse;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
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

    @Autowired
    private ChildService childService;

    @CrossOrigin()
    @ApiOperation(value = "save pregnant woman registration details", consumes = "application/json", produces = "application/json")
    @RequestMapping(value = {"/pregnantWoman/saveAll"}, method = {RequestMethod.POST})
    public String savePregnantWomanRegistrations(@RequestBody List<PregnantWomanDTO> pregnantWomanDTOs,
                                                 @RequestHeader(value = "Authorization") String Authorization) {
        OutputResponse response = new OutputResponse();
        try {
            if (pregnantWomanDTOs.size() != 0) {
                logger.info("Saving pregnant woman details with timestamp : " + new Timestamp(System.currentTimeMillis()));
                String s = pregnantWomanService.registerPregnantWoman(pregnantWomanDTOs);
                if (s != null)
                    response.setResponse(s);
                else
                    response.setError(5000, "No record found");
            } else
                response.setError(5000, "Invalid/NULL request obj");
        } catch (Exception e) {
            logger.error("Error in save pregnant woman register details : " + e);
            response.setError(5000, "Error in save pregnant woman register details : " + e);
        }
        return response.toString();
    }

    @CrossOrigin()
    @ApiOperation(value = "get List of pregnant woman registration details", consumes = "application/json", produces = "application/json")
    @RequestMapping(value = {"/pregnantWoman/getAll"}, method = {RequestMethod.POST})
    public String getPregnantWomanList(@RequestBody GetBenRequestHandler requestDTO,
                                       @RequestHeader(value = "Authorization") String Authorization) {
        OutputResponse response = new OutputResponse();
        try {
            if (requestDTO != null) {
                logger.info("request object with timestamp : " + new Timestamp(System.currentTimeMillis()) + " "
                        + requestDTO);
                List<PregnantWomanDTO> result = pregnantWomanService.getPregnantWoman(requestDTO);
                String s = new Gson().toJson(result);
                if (s != null)
                    response.setResponse(s);
                else
                    response.setError(5000, "No record found");
            } else
                response.setError(5000, "Invalid/NULL request obj");
        } catch (Exception e) {
            logger.error("Error in pregnant woman get data : " + e);
            response.setError(5000, "Error in pregnant woman get data : " + e);
        }
        return response.toString();
    }

    @CrossOrigin()
    @ApiOperation(value = "save anc visit details", consumes = "application/json", produces = "application/json")
    @RequestMapping(value = {"/ancVisit/saveAll"}, method = {RequestMethod.POST})
    public String saveANCVisit(@RequestBody List<ANCVisitDTO> ancVisitDTOs,
                               @RequestHeader(value = "Authorization") String Authorization) {
        OutputResponse response = new OutputResponse();
        try {
            if (ancVisitDTOs.size() != 0) {
                logger.info("Saving ANC visits with timestamp : " + new Timestamp(System.currentTimeMillis()));
                String s = pregnantWomanService.saveANCVisit(ancVisitDTOs);
                if (s != null)
                    response.setResponse(s);
                else
                    response.setError(5000, "No record found");
            } else
                response.setError(5000, "Invalid/NULL request obj");
        } catch (Exception e) {
            logger.error("Error in save ANC visit details : " + e);
            response.setError(5000, "Error in save ANC visit details : " + e);
        }
        return response.toString();
    }

    @CrossOrigin()
    @ApiOperation(value = "get anc visit details", consumes = "application/json", produces = "application/json")
    @RequestMapping(value = {"/ancVisit/getAll"}, method = {RequestMethod.POST})
    public String getANCVisitDetails(@RequestBody GetBenRequestHandler requestDTO,
                                     @RequestHeader(value = "Authorization") String Authorization) {
        OutputResponse response = new OutputResponse();
        try {
            if (requestDTO != null) {
                logger.info("request object with timestamp : " + new Timestamp(System.currentTimeMillis()) + " "
                        + requestDTO);
                List<ANCVisitDTO> result = pregnantWomanService.getANCVisits(requestDTO);
                String s = new Gson().toJson(result);
                if (s != null)
                    response.setResponse(s);
                else
                    response.setError(5000, "No record found");
            } else
                response.setError(5000, "Invalid/NULL request obj");
        } catch (Exception e) {
            logger.error("Error in Anc visit get data : " + e);
            response.setError(5000, "Error in Anc visit get data : " + e);
        }
        return response.toString();
    }

    @CrossOrigin()
    @ApiOperation(value = "save Delivery Outcome details", consumes = "application/json", produces = "application/json")
    @RequestMapping(value = {"/deliveryOutcome/saveAll"}, method = {RequestMethod.POST})
    public String saveDeliveryOutcome(@RequestBody List<DeliveryOutcomeDTO> deliveryOutcomeDTOS,
                                      @RequestHeader(value = "Authorization") String Authorization) {
        OutputResponse response = new OutputResponse();
        try {
            if (deliveryOutcomeDTOS.size() != 0) {
                logger.info("Saving delivery outcomes with timestamp : " + new Timestamp(System.currentTimeMillis()));
                String s = deliveryOutcomeService.registerDeliveryOutcome(deliveryOutcomeDTOS);
                if (s != null)
                    response.setResponse(s);
                else
                    response.setError(5000, "No record found");
            } else
                response.setError(5000, "Invalid/NULL request obj");
        } catch (Exception e) {
            logger.error("Error in save delivery outcome details : " + e);
            response.setError(5000, "Error in save delivery outcome details : " + e);
        }
        return response.toString();
    }

    @CrossOrigin()
    @ApiOperation(value = "get Delivery Outcome details", consumes = "application/json", produces = "application/json")
    @RequestMapping(value = {"/deliveryOutcome/getAll"}, method = {RequestMethod.POST})
    public String getDeliveryOutcome(@RequestBody GetBenRequestHandler requestDTO,
                                     @RequestHeader(value = "Authorization") String Authorization) {
        OutputResponse response = new OutputResponse();
        try {
            if (requestDTO != null) {
                logger.info("request object with timestamp : " + new Timestamp(System.currentTimeMillis()) + " "
                        + requestDTO);
                List<DeliveryOutcomeDTO> result = deliveryOutcomeService.getDeliveryOutcome(requestDTO);
                String s = new Gson().toJson(result);
                if (s != null)
                    response.setResponse(s);
                else
                    response.setError(5000, "No record found");
            } else
                response.setError(5000, "Invalid/NULL request obj");
        } catch (Exception e) {
            logger.error("Error in delivery outcomes get data : " + e);
            response.setError(5000, "Error in delivery outcomes get data : " + e);
        }
        return response.toString();
    }

    @CrossOrigin()
    @ApiOperation(value = "save Infant registration details", consumes = "application/json", produces = "application/json")
    @RequestMapping(value = {"/infant/saveAll"}, method = {RequestMethod.POST})
    public String saveInfantList(@RequestBody List<InfantRegisterDTO> infantRegisterDTOs,
                                 @RequestHeader(value = "Authorization") String Authorization) {
        OutputResponse response = new OutputResponse();
        try {
            if (infantRegisterDTOs.size() != 0) {
                logger.info("Saving infant Register with timestamp : " + new Timestamp(System.currentTimeMillis()));
                String s = infantService.registerInfant(infantRegisterDTOs);
                if (s != null)
                    response.setResponse(s);
                else
                    response.setError(5000, "No record found");
            } else
                response.setError(5000, "Invalid/NULL request obj");
        } catch (Exception e) {
            logger.error("Error in save infant register details : " + e);
            response.setError(5000, "Error in save infant register details : " + e);
        }
        return response.toString();
    }

    @CrossOrigin()
    @ApiOperation(value = "get infant registration details", consumes = "application/json", produces = "application/json")
    @RequestMapping(value = {"/infant/getAll"}, method = {RequestMethod.POST})
    public String getInfantList(@RequestBody GetBenRequestHandler requestDTO,
                                @RequestHeader(value = "Authorization") String Authorization) {
        OutputResponse response = new OutputResponse();
        try {
            if (requestDTO != null) {
                logger.info("request object with timestamp : " + new Timestamp(System.currentTimeMillis()) + " "
                        + requestDTO);
                List<InfantRegisterDTO> result = infantService.getInfantDetails(requestDTO);
                String s = new Gson().toJson(result);
                if (s != null)
                    response.setResponse(s);
                else
                    response.setError(5000, "No record found");
            } else
                response.setError(5000, "Invalid/NULL request obj");
        } catch (Exception e) {
            logger.error("Error in infant register get data : " + e);
            response.setError(5000, "Error in infant register get data : " + e);
        }
        return response.toString();
    }

    @CrossOrigin()
    @ApiOperation(value = "get child register data of all beneficiaries registered with given user id", consumes = "application/json", produces = "application/json")
    @RequestMapping(value = {"/child/getAll"}, method = {RequestMethod.POST})
    public String getAllChildRegisterDetails(@RequestBody GetBenRequestHandler requestDTO,
                                             @RequestHeader(value = "Authorization") String Authorization) {
        OutputResponse response = new OutputResponse();
        try {
            if (requestDTO != null) {
                logger.info("request object with timestamp : " + new Timestamp(System.currentTimeMillis()) + " "
                        + requestDTO);
                String s = childService.getByUserId(requestDTO);
                if (s != null)
                    response.setResponse(s);
                else
                    response.setError(5000, "No record found");
            } else
                response.setError(5000, "Invalid/NULL request obj");
        } catch (Exception e) {
            logger.error("Error in child register get data : " + e);
            response.setError(5000, "Error in child register get data : " + e);
        }
        return response.toString();
    }


    @CrossOrigin()
    @ApiOperation(value = "save child register data of all beneficiaries registered with given user id", consumes = "application/json", produces = "application/json")
    @RequestMapping(value = {"/child/saveAll"}, method = {RequestMethod.POST})
    public String saveAllChildDetails(@RequestBody List<ChildRegisterDTO> childRegisterDTOs,
                                      @RequestHeader(value = "Authorization") String Authorization) {
        OutputResponse response = new OutputResponse();
        try {

            if (childRegisterDTOs.size() != 0) {
                logger.info("Saving Child Register with timestamp : " + new Timestamp(System.currentTimeMillis()));
                String s = childService.save(childRegisterDTOs);
                if (s != null)
                    response.setResponse(s);
                else
                    response.setError(5000, "No record found");
            } else
                response.setError(5000, "Invalid/NULL request obj");
        } catch (Exception e) {
            logger.error("Error in save child register details : " + e);
            response.setError(5000, "Error in save child register details : " + e);
        }
        return response.toString();
    }

    @CrossOrigin()
    @ApiOperation(value = "get PMSMA data of all beneficiaries registered with given user id", consumes = "application/json", produces = "application/json")
    @RequestMapping(value = {"/pmsma/getAll"}, method = {RequestMethod.POST})
    public String getAllPmsmaDetails(@RequestBody GetBenRequestHandler requestDTO,
                                             @RequestHeader(value = "Authorization") String Authorization) {
        OutputResponse response = new OutputResponse();
        try {
            if (requestDTO != null) {
                logger.info("fetch pmsma request object with timestamp : " + new Timestamp(System.currentTimeMillis()) + " "
                        + requestDTO);
                List<PmsmaDTO> result = pregnantWomanService.getPmsmaRecords(requestDTO);
                String s = (new Gson()).toJson(result);
                if (s != null)
                    response.setResponse(s);
                else
                    response.setError(5000, "No record found");
            } else
                response.setError(5000, "Invalid/NULL request obj");
        } catch (Exception e) {
            logger.error("Error in pmsma get data : " + e);
            response.setError(5000, "Error in pmsma get data : " + e);
        }
        return response.toString();
    }


    @CrossOrigin()
    @ApiOperation(value = "save PMSMA data of all beneficiaries registered with given user id", consumes = "application/json", produces = "application/json")
    @RequestMapping(value = {"/pmsma/saveAll"}, method = {RequestMethod.POST})
    public String saveAllPmsmaRecords(@RequestBody List<PmsmaDTO> pmsmaDTOs,
                                      @RequestHeader(value = "Authorization") String Authorization) {
        OutputResponse response = new OutputResponse();
        try {

            if (pmsmaDTOs.size() != 0) {
                logger.info("Saving PMSMA Records with timestamp : " + new Timestamp(System.currentTimeMillis()));
                String s = pregnantWomanService.savePmsmaRecords(pmsmaDTOs);
                if (s != null)
                    response.setResponse(s);
                else
                    response.setError(5000, "Error in save pmsma details");
            } else
                response.setError(5000, "Invalid/NULL request obj");
        } catch (Exception e) {
            logger.error("Error in save pmsma details : " + e);
            response.setError(5000, "Error in save pmsma details : " + e);
        }
        return response.toString();
    }
}
