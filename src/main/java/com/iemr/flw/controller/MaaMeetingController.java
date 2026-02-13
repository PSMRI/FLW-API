package com.iemr.flw.controller;

import com.iemr.flw.domain.iemr.MaaMeeting;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.MaaMeetingListResponseDTO;
import com.iemr.flw.dto.iemr.MaaMeetingRequestDTO;
import com.iemr.flw.dto.iemr.MaaMeetingResponseDTO;
import com.iemr.flw.service.MaaMeetingService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("maa-meetings")
public class MaaMeetingController {


    private final MaaMeetingService service;

    public MaaMeetingController(MaaMeetingService service) {
        this.service = service;
    }

    @PostMapping(value = "/saveAll", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> saveMeeting(

            @RequestPart(value = "villageName", required = false) String villageName,
            @RequestPart(value = "noOfPragnentWoment", required = false) String noOfPragnentWomen,
            @RequestPart(value = "noOfLactingMother", required = false) String noOfLactingMother,
            @RequestPart(value = "mitaninActivityCheckList", required = false) String mitaninActivityCheckList,
            @RequestPart(value = "meetingDate") String meetingDate,
            @RequestPart(value = "place", required = false) String place,
            @RequestPart(value = "participants") String participants,
            @RequestPart(value = "ashaId", required = false) String ashaId,
            @RequestPart(value = "createdBy", required = false) String createdBy,
            @RequestPart(value = "meetingImages", required = false) List<MultipartFile> meetingImages
    ) {
        try {
            MaaMeetingRequestDTO dto = new MaaMeetingRequestDTO();

            if (meetingDate != null && !meetingDate.isEmpty()) {
                dto.setMeetingDate(LocalDate.parse(meetingDate));
            }

            dto.setPlace(place);
            dto.setVillageName(villageName);
            dto.setMitaninActivityCheckList(mitaninActivityCheckList);
            dto.setCreatedBY(createdBy);

            if (participants != null && !participants.isEmpty()) {
                dto.setParticipants(Integer.parseInt(participants));
            }

            if (ashaId != null && !ashaId.isEmpty()) {
                dto.setAshaId(Integer.parseInt(ashaId));
            }

            if (noOfLactingMother != null && !noOfLactingMother.isEmpty()) {
                dto.setNoOfLactingMother(Integer.parseInt(noOfLactingMother));
            }

            if (noOfPragnentWomen != null && !noOfPragnentWomen.isEmpty()) {
                dto.setNoOfPragnentWomen(Integer.parseInt(noOfPragnentWomen));
            }

            if (meetingImages != null && !meetingImages.isEmpty()) {
                dto.setMeetingImages(meetingImages.toArray(new MultipartFile[0]));
            }

            service.saveMeeting(dto);

            return ResponseEntity.ok("Saved Successfully");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateMeeting(
            @RequestPart("meetingDate") String meetingDate,
            @RequestPart("place") String place,
            @RequestPart("participants") String participants,
            @RequestPart("ashaId") String ashaId,
            @RequestPart("createdBy") String createdBy,
            @RequestPart(value = "meetingImages", required = false) List<MultipartFile> meetingImages) {
        try {
            MaaMeetingRequestDTO dto = new MaaMeetingRequestDTO();
            if (meetingDate != null) {
                dto.setMeetingDate(LocalDate.parse(meetingDate));

            }
            if (place != null) {
                dto.setPlace(place);

            }
            if (participants != null) {
                dto.setParticipants(Integer.parseInt(participants));

            }
            if (ashaId != null) {
                dto.setAshaId(Integer.parseInt(ashaId));

            }
            if (createdBy != null) {
                dto.setCreatedBY(createdBy);

            }
            if (meetingImages != null) {
                dto.setMeetingImages(meetingImages != null ? meetingImages.toArray(new MultipartFile[0]) : null);

            }
            if (dto != null) {
                service.updateMeeting(dto);

            }
            return ResponseEntity.ok("Saved Successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping("/getAll")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched meetings",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MaaMeetingListResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<?> getMeetings(@RequestBody GetBenRequestHandler getBenRequestHandler) {
        MaaMeetingListResponseDTO response = new MaaMeetingListResponseDTO();

        try {
            response.setData(service.getAllMeetings(getBenRequestHandler));
            response.setStatusCode(200);
            response.setStatus("Success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setStatus("Something went wrong");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
