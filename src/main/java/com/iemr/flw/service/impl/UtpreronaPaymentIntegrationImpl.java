package com.iemr.flw.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iemr.flw.dto.PaymentResponse;
import com.iemr.flw.dto.iemr.PaymentRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Service
public class UtpreronaPaymentIntegrationImpl {

    private String API_URL =
            "https://nhmssd.assam.gov.in/APPMS_2024_25/api/utpreronaPayment.php";

    private String API_KEY =
            "YOUR_API_KEY";

    private static final int TIMEOUT_SECONDS = 30;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public UtpreronaPaymentIntegrationImpl() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                .build();

        this.objectMapper = new ObjectMapper();
    }

    public PaymentResponse sendPaymentRequest(PaymentRequest paymentRequest)
            throws IOException, InterruptedException {

        String jsonBody =
                objectMapper.writeValueAsString(paymentRequest);

        System.out.println("Sending Request:");
        System.out.println(
                objectMapper.writerWithDefaultPrettyPrinter()
                        .writeValueAsString(paymentRequest)
        );

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("x-api-key", API_KEY)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> httpResponse =
                httpClient.send(
                        httpRequest,
                        HttpResponse.BodyHandlers.ofString()
                );

        System.out.println(
                "Response Status: " + httpResponse.statusCode()
        );

        System.out.println(
                "Response Body: " + httpResponse.body()
        );

        if (httpResponse.statusCode() >= 200
                && httpResponse.statusCode() < 300) {

            PaymentResponse response =
                    objectMapper.readValue(
                            httpResponse.body(),
                            PaymentResponse.class
                    );

            System.out.println(
                    "Submission ID: " + response.getSubmissionId()
            );

            System.out.println(
                    "Status: " + response.getStatus()
            );

            System.out.println(
                    "Receipt: " + response.getReceipt()
            );

            return response;

        } else {

            throw new IOException(
                    "API Error - Status: "
                            + httpResponse.statusCode()
                            + " | Body: "
                            + httpResponse.body()
            );
        }
    }
}