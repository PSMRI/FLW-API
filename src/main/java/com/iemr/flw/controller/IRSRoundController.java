package com.iemr.flw.controller;

import com.iemr.flw.domain.iemr.IRSRound;
import com.iemr.flw.dto.iemr.IRSRoundDTO;
import com.iemr.flw.dto.iemr.IRSRoundListDTO;
import com.iemr.flw.service.IRSRoundService;
import com.iemr.flw.utils.response.OutputResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @PostMapping(value = "/add", headers = "Authorization")
    public ResponseEntity<Map<String, Object>> addRound(@RequestBody IRSRoundListDTO dto) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            if (dto.getRounds().size() != 0) {
                List<IRSRound> s = irsRoundService.addRounds(dto.getRounds());
                if (s.size() != 0) {
                    Map<String, Object> data = new HashMap<>();
                                  data.put("entries", s);
                                   response.put("data", data);
                                   response.put("statusCode", 200);
                                   response.put("message", "Success");
                                   return ResponseEntity.ok(response);
                } else {
                    response.put("statusCode", 500);
                                  response.put("errorMessage", "Invalid/NULL request obj");
                                  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                }

            } else
                response.put("statusCode", 400);
                     response.put("errorMessage", "Invalid/NULL request obj");
                       return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("statusCode", 500);
                   response.put("errorMessage", "Error in saving IRS round details: " + e.getMessage());
                   return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }

    @GetMapping(value = "/list/{householdId}", headers = "Authorization")
    public ResponseEntity<Map<String, Object>> getRounds(@PathVariable Long householdId) {

        Map<String, Object> response = new LinkedHashMap<>();

        try {
            Map<String, Object> data = new HashMap<>();
            data.put("entries", irsRoundService.getRounds(householdId));
            response.put("data", data);
            response.put("statusCode", 200);
            response.put("message", "Success");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("statusCode", 500);
            response.put("errorMessage", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);

        }

    }


}
