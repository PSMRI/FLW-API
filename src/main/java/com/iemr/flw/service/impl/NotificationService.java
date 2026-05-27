package com.iemr.flw.service.impl;

import com.google.gson.Gson;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.Map;

@Service
public class NotificationService {
    final Logger logger = LoggerFactory.getLogger(this.getClass().getName());


    private String NOTIFICATION_URL = "https://uatamrit.piramalswasthya.org/common-api/firebaseNotification/sendNotification";

    public String sendNotification(String appType, String topic, String title, String body, String redirect) {
        String authHeader = null;
        String jwtToken = null;

        // Check if we have HTTP request context
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest httpServletRequest = attributes.getRequest();

            authHeader = httpServletRequest.getHeader("Authorization");

            if (httpServletRequest.getCookies() != null) {
                for (Cookie cookie : httpServletRequest.getCookies()) {
                    if ("Jwttoken".equals(cookie.getName())) {
                        jwtToken = cookie.getValue();
                    }
                }
            }
        }

        // If no request context, set default (for scheduler/startup use)
        if (authHeader == null) {
            authHeader = "Bearer DEFAULT_TOKEN_IF_REQUIRED"; // or leave null if API allows
        }
        if (jwtToken == null) {
            jwtToken = "DEFAULT_JWT_IF_REQUIRED";
        }

        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/json");
//        headers.add("AUTHORIZATION", authHeader);
//        headers.add("Cookie", "Jwttoken=" + jwtToken);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("appType", appType);
        requestBody.put("token", topic);
        requestBody.put("title", title);
        requestBody.put("body", body);

        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("NotificationTypeId", redirect);
        requestBody.put("data", dataMap);

        String jsonRequest = new Gson().toJson(requestBody);

        HttpEntity<Object> request = new HttpEntity<>(jsonRequest, headers);

        ResponseEntity<String> response = restTemplate.exchange(NOTIFICATION_URL, HttpMethod.POST, request, String.class);

        return response.getBody();
    }
}
