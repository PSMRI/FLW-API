package com.iemr.flw.controller;

import com.iemr.flw.dto.iemr.OTPRequestParsor;
import com.iemr.flw.dto.iemr.OtpRequestDTO;
import com.iemr.flw.mapper.InputMapper;
import com.iemr.flw.service.OTPHandler;
import com.iemr.flw.utils.exception.IEMRException;
import com.iemr.flw.utils.response.OutputResponse;
import io.lettuce.core.dynamic.annotation.Param;
import io.swagger.v3.oas.annotations.Operation;
import org.checkerframework.checker.units.qual.A;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.MediaType;

@RestController
@RequestMapping("/beneficiary")
public class BeneficiaryOTPGatewayController {
    final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    private OTPHandler otpHandler;

    @Operation(summary = "Send OTP")
    @RequestMapping(value = "/sendOTP", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public String  sendOTP(@RequestParam String phoneNumber, @RequestHeader String Authorization) throws Exception {
        logger.info(phoneNumber);

        OutputResponse response = new OutputResponse();

        try {

            String success = otpHandler.sendOTP(phoneNumber,Authorization);
            logger.info(success.toString());
            if (success.contains("otp"))
                response.setResponse(success);
            else
                response.setError(5000, "failure");

        } catch (Exception e) {
            logger.error("error in sending OTP : " + e);
            response.setError(5000, "error : " + e);
        }
        return response.toString();


    }

    @CrossOrigin()
    @Operation(summary = "Validate OTP")
    @RequestMapping(value = "/validateOTP", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public String validateOTP(@Param(value = "{\"mobNo\":\"String\",\"otp\":\"Integer\"}") @RequestBody String requestOBJ, @RequestHeader String Authorization) throws Exception {

        logger.info(requestOBJ);
        OutputResponse response = new OutputResponse();

        try {
            OTPRequestParsor obj = InputMapper.gson().fromJson(requestOBJ, OTPRequestParsor.class);

            String responseOBJ = otpHandler.validateOTP(obj, Authorization);
            if (responseOBJ != null)
                response.setResponse(responseOBJ.toString());
            else
                response.setError(5000, "failure");

        } catch (Exception e) {
            logger.error("error in validating OTP : " + e);
            response.setError(5000, "error : " + e);
        }
        return response.toString();
    }

    @CrossOrigin()
    @Operation(summary = "Resend OTP")
    @RequestMapping(value = "/resendOTP", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public String resendOTP(@RequestParam String phoneNumber, @RequestHeader String Authorization) throws Exception {
        logger.info(phoneNumber);

        OutputResponse response = new OutputResponse();

        try {

            String success = otpHandler.resendOTP(phoneNumber,Authorization);
            logger.info(success.toString());
            if (success.contains("otp"))
                response.setResponse(success);
            else
                response.setError(5000, "failure");

        } catch (Exception e) {
            logger.error("error in sending OTP : " + e);
            response.setError(5000, "error : " + e);
        }
        return response.toString();
    }

}
