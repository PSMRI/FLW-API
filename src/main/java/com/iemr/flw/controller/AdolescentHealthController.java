package com.iemr.flw.controller;

import com.iemr.flw.domain.iemr.AdolescentHealth;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.AdolescentHealthDTO;
import com.iemr.flw.service.AdolescentHealthService;
import com.iemr.flw.utils.response.OutputResponse;
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
@RequestMapping(value = "/adolescentHealth", headers = "Authorization")
public class AdolescentHealthController {
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(AdolescentHealthController.class);

    @Autowired
    private AdolescentHealthService adolescentHealthService;

    @RequestMapping(value = "/saveAll", method = RequestMethod.POST, headers = "Authorization")
    public ResponseEntity<Map<String,Object>>  saveAdolescentHealth(@RequestBody AdolescentHealthDTO adolescentHealthDTO) {
        Map<String,Object> response = new HashMap<>();

        try {
            if (adolescentHealthDTO.getAdolescentHealths().size() != 0) {
                String result = adolescentHealthService.saveAll(adolescentHealthDTO);
                if (result != null) {
                    response.put("statusCode",200);
                    response.put("message",result);

                }

            } else {
                response.put("statusCode", 500);
                response.put("error", "Invalid/NULL request obj");
            }
        } catch (Exception e) {
            logger.error("Error in saving adolescent health data : " + e);
            response.put("statusCode",500);
            response.put("error","Error in saving adolescent health data : " + e);
        }
        return ResponseEntity.ok(response);

    }

    @RequestMapping(value = "/getAll",method = RequestMethod.POST, headers = "Authorization")
    public ResponseEntity<Map<String,Object>> getAllAdolescentHealth(@RequestBody GetBenRequestHandler getBenRequestHandler) {
        Map<String,Object> response = new HashMap<>();
        try {
            List<AdolescentHealth> resultList = adolescentHealthService.getAllAdolescentHealth(getBenRequestHandler);

            if (resultList != null && !resultList.isEmpty()) {
                response.put("statusCode",200);
                response.put("data", resultList);
            } else {
                response.put("statusCode", 500);
                response.put("error", "Invalid/NULL request obj");
            }
        } catch (Exception e) {
            logger.error("Error in get data : " + e);
            response.put("statusCode",500);
            response.put("error","Error in get data : " + e);

        }
        return ResponseEntity.ok(response);
    }
}
