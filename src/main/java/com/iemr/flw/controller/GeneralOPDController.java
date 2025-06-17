package com.iemr.flw.controller;

import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.GeneralOpdDto;
import com.iemr.flw.service.BeneficiaryService;
import com.iemr.flw.service.GeneralOpdService;
import com.iemr.flw.utils.response.OutputResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/general-opd")
public class GeneralOPDController {
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(BeneficiaryController.class);

    @Autowired
    private GeneralOpdService generalOpdService;


    @RequestMapping(value = "getData", method = RequestMethod.POST)
    @Operation(summary = "get beneficiary data for given user ")
    public ResponseEntity<Map<String, Object>> getBeneficiaryDataByAsha(@RequestBody GetBenRequestHandler requestDTO,
                                                                        @RequestHeader(value = "Authorization") String authorization) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("userId", requestDTO.getUserId());
            data.put("entries", generalOpdService.getOpdListForAsha(requestDTO, authorization));
            response.put("data", data);
            response.put("statusCode", 200);
            response.put("errorMessage", "Success");
            response.put("status", "Success");
        } catch (Exception e) {
            logger.error("Error in get data : " + e);
            response.put("status", 500);
            response.put("message", "Error in get data : " + e);
        }
        return ResponseEntity.ok(response);

    }
} 