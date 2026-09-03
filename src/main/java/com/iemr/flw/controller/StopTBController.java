package com.iemr.flw.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iemr.flw.service.StopTBService;
import com.iemr.flw.utils.response.OutputResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/stoptb")
public class StopTBController {

    private final Logger logger = LoggerFactory.getLogger(StopTBController.class);

    // serializeNulls() so fields with no value (e.g. hivStatus, keyPopulationRiskFactor*)
    // still appear in the response as null instead of being silently dropped
    private final Gson gson = new GsonBuilder().serializeNulls().create();

    @Autowired
    private StopTBService stopTBService;

    // ── Nurse: General Examination ────────────────────────────────────────────

    @PostMapping("/nurse/generalExamination/save")
    @Operation(summary = "Save general examination for Stop TB beneficiary (array of objects)")
    public String saveGeneralExamination(@RequestBody List<Map<String, Object>> dataList) {
        OutputResponse response = new OutputResponse();
        try {
            List<Map<String, Object>> result = stopTBService.saveGeneralExamination(dataList);
            response.setResponse(gson.toJson(result));
        } catch (Exception e) {
            logger.error("Error in saveGeneralExamination: " + e);
            response.setError(5000, "Error saving general examination: " + e.getMessage());
        }
        return response.toString();
    }

    @PostMapping("/nurse/generalExamination/getAll")
    @Operation(summary = "Get all general examinations for a camp")
    public String getAllGeneralExaminations(@RequestBody Map<String, Object> body) {
        OutputResponse response = new OutputResponse();
        try {
            Object raw = body.get("providerServiceMapID");
            if (raw == null) throw new Exception("providerServiceMapID is required");
            Integer villageID = body.get("villageID") != null ? Integer.parseInt(body.get("villageID").toString()) : null;
            Map<String, Object> data = stopTBService.getAllGeneralExaminations(Integer.parseInt(raw.toString()), villageID);
            response.setResponse(gson.toJson(data));
        } catch (Exception e) {
            logger.error("Error in getAllGeneralExaminations: " + e);
            response.setError(5000, "Error fetching general examinations: " + e.getMessage());
        }
        return response.toString();
    }

    // ── Nurse: TB Screening ───────────────────────────────────────────────────

    @PostMapping("/nurse/tbScreening/save")
    @Operation(summary = "Save TB screening for Stop TB beneficiary (array of objects)")
    public String saveNurseTBScreening(@RequestBody List<Map<String, Object>> dataList) {
        OutputResponse response = new OutputResponse();
        try {
            List<Map<String, Object>> result = stopTBService.saveNurseTBScreening(dataList);
            response.setResponse(gson.toJson(result));
        } catch (Exception e) {
            logger.error("Error in saveNurseTBScreening: " + e);
            response.setError(5000, "Error saving TB screening: " + e.getMessage());
        }
        return response.toString();
    }

    @PostMapping("/nurse/tbScreening/getAll")
    @Operation(summary = "Get all TB screenings for a camp")
    public String getAllNurseTBScreenings(@RequestBody Map<String, Object> body) {
        OutputResponse response = new OutputResponse();
        try {
            Object raw = body.get("providerServiceMapID");
            if (raw == null) throw new Exception("providerServiceMapID is required");
            Integer villageID = body.get("villageID") != null ? Integer.parseInt(body.get("villageID").toString()) : null;
            Map<String, Object> data = stopTBService.getAllNurseTBScreenings(Integer.parseInt(raw.toString()), villageID);
            response.setResponse(gson.toJson(data));
        } catch (Exception e) {
            logger.error("Error in getAllNurseTBScreenings: " + e);
            response.setError(5000, "Error fetching TB screenings: " + e.getMessage());
        }
        return response.toString();
    }

    // ── Nurse: General OPD ────────────────────────────────────────────────────

    @PostMapping("/nurse/generalOpd/save")
    @Operation(summary = "Save general OPD record for Stop TB beneficiary (array of objects)")
    public String saveGeneralOpd(@RequestBody List<Map<String, Object>> dataList) {
        OutputResponse response = new OutputResponse();
        try {
            List<Map<String, Object>> result = stopTBService.saveGeneralOpd(dataList);
            response.setResponse(gson.toJson(result));
        } catch (Exception e) {
            logger.error("Error in saveGeneralOpd: " + e);
            response.setError(5000, "Error saving general OPD: " + e.getMessage());
        }
        return response.toString();
    }

    @PostMapping("/nurse/generalOpd/getAll")
    @Operation(summary = "Get all general OPD records for a camp")
    public String getAllGeneralOpd(@RequestBody Map<String, Object> body) {
        OutputResponse response = new OutputResponse();
        try {
            Object raw = body.get("providerServiceMapID");
            if (raw == null) throw new Exception("providerServiceMapID is required");
            Integer villageID = body.get("villageID") != null ? Integer.parseInt(body.get("villageID").toString()) : null;
            Map<String, Object> data = stopTBService.getAllGeneralOpd(Integer.parseInt(raw.toString()), villageID);
            response.setResponse(gson.toJson(data));
        } catch (Exception e) {
            logger.error("Error in getAllGeneralOpd: " + e);
            response.setError(5000, "Error fetching general OPD records: " + e.getMessage());
        }
        return response.toString();
    }

    // ── Nurse: Diagnostics ───────────────────────────────────────────────────

    @PostMapping("/nurse/diagnostics/save")
    @Operation(summary = "Save diagnostics for Stop TB beneficiary (array of objects)")
    public String saveDiagnostics(@RequestBody List<Map<String, Object>> dataList) {
        OutputResponse response = new OutputResponse();
        try {
            List<Map<String, Object>> result = stopTBService.saveDiagnostics(dataList);
            response.setResponse(gson.toJson(result));
        } catch (Exception e) {
            logger.error("Error in saveDiagnostics: " + e);
            response.setError(5000, "Error saving diagnostics: " + e.getMessage());
        }
        return response.toString();
    }

    @PostMapping("/nurse/diagnostics/getAll")
    @Operation(summary = "Get all diagnostics records for a camp")
    public String getAllDiagnostics(@RequestBody Map<String, Object> body) {
        OutputResponse response = new OutputResponse();
        try {
            Object raw = body.get("providerServiceMapID");
            if (raw == null) throw new Exception("providerServiceMapID is required");
            Integer villageID = body.get("villageID") != null ? Integer.parseInt(body.get("villageID").toString()) : null;
            Map<String, Object> data = stopTBService.getAllDiagnostics(Integer.parseInt(raw.toString()), villageID);
            response.setResponse(gson.toJson(data));
        } catch (Exception e) {
            logger.error("Error in getAllDiagnostics: " + e);
            response.setError(5000, "Error fetching diagnostics records: " + e.getMessage());
        }
        return response.toString();
    }

}
