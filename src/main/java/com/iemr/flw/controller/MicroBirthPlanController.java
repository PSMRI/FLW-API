package com.iemr.flw.controller;

import com.iemr.flw.domain.iemr.MicroBirthPlan;
import com.iemr.flw.dto.iemr.MicroBirthPlanDTO;
import com.iemr.flw.service.MicroBirthPlanService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/micro-birthPlan",headers = "Authorization", produces = "application/json")
public class MicroBirthPlanController {
    @Autowired
    private  MicroBirthPlanService service;
    @CrossOrigin()
    @Operation(summary = "Micro BirthPlan")

    @RequestMapping(value = "saveAll",method = RequestMethod.POST)
    public ResponseEntity<MicroBirthPlan> createMicroBirthPlan(@RequestBody MicroBirthPlanDTO birthPlan) {
        return ResponseEntity.ok(service.createMicroBirthPlan(birthPlan));
    }


    @RequestMapping(value = "getAll",method = RequestMethod.GET)
    public ResponseEntity<List<MicroBirthPlan>> getAllMicroBirthPlans() {
        return ResponseEntity.ok(service.getAllMicroBirthPlans());
    }

    @RequestMapping(value = "getAllMicroBirthPlansBy{id}",method = RequestMethod.GET)
    public ResponseEntity<MicroBirthPlan> getMicroBirthPlanById(@PathVariable Long id) {
        return service.getMicroBirthPlanById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


}