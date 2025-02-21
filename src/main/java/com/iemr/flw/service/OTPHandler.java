package com.iemr.flw.service;

import com.iemr.flw.dto.iemr.OTPRequestParsor;
import com.iemr.flw.dto.iemr.OtpRequestDTO;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

public interface OTPHandler {
    public String sendOTP(String mobNo) throws Exception;

    public JSONObject validateOTP(OtpRequestDTO obj) throws Exception;

    public String resendOTP(String mobNo) throws Exception;

}
