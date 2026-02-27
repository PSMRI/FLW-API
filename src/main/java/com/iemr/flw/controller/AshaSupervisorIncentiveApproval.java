package com.iemr.flw.controller;

import com.iemr.flw.dto.iemr.AshaByFacilityRequestDTO;
import com.iemr.flw.service.impl.AshaSupervisorLoginService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/ashaSupervisor")
public class AshaSupervisorIncentiveApproval {

    @Autowired
    private AshaSupervisorLoginService ashaSupervisorLoginService;

    @PostMapping("/getAshaList")
    public ResponseEntity<?> getAshaList(@RequestBody AshaByFacilityRequestDTO request) {
        try {
            List<Map<String, Object>> result = ashaSupervisorLoginService.getAshasAtFacility(
                    request.getSuperVisorId(),
                    request.getFacilityId()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("data", result);
            response.put("statusCode", 200);

            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Server error: " + e.getMessage());
        }
    }

}
