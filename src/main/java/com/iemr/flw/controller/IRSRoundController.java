package com.iemr.flw.controller;

import com.iemr.flw.domain.iemr.IRSRound;
import com.iemr.flw.dto.iemr.IRSRoundDTO;
import com.iemr.flw.dto.iemr.IRSRoundListDTO;
import com.iemr.flw.service.IRSRoundService;
import com.iemr.flw.utils.response.OutputResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.List;

@RestController
@RequestMapping(value = "/irsRound", headers = "Authorization")

public class IRSRoundController {
    @Autowired
    private IRSRoundService irsRoundService;

    @RequestMapping(name = "/add", method = RequestMethod.POST)
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

    @RequestMapping(name = "/list/{householdId}", method = RequestMethod.GET)
    public List<IRSRound> getRounds(@PathVariable Long householdId) {
        return irsRoundService.getRounds(householdId);
    }


}
