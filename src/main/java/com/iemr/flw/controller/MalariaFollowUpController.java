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
@RequestMapping(value = "/api/follow-up")
public class MalariaFollowUpController {

    @Autowired
    private MalariaFollowUpService followUpService;

    @RequestMapping(value = "save",method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> save(@RequestBody MalariaFollowUpDTO dto) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "Success");
        response.put("statusCode", 200);
        response.put("msg", followUpService.saveFollowUp(dto));

        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "get",method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> getFollowUpsByUserId(@RequestBody GetDiseaseRequestHandler getDiseaseRequestHandler) {
        Map<String, Object> response = new HashMap<>();

        List<MalariaFollowListUpDTO> data = followUpService.getByUserId(getDiseaseRequestHandler.getUserId());
        if(!data.isEmpty()){
            response.put("status", "Success");
            response.put("statusCode", 200);
            response.put("data",data);
        }

        return ResponseEntity.ok(response);
    }
}
