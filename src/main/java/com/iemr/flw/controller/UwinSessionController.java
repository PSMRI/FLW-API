package com.iemr.flw.controller;

import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.UwinSessionRequestDTO;
import com.iemr.flw.dto.iemr.UwinSessionResponseDTO;
import com.iemr.flw.service.UwinSessionService;
import com.iemr.flw.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/uwin/session")
public class UwinSessionController {


     @Autowired
    private  UwinSessionService service;

     @Autowired
     private JwtUtil jwtUtil;

    @RequestMapping(value = "saveAll", method = RequestMethod.POST, headers = "Authorization", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> saveSession(
            @RequestPart("meetingDate") String meetingDate,
            @RequestPart("place") String place,
            @RequestPart("participants") String participants,
            @RequestPart("ashaId") String ashaId,
            @RequestPart("createdBy") String createdBy,
            @RequestPart(value = "meetingImages", required = false) List<MultipartFile> images) throws Exception {
        Map<String, Object> response = new LinkedHashMap<>();

        UwinSessionRequestDTO dto = new UwinSessionRequestDTO();
        dto.setAshaId(Integer.valueOf(ashaId));
        dto.setDate(Timestamp.valueOf(meetingDate));
        dto.setPlace(place);
        dto.setParticipants(Integer.valueOf(participants));
        dto.setCreatedBy(createdBy);
        dto.setAttachments(images != null ? images.toArray(new MultipartFile[0]) : null);
        UwinSessionResponseDTO uwinSessionResponse = service.saveSession(dto);
        try {
            if (uwinSessionResponse != null) {
                response.put("statusCode", HttpStatus.OK.value());
                response.put("message", "Data saved successfully");
            } else {
                response.put("statusCode", HttpStatus.BAD_REQUEST.value());
                response.put("message", HttpStatus.BAD_REQUEST.getReasonPhrase());
            }

        } catch (Exception e) {
            response.put("statusCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("errorMessage", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "getAll", method = RequestMethod.POST,headers = "Authorization")
    public ResponseEntity<Map<String, Object>> getSessions(@RequestBody GetBenRequestHandler getBenRequestHandler, HttpServletRequest request) throws Exception {
        Map<String, Object> response = new LinkedHashMap<>();

        try {
            Map<String, Object> data = new HashMap<>();

            List<UwinSessionResponseDTO> uwinSessionResponse = service.getSessionsByAsha(getBenRequestHandler.getAshaId());
            if (uwinSessionResponse!= null) {
                data.put("userId", getBenRequestHandler.getUserId());
                data.put("entries", service.getSessionsByAsha(getBenRequestHandler.getAshaId()));
                response.put("data", data);
                response.put("statusCode", HttpStatus.OK.value());
                response.put("message", "Success");
            } else {
                response.put("statusCode", HttpStatus.NOT_FOUND.value());
                response.put("message", "Data not found for this user");
            }

        } catch (Exception e) {
            response.put("statusCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("errorMessage", e.getMessage());

        }

        return ResponseEntity.ok(response);
    }
}
