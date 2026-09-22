package com.iemr.flw.controller;

import com.iemr.flw.dto.iemr.BadgeEarnedPushDTO;
import com.iemr.flw.service.BadgeService;
import com.iemr.flw.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Badges API consumed by the SAKHI app's BadgeSyncWorker:
 *   GET  /badges/config   -> { "config": { key: value } }
 *   GET  /badges/freezes  -> { "freezes": [ { badgeId, startDate, endDate } ] }
 *   GET  /badges/earned   -> { "earned":  [ { badgeId, level, earnedAt } ] }
 *   POST /badges/earned   <- { userId, badges: [ { badgeId, level, earnedAt } ] }
 * The ASHA is always identified from the JWT, never from the payload.
 */
@RestController
@RequestMapping(value = "/badges", produces = "application/json")
public class BadgeController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
    private final BadgeService badgeService;
    private final JwtUtil jwtUtil;

    public BadgeController(BadgeService badgeService, JwtUtil jwtUtil) {
        this.badgeService = badgeService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/config")
    public ResponseEntity<Map<String, Object>> getConfig() {
        Map<String, Object> response = ok();
        try {
            response.put("config", badgeService.getConfig());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return error(e);
        }
    }

    @GetMapping("/freezes")
    public ResponseEntity<Map<String, Object>> getFreezes(
            @RequestHeader(value = "JwtToken") String jwtToken) {
        Map<String, Object> response = ok();
        try {
            Integer userId = jwtUtil.extractUserId(jwtToken);
            response.put("freezes", badgeService.getFreezes(userId));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return error(e);
        }
    }

    @GetMapping("/earned")
    public ResponseEntity<Map<String, Object>> getEarned(
            @RequestHeader(value = "JwtToken") String jwtToken) {
        Map<String, Object> response = ok();
        try {
            Integer userId = jwtUtil.extractUserId(jwtToken);
            response.put("earned", badgeService.getEarned(userId));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return error(e);
        }
    }

    @PostMapping(value = "/earned", consumes = "application/json")
    public ResponseEntity<Map<String, Object>> postEarned(
            @RequestHeader(value = "JwtToken") String jwtToken,
            @RequestBody BadgeEarnedPushDTO body) {
        Map<String, Object> response = ok();
        try {
            Integer userId = jwtUtil.extractUserId(jwtToken);
            int inserted = badgeService.saveEarned(userId, body == null ? null : body.getBadges());
            response.put("inserted", inserted);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return error(e);
        }
    }

    private Map<String, Object> ok() {
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", 200);
        response.put("status", "Success");
        return response;
    }

    private ResponseEntity<Map<String, Object>> error(Exception e) {
        logger.error("Badges API error:", e);
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", 500);
        response.put("status", "Error");
        response.put("errorMessage", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
