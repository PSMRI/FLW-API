package com.iemr.flw.service;

import com.iemr.flw.domain.iemr.OtpBeneficiary;
import com.iemr.flw.dto.iemr.OTPRequestParsor;
import com.iemr.flw.dto.iemr.OtpRequestDTO;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

public interface OTPHandler {
    public String sendOTP(String mobNo,String auth) throws Exception;

    public String validateOTP(OTPRequestParsor obj,String auth) throws Exception;

    public String resendOTP(String mobNo,String auth) throws Exception;

    JSONObject saveBenficiary(OtpRequestDTO requestOBJ);

    public String sendSMS(String request, String Authorization);

}
