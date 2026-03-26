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

    private static String ABHA_AUTH_TOKEN;
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
    public synchronized String getAbhaToken() throws Exception {
        try {
            if (ABHA_AUTH_TOKEN == null || ABHA_TOKEN_EXP == null
                    || ABHA_TOKEN_EXP < System.currentTimeMillis()) {
                generateAbhaToken();
                logger.info("ABHA token generated successfully");
            }
        } catch (Exception e) {
            logger.error("Error generating ABHA token: " + e.getMessage());
            throw new Exception("Failed to generate ABHA token: " + e.getMessage());
        }
        return ABHA_AUTH_TOKEN;
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
            ABHA_AUTH_TOKEN = jsonResponse.get("accessToken").getAsString();
            Integer expiresIn = jsonResponse.get("expiresIn").getAsInt();

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.SECOND, expiresIn);
            ABHA_TOKEN_EXP = calendar.getTimeInMillis();
        } else {
            throw new Exception("Empty response from ABDM token API");
        }
    }
}
