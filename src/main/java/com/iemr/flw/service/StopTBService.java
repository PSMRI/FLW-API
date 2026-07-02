package com.iemr.flw.service;

import com.iemr.flw.dto.iemr.StopTBRegistrationDto;

import java.util.List;
import java.util.Map;

public interface StopTBService {
    // Registrar
    Map<String, Object> saveRegistration(String requestBody, String authorization) throws Exception;
    Map<String, Object> getRegistrarWorklist(StopTBRegistrationDto dto) throws Exception;

    // Nurse — worklist
    Map<String, Object> getNurseWorklist(StopTBRegistrationDto dto) throws Exception;

    // Nurse — General Examination
    List<Map<String, Object>> saveGeneralExamination(List<Map<String, Object>> dataList) throws Exception;
    Map<String, Object> getGeneralExamination(Long benRegID) throws Exception;
    Map<String, Object> getAllGeneralExaminations(Integer providerServiceMapID, Integer villageID) throws Exception;

    // Nurse — TB Screening
    List<Map<String, Object>> saveNurseTBScreening(List<Map<String, Object>> dataList) throws Exception;
    Map<String, Object> getNurseTBScreening(Long benRegID) throws Exception;
    Map<String, Object> getAllNurseTBScreenings(Integer providerServiceMapID, Integer villageID) throws Exception;

    // Nurse — General OPD
    List<Map<String, Object>> saveGeneralOpd(List<Map<String, Object>> dataList) throws Exception;
    Map<String, Object> getGeneralOpd(Long benRegID) throws Exception;
    Map<String, Object> getAllGeneralOpd(Integer providerServiceMapID, Integer villageID) throws Exception;

    // Nurse — Diagnostics
    List<Map<String, Object>> saveDiagnostics(List<Map<String, Object>> dataList) throws Exception;
    Map<String, Object> getDiagnostics(Long benRegID) throws Exception;
    Map<String, Object> getAllDiagnostics(Integer providerServiceMapID, Integer villageID) throws Exception;

    // Nurse — submit
    Map<String, Object> submitNurseData(Map<String, Object> data) throws Exception;

    // Master data
    List<Map<String, Object>> getChiefComplaintMaster() throws Exception;
    List<Map<String, Object>> getDrugMasterList() throws Exception;
}
