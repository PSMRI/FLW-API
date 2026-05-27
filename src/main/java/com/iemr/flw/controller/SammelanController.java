package com.iemr.flw.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.*;
import com.iemr.flw.service.SammelanService;
import com.iemr.flw.utils.DateUtil;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping(value = "/sammelans")
public class SammelanController {
    private final Logger logger = LoggerFactory.getLogger(DeathReportsController.class);

    @Autowired
    private  SammelanService service;



    @RequestMapping(value = "saveAll",method = RequestMethod.POST,consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> create(

            @RequestPart("date") String date,
            @RequestPart("place") String place,
            @RequestPart("participants") String participants,
            @RequestPart("ashaId") String ashaId,
            @RequestPart(value = "sammelanImages", required = false) List<MultipartFile> sammelanImages) throws JsonProcessingException {
        Map<String, Object> response = new LinkedHashMap<>();

        SammelanRequestDTO sammelanRequestDTO = new SammelanRequestDTO();
        sammelanRequestDTO.setPlace(place);
        sammelanRequestDTO.setParticipants(Integer.valueOf(participants));
        sammelanRequestDTO.setDate(Long.parseLong(date));
        sammelanRequestDTO.setAshaId(Integer.valueOf(ashaId));
        sammelanRequestDTO.setSammelanImages(sammelanImages != null ? sammelanImages.toArray(new MultipartFile[0]) : null);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(sammelanRequestDTO);
        logger.info("📥 Incoming HBYC Request: \n" + json+"date"+date);
        SammelanResponseDTO resp = service.submitSammelan(sammelanRequestDTO);

        try {
            if (resp != null) {
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


    @PostMapping("/getAll")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched meetings",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SammelanListResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<?> getMeetings(@RequestBody GetBenRequestHandler getBenRequestHandler) {
        SammelanListResponseDTO response = new SammelanListResponseDTO();

        try {
            response.setData(service.getSammelanHistory(getBenRequestHandler.getAshaId()));
            response.setStatusCode(HttpStatus.OK.value());
            response.setStatus("Success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()+"\n"+e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}

