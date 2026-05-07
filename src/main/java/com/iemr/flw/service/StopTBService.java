package com.iemr.flw.service;

import com.iemr.flw.dto.iemr.StopTBRegistrationDto;

import java.util.Map;

public interface StopTBService {
    // Registrar
    Map<String, Object> saveRegistration(String requestBody, String authorization) throws Exception;
    Map<String, Object> getRegistration(StopTBRegistrationDto dto) throws Exception;
    Map<String, Object> getRegistrarWorklist(StopTBRegistrationDto dto) throws Exception;

    // Nurse — worklist
    Map<String, Object> getNurseWorklist(StopTBRegistrationDto dto) throws Exception;

    // Nurse — General Examination
    Map<String, Object> saveGeneralExamination(Map<String, Object> data) throws Exception;
    Map<String, Object> getGeneralExamination(Long benRegID) throws Exception;
    Map<String, Object> getAllGeneralExaminations(Integer providerServiceMapID) throws Exception;

    // Nurse — TB Screening
    Map<String, Object> saveNurseTBScreening(Map<String, Object> data) throws Exception;
    Map<String, Object> getNurseTBScreening(Long benRegID) throws Exception;
    Map<String, Object> getAllNurseTBScreenings(Integer providerServiceMapID) throws Exception;

    // Nurse — General OPD
    Map<String, Object> saveGeneralOpd(Map<String, Object> data) throws Exception;
    Map<String, Object> getGeneralOpd(Long benRegID) throws Exception;
    Map<String, Object> getAllGeneralOpd(Integer providerServiceMapID) throws Exception;

    // Nurse — submit (mark nurse done, transition flow)
    Map<String, Object> submitNurseData(Map<String, Object> data) throws Exception;
}
