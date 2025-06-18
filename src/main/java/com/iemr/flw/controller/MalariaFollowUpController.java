package com.iemr.flw.controller;

import com.iemr.flw.dto.iemr.GetDiseaseRequestHandler;
import com.iemr.flw.dto.iemr.MalariaFollowListUpDTO;
import com.iemr.flw.dto.iemr.MalariaFollowUpDTO;
import com.iemr.flw.service.MalariaFollowUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/follow-up", headers = "Authorization")
public class MalariaFollowUpController {

    @Autowired
    private MalariaFollowUpService followUpService;

    @RequestMapping(value = "save", method = RequestMethod.POST, headers = "Authorization")
    public ResponseEntity<Map<String, Object>> save(@RequestBody MalariaFollowUpDTO dto) {
        Map<String, Object> response = new HashMap<>();

        try {
            Boolean result = followUpService.saveFollowUp(dto);
            if (result) {
                response.put("status", "Success");
                response.put("statusCode", 200);
                response.put("message", "Follow-up saved successfully");
            } else {
                response.put("status", "Failed");
                response.put("statusCode", 400);
            }
        } catch (Exception e) {
            response.put("status", "Error: " + e.getMessage());
            response.put("statusCode", 500);
        }
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "get", method = RequestMethod.POST, headers = "Authorization")
    public ResponseEntity<Map<String, Object>> getFollowUpsByUserId(@RequestBody GetDiseaseRequestHandler getDiseaseRequestHandler) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<MalariaFollowListUpDTO> data = followUpService.getByUserId(getDiseaseRequestHandler.getUserId());
            response.put("status", "Success");
            response.put("statusCode", 200);
            response.put("data", data);
            if (data.isEmpty()) {
                response.put("message", "No records found");
            }
        } catch (Exception e) {
            response.put("status", "Error: " + e.getMessage());
            response.put("statusCode", 500);
        }
        return ResponseEntity.ok(response);


    }
}
