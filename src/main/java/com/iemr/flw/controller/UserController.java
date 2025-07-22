package com.iemr.flw.controller;

import com.iemr.flw.dto.iemr.UserServiceRoleDTO;
import com.iemr.flw.service.UserService;
import com.iemr.flw.utils.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/user", headers = "Authorization")
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(CoupleController.class);

    @Autowired
    private UserService userService;

    @Operation(summary = "get user Detail of userId and roleId")
    @RequestMapping(value = { "/getUserDetail" }, method = { RequestMethod.GET })
    public ResponseEntity<?> getUserDetail(@RequestParam(value = "userId") Integer userId,
            @RequestHeader(value = "Authorization") String Authorization) {
        try {
            UserServiceRoleDTO result = userService.getUserDetail(userId);
            return new ResponseEntity<>(
                    new ApiResponse(true, null, result), HttpStatus.ACCEPTED);
        } catch (Exception e) {
            logger.error("Error in fetching user role, " + e.getMessage());
            return new ResponseEntity<>(
                    new ApiResponse(false, "Error in fetching user role, " + e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
