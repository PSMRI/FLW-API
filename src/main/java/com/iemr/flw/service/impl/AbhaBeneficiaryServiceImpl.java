package com.iemr.flw.service.impl;

import com.iemr.flw.dto.abhaBeneficiary.AbhaBeneficiaryDTO;
import com.iemr.flw.dto.iemr.AbhaRequestDTO;
import com.iemr.flw.service.AbhaBeneficiaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class AbhaBeneficiaryServiceImpl implements AbhaBeneficiaryService {

    @Autowired
    private RestTemplate restTemplate;
    @Override
    public List<AbhaBeneficiaryDTO> getBeneficiaryByAbha(AbhaRequestDTO request) {
        return getAbhaResponse(request.getRequestId()).getBody();
    }

   ResponseEntity<List<AbhaBeneficiaryDTO>> getAbhaResponse(String  requestId) {
        String url = "" + requestId;
        ResponseEntity<List<AbhaBeneficiaryDTO>> response = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<AbhaBeneficiaryDTO>>() {
        });
      return  response;

    }

}
