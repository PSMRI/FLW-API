package com.iemr.flw.controller;

import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.*;
import com.iemr.flw.service.VhndFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/forms/villageLevel", headers = "Authorization")
public class VilageLevelFormController {

    @Autowired
    private VhndFormService vhndFormService;

    @RequestMapping(value = "vhnd/saveAll",method = RequestMethod.POST, headers = "Authorization")
    public ResponseEntity<Map<String, Object>> submitVhndForm(@RequestBody VhndDto dto) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "Success");
        response.put("statusCode", 200);
        response.put("msg", vhndFormService.submitForm(dto));
        return ResponseEntity.ok(response);
    }
    @RequestMapping(value = "vhnc/saveAll",method = RequestMethod.POST, headers = "Authorization")
    public ResponseEntity<Map<String, Object>> submitVhncForm(@RequestBody VhncDto dto) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "Success");
        response.put("statusCode", 200);
        response.put("msg", vhndFormService.submitVhncForm(dto));
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "phc/saveAll",method = RequestMethod.POST, headers = "Authorization")
    public ResponseEntity<Map<String, Object>> submitPhcForm(@RequestBody PhcReviewMeetingDTO dto) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "Success");
        response.put("statusCode", 200);
        response.put("msg", vhndFormService.submitPhcForm(dto));
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "ahd/saveAll",method = RequestMethod.POST, headers = "Authorization")
    public ResponseEntity<Map<String, Object>> submitAhdForm(@RequestBody AhdMeetingDto dto) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "Success");
        response.put("statusCode", 200);
        response.put("msg", vhndFormService.submitAdhForm(dto));
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "deworming/saveAll",method = RequestMethod.POST, headers = "Authorization")
    public ResponseEntity<Map<String, Object>> submitDewormingForm(@RequestBody DewormingDto dto) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "Success");
        response.put("statusCode", 200);
        response.put("msg", vhndFormService.submitDewormingForm(dto));
        return ResponseEntity.ok(response);
    }


    @RequestMapping(value = "getAll",method = RequestMethod.POST, headers = "Authorization")
    public ResponseEntity<Map<String, Object>> getVillageLevelFormData(@RequestBody GetVillageLevelRequestHandler getVillageLevelRequestHandler) {
        Map<String, Object> response = new LinkedHashMap<>();

        try {
            Map<String, Object> data = new HashMap<>();
            data.put("userId", getVillageLevelRequestHandler.getUserId());
            data.put("entries", vhndFormService.getAll(getVillageLevelRequestHandler));

            response.put("data", data);
            response.put("statusCode", 200);
            response.put("errorMessage", "Success");
            response.put("status", "Success");
        }catch (Exception e){
            response.put("statusCode", 500);
            response.put("errorMessage", e.getMessage());

        }

        return ResponseEntity.ok(response);
    }



}