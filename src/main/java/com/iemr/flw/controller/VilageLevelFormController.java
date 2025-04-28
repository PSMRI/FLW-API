package com.iemr.flw.controller;

import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.VhndDto;
import com.iemr.flw.dto.iemr.VilageLevelFormDto;
import com.iemr.flw.service.VhndFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/forms/vilageLevel")
public class VilageLevelFormController {

    @Autowired
    private VhndFormService vhndFormService;

    @RequestMapping(value = "vhnd/saveAll",method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> submitLevelForm(@RequestBody VhndDto dto) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "Success");
        response.put("statusCode", 200);
        response.put("msg", vhndFormService.submitForm(dto));
        return ResponseEntity.ok(response);
    }


    @RequestMapping(value = "getAll",method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> getVilageLevelFormData(@RequestBody GetBenRequestHandler getBenRequestHandler) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "Success");
        response.put("statusCode", 200);
        response.put("data", vhndFormService.getAll(getBenRequestHandler.getUserId()));
        return ResponseEntity.ok(response);
    }



}