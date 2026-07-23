package com.iemr.flw.service;

import java.util.List;
import java.util.Map;

public interface StopTBService {
    // Nurse — General Examination
    List<Map<String, Object>> saveGeneralExamination(List<Map<String, Object>> dataList) throws Exception;
    Map<String, Object> getAllGeneralExaminations(Integer providerServiceMapID, Integer villageID) throws Exception;

    // Nurse — TB Screening
    List<Map<String, Object>> saveNurseTBScreening(List<Map<String, Object>> dataList) throws Exception;
    Map<String, Object> getAllNurseTBScreenings(Integer providerServiceMapID, Integer villageID) throws Exception;

    // Nurse — General OPD
    List<Map<String, Object>> saveGeneralOpd(List<Map<String, Object>> dataList) throws Exception;
    Map<String, Object> getAllGeneralOpd(Integer providerServiceMapID, Integer villageID) throws Exception;

    // Nurse — Diagnostics
    List<Map<String, Object>> saveDiagnostics(List<Map<String, Object>> dataList) throws Exception;
    Map<String, Object> getAllDiagnostics(Integer providerServiceMapID, Integer villageID) throws Exception;
}
