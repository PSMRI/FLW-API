package com.iemr.flw.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iemr.flw.dto.iemr.AesJeDTO;
import com.iemr.flw.dto.iemr.FilariaDTO;
import com.iemr.flw.dto.iemr.KalaAzarDTO;
import com.iemr.flw.dto.iemr.MalariaDTO;
import com.iemr.flw.dto.iemr.LeprosyDTO;
import com.iemr.flw.dto.iemr.GetDiseaseRequestHandler;
import com.iemr.flw.service.DiseaseControlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/disease",headers = "Authorization")
public class DiseaseControlController {
    private final Logger logger = LoggerFactory.getLogger(CoupleController.class);

    @Autowired
    private DiseaseControlService diseaseControlService;


    @RequestMapping(value = "Malaria/saveAll", method = RequestMethod.POST, consumes = "application/json", produces = "application/json",headers = "Authorization")
    public ResponseEntity<Map<String, Object>> saveMalaria(@RequestBody MalariaDTO malariaDTO) {
        Map<String, Object> response = new HashMap<>();

        try {

            logger.info("Malaria DTO: {}", new ObjectMapper().writeValueAsString(malariaDTO));
             if(!malariaDTO.getMalariaLists().isEmpty()){
                 response.put("message", "Success");
                 response.put("statusCode", 200);
                 response.put("data", diseaseControlService.saveMalaria(malariaDTO));
             }else {
                 response.put("message", "Invalid request");
                 response.put("statusCode", 200);
             }

        } catch (Exception e) {
            response.put("status", "Error" + e.getMessage());
            response.put("statusCode", 500);
        }
        return ResponseEntity.ok(response);

    }

    @RequestMapping(value = "KalaAzar/saveAll", method = RequestMethod.POST,consumes = "application/json", produces = "application/json",headers = "Authorization")
    public ResponseEntity<Map<String, Object>> saveKalaAzar(@RequestBody KalaAzarDTO kalaAzarDTO) {
        Map<String, Object> response = new HashMap<>();

        try {
            logger.info("KalaAzar DTO: {}", new ObjectMapper().writeValueAsString(kalaAzarDTO));

            response.put("message", "Success");
            response.put("statusCode", 200);
            response.put("data", diseaseControlService.saveKalaAzar(kalaAzarDTO));
        } catch (Exception e) {
            response.put("status", "Error" + e.getMessage());
            response.put("statusCode", 500);
        }
        return ResponseEntity.ok(response);

    }

    @RequestMapping(value = "AesJe/saveAll", method = RequestMethod.POST, consumes = "application/json", produces = "application/json",headers = "Authorization")
    public ResponseEntity<Map<String, Object>> saveAESJE(@RequestBody AesJeDTO aesJeDTO) {
        Map<String, Object> response = new HashMap<>();

        try {
            response.put("message", "Success");
            response.put("statusCode", 200);
            response.put("data", diseaseControlService.saveAES(aesJeDTO));
        } catch (Exception e) {
            response.put("status", "Error" + e.getMessage());
            response.put("statusCode", 500);
        }
        return ResponseEntity.ok(response);

    }

    @RequestMapping(value = "Filaria/saveAll", method = RequestMethod.POST, consumes = "application/json", produces = "application/json",headers = "Authorization")
    public ResponseEntity<Map<String, Object>> saveFilaria(@RequestBody FilariaDTO filariaDTO) {
        Map<String, Object> response = new HashMap<>();

        try {
            response.put("message", "Success");
            response.put("statusCode", 200);
            response.put("data", diseaseControlService.saveFilaria(filariaDTO));
        } catch (Exception e) {
            response.put("status", "Error" + e.getMessage());
            response.put("statusCode", 500);
        }
        return ResponseEntity.ok(response);

    }

    @RequestMapping(value = "Leprosy/saveAll", method = RequestMethod.POST, consumes = "application/json", produces = "application/json",headers = "Authorization")
    public ResponseEntity<Map<String, Object>> saveLeprosy(@RequestBody LeprosyDTO leprosyDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            response.put("message", "Success");
            response.put("statusCode", 200);
            response.put("data", diseaseControlService.saveLeprosy(leprosyDTO));
        } catch (Exception e) {
            response.put("status", "Error" + e.getMessage());
            response.put("statusCode", 500);
        }


        return ResponseEntity.ok(response);

    }

    @RequestMapping(value = "getAll", method = RequestMethod.POST, produces = "application/json",headers = "Authorization")
    public ResponseEntity<Map<String, Object>> getAllData(@RequestBody GetDiseaseRequestHandler getDiseaseRequestHandler) {
        Map<String, Object> response = new HashMap<>();
        try {
            response.put("status", "Success");
            response.put("statusCode", 200);
            response.put("data", diseaseControlService.getAllScreeningData(getDiseaseRequestHandler));

        } catch (Exception e) {
            response.put("status", "Error" + e.getMessage());
            response.put("statusCode", 500);

        }


        return ResponseEntity.ok(response);
    }


}