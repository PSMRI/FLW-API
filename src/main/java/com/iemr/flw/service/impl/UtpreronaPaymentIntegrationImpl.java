package com.iemr.flw.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iemr.flw.dto.iemr.PaymentRequest;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Service
public class UtpreronaPaymentIntegrationImpl {
//    @Value("${ssdPortalUrl}")
    private  String API_URL = "";
    private static final int TIMEOUT_SECONDS = 30;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public UtpreronaPaymentIntegrationImpl() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public ApiResponse sendPaymentRequest(PaymentRequest paymentRequest)
            throws IOException, InterruptedException {

        String jsonBody = objectMapper.writeValueAsString(paymentRequest);
        System.out.println("📤 Sending Request:");
        System.out.println(objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(paymentRequest));

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> httpResponse = httpClient.send(
                httpRequest,
                HttpResponse.BodyHandlers.ofString()
        );

        System.out.println("Response Status: " + httpResponse.statusCode());
        System.out.println("Response Body: " + httpResponse.body());

        if (httpResponse.statusCode() == 200) {
            return objectMapper.readValue(httpResponse.body(), ApiResponse.class);
        } else {
            throw new IOException("API Error - Status: " + httpResponse.statusCode()
                    + " | Body: " + httpResponse.body());
        }
    }

}
