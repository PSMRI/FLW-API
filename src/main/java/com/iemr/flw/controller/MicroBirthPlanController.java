package com.iemr.flw.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iemr.flw.domain.iemr.MicroBirthPlan;
import com.iemr.flw.dto.iemr.MicroBirthPlanDTO;
import com.iemr.flw.service.MicroBirthPlanService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/micro-birthPlan",headers = "Authorization", produces = "application/json")
public class MicroBirthPlanController {
    @Autowired
    private  MicroBirthPlanService microBirthPlanService;
    @CrossOrigin()
    @Operation(summary = "Micro BirthPlan")

    @RequestMapping(value = "saveAll",method = RequestMethod.POST)
    public ResponseEntity<String> createMicroBirthPlan(@RequestBody MicroBirthPlanDTO birthPlan) {
        MicroBirthPlan microBirthPlan = new MicroBirthPlan();
        microBirthPlan=    microBirthPlanService.createMicroBirthPlan(birthPlan);
        Map<String, Object> response = new HashMap<>();
        String jsonResponse ="";

        try {

            Map<String, Object> data = new HashMap<>();
            data.put("userId", birthPlan.getUserId());
            data.put("entries", microBirthPlan); // Empty array

            response.put("data", data);
            response.put("statusCode", 200);
            response.put("errorMessage", "Success");
            response.put("status", "Success");

            ObjectMapper objectMapper = new ObjectMapper();
            jsonResponse = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response);


        }catch (Exception e){


        }
        return ResponseEntity.ok(jsonResponse);
    }


    @RequestMapping(value = "getAll",method = RequestMethod.GET)
    public ResponseEntity<String> getAllMicroBirthPlans(@Param("userId")Integer userId) {
        Map<String, Object> response = new HashMap<>();
        String jsonResponse ="";

        try {

             Map<String, Object> data = new HashMap<>();
             data.put("userId", userId);
             data.put("entries", microBirthPlanService.getAllMicroBirthPlans(userId)); // Empty array

             response.put("data", data);
             response.put("statusCode", 200);
             response.put("errorMessage", "Success");
             response.put("status", "Success");

             ObjectMapper objectMapper = new ObjectMapper();
              jsonResponse = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response);


         }catch (Exception e){

         }
        return ResponseEntity.ok(jsonResponse);


    }

    @RequestMapping(value = "getAllMicroBirthPlansBy{id}",method = RequestMethod.GET)
    public ResponseEntity<MicroBirthPlan> getMicroBirthPlanById(@PathVariable Integer id) {
        return microBirthPlanService.getMicroBirthPlanById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


}