package com.iemr.flw.service;

import com.iemr.flw.dto.iemr.OTPRequestParsor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

public interface OTPHandler {
    public String sendOTP(String mobNo) throws Exception;

    public JSONObject validateOTP(OTPRequestParsor obj) throws Exception;

    public String resendOTP(String mobNo) throws Exception;

}
