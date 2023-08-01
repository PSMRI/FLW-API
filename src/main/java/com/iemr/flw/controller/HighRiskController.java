package com.iemr.flw.controller;

import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.TBScreeningRequestDTO;
import com.iemr.flw.dto.iemr.TBSuspectedRequestDTO;
import com.iemr.flw.dto.iemr.UserDataDTO;
import com.iemr.flw.service.HighRiskNonPregnantService;
import com.iemr.flw.service.HighRiskPregnantService;
import com.iemr.flw.service.TBScreeningService;
import com.iemr.flw.service.TBSuspectedService;
import com.iemr.flw.utils.response.OutputResponse;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;

@RestController
@RequestMapping(value = "/highRisk", headers = "Authorization")
public class HighRiskController {

    private final Logger logger = LoggerFactory.getLogger(CoupleController.class);

//    @Autowired
//    private HighRiskNonPregnantService highRiskNonPregnantService;
//
//    @Autowired
//    private HighRiskPregnantService highRiskPregnantService;
//
//    @CrossOrigin()
//    @ApiOperation(value = "get high risk pregnant assessment data of all beneficiaries registered with given user id", consumes = "application/json", produces = "application/json")
//    @RequestMapping(value = { "/pregnant/assess/getAll" }, method = { RequestMethod.POST })
//    public String getAllPregnantAssessByUserId(@RequestBody GetBenRequestHandler requestDTO,
//                                          @RequestHeader(value = "Authorization") String Authorization) {
//        OutputResponse response = new OutputResponse();
//        try {
//
//            if (requestDTO != null) {
//                logger.info("request object with timestamp : " + new Timestamp(System.currentTimeMillis()) + " "
//                        + requestDTO);
//                String s = highRiskPregnantService.getAllAssessments(requestDTO);
//                if (s != null)
//                    response.setResponse(s);
//                else
//                    response.setError(5000, "No record found");
//            } else
//                response.setError(5000, "Invalid/NULL request obj");
//        } catch (Exception e) {
//            logger.error("Error in high risk pregnant assessment data : " + e);
//            response.setError(5000, "Error in high risk pregnant assessment data : " + e);
//        }
//        return response.toString();
//    }
//
//
//    @CrossOrigin()
//    @ApiOperation(value = "save high risk pregnant assessment data of all beneficiaries registered with given user id", consumes = "application/json", produces = "application/json")
//    @RequestMapping(value = { "/pregnant/assess/saveAll" }, method = { RequestMethod.POST })
//    public String saveAllPregnantAssessByUserId(@RequestBody UserDataDTO requestDTO,
//                                           @RequestHeader(value = "Authorization") String Authorization) {
//        OutputResponse response = new OutputResponse();
//        try {
//
//            if (requestDTO != null) {
//                logger.info("request object with timestamp : " + new Timestamp(System.currentTimeMillis()) + " "
//                        + requestDTO);
//                String s = highRiskPregnantService.saveAllAssessment(requestDTO);
//                if (s != null)
//                    response.setResponse(s);
//                else
//                    response.setError(5000, "No record found");
//            } else
//                response.setError(5000, "Invalid/NULL request obj");
//        } catch (Exception e) {
//            logger.error("Error in  saving high risk pregnant tracking data : " + e);
//            response.setError(5000, "Error in saving high risk pregnant tracking data : " + e);
//        }
//        return response.toString();
//    }
//
//    @CrossOrigin()
//    @ApiOperation(value = "get high risk pregnant tracking data of all beneficiaries registered with given user id", consumes = "application/json", produces = "application/json")
//    @RequestMapping(value = { "/pregnant/track/getAll" }, method = { RequestMethod.POST })
//    public String getAllPregnantTrackByUserId(@RequestBody GetBenRequestHandler requestDTO,
//                                               @RequestHeader(value = "Authorization") String Authorization) {
//        OutputResponse response = new OutputResponse();
//        try {
//
//            if (requestDTO != null) {
//                logger.info("request object with timestamp : " + new Timestamp(System.currentTimeMillis()) + " "
//                        + requestDTO);
//                String s = highRiskPregnantService.getAllTracking(requestDTO);
//                if (s != null)
//                    response.setResponse(s);
//                else
//                    response.setError(5000, "No record found");
//            } else
//                response.setError(5000, "Invalid/NULL request obj");
//        } catch (Exception e) {
//            logger.error("Error in high risk pregnant tracking data : " + e);
//            response.setError(5000, "Error in high risk pregnant tracking data : " + e);
//        }
//        return response.toString();
//    }
//
//
//    @CrossOrigin()
//    @ApiOperation(value = "save high risk pregnant tracking data of all beneficiaries registered with given user id", consumes = "application/json", produces = "application/json")
//    @RequestMapping(value = { "/pregnant/track/saveAll" }, method = { RequestMethod.POST })
//    public String saveAllPregnantTrackByUserId(@RequestBody UserDataDTO requestDTO,
//                                                @RequestHeader(value = "Authorization") String Authorization) {
//        OutputResponse response = new OutputResponse();
//        try {
//
//            if (requestDTO != null) {
//                logger.info("request object with timestamp : " + new Timestamp(System.currentTimeMillis()) + " "
//                        + requestDTO);
//                String s = highRiskPregnantService.saveAllTracking(requestDTO);
//                if (s != null)
//                    response.setResponse(s);
//                else
//                    response.setError(5000, "No record found");
//            } else
//                response.setError(5000, "Invalid/NULL request obj");
//        } catch (Exception e) {
//            logger.error("Error in  saving high risk pregnant assessment data : " + e);
//            response.setError(5000, "Error in saving high risk pregnant assessment data : " + e);
//        }
//        return response.toString();
//    }
//    @CrossOrigin()
//    @ApiOperation(value = "get high risk non pregnant assessment data of all beneficiaries registered with given user id", consumes = "application/json", produces = "application/json")
//    @RequestMapping(value = { "/nonPregnant/assess/getAll" }, method = { RequestMethod.POST })
//    public String getAllNonPregnantAssessByUserId(@RequestBody GetBenRequestHandler requestDTO,
//                                          @RequestHeader(value = "Authorization") String Authorization) {
//
//        OutputResponse response = new OutputResponse();
//        try {
//
//            if (requestDTO != null) {
//                logger.info("request object with timestamp : " + new Timestamp(System.currentTimeMillis()) + " "
//                        + requestDTO);
//                String s = highRiskNonPregnantService.getAllAssessment(requestDTO);
//                if (s != null)
//                    response.setResponse(s);
//                else
//                    response.setError(5000, "No record found");
//            } else
//                response.setError(5000, "Invalid/NULL request obj");
//        } catch (Exception e) {
//            logger.error("Error in get data : " + e);
//            response.setError(5000, "Error in get data : " + e);
//        }
//        return response.toString();
//    }
//
//    @CrossOrigin()
//    @ApiOperation(value = "save high risk non pregnant assessment data of all beneficiaries registered with given user id", consumes = "application/json", produces = "application/json")
//    @RequestMapping(value = { "/nonPregnant/assess/saveAll" }, method = { RequestMethod.POST })
//    public String saveAllNonPregnantAssessByUserId(@RequestBody TBSuspectedRequestDTO requestDTO,
//                                           @RequestHeader(value = "Authorization") String Authorization) {
//        OutputResponse response = new OutputResponse();
//        try {
//
//            if (requestDTO != null) {
//                logger.info("request object with timestamp : " + new Timestamp(System.currentTimeMillis()) + " "
//                        + requestDTO);
//                String s = highRiskNonPregnantService.saveAllAssessment(requestDTO);
//                if (s != null)
//                    response.setResponse(s);
//                else
//                    response.setError(5000, "No record found");
//            } else
//                response.setError(5000, "Invalid/NULL request obj");
//        } catch (Exception e) {
//            logger.error("Error in save tb suspected details : " + e);
//            response.setError(5000, "Error in save tb suspected details : " + e);
//        }
//        return response.toString();
//    }
//
//    @CrossOrigin()
//    @ApiOperation(value = "get high risk non pregnant track data of all beneficiaries registered with given user id", consumes = "application/json", produces = "application/json")
//    @RequestMapping(value = { "/nonPregnant/track/getAll" }, method = { RequestMethod.POST })
//    public String getAllNonPregnantTrackByUserId(@RequestBody GetBenRequestHandler requestDTO,
//                                                  @RequestHeader(value = "Authorization") String Authorization) {
//
//        OutputResponse response = new OutputResponse();
//        try {
//
//            if (requestDTO != null) {
//                logger.info("request object with timestamp : " + new Timestamp(System.currentTimeMillis()) + " "
//                        + requestDTO);
//                String s = highRiskNonPregnantService.getAllTracking(requestDTO);
//                if (s != null)
//                    response.setResponse(s);
//                else
//                    response.setError(5000, "No record found");
//            } else
//                response.setError(5000, "Invalid/NULL request obj");
//        } catch (Exception e) {
//            logger.error("Error in get data : " + e);
//            response.setError(5000, "Error in get data : " + e);
//        }
//        return response.toString();
//    }
//
//    @CrossOrigin()
//    @ApiOperation(value = "save high risk non pregnant track data of all beneficiaries registered with given user id", consumes = "application/json", produces = "application/json")
//    @RequestMapping(value = { "/nonPregnant/track/saveAll" }, method = { RequestMethod.POST })
//    public String saveAllNonPregnantTrackByUserId(@RequestBody TBSuspectedRequestDTO requestDTO,
//                                                   @RequestHeader(value = "Authorization") String Authorization) {
//        OutputResponse response = new OutputResponse();
//        try {
//            if (requestDTO != null) {
//                logger.info("request object with timestamp : " + new Timestamp(System.currentTimeMillis()) + " "
//                        + requestDTO);
//                String s = highRiskNonPregnantService.saveAllTracking(requestDTO);
//                if (s != null)
//                    response.setResponse(s);
//                else
//                    response.setError(5000, "No record found");
//            } else
//                response.setError(5000, "Invalid/NULL request obj");
//        } catch (Exception e) {
//            logger.error("Error in save tb suspected details : " + e);
//            response.setError(5000, "Error in save tb suspected details : " + e);
//        }
//        return response.toString();
//    }
}
