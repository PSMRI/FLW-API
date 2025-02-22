package com.iemr.flw.service.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.primitives.Ints;
import com.iemr.flw.domain.iemr.OtpBeneficiary;
import com.iemr.flw.dto.iemr.OTPRequestParsor;
import com.iemr.flw.repo.iemr.OtpBeneficiaryRepository;
import com.iemr.flw.service.OTPHandler;
import com.iemr.flw.utils.config.ConfigProperties;
import com.iemr.flw.utils.http.HttpUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class OTPHandlerServiceImpl implements OTPHandler {
    @Autowired
    HttpUtils httpUtils;

    @Autowired
    OtpBeneficiaryRepository otpBeneficiaryRepository;
    final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    private LoadingCache<String, String> otpCache;

    private static final Integer EXPIRE_MIN = 5;

    private static final String SMS_GATEWAY_URL = ConfigProperties.getPropertyByName("sms-gateway-url");


    // Constructor for new object creation
    public OTPHandlerServiceImpl() {
        otpCache = CacheBuilder.newBuilder().expireAfterWrite(EXPIRE_MIN, TimeUnit.MINUTES)
                .build(new CacheLoader<String, String>() {
                    public String load(String key) {
                        return "0";
                    }
                });
    }

    /***
     * @param
     * @return success if OTP sent successfully
     */
    @Override
    public String sendOTP(String  mobNo) throws Exception {
        System.out.println("mobNo:"+mobNo);
        int otp = generateOTP(mobNo);
        sendSMS(otp, mobNo, "OTP is ");
        saveOtp(mobNo,otp);
        return "success";
    }

    /***
     * @param obj
     * @return OTP verification success or failure
     *
     */
    @Override
    public JSONObject validateOTP(OTPRequestParsor obj) throws Exception {
        String cachedOTP = otpCache.get(obj.getMobNo());
        String inputOTPEncrypted = getEncryptedOTP(obj.getOtp());
        System.out.println(cachedOTP.toString() +" "+inputOTPEncrypted.toString());

        if (cachedOTP.equalsIgnoreCase(inputOTPEncrypted)) {
            JSONObject responseObj = new JSONObject();
            responseObj.put("userName", obj.getMobNo());
            responseObj.put("userID", obj.getMobNo());
            verifyOtp(obj.getMobNo(),obj.getOtp(),null);



            return responseObj;
        } else {
            throw new Exception("Please enter valid OTP");
        }

    }
    public String verifyOtp(String phoneNumber, Integer otp,Long otpBeneficiaryId) {
        Optional<OtpBeneficiary> otpEntry = otpBeneficiaryRepository.findByPhoneNumberAndOtp(phoneNumber, otp);

        if (otpEntry.isPresent()) {
            OtpBeneficiary otpBeneficiary = otpEntry.get();
            otpBeneficiary.setBeneficiaryId(otpBeneficiaryId);
            otpBeneficiary.setIsOtpVerify(true);
            otpBeneficiary.setIsExpired(true);
            otpBeneficiaryRepository.save(otpBeneficiary);
            return "OTP verified successfully.";
        } else {
            return "Invalid or expired OTP.";
        }
    }


    /***
     * @param
     * @return success if OTP re-sent successfully
     */
    @Override
    public String resendOTP(String mobNo) throws Exception {
        int otp = generateOTP(mobNo);
        sendSMS(otp, mobNo, "OTP is ");
        saveOtp(mobNo,otp);

        return "success";
    }
    private void saveOtp(String phoneNo,Integer otp){
        OtpBeneficiary otpEntry = new OtpBeneficiary();
        otpEntry.setBeneficiaryId(null);
        otpEntry.setPhoneNumber(phoneNo);
        otpEntry.setOtp(otp);
        otpEntry.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        otpBeneficiaryRepository.save(otpEntry);
    }

    // generate 6 digit random no #
    public int generateOTP(String authKey) throws Exception {
        String generatedPassword = null;

//		Random random = new Random();
        Random random = SecureRandom.getInstanceStrong();
        int otp = 100000 + random.nextInt(900000);

        generatedPassword = getEncryptedOTP(otp);

        if (otpCache != null)
            otpCache.put(authKey, generatedPassword);
        else {
            OTPHandlerServiceImpl obj = new OTPHandlerServiceImpl();
            obj.otpCache.put(authKey, generatedPassword);
        }
        return otp;
    }

    // SHA-256 encoding logic implemented
    private String getEncryptedOTP(int otp) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] bytes = md.digest(Ints.toByteArray(otp));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }

    // send SMS to user
    private void sendSMS(int otp, String phoneNo, String msgText) throws Exception {
        String sendSMSURL = ConfigProperties.getPropertyByName("send-message-url");
        String sendSMSAPI = SMS_GATEWAY_URL + "/" + sendSMSURL;
        String senderName = ConfigProperties.getPropertyByName("sms-username");
        String senderPassword = ConfigProperties.getPropertyByName("sms-password");
        String senderNumber = ConfigProperties.getPropertyByName("sms-sender-number");
        System.out.println("OTP"+otp+"Phone"+phoneNo+"SMSURL"+sendSMSAPI+"sendName"+senderName+"senderPassword"+senderPassword+"senderNumber"+senderNumber);


        sendSMSAPI = sendSMSAPI.replace("USERNAME", senderName).replace("PASSWORD", senderPassword)
                .replace("SENDER_NUMBER", senderNumber);

        sendSMSAPI = sendSMSAPI
                .replace("SMS_TEXT",
                        msgText.concat(String.valueOf(otp))
                                .concat(" for Tele-consultation verification and validity is 5 mins"))
                .replace("RECEIVER_NUMBER", phoneNo);

        ResponseEntity<String> response = httpUtils.getV1(sendSMSAPI);
        System.out.println("Otp Response"+response);
        if (response.getStatusCodeValue() == 200) {
            String smsResponse = response.getBody();
            // JSONObject obj = new JSONObject(smsResponse);
            // String jobID = obj.getString("JobId");
            switch (smsResponse) {
                case "0x200 - Invalid Username or Password":
                case "0x201 - Account suspended due to one of several defined reasons":
                case "0x202 - Invalid Source Address/Sender ID. As per GSM standard, the sender ID should "
                        + "be within 11 characters":
                case "0x203 - Message length exceeded (more than 160 characters) if concat is set to 0":
                case "0x204 - Message length exceeded (more than 459 characters) in concat is set to 1":
                case "0x205 - DRL URL is not set":
                case "0x206 - Only the subscribed service type can be accessed – "
                        + "make sure of the service type you are trying to connect with":
                case "0x207 - Invalid Source IP – kindly check if the IP is responding":
                case "0x208 - Account deactivated/expired":
                case "0x209 - Invalid message length (less than 160 characters) if concat is set to 1":
                case "0x210 - Invalid Parameter values":
                case "0x211 - Invalid Message Length (more than 280 characters)":
                case "0x212 - Invalid Message Length":
                case "0x213 - Invalid Destination Number":
                    throw new Exception(smsResponse);
                default:
                    logger.info("SMS Sent successfully by calling API " + sendSMSAPI);
                    logger.info("SMS Sent successfully sent to : " + phoneNo);
                    break;
            }
        } else {
            throw new Exception(response.getStatusCodeValue() + " and error " + response.getStatusCode().toString());
        }
    }
}
