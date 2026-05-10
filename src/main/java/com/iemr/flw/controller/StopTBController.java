package com.iemr.flw.controller;

import com.google.gson.Gson;
import com.iemr.flw.dto.iemr.StopTBRegistrationDto;
import com.iemr.flw.service.StopTBService;
import com.iemr.flw.utils.response.OutputResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/stoptb")
public class StopTBController {

    private final Logger logger = LoggerFactory.getLogger(StopTBController.class);

    @Autowired
    private StopTBService stopTBService;

    // ── Registration ──────────────────────────────────────────────────────────

    @PostMapping("/registration/save")
    @Operation(summary = "Save Stop TB beneficiary registration")
    public String saveRegistration(
            @RequestBody String requestBody,
            @RequestHeader("Authorization") String authorization) {
        OutputResponse response = new OutputResponse();
        try {
            Map<String, Object> data = stopTBService.saveRegistration(requestBody, authorization);
            response.setResponse(new Gson().toJson(data));
        } catch (Exception e) {
            logger.error("Error in saveRegistration: " + e);
            response.setError(5000, "Error in registration: " + e.getMessage());
        }
        return response.toString();
    }

    // ── Worklists ─────────────────────────────────────────────────────────────

    @PostMapping("/registrar/worklist")
    @Operation(summary = "Get registrar worklist — full beneficiary details for Stop TB")
    public String getRegistrarWorklist(@RequestBody StopTBRegistrationDto dto) {
        OutputResponse response = new OutputResponse();
        try {
            Map<String, Object> data = stopTBService.getRegistrarWorklist(dto);
            response.setResponse(new Gson().toJson(data.get("data")));
        } catch (Exception e) {
            logger.error("Error in getRegistrarWorklist: " + e);
            response.setError(5000, "Error fetching worklist: " + e.getMessage());
        }
        return response.toString();
    }

    @PostMapping("/nurse/worklist")
    @Operation(summary = "Get nurse worklist — full beneficiary details + nurse module data")
    public String getNurseWorklist(@RequestBody StopTBRegistrationDto dto) {
        OutputResponse response = new OutputResponse();
        try {
            Map<String, Object> data = stopTBService.getNurseWorklist(dto);
            response.setResponse(new Gson().toJson(data.get("data")));
        } catch (Exception e) {
            logger.error("Error in getNurseWorklist: " + e);
            response.setError(5000, "Error fetching nurse worklist: " + e.getMessage());
        }
        return response.toString();
    }

    // ── Nurse: General Examination ────────────────────────────────────────────

    @PostMapping("/nurse/generalExamination/save")
    @Operation(summary = "Save general examination for Stop TB beneficiary")
    public String saveGeneralExamination(@RequestBody Map<String, Object> data) {
        OutputResponse response = new OutputResponse();
        try {
            Map<String, Object> result = stopTBService.saveGeneralExamination(data);
            response.setResponse(new Gson().toJson(result));
        } catch (Exception e) {
            logger.error("Error in saveGeneralExamination: " + e);
            response.setError(5000, "Error saving general examination: " + e.getMessage());
        }
        return response.toString();
    }

    @PostMapping("/nurse/generalExamination/get")
    @Operation(summary = "Get general examination for a Stop TB beneficiary")
    public String getGeneralExamination(@RequestBody Map<String, Object> body) {
        OutputResponse response = new OutputResponse();
        try {
            Object raw = body.get("benRegID");
            if (raw == null) throw new Exception("benRegID is required");
            Map<String, Object> data = stopTBService.getGeneralExamination(Long.parseLong(raw.toString()));
            response.setResponse(new Gson().toJson(data));
        } catch (Exception e) {
            logger.error("Error in getGeneralExamination: " + e);
            response.setError(5000, "Error fetching general examination: " + e.getMessage());
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
            Map<String, Object> data = stopTBService.getAllGeneralExaminations(Integer.parseInt(raw.toString()));
            response.setResponse(new Gson().toJson(data));
        } catch (Exception e) {
            logger.error("Error in getAllGeneralExaminations: " + e);
            response.setError(5000, "Error fetching general examinations: " + e.getMessage());
        }
        return response.toString();
    }

    // ── Nurse: TB Screening ───────────────────────────────────────────────────

    @PostMapping("/nurse/tbScreening/save")
    @Operation(summary = "Save TB screening for Stop TB beneficiary")
    public String saveNurseTBScreening(@RequestBody Map<String, Object> data) {
        OutputResponse response = new OutputResponse();
        try {
            Map<String, Object> result = stopTBService.saveNurseTBScreening(data);
            response.setResponse(new Gson().toJson(result));
        } catch (Exception e) {
            logger.error("Error in saveNurseTBScreening: " + e);
            response.setError(5000, "Error saving TB screening: " + e.getMessage());
        }
        return response.toString();
    }

    @PostMapping("/nurse/tbScreening/get")
    @Operation(summary = "Get TB screening for a Stop TB beneficiary")
    public String getNurseTBScreening(@RequestBody Map<String, Object> body) {
        OutputResponse response = new OutputResponse();
        try {
            Object raw = body.get("benRegID");
            if (raw == null) throw new Exception("benRegID is required");
            Map<String, Object> data = stopTBService.getNurseTBScreening(Long.parseLong(raw.toString()));
            response.setResponse(new Gson().toJson(data));
        } catch (Exception e) {
            logger.error("Error in getNurseTBScreening: " + e);
            response.setError(5000, "Error fetching TB screening: " + e.getMessage());
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
            Map<String, Object> data = stopTBService.getAllNurseTBScreenings(Integer.parseInt(raw.toString()));
            response.setResponse(new Gson().toJson(data));
        } catch (Exception e) {
            logger.error("Error in getAllNurseTBScreenings: " + e);
            response.setError(5000, "Error fetching TB screenings: " + e.getMessage());
        }
        return response.toString();
    }

    // ── Nurse: General OPD ────────────────────────────────────────────────────

    @PostMapping("/nurse/generalOpd/save")
    @Operation(summary = "Save general OPD record for Stop TB beneficiary")
    public String saveGeneralOpd(@RequestBody Map<String, Object> data) {
        OutputResponse response = new OutputResponse();
        try {
            Map<String, Object> result = stopTBService.saveGeneralOpd(data);
            response.setResponse(new Gson().toJson(result));
        } catch (Exception e) {
            logger.error("Error in saveGeneralOpd: " + e);
            response.setError(5000, "Error saving general OPD: " + e.getMessage());
        }
        return response.toString();
    }

    @PostMapping("/nurse/generalOpd/get")
    @Operation(summary = "Get general OPD record for a Stop TB beneficiary")
    public String getGeneralOpd(@RequestBody Map<String, Object> body) {
        OutputResponse response = new OutputResponse();
        try {
            Object raw = body.get("benRegID");
            if (raw == null) throw new Exception("benRegID is required");
            Map<String, Object> data = stopTBService.getGeneralOpd(Long.parseLong(raw.toString()));
            response.setResponse(new Gson().toJson(data));
        } catch (Exception e) {
            logger.error("Error in getGeneralOpd: " + e);
            response.setError(5000, "Error fetching general OPD: " + e.getMessage());
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
            Map<String, Object> data = stopTBService.getAllGeneralOpd(Integer.parseInt(raw.toString()));
            response.setResponse(new Gson().toJson(data));
        } catch (Exception e) {
            logger.error("Error in getAllGeneralOpd: " + e);
            response.setError(5000, "Error fetching general OPD records: " + e.getMessage());
        }
        return response.toString();
    }

    // ── Nurse: Submit ─────────────────────────────────────────────────────────

    @PostMapping("/nurse/submit")
    @Operation(summary = "Submit nurse data — mark nurse done for a Stop TB beneficiary")
    public String submitNurseData(@RequestBody Map<String, Object> data) {
        OutputResponse response = new OutputResponse();
        try {
            Map<String, Object> result = stopTBService.submitNurseData(data);
            response.setResponse(new Gson().toJson(result));
        } catch (Exception e) {
            logger.error("Error in submitNurseData: " + e);
            response.setError(5000, "Error submitting nurse data: " + e.getMessage());
        }
        return response.toString();
    }
}
