package com.iemr.flw.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.*;
import com.iemr.flw.service.ChildCareService;
import com.iemr.flw.utils.response.OutputResponse;

import io.swagger.v3.oas.annotations.Operation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;

@RestController
@RequestMapping(value = "/child-care", produces = "application/json",headers = "Authorization")
public class ChildCareController {

    private final Logger logger = LoggerFactory.getLogger(DeathReportsController.class);

    @Autowired
    private ChildCareService childCareService;

    @Operation(summary = "save HBYC details")
    @RequestMapping(value = {"/hbyc/saveAll"}, method = {RequestMethod.POST})
    public String saveHbycRecords(@RequestBody List<HbycDTO> hbycDTOs,
                                  @RequestHeader(value = "Authorization") String Authorization) {
        OutputResponse response = new OutputResponse();
        try {
            logger.info("Saving All HBYC Details");
            if (hbycDTOs != null) {
                String s = childCareService.registerHBYC(hbycDTOs);
                if (s != null)
                    response.setResponse(s);
                else
                    response.setError(5000, "No record found");
            } else
                response.setError(5000, "Invalid/NULL request obj");
        } catch (Exception e) {
            logger.error("Error in saving HBYC details, " + e);
            response.setError(5000, "Error in saving HBYC details" + e);
        }
        return response.toString();
    }

    @Operation(summary = "get List of HBYC details")
    @RequestMapping(value = {"/hbyc/getAll"}, method = {RequestMethod.POST})
    public String getHbycRecords(@RequestBody GetBenRequestHandler requestDTO,
                                 @RequestHeader(value = "Authorization") String Authorization) {
        OutputResponse response = new OutputResponse();
        try {
            logger.info("fetching All HBYC Details for user: " + requestDTO.getAshaId());
            if (requestDTO != null) {
                List<HbycDTO> result = childCareService.getHbycRecords(requestDTO);
                Gson gson = new GsonBuilder().setDateFormat("MMM dd, yyyy h:mm:ss a").create();
                String s = gson.toJson(result);
                if (s != null)
                    response.setResponse(s);
                else
                    response.setError(5000, "No record found");
            } else
                response.setError(5000, "Invalid/NULL request obj");

        } catch (Exception e) {
            logger.error("Error in fetching HBYC details, " + e);
            response.setError(5000, "Error in fetching HBYC details : " + e);
        }
        return response.toString();
    }

    @PostMapping("/hbncVisit/saveAll")
    public String saveHBNCVisit(@RequestBody List<HbncRequestDTO> hbncRequestDTOs,
                                @RequestHeader(value = "Authorization") String Authorization) {
        OutputResponse response = new OutputResponse();
        try {
            if (!hbncRequestDTOs.isEmpty()) {
                logger.info("Saving HBNC details at: " + new Timestamp(System.currentTimeMillis()));

                String result = childCareService.saveHBNCDetails(hbncRequestDTOs); // <-- actual save

                if (result != null)
                    response.setResponse(result);
                else
                    response.setError(500, "Failed to save HBNC visit data.");
            } else {
                response.setError(400, "Empty request list.");
            }
        } catch (Exception e) {
            logger.error("Error saving HBNC visit: ", e);
            response.setError(500, "Server error: " + e.getMessage());
        }
        return response.toString();
    }


    @Operation(summary = "get hbnc visit details")
    @RequestMapping(value = {"/hbncVisit/getAll"}, method = {RequestMethod.POST})
    public String getHBNCVisitDetails(@RequestBody GetBenRequestHandler requestDTO) {
        OutputResponse response = new OutputResponse();
        try {
            if (requestDTO != null) {
                logger.info("request object with timestamp : " + new Timestamp(System.currentTimeMillis()) + " "
                        + requestDTO);
                List<HbncRequestDTO> result = childCareService.getHBNCDetails(requestDTO);
                Gson gson = new GsonBuilder().setDateFormat("MMM dd, yyyy h:mm:ss a").create();
                String s = gson.toJson(result);
                if (s != null)
                    response.setResponse(s);
                else
                    response.setError(500, "No record found");
            } else
                response.setError(500, "Invalid/NULL request obj");
        } catch (Exception e) {
            logger.error("Error in Hbnc visit get data : " + e);
            response.setError(500, "Error in Hbnc visit get data : " + e);
        }
        return response.toString();
    }

    @Operation(summary = "save child vaccination details")
    @RequestMapping(value = {"/vaccination/saveAll"}, method = {RequestMethod.POST})
    public String saveChildVaccinationDetails(@RequestBody List<ChildVaccinationDTO> childVaccinationDTOS,
                                              @RequestHeader(value = "Authorization") String Authorization) {
        OutputResponse response = new OutputResponse();
        try {
            if (childVaccinationDTOS.size() != 0) {
                logger.info("Saving Child Vaccination details with timestamp : "
                        + new Timestamp(System.currentTimeMillis()));
                String s = childCareService.saveChildVaccinationDetails(childVaccinationDTOS);
                if (s != null)
                    response.setResponse(s);
                else
                    response.setError(5000, "Saving Child vaccination data to db failed");
            } else
                response.setError(5000, "Invalid/NULL request obj");
        } catch (Exception e) {
            logger.error("Error in save child vaccination details : " + e);
            response.setError(5000, "Error in save child vaccination details : " + e);
        }
        return response.toString();
    }

    @Operation(summary = "get child vaccination details")
    @RequestMapping(value = {"/vaccination/getAll"}, method = {RequestMethod.POST})
    public String getChildVaccinationDetails(@RequestBody GetBenRequestHandler requestDTO,
                                             @RequestHeader(value = "Authorization") String Authorization) {
        OutputResponse response = new OutputResponse();
        try {
            if (requestDTO != null) {
                logger.info("request object for getting child vaccination with timestamp : "
                        + new Timestamp(System.currentTimeMillis()) + " "
                        + requestDTO);
                List<ChildVaccinationDTO> result = childCareService.getChildVaccinationDetails(requestDTO);
                Gson gson = new GsonBuilder().setDateFormat("MMM dd, yyyy h:mm:ss a").create();
                String s = gson.toJson(result);
                if (s != null)
                    response.setResponse(s);
                else
                    response.setError(5000, "No record found");
            } else
                response.setError(5000, "Invalid/NULL request obj");
        } catch (Exception e) {
            logger.error("Error in Child Vaccination get data : " + e);
            response.setError(5000, "Error in Child Vaccination get data : " + e);
        }
        return response.toString();
    }

    @Operation(summary = "get child vaccination details")
    @RequestMapping(value = {"/vaccine/getAll"}, method = {RequestMethod.GET})
    public String getChildVaccinationDetails(@RequestParam(value = "category") String category,
                                             @RequestHeader(value = "Authorization") String Authorization) {
        OutputResponse response = new OutputResponse();
        try {
            logger.info("request object for getting all vaccines with timestamp : "
                    + new Timestamp(System.currentTimeMillis()) +
                    " for category: " + category);
            List<VaccineDTO> result = childCareService.getAllChildVaccines(category);
            Gson gson = new GsonBuilder().setDateFormat("MMM dd, yyyy h:mm:ss a").create();
            String s = gson.toJson(result);
            if (s != null)
                response.setResponse(s);
            else
                response.setError(5000, "No record found");
        } catch (Exception e) {
            logger.error("Error in Vaccines get data : " + e);
            response.setError(5000, "Error in Vaccines get data : " + e);
        }
        return response.toString();
    }
}
