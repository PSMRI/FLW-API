package com.iemr.flw.controller;

import com.iemr.flw.dto.iemr.DiseaseControlDTO;
import com.iemr.flw.service.DiseaseControlService;
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
@RequestMapping(value = "/disease" ,headers = "Authorization")
public class DiseaseControlController {
    @Autowired
    private DiseaseControlService diseaseControlService;

    @RequestMapping(value = "saveAll", method = RequestMethod.POST,consumes = "application/json",produces = "application/json")
    public ResponseEntity<Map<String, Object>> saveDiseaseData(@RequestBody List<DiseaseControlDTO> diseaseControlDTO) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "Success");
        response.put("statusCode", 200);
        response.put("data", diseaseControlService.save(diseaseControlDTO));
        diseaseControlService.save(diseaseControlDTO);

        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "getAll",method = RequestMethod.GET,produces = "application/json")
    public ResponseEntity<Map<String, Object>> getAllData(){
        Map<String, Object> response = new HashMap<>();
        response.put("status", "Success");
        response.put("statusCode", 200);
        response.put("data", diseaseControlService.getAll());
        return  ResponseEntity.ok(response);
    }


}
