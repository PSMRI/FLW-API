package com.iemr.flw.controller;

import com.iemr.flw.dto.iemr.StopTBRegistrationDto;
import com.iemr.flw.service.StopTBService;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/stoptb")
public class StopTBController {

    private final Logger logger = LoggerFactory.getLogger(StopTBController.class);

    @Autowired
    private StopTBService stopTBService;

    @PostMapping("/registration/save")
    @Operation(summary = "Save Stop TB beneficiary registration")
    public ResponseEntity<Map<String, Object>> saveRegistration(
            @RequestBody String requestBody,
            @RequestHeader("JwtToken") String jwtToken) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            Map<String, Object> data = stopTBService.saveRegistration(requestBody, jwtToken);
            response.put("data", data);
            response.put("statusCode", 200);
            response.put("status", "Success");
        } catch (Exception e) {
            logger.error("Error in saveRegistration: " + e);
            response.put("statusCode", 5000);
            response.put("errorMessage", "Error in registration: " + e.getMessage());
            response.put("status", "FAILURE");
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/registration/get")
    @Operation(summary = "Get Stop TB beneficiary registration details")
    public ResponseEntity<Map<String, Object>> getRegistration(@RequestBody StopTBRegistrationDto dto) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            Map<String, Object> data = stopTBService.getRegistration(dto);
            response.put("data", data);
            response.put("statusCode", 200);
            response.put("status", "Success");
        } catch (Exception e) {
            logger.error("Error in getRegistration: " + e);
            response.put("statusCode", 5000);
            response.put("errorMessage", "Error fetching registration: " + e.getMessage());
            response.put("status", "FAILURE");
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/beneficiary/getDetails")
    @Operation(summary = "Get full beneficiary details with Stop TB fields as individual objects")
    public ResponseEntity<Map<String, Object>> getBeneficiaryDetails(
            @RequestBody Map<String, Object> body,
            @RequestHeader("JwtToken") String jwtToken) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            Object raw = body.get("beneficiaryRegID");
            if (raw == null) throw new Exception("beneficiaryRegID is required");
            Long beneficiaryRegID = Long.parseLong(raw.toString());
            Map<String, Object> data = stopTBService.getBeneficiaryDetails(beneficiaryRegID, jwtToken);
            response.put("data", data);
            response.put("statusCode", 200);
            response.put("status", "Success");
        } catch (Exception e) {
            logger.error("Error in getBeneficiaryDetails: " + e);
            response.put("statusCode", 5000);
            response.put("errorMessage", "Error fetching beneficiary details: " + e.getMessage());
            response.put("status", "FAILURE");
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/registrar/worklist")
    @Operation(summary = "Get registrar worklist for Stop TB")
    public ResponseEntity<Map<String, Object>> getRegistrarWorklist(@RequestBody StopTBRegistrationDto dto) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            Map<String, Object> data = stopTBService.getRegistrarWorklist(dto);
            response.put("data", data);
            response.put("statusCode", 200);
            response.put("status", "Success");
        } catch (Exception e) {
            logger.error("Error in getRegistrarWorklist: " + e);
            response.put("statusCode", 5000);
            response.put("errorMessage", "Error fetching worklist: " + e.getMessage());
            response.put("status", "FAILURE");
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/nurse/worklist")
    @Operation(summary = "Get nurse worklist for Stop TB")
    public ResponseEntity<Map<String, Object>> getNurseWorklist(@RequestBody StopTBRegistrationDto dto) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            Map<String, Object> data = stopTBService.getNurseWorklist(dto);
            response.put("data", data);
            response.put("statusCode", 200);
            response.put("status", "Success");
        } catch (Exception e) {
            logger.error("Error in getNurseWorklist: " + e);
            response.put("statusCode", 5000);
            response.put("errorMessage", "Error fetching nurse worklist: " + e.getMessage());
            response.put("status", "FAILURE");
        }
        return ResponseEntity.ok(response);
    }

    // ── Nurse: General Examination ────────────────────────────────────────────

    @PostMapping("/nurse/generalExamination/save")
    @Operation(summary = "Save general examination for Stop TB beneficiary")
    public ResponseEntity<Map<String, Object>> saveGeneralExamination(@RequestBody Map<String, Object> data) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            Map<String, Object> result = stopTBService.saveGeneralExamination(data);
            response.put("data", result);
            response.put("statusCode", 200);
            response.put("status", "Success");
        } catch (Exception e) {
            logger.error("Error in saveGeneralExamination: " + e);
            response.put("statusCode", 5000);
            response.put("errorMessage", "Error saving general examination: " + e.getMessage());
            response.put("status", "FAILURE");
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/nurse/generalExamination/get")
    @Operation(summary = "Get general examination for a Stop TB beneficiary")
    public ResponseEntity<Map<String, Object>> getGeneralExamination(@RequestBody Map<String, Object> body) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            Object raw = body.get("benRegID");
            if (raw == null) throw new Exception("benRegID is required");
            Long benRegID = Long.parseLong(raw.toString());
            Map<String, Object> data = stopTBService.getGeneralExamination(benRegID);
            response.put("data", data);
            response.put("statusCode", 200);
            response.put("status", "Success");
        } catch (Exception e) {
            logger.error("Error in getGeneralExamination: " + e);
            response.put("statusCode", 5000);
            response.put("errorMessage", "Error fetching general examination: " + e.getMessage());
            response.put("status", "FAILURE");
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/nurse/generalExamination/getAll")
    @Operation(summary = "Get all general examinations at a camp")
    public ResponseEntity<Map<String, Object>> getAllGeneralExaminations(@RequestBody Map<String, Object> body) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            Object raw = body.get("providerServiceMapID");
            if (raw == null) throw new Exception("providerServiceMapID is required");
            Integer psmId = Integer.parseInt(raw.toString());
            Map<String, Object> data = stopTBService.getAllGeneralExaminations(psmId);
            response.put("data", data);
            response.put("statusCode", 200);
            response.put("status", "Success");
        } catch (Exception e) {
            logger.error("Error in getAllGeneralExaminations: " + e);
            response.put("statusCode", 5000);
            response.put("errorMessage", "Error fetching general examinations: " + e.getMessage());
            response.put("status", "FAILURE");
        }
        return ResponseEntity.ok(response);
    }

    // ── Nurse: TB Screening ───────────────────────────────────────────────────

    @PostMapping("/nurse/tbScreening/save")
    @Operation(summary = "Save TB screening for Stop TB beneficiary")
    public ResponseEntity<Map<String, Object>> saveNurseTBScreening(@RequestBody Map<String, Object> data) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            Map<String, Object> result = stopTBService.saveNurseTBScreening(data);
            response.put("data", result);
            response.put("statusCode", 200);
            response.put("status", "Success");
        } catch (Exception e) {
            logger.error("Error in saveNurseTBScreening: " + e);
            response.put("statusCode", 5000);
            response.put("errorMessage", "Error saving TB screening: " + e.getMessage());
            response.put("status", "FAILURE");
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/nurse/tbScreening/get")
    @Operation(summary = "Get TB screening for a Stop TB beneficiary")
    public ResponseEntity<Map<String, Object>> getNurseTBScreening(@RequestBody Map<String, Object> body) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            Object raw = body.get("benRegID");
            if (raw == null) throw new Exception("benRegID is required");
            Long benRegID = Long.parseLong(raw.toString());
            Map<String, Object> data = stopTBService.getNurseTBScreening(benRegID);
            response.put("data", data);
            response.put("statusCode", 200);
            response.put("status", "Success");
        } catch (Exception e) {
            logger.error("Error in getNurseTBScreening: " + e);
            response.put("statusCode", 5000);
            response.put("errorMessage", "Error fetching TB screening: " + e.getMessage());
            response.put("status", "FAILURE");
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/nurse/tbScreening/getAll")
    @Operation(summary = "Get all TB screenings at a camp")
    public ResponseEntity<Map<String, Object>> getAllNurseTBScreenings(@RequestBody Map<String, Object> body) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            Object raw = body.get("providerServiceMapID");
            if (raw == null) throw new Exception("providerServiceMapID is required");
            Integer psmId = Integer.parseInt(raw.toString());
            Map<String, Object> data = stopTBService.getAllNurseTBScreenings(psmId);
            response.put("data", data);
            response.put("statusCode", 200);
            response.put("status", "Success");
        } catch (Exception e) {
            logger.error("Error in getAllNurseTBScreenings: " + e);
            response.put("statusCode", 5000);
            response.put("errorMessage", "Error fetching TB screenings: " + e.getMessage());
            response.put("status", "FAILURE");
        }
        return ResponseEntity.ok(response);
    }

    // ── Nurse: General OPD ───────────────────────────────────────────────────

    @PostMapping("/nurse/generalOpd/save")
    @Operation(summary = "Save general OPD record for Stop TB beneficiary")
    public ResponseEntity<Map<String, Object>> saveGeneralOpd(@RequestBody Map<String, Object> data) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            Map<String, Object> result = stopTBService.saveGeneralOpd(data);
            response.put("data", result);
            response.put("statusCode", 200);
            response.put("status", "Success");
        } catch (Exception e) {
            logger.error("Error in saveGeneralOpd: " + e);
            response.put("statusCode", 5000);
            response.put("errorMessage", "Error saving general OPD: " + e.getMessage());
            response.put("status", "FAILURE");
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/nurse/generalOpd/get")
    @Operation(summary = "Get general OPD record for a Stop TB beneficiary")
    public ResponseEntity<Map<String, Object>> getGeneralOpd(@RequestBody Map<String, Object> body) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            Object raw = body.get("benRegID");
            if (raw == null) throw new Exception("benRegID is required");
            Long benRegID = Long.parseLong(raw.toString());
            Map<String, Object> data = stopTBService.getGeneralOpd(benRegID);
            response.put("data", data);
            response.put("statusCode", 200);
            response.put("status", "Success");
        } catch (Exception e) {
            logger.error("Error in getGeneralOpd: " + e);
            response.put("statusCode", 5000);
            response.put("errorMessage", "Error fetching general OPD: " + e.getMessage());
            response.put("status", "FAILURE");
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/nurse/generalOpd/getAll")
    @Operation(summary = "Get all general OPD records at a camp")
    public ResponseEntity<Map<String, Object>> getAllGeneralOpd(@RequestBody Map<String, Object> body) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            Object raw = body.get("providerServiceMapID");
            if (raw == null) throw new Exception("providerServiceMapID is required");
            Integer psmId = Integer.parseInt(raw.toString());
            Map<String, Object> data = stopTBService.getAllGeneralOpd(psmId);
            response.put("data", data);
            response.put("statusCode", 200);
            response.put("status", "Success");
        } catch (Exception e) {
            logger.error("Error in getAllGeneralOpd: " + e);
            response.put("statusCode", 5000);
            response.put("errorMessage", "Error fetching general OPD records: " + e.getMessage());
            response.put("status", "FAILURE");
        }
        return ResponseEntity.ok(response);
    }

    // ── Nurse: Submit ─────────────────────────────────────────────────────────

    @PostMapping("/nurse/submit")
    @Operation(summary = "Submit nurse data — mark nurse done for a Stop TB beneficiary")
    public ResponseEntity<Map<String, Object>> submitNurseData(@RequestBody Map<String, Object> data) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            Map<String, Object> result = stopTBService.submitNurseData(data);
            response.put("data", result);
            response.put("statusCode", 200);
            response.put("status", "Success");
        } catch (Exception e) {
            logger.error("Error in submitNurseData: " + e);
            response.put("statusCode", 5000);
            response.put("errorMessage", "Error submitting nurse data: " + e.getMessage());
            response.put("status", "FAILURE");
        }
        return ResponseEntity.ok(response);
    }
}
