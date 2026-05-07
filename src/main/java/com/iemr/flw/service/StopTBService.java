package com.iemr.flw.service;

import com.iemr.flw.dto.iemr.StopTBRegistrationDto;

import java.util.Map;

public interface StopTBService {
    Map<String, Object> saveRegistration(String requestBody, String authorization) throws Exception;
    Map<String, Object> getRegistration(StopTBRegistrationDto dto) throws Exception;
    Map<String, Object> getRegistrarWorklist(StopTBRegistrationDto dto) throws Exception;
    Map<String, Object> getNurseWorklist(StopTBRegistrationDto dto) throws Exception;
}
