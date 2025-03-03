package com.iemr.flw.controller;

import com.iemr.flw.domain.iemr.AshaWorker;
import com.iemr.flw.domain.iemr.M_User;
import com.iemr.flw.dto.iemr.UserServiceRoleDTO;
import com.iemr.flw.repo.iemr.UserServiceRoleRepo;
import com.iemr.flw.service.AshaProfileService;
import com.iemr.flw.service.EmployeeMasterInter;
import com.iemr.flw.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping(value = "/asha", headers = "Authorization", produces = "application/json")
public class AshaProfileController {
    private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
    @Autowired
    AshaProfileService ashaProfileService;
    private Map<String,Object> response = new HashMap<>();

    @Autowired
    private EmployeeMasterInter employeeMasterInter;
    @CrossOrigin()
    @Operation(summary = "Edit Asha Profile")
    

    @RequestMapping(value = "editProfile", method = { RequestMethod.POST }, produces = {
            "application/json" },consumes = "application/json" )
    public ResponseEntity<Map<String,Object>> editEmployee(@RequestBody AshaWorker editEmployee) {

        try {
            System.out.println(editEmployee.toString());


            AshaWorker ashaWorker = ashaProfileService.saveEditData(editEmployee);
            response.put("data",ashaWorker);
            response.put("statusCode",200);
            response.put("status","Success");
            response.put("errorMessage","Success");




        } catch (Exception e) {
            logger.error("Unexpected error:", e);
            ResponseEntity.status(500).body(e.getMessage());

        }

        return ResponseEntity.ok().body(response);

    }
    @Operation(summary = "Profile Detail")
    @RequestMapping(value = "getProfile",method = RequestMethod.GET ,headers = "Authorization" )
     public ResponseEntity<Map<String,Object>> getProfile(@RequestParam ("employeeId")Integer employeeId){
        logger.info("employee_id"+employeeId);
        try {
            AshaWorker ashaWorker = ashaProfileService.getProfileData(employeeId);
            if(ashaWorker!=null){
                response.put("data",ashaWorker);
                response.put("statusCode",200);
                response.put("status","Success");
                response.put("errorMessage","Success");
            }else {
                response.put("data",ashaWorker);
                response.put("statusCode",200);
                response.put("status","Success");
                response.put("errorMessage","Asha profile not found");
            }

        }catch (Exception e) {
            logger.error("Unexpected error:", e);
            ResponseEntity.status(500).body(e.getMessage());

        }

        return  ResponseEntity.ok().body(response);


    }
}
