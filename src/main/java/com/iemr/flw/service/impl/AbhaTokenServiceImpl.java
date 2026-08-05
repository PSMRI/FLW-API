package com.iemr.flw.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.iemr.flw.service.AbhaTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class AbhaTokenServiceImpl implements AbhaTokenService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    private static Map<String, Object> ABHA_TOKEN_RESPONSE;
    private static Long ABHA_TOKEN_EXP;

    @Value("${abha.client.id}")
    private String clientId;

    @Value("${abha.client.secret}")
    private String clientSecret;

    @Value("${abha.token.url}")
    private String abhaTokenUrl;

    @Value("${abha.xcmid:sbx}")
    private String xCmId;

    @Override
    public synchronized Map<String, Object> getAbhaToken() throws Exception {
        try {
            if (ABHA_TOKEN_RESPONSE == null || ABHA_TOKEN_EXP == null
                    || ABHA_TOKEN_EXP < System.currentTimeMillis()) {
                generateAbhaToken();
                logger.info("ABHA token generated successfully");
            }
        } catch (Exception e) {
            logger.error("Error generating ABHA token: " + e.getMessage());
            throw new Exception("Failed to generate ABHA token: " + e.getMessage());
        }
        return ABHA_TOKEN_RESPONSE;
    }

    private void generateAbhaToken() throws Exception {
        RestTemplate restTemplate = new RestTemplate();

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("clientId", clientId);
        requestBody.put("clientSecret", clientSecret);
        requestBody.put("grantType", "client_credentials");

        String requestJson = new Gson().toJson(requestBody);

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8");
        headers.add("REQUEST-ID", UUID.randomUUID().toString());

        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone(tz);
        headers.add("TIMESTAMP", df.format(new Date()));
        headers.add("X-CM-ID", xCmId);

        HttpEntity<String> httpEntity = new HttpEntity<>(requestJson, headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                abhaTokenUrl, HttpMethod.POST, httpEntity, String.class);

        String responseBody = responseEntity.getBody();
        if (responseBody != null) {
            JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();

            Map<String, Object> tokenResponse = new HashMap<>();
            tokenResponse.put("accessToken", jsonResponse.get("accessToken").getAsString());
            tokenResponse.put("expiresIn", jsonResponse.get("expiresIn").getAsInt());
            tokenResponse.put("refreshExpiresIn", jsonResponse.get("refreshExpiresIn").getAsInt());
            tokenResponse.put("refreshToken", jsonResponse.get("refreshToken").getAsString());
            tokenResponse.put("tokenType", jsonResponse.get("tokenType").getAsString());

            Integer expiresIn = jsonResponse.get("expiresIn").getAsInt();
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.SECOND, expiresIn);
            ABHA_TOKEN_EXP = calendar.getTimeInMillis();

            ABHA_TOKEN_RESPONSE = tokenResponse;
        } else {
            throw new Exception("Empty response from ABDM token API");
        }
    }
}
