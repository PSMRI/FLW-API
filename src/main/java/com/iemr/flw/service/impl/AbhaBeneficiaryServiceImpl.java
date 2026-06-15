package com.iemr.flw.service.impl;

import com.iemr.flw.domain.iemr.AbhaApiResponse;
import com.iemr.flw.dto.abhaBeneficiary.AbhaBeneficiaryDTO;
import com.iemr.flw.dto.iemr.AbhaRequestDTO;
import com.iemr.flw.repo.identity.BeneficiaryRepo;
import com.iemr.flw.service.AbhaBeneficiaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AbhaBeneficiaryServiceImpl implements AbhaBeneficiaryService {

    @Autowired
    private BeneficiaryRepo beneficiaryRepo;

    @Value("${govthealth.user.details.url}")
     private String getUserDetailsUrl;

    @Value("${govthealth.user.id}")
    private String govthealthUserId;

    @Value("${govthealth.password}")
    private String govthealthPassword;

    @Override
    public Object getBeneficiaryByAbha(AbhaRequestDTO request) {

        List<Object[]> health = beneficiaryRepo.getBenHealthDetails(request.getCardNo());
        Map<String, Object> response = new HashMap<>();

        if (health != null && !health.isEmpty()) {
            for (Object[] objects : health) {
                if (request.getCardNo().equals(String.valueOf(objects[1]))) {

                    response.put("statusCode", 5000);
                    response.put("message", "This ABHA No already exists");

                    return response;
                }
            }
        }
        AbhaApiResponse abhaApiResponse = getAbhaResponse(request.getCardNo()).getBody();


        if (abhaApiResponse != null && abhaApiResponse.getData() != null) {

            for (AbhaBeneficiaryDTO dto : abhaApiResponse.getData()) {

                String personName = dto.getPersonName();

                if (personName != null && !personName.trim().isEmpty()) {

                    String[] names = personName.trim().split("\\s+", 2);

                    dto.setFirstName(names[0]);

                    if (names.length > 1) {
                        dto.setLastName(names[1]);
                    } else {
                        dto.setLastName("");
                    }
                }
            }
        }

        System.out.println("Status = " + abhaApiResponse.getStatusCode());
        System.out.println("Body = " + abhaApiResponse);

        return abhaApiResponse;
    }

    public ResponseEntity<AbhaApiResponse> getAbhaResponse(String requestId) {
        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> body = new HashMap<>();
        body.put("userId",govthealthUserId);
        body.put("password", govthealthPassword);
        body.put("cardNo", requestId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<?> request = new HttpEntity<>(body, headers);

        ResponseEntity<AbhaApiResponse> response = restTemplate.exchange(
                getUserDetailsUrl,
                HttpMethod.POST,
                request,
                AbhaApiResponse.class
        );

        System.out.println("Status = " + response.getStatusCode());
        System.out.println("Body = " + response.getBody());

        return response;
    }

}
