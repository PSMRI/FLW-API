package com.iemr.flw.service.impl;

import com.google.gson.Gson;
import com.iemr.flw.controller.AbhaBeneficiaryController;
import com.iemr.flw.domain.iemr.AbhaApiResponse;
import com.iemr.flw.dto.abhaBeneficiary.AbhaBeneficiaryDTO;
import com.iemr.flw.dto.iemr.AbhaRequestDTO;
import com.iemr.flw.repo.identity.BeneficiaryRepo;
import com.iemr.flw.service.AbhaBeneficiaryService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AbhaBeneficiaryServiceImpl implements AbhaBeneficiaryService {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(AbhaBeneficiaryService.class);


    @Autowired
    private BeneficiaryRepo beneficiaryRepo;

//    @Value("${govthealth.user.details.url}")
//     private String getUserDetailsUrl;
//
//    @Value("${govthealth.user.id}")
//    private String govthealthUserId;
//
//    @Value("${govthealth.password}")
//    private String govthealthPassword;

    @Override
    public Object getBeneficiaryByAbha(AbhaRequestDTO request) {

        try {

            AbhaApiResponse abhaApiResponse =
                    getAbhaResponse(request.getCardNo()).getBody();

            if (abhaApiResponse == null || abhaApiResponse.getData() == null) {

                Map<String, Object> response = new HashMap<>();
                response.put("statusCode", 5000);
                response.put("message", "No data found");

                return response;
            }

            for (AbhaBeneficiaryDTO dto : abhaApiResponse.getData()) {

                // Check if ABHA already exists in system
                List<Object[]> health =
                        beneficiaryRepo.getBenHealthDetails(dto.getAbhaId());

                if (health != null && !health.isEmpty()) {

                    for (Object[] objects : health) {

                        if (objects != null
                                && objects.length > 1
                                && dto.getAbhaId() != null
                                && dto.getAbhaId()
                                .equals(String.valueOf(objects[1]))) {

                            Map<String, Object> response = new HashMap<>();
                            response.put("statusCode", 5000);
                            response.put("message",
                                    "This ABHA No already exists");

                            return response;
                        }
                    }
                }

                // Split name into firstName and lastName
                String personName = dto.getPersonName();

                if (personName != null
                        && !personName.trim().isEmpty()) {

                    String[] names =
                            personName.trim().split("\\s+", 2);

                    dto.setFirstName(names[0]);

                    if (names.length > 1) {
                        dto.setLastName(names[1]);
                    } else {
                        dto.setLastName("");
                    }
                }
            }

            logger.info("ABHA Response Status : {}",
                    abhaApiResponse.getStatusCode());

            logger.info("ABHA Response : {}",
                    new Gson().toJson(abhaApiResponse));

            return abhaApiResponse;

        } catch (Exception e) {

            logger.error("Error while fetching beneficiary by ABHA", e);

            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", 500);
            response.put("message", "Internal Server Error");

            return response;
        }
    }

    public ResponseEntity<AbhaApiResponse> getAbhaResponse(String requestId) {
        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> body = new HashMap<>();
        body.put("userId","govthealthUserId");
        body.put("password", "govthealthPassword");
        body.put("cardNo", requestId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<?> request = new HttpEntity<>(body, headers);

        ResponseEntity<AbhaApiResponse> response = restTemplate.exchange(
                "getUserDetailsUrl",
                HttpMethod.POST,
                request,
                AbhaApiResponse.class
        );

        System.out.println("Status = " + response.getStatusCode());
        System.out.println("Body = " + response.getBody());

        return response;
    }

}
