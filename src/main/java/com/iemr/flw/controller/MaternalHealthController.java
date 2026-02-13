package com.iemr.flw.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.*;
import com.iemr.flw.service.ChildService;
import com.iemr.flw.service.DeliveryOutcomeService;
import com.iemr.flw.service.InfantService;
import com.iemr.flw.service.MaternalHealthService;
import com.iemr.flw.utils.JwtUtil;
import com.iemr.flw.utils.exception.IEMRException;
import com.iemr.flw.utils.response.OutputResponse;

import io.swagger.v3.oas.annotations.Operation;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;

@RestController
@RequestMapping(value = "/maternalCare", headers = "Authorization", consumes = "application/json", produces = "application/json")
public class MaternalHealthController {

    private final Logger logger = LoggerFactory.getLogger(CoupleController.class);

    @Autowired
    private MaternalHealthService maternalHealthService;

    @Autowired
    private DeliveryOutcomeService deliveryOutcomeService;

    @Autowired
    private InfantService infantService;

    @Autowired
    private ChildService childService;

    @Autowired
    private JwtUtil jwtUtil;

    @Operation(summary = "save pregnant woman registration details")
    @RequestMapping(value = { "/pregnantWoman/saveAll" }, method = { RequestMethod.POST })
    public String savePregnantWomanRegistrations(@RequestBody List<PregnantWomanDTO> pregnantWomanDTOs,
            @RequestHeader(value = "Authorization") String Authorization) {
        OutputResponse response = new OutputResponse();
        try {
            if (pregnantWomanDTOs.size() != 0) {
                logger.info(
                        "Saving pregnant woman details with timestamp : " + new Timestamp(System.currentTimeMillis()));
                String s = maternalHealthService.registerPregnantWoman(pregnantWomanDTOs);
                if (s != null)
                    response.setResponse(s);
                else
                    response.setError(5000, "Saving pwr data to db failed");
            } else
                response.setError(5000, "Invalid/NULL request obj");
        } catch (Exception e) {
            logger.error("Error in save pregnant woman register details : " + e);
            response.setError(5000, "Error in save pregnant woman register details : " + e);
        }
        return response.toString();
    }

    @Operation(summary = "get List of pregnant woman registration details")
    @RequestMapping(value = { "/pregnantWoman/getAll" }, method = { RequestMethod.POST })
    public String getPregnantWomanList(@RequestBody GetBenRequestHandler requestDTO,
            @RequestHeader(value = "Authorization") String Authorization) {
        OutputResponse response = new OutputResponse();
        try {
            if (requestDTO != null) {
                logger.info("request object with timestamp : " + new Timestamp(System.currentTimeMillis()) + " "
                        + requestDTO);
                List<PregnantWomanDTO> result = maternalHealthService.getPregnantWoman(requestDTO);
                Gson gson = new GsonBuilder().setDateFormat("MMM dd, yyyy h:mm:ss a").create();
                String s = gson.toJson(result);
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

    @Operation(summary = "save anc visit details")
    @RequestMapping(value = { "/ancVisit/saveAll" }, method = { RequestMethod.POST })
    public String saveANCVisit(@RequestBody List<ANCVisitDTO> ancVisitDTOs,
            @RequestHeader(value = "Authorization") String Authorization) {
        OutputResponse response = new OutputResponse();
        try {
            if (ancVisitDTOs.size() != 0) {
                logger.info("Saving ANC visits with timestamp : " + new Timestamp(System.currentTimeMillis()));
                String s = maternalHealthService.saveANCVisit(ancVisitDTOs);
                if (s != null)
                    response.setResponse(s);
                else
                    response.setError(5000, "Saving anc data to db failed");
            } else
                response.setError(5000, "Invalid/NULL request obj");
        } catch (Exception e) {
            logger.error("Error in save ANC visit details : ",e);

            response.setError(5000, "Error in save ANC visit details : " + e);
        }
        return response.toString();
    }

    @Operation(summary = "get anc visit details")
    @RequestMapping(value = { "/ancVisit/getAll" }, method = { RequestMethod.POST })
    public String getANCVisitDetails(@RequestBody GetBenRequestHandler requestDTO,
            @RequestHeader(value = "Authorization") String Authorization) {
        OutputResponse response = new OutputResponse();
        try {
            if (requestDTO != null) {
                logger.info("request object with timestamp : " + new Timestamp(System.currentTimeMillis()) + " "
                        + requestDTO);
                List<ANCVisitDTO> result = maternalHealthService.getANCVisits(requestDTO);
                Gson gson = new GsonBuilder().setDateFormat("MMM dd, yyyy h:mm:ss a").create();
                String s = gson.toJson(result);
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

    @Operation(summary = "save anc visit question")
    @RequestMapping(value = { "/ancVisit/counselling/saveAll" }, method = { RequestMethod.POST })
    public String saveANCVisitQuestion(@RequestBody List<AncCounsellingCareDTO> ancVisitQuestionsDTOS,
                               @RequestHeader(value = "JwtToken") String Authorization) {
        OutputResponse response = new OutputResponse();
        try {
            if (ancVisitQuestionsDTOS.size() != 0) {

                logger.info("Saving ANC visits with timestamp : " + new Timestamp(System.currentTimeMillis()));
                String s = maternalHealthService.saveANCVisitQuestions(ancVisitQuestionsDTOS,Authorization);
                if (s != null)
                    response.setResponse(s);
                else
                    response.setError(500, "Saving anc data to db failed");
            } else
                response.setError(500, "Invalid/NULL request obj");
        } catch (Exception e) {
            logger.error("Error in save ANC visit details : ",e);

            response.setError(500, "Error in save ANC visit details : " + e);
        }
        return response.toString();
    }

    @Operation(summary = "get anc visit questions")
    @RequestMapping(value = { "/ancVisit/counselling/getAll" }, method = { RequestMethod.POST })
    public ResponseEntity<StandardResponse<List<AncCounsellingCareResponseDTO>>> getANCVisitQuestion(@RequestBody GetBenRequestHandler requestDTO,
                                     @RequestHeader(value = "JwtToken") String Authorization) {
        StandardResponse<List<AncCounsellingCareResponseDTO>> response = new StandardResponse<>();

        try {
            if (requestDTO != null) {
                logger.info("Request: " + requestDTO);

                List<AncCounsellingCareResponseDTO> result = maternalHealthService.getANCCounselling(requestDTO);

                response.setStatusCode(200);
                response.setStatus("Success");
                response.setErrorMessage("Success");
                response.setData(result);

                return ResponseEntity.ok(response);

            } else {
                response.setStatusCode(400);
                response.setStatus("Failed");
                response.setErrorMessage("Invalid request object");
                response.setData(null);
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            logger.error("Exception in fetching HBNC visits", e);

            response.setStatusCode(500);
            response.setStatus("Failed");
            response.setErrorMessage("Internal Server Error: " + e.getMessage());
            response.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "save Delivery Outcome details")
    @RequestMapping(value = { "/deliveryOutcome/saveAll" }, method = { RequestMethod.POST })
    public String saveDeliveryOutcome(@RequestBody List<DeliveryOutcomeDTO> deliveryOutcomeDTOS,
                                      @RequestHeader(value = "Authorization") String Authorization, HttpServletRequest request) throws IEMRException {


        OutputResponse response = new OutputResponse();
        try {
            if (deliveryOutcomeDTOS.size() != 0) {
                logger.info("Saving delivery outcomes with timestamp : " + new Timestamp(System.currentTimeMillis()));
                String s = deliveryOutcomeService.registerDeliveryOutcome(deliveryOutcomeDTOS);
                if (s != null)
                    response.setResponse(s);
                else
                    response.setError(5000, "Saving delivery outcome to db failed");
            } else
                response.setError(5000, "Invalid/NULL request obj");
        } catch (Exception e) {
            logger.error("Error in save delivery outcome details : " + e);
            response.setError(5000, "Error in save delivery outcome details : " + e);
        }
        return response.toString();

    }

    @Operation(summary = "get Delivery Outcome details")
    @RequestMapping(value = { "/deliveryOutcome/getAll" }, method = { RequestMethod.POST })
    public String getDeliveryOutcome(@RequestBody GetBenRequestHandler requestDTO,
            @RequestHeader(value = "Authorization") String Authorization) {
        OutputResponse response = new OutputResponse();
        try {
            if (requestDTO != null) {
                logger.info("request object with timestamp : " + new Timestamp(System.currentTimeMillis()) + " "
                        + requestDTO);
                List<DeliveryOutcomeDTO> result = deliveryOutcomeService.getDeliveryOutcome(requestDTO);
                Gson gson = new GsonBuilder().setDateFormat("MMM dd, yyyy h:mm:ss a").create();
                String s = gson.toJson(result);
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

    @Operation(summary = "save Infant registration details")
    @RequestMapping(value = { "/infant/saveAll" }, method = { RequestMethod.POST })
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
                    response.setError(5000, "Saving infant register data to db failed");
            } else
                response.setError(5000, "Invalid/NULL request obj");
        } catch (Exception e) {
            logger.error("Error in save infant register details : " + e);
            response.setError(5000, "Error in save infant register details : " + e);
        }
        return response.toString();
    }

    @Operation(summary = "get infant registration details")
    @RequestMapping(value = { "/infant/getAll" }, method = { RequestMethod.POST })
    public String getInfantList(@RequestBody GetBenRequestHandler requestDTO,
            @RequestHeader(value = "Authorization") String Authorization) {
        OutputResponse response = new OutputResponse();
        try {
            if (requestDTO != null) {
                logger.info("request object with timestamp : " + new Timestamp(System.currentTimeMillis()) + " "
                        + requestDTO);
                List<InfantRegisterDTO> result = infantService.getInfantDetails(requestDTO);
                Gson gson = new GsonBuilder().setDateFormat("MMM dd, yyyy h:mm:ss a").create();
                String s = gson.toJson(result);
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

    @Operation(summary = "get child register data of all beneficiaries registered with given user id")
    @RequestMapping(value = { "/child/getAll" }, method = { RequestMethod.POST })
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

    @Operation(summary = "save child register data of all beneficiaries registered with given user id")
    @RequestMapping(value = { "/child/saveAll" }, method = { RequestMethod.POST })
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
                    response.setError(5000, "Saving child register data to db failed");
            } else
                response.setError(5000, "Invalid/NULL request obj");
        } catch (Exception e) {
            logger.error("Error in save child register details : " + e);
            response.setError(5000, "Error in save child register details : " + e);
        }
        return response.toString();
    }

    @Operation(summary = "get PMSMA data of all beneficiaries registered with given user id")
    @RequestMapping(value = { "/pmsma/getAll" }, method = { RequestMethod.POST })
    public String getAllPmsmaDetails(@RequestBody GetBenRequestHandler requestDTO,
            @RequestHeader(value = "Authorization") String Authorization) {
        OutputResponse response = new OutputResponse();
        try {
            if (requestDTO != null) {
                logger.info(
                        "fetch pmsma request object with timestamp : " + new Timestamp(System.currentTimeMillis()) + " "
                                + requestDTO);
                List<PmsmaDTO> result = maternalHealthService.getPmsmaRecords(requestDTO);
                Gson gson = new GsonBuilder().setDateFormat("MMM dd, yyyy h:mm:ss a").create();
                String s = gson.toJson(result);
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

    @Operation(summary = "save PMSMA data of all beneficiaries registered with given user id")
    @RequestMapping(value = { "/pmsma/saveAll" }, method = { RequestMethod.POST })
    public String saveAllPmsmaRecords(@RequestBody List<PmsmaDTO> pmsmaDTOs,
            @RequestHeader(value = "Authorization") String Authorization) {
        OutputResponse response = new OutputResponse();
        try {

            if (pmsmaDTOs.size() != 0) {
                logger.info("Saving PMSMA Records with timestamp : " + new Timestamp(System.currentTimeMillis()));
                String s = maternalHealthService.savePmsmaRecords(pmsmaDTOs);
                if (s != null)
                    response.setResponse(s);
                else
                    response.setError(5000, "Saving pmsma to db failed");
            } else
                response.setError(5000, "Invalid/NULL request obj");
        } catch (Exception e) {
            logger.error("Error in save pmsma details : " + e);
            response.setError(5000, "Error in save pmsma details : " + e);
        }
        return response.toString();
    }

    @Operation(summary = "save pnc visit details")
    @RequestMapping(value = { "/pnc/saveAll" }, method = { RequestMethod.POST })
    public String savePNCVisit(@RequestBody List<PNCVisitDTO> pncVisitDTOs,
            @RequestHeader(value = "Authorization") String Authorization) {
        OutputResponse response = new OutputResponse();
        try {
            if (pncVisitDTOs.size() != 0) {
                logger.info("Saving PNC visits with timestamp : " + new Timestamp(System.currentTimeMillis()));
                String s = maternalHealthService.savePNCVisit(pncVisitDTOs);
                if (s != null)
                    response.setResponse(s);
                else
                    response.setError(5000, "Saving pnc to db failed");
            } else
                response.setError(5000, "Invalid/NULL request obj");
        } catch (Exception e) {
            logger.error("Error in save PNC visit details : " + e);
            response.setError(5000, "Error in save PNC visit details : " + e);
        }
        return response.toString();
    }

    @Operation(summary = "get pnc visit details")
    @RequestMapping(value = { "/pnc/getAll" }, method = { RequestMethod.POST })
    public String getPNCVisitDetails(@RequestBody GetBenRequestHandler requestDTO,
            @RequestHeader(value = "Authorization") String Authorization) {
        OutputResponse response = new OutputResponse();
        try {
            if (requestDTO != null) {
                logger.info("request object with timestamp : " + new Timestamp(System.currentTimeMillis()) + " "
                        + requestDTO);
                List<PNCVisitDTO> result = maternalHealthService.getPNCVisits(requestDTO);
                Gson gson = new GsonBuilder().setDateFormat("MMM dd, yyyy h:mm:ss a").create();
                String s = gson.toJson(result);
                if (s != null)
                    response.setResponse(s);
                else
                    response.setError(5000, "No record found");
            } else
                response.setError(5000, "Invalid/NULL request obj");
        } catch (Exception e) {
            logger.error("Error in Pnc visit get data : " + e);
            response.setError(5000, "Error in Pnc visit get data : " + e);
        }
        return response.toString();
    }

}
