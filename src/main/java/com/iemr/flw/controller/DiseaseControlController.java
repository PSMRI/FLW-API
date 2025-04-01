package com.iemr.flw.controller;

import com.iemr.flw.dto.iemr.*;
import com.iemr.flw.service.DiseaseControlService;
import org.apache.commons.lang3.function.Failable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/disease", headers = "Authorization")
public class DiseaseControlController {
    @Autowired
    private DiseaseControlService diseaseControlService;



    @RequestMapping(value = "Malaria/saveAll", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public ResponseEntity<Map<String, Object>> saveMalaria(@RequestBody MalariaDTO malariaDTO) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "Success");
        response.put("statusCode", 200);
        response.put("data", diseaseControlService.saveMalaria(malariaDTO));

        return ResponseEntity.ok(response);

    }

    @RequestMapping(value = "KalaAzar/saveAll", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public ResponseEntity<Map<String, Object>> saveMalaria(@RequestBody KalaAzarDTO kalaAzarDTO) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "Success");
        response.put("statusCode", 200);
        response.put("data", diseaseControlService.saveKalaAzar(kalaAzarDTO));

        return ResponseEntity.ok(response);

    }

    @RequestMapping(value = "AesJe/saveAll", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public ResponseEntity<Map<String, Object>> saveMalaria(@RequestBody AesJeDTO aesJeDTO) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "Success");
        response.put("statusCode", 200);
        response.put("data", diseaseControlService.saveAES(aesJeDTO));

        return ResponseEntity.ok(response);

    }

    @RequestMapping(value = "Filaria/saveAll", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public ResponseEntity<Map<String, Object>> saveMalaria(@RequestBody FilariaDTO filariaDTO) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "Success");
        response.put("statusCode", 200);
        response.put("data", diseaseControlService.saveFilaria(filariaDTO));

        return ResponseEntity.ok(response);

    }

    @RequestMapping(value = "Leprosy/saveAll", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public ResponseEntity<Map<String, Object>> saveMalaria(@RequestBody LeprosyDTO leprosyDTO) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "Success");
        response.put("statusCode", 200);
        response.put("data", diseaseControlService.saveLeprosy(leprosyDTO));

        return ResponseEntity.ok(response);

    }

    @RequestMapping(value = "getAll", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Map<String, Object>> getAllData(@Param("diseaseTypeID") BigInteger diseaseTypeID) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "Success");
        response.put("statusCode", 200);
        response.put("data", diseaseControlService.getAll(diseaseTypeID));
        return ResponseEntity.ok(response);
    }


}
