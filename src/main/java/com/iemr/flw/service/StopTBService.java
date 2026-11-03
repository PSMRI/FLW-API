package com.iemr.flw.service;

import java.util.Collection;
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

    // The same records the getAll endpoints return, for specific rows (STOP-TB changes feed), keyed by
    // the row id. Deleted rows, and rows getAll would not return, are left out.
    Map<Long, Map<String, Object>> getNurseTBScreeningsByIds(Collection<Long> ids);
    Map<Long, Map<String, Object>> getGeneralExaminationsByIds(Collection<Long> ids);
    Map<Long, Map<String, Object>> getDiagnosticsByIds(Collection<Long> ids);
    // Standard-table OPD visits, keyed by BenVisitID
    Map<Long, Map<String, Object>> getGeneralOpdByVisitIds(Collection<Long> benVisitIds);
    // Older free-text OPD rows (tb_stoptb_general_opd), keyed by their id; left out when the same visit
    // also exists in the standard tables, as getAllGeneralOpd does
    Map<Long, Map<String, Object>> getLegacyGeneralOpdByIds(Collection<Long> ids);
}
