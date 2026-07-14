package com.iemr.flw.controller;

import com.iemr.flw.domain.iemr.AdolescentHealth;
import com.iemr.flw.dto.abhaBeneficiary.AbhaBeneficiaryDTO;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.AbhaRequestDTO;
import com.iemr.flw.service.AbhaBeneficiaryService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/UserRegistration")
public class AbhaBeneficiaryController {
    @Autowired
    private AbhaBeneficiaryService abhaBeneficiaryService;

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(AbhaBeneficiaryController.class);



    @RequestMapping(value = "/GetUserDetailsByAyushmanCardNo",method = RequestMethod.POST)
    public ResponseEntity<Map<String,Object>> getAllAdolescentHealth(@RequestBody AbhaRequestDTO request ) {
        Map<String,Object> response = new HashMap<>();
        try {
            if(request.getCardNo()!=null){
                Object abhaBeneficiaryDTOList = abhaBeneficiaryService.getBeneficiaryByAbha(request);

                if (abhaBeneficiaryDTOList != null ) {
                    response.put("statusCode",200);
                    response.put("data", abhaBeneficiaryDTOList);
                } else {
                    response.put("statusCode", 5000);
                    response.put("error", "Invalid/NULL request obj");
                }
            }

        } catch (Exception e) {
            logger.error("Error in get data : " + e);
            response.put("statusCode",5000);
            response.put("error","Error in get data : " + e);

        }
        return ResponseEntity.ok(response);
    }
}
