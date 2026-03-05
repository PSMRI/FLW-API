package com.iemr.flw.dbchecker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/schema")
public class SchemaCheckController {

    @Autowired
    private SchemaCheckService schemaCheckService;

    /**
     * GET /api/schema/check
     *
     * Response — DB ke hisaab se grouped:
     * {
     *   "status": "ISSUES_FOUND",
     *   "summary": "❌ 0 missing tables, 2 missing columns, 0 type mismatches",
     *   "missingTables": {
     *     "db_iemr":     ["t_new_table"],
     *     "db_identity": []
     *   },
     *   "missingColumns": {
     *     "db_iemr":     ["t_anc_visit.new_col", "asha_profile.some_field"],
     *     "db_identity": ["i_cbacdetails.missing_col"]
     *   },
     *   "typeMismatches": {
     *     "db_iemr": ["t_pmsma.mobile_number [expected=BIGINT, actual=VARCHAR]"]
     *   }
     * }
     */
    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkSchema() {
        SchemaCheckResult result = schemaCheckService.runFullSchemaCheck();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("checkedAt",      result.getCheckedAt().toString());
        response.put("status",         result.hasIssues() ? "ISSUES_FOUND" : "OK");
        response.put("summary",        result.getSummary());
        response.put("missingTables",  result.getMissingTablesByDb());   // grouped by DB
        response.put("missingColumns", result.getMissingColumnsByDb());  // grouped by DB
        response.put("typeMismatches", result.getTypeMismatchesByDb());  // grouped by DB
        response.put("passedChecks",   result.getPassedChecks().size() + " tables OK");

        return ResponseEntity.status(result.hasIssues() ? 409 : 200).body(response);
    }

    /**
     * GET /api/schema/health
     * CI/CD ke liye — 200 OK ya 409 CONFLICT
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> schemaHealth() {
        SchemaCheckResult result = schemaCheckService.runFullSchemaCheck();
        return ResponseEntity.status(result.hasIssues() ? 409 : 200).body(Map.of(
                "status",  result.hasIssues() ? "SCHEMA_MISMATCH" : "HEALTHY",
                "summary", result.getSummary()
        ));
    }
}