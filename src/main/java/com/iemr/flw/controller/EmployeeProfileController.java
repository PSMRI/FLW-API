package com.iemr.flw.controller;

import com.iemr.flw.domain.iemr.M_User;
import com.iemr.flw.service.EmployeeProfileService;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class EmployeeProfileController {
    private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
    @Autowired
    EmployeeProfileService employeeProfileService;
    private Map<String,Object> response = new HashMap<>();

    @CrossOrigin()
    @Operation(summary = "Edit employee")
    

    @RequestMapping(value = "employee/editProfile", method = { RequestMethod.POST }, produces = {
            "application/json" },headers = "Authorization")
    public ResponseEntity<Map<String,Object>> editEmployee(@RequestBody M_User editEmployee) {

        try {
            System.out.println(editEmployee.toString());



            M_User editdata1 = employeeProfileService.saveEditData(editEmployee);
            response.put("data",editdata1);
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
    @RequestMapping(value = "employee/getProfile",method = RequestMethod.POST,headers = "Authorization")
     public ResponseEntity<Map<String,Object>> getProfile(@RequestParam ("UserID")Integer userId){
        try {
            response.put("data",employeeProfileService.getProfileData(userId));
            response.put("statusCode",200);
            response.put("status","Success");
            response.put("errorMessage","Success");
        }catch (Exception e) {
            logger.error("Unexpected error:", e);
            ResponseEntity.status(500).body(e.getMessage());

        }

        return  ResponseEntity.ok().body(response);


    }
}
