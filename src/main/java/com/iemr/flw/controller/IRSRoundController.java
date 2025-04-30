package com.iemr.flw.controller;

import com.iemr.flw.domain.iemr.IRSRound;
import com.iemr.flw.dto.iemr.IRSRoundDTO;
import com.iemr.flw.dto.iemr.IRSRoundListDTO;
import com.iemr.flw.service.IRSRoundService;
import com.iemr.flw.utils.response.OutputResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/irsRound", headers = "Authorization")

public class IRSRoundController {
    @Autowired
    private IRSRoundService irsRoundService;

    @RequestMapping(name = "/add", method = RequestMethod.POST, headers = "Authorization")
    public String addRound(@RequestBody IRSRoundListDTO dto) {
        OutputResponse response = new OutputResponse();
        try {
            if (dto.getRounds().size() != 0) {
                List<IRSRound> s = irsRoundService.addRounds(dto.getRounds());
                if (s.size() != 0) {
                    response.setResponse(s.toString());

                } else {
                    response.setError(500, "Invalid/NULL request obj");

                }

            } else
                response.setError(500, "Invalid/NULL request obj");
        } catch (Exception e) {
            response.setError(500, "Error in saving cbac details : " + e);
        }
        return response.toString();

    }

    @RequestMapping(name = "/list/{householdId}", method = RequestMethod.GET, headers = "Authorization")
    public ResponseEntity<Map<String, Object>> getRounds(@PathVariable Long householdId) {

        Map<String, Object> response = new LinkedHashMap<>();

        try {
            Map<String, Object> data = new HashMap<>();
            data.put("entries", irsRoundService.getRounds(householdId));
            response.put("data", data);
            response.put("statusCode", 200);
            response.put("message", "Success");
        } catch (Exception e) {
            response.put("statusCode", 500);
            response.put("errorMessage", e.getMessage());

        }

        return ResponseEntity.ok(response);
    }


}
