package com.iemr.flw.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iemr.flw.domain.iemr.*;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.MaaMeetingRequestDTO;
import com.iemr.flw.dto.iemr.MaaMeetingResponseDTO;
import com.iemr.flw.masterEnum.GroupName;
import com.iemr.flw.repo.iemr.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MaaMeetingService {
    @Autowired
    private IncentivesRepo incentivesRepo;

    @Autowired
    private UserServiceRoleRepo userRepo;

    @Autowired
    private IncentiveRecordRepo recordRepo;
    private final MaaMeetingRepository repository;
    private final ObjectMapper objectMapper;

    @Autowired
    private IncentivePendingActivityRepository incentivePendingActivityRepository;

    public MaaMeetingService(MaaMeetingRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    public MaaMeeting saveMeeting(MaaMeetingRequestDTO req) throws Exception {
        MaaMeeting meeting = new MaaMeeting();
        meeting.setMeetingDate(req.getMeetingDate());
        meeting.setPlace(req.getPlace());
        meeting.setParticipants(req.getParticipants());
        meeting.setAshaId(req.getAshaId());
        meeting.setNoOfLactingMother(req.getNoOfLactingMother());
        meeting.setNoOfPragnentWomen(req.getNoOfPragnentWomen());
        meeting.setVillageName(req.getVillageName());
        meeting.setMitaninActivityCheckList(req.getMitaninActivityCheckList());

        meeting.setCreatedBy(req.getCreatedBY());

        // Convert meeting images to Base64 JSON
        if (req.getMeetingImages() != null && req.getMeetingImages().length > 0) {
            List<String> base64Images = Arrays.stream(req.getMeetingImages())
                    .filter(file -> !file.isEmpty())
                    .map(file -> {
                        try {
                            return Base64.getEncoder().encodeToString(file.getBytes());
                        } catch (IOException e) {
                            throw new RuntimeException("Error converting image to Base64", e);
                        }
                    })
                    .collect(Collectors.toList());

            String imagesJson = objectMapper.writeValueAsString(base64Images);
            meeting.setMeetingImagesJson(imagesJson);
        }

        checkAndAddIncentive(meeting);


        return repository.save(meeting);
    }

    public MaaMeeting updateMeeting(MaaMeetingRequestDTO req) throws JsonProcessingException {
        MaaMeeting existingMeeting = repository.findById(req.getId())
                .orElseThrow(() -> new EntityNotFoundException("Meeting not found: " + req.getId()));

        // ✅ NULL CHECK
        if (req.getMeetingDate() != null) {
            existingMeeting.setMeetingDate(req.getMeetingDate());
        }
        if (req.getPlace() != null) {
            existingMeeting.setPlace(req.getPlace());
        }
        if (req.getParticipants() != null) {
            existingMeeting.setParticipants(req.getParticipants());
        }
        if (req.getAshaId() != null) {
            existingMeeting.setAshaId(req.getAshaId());
        }
        if (req.getCreatedBY() != null) {  // ✅ Typo fixed: CreatedBY → CreatedBy
            existingMeeting.setCreatedBy(req.getCreatedBY());
        }

        // Images - only if provided
        if (req.getMeetingImages() != null && req.getMeetingImages().length > 0) {
            List<String> base64Images = Arrays.stream(req.getMeetingImages())
                    .filter(file -> file != null && !file.isEmpty())
                    .map(this::convertToBase64)
                    .collect(Collectors.toList());
            existingMeeting.setMeetingImagesJson(objectMapper.writeValueAsString(base64Images));
        }

        checkAndAddIncentive(existingMeeting);
        if (existingMeeting.getMeetingImagesJson() != null) {
            checkAndUpdateIncentive(existingMeeting);

        }
        return repository.save(existingMeeting);
    }


    private String convertToBase64(MultipartFile file) {
        try {
            return Base64.getEncoder().encodeToString(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert image to Base64: " + file.getOriginalFilename(), e);
        }
    }


    public List<MaaMeetingResponseDTO> getAllMeetings(GetBenRequestHandler getBenRequestHandler) throws Exception {
        List<MaaMeeting> meetings = repository.findByAshaId(getBenRequestHandler.getAshaId());

        return meetings.stream().map(meeting -> {
            MaaMeetingResponseDTO dto = new MaaMeetingResponseDTO();
            dto.setId(meeting.getId());
            dto.setMeetingDate(meeting.getMeetingDate());
            dto.setPlace(meeting.getPlace());
            dto.setParticipants(meeting.getParticipants());
            dto.setAshaId(meeting.getAshaId());
            dto.setVillageName(meeting.getVillageName());
            dto.setNoOfLactingMother(String.valueOf(meeting.getNoOfLactingMother()));
            dto.setNoOfPragnentWomen(String.valueOf(meeting.getNoOfPragnentWomen()));
            dto.setMitaninActivityCheckList(meeting.getMitaninActivityCheckList());
            dto.setCreatedBy(meeting.getCreatedBy());

            try {
                if (meeting.getMeetingImagesJson() != null) {
                    List<String> base64Images = objectMapper.readValue(
                            meeting.getMeetingImagesJson(),
                            new TypeReference<List<String>>() {
                            }
                    );

                    dto.setMeetingImages(base64Images);
                } else {
                    dto.setMeetingImages(List.of());
                }
            } catch (Exception e) {
                dto.setMeetingImages(List.of());
            }

            return dto;
        }).collect(Collectors.toList());
    }

    private void updatePendingActivity(Integer userId, Long recordId, Long activityId, String moduleName) {
        IncentivePendingActivity incentivePendingActivity = new IncentivePendingActivity();
        incentivePendingActivity.setActivityId(activityId);
        incentivePendingActivity.setRecordId(recordId);
        incentivePendingActivity.setUserId(userId);
        incentivePendingActivity.setModuleName(moduleName);
        if (incentivePendingActivity != null) {
            incentivePendingActivityRepository.save(incentivePendingActivity);
        }

    }

    private void checkAndUpdateIncentive(MaaMeeting meeting) {
        IncentiveActivity incentiveActivityAM = incentivesRepo.findIncentiveMasterByNameAndGroup("MAA_QUARTERLY_MEETING", GroupName.CHILD_HEALTH.getDisplayName());
        IncentiveActivity incentiveActivityCH = incentivesRepo.findIncentiveMasterByNameAndGroup("MAA_QUARTERLY_MEETING", GroupName.ACTIVITY.getDisplayName());
        if (incentiveActivityAM != null) {
            updateIncentive(incentiveActivityAM, meeting);
        }
        if (incentiveActivityCH != null) {
            updateIncentive(incentiveActivityCH, meeting);

        }

    }

    private void checkAndAddIncentive(MaaMeeting meeting) {
        IncentiveActivity incentiveActivityAM = incentivesRepo.findIncentiveMasterByNameAndGroup("MAA_QUARTERLY_MEETING", GroupName.CHILD_HEALTH.getDisplayName());
        IncentiveActivity incentiveActivityCH = incentivesRepo.findIncentiveMasterByNameAndGroup("MAA_QUARTERLY_MEETING", GroupName.ACTIVITY.getDisplayName());
        if (incentiveActivityAM != null) {
            addIncentive(incentiveActivityAM, meeting);
        }
        if (incentiveActivityCH != null) {
            addIncentive(incentiveActivityCH, meeting);

        }

    }

    private void addIncentive(IncentiveActivity incentiveActivity, MaaMeeting meeting) {
        IncentiveActivityRecord record = recordRepo
                .findRecordByActivityIdCreatedDateBenId(incentiveActivity.getId(), Timestamp.valueOf(meeting.getMeetingDate().atStartOfDay()), 0L, meeting.getAshaId());

        if (record == null) {
            record = new IncentiveActivityRecord();
            record.setActivityId(incentiveActivity.getId());
            record.setCreatedDate(Timestamp.valueOf(meeting.getMeetingDate().atStartOfDay()));
            record.setCreatedBy(meeting.getCreatedBy());
            record.setStartDate(Timestamp.valueOf(meeting.getMeetingDate().atStartOfDay()));
            record.setEndDate(Timestamp.valueOf(meeting.getMeetingDate().atStartOfDay()));
            record.setUpdatedDate(Timestamp.valueOf(meeting.getMeetingDate().atStartOfDay()));
            record.setUpdatedBy(meeting.getCreatedBy());
            record.setBenId(0L);
            record.setAshaId(meeting.getAshaId());
            if (meeting.getMeetingImagesJson() != null) {
                record.setIsEligible(true);
            } else {
                record.setIsEligible(false);
                updatePendingActivity(meeting.getAshaId(), meeting.getId(), record.getId(), "MAA_MEETING");

            }
            record.setAmount(Long.valueOf(incentiveActivity.getRate()));
            recordRepo.save(record);
        }

    }

    private void updateIncentive(IncentiveActivity incentiveActivity, MaaMeeting meeting) {
        IncentiveActivityRecord record = recordRepo
                .findRecordByActivityIdCreatedDateBenId(incentiveActivity.getId(), Timestamp.valueOf(meeting.getMeetingDate().atStartOfDay()), 0L, meeting.getAshaId());

        if (record != null) {
            record = new IncentiveActivityRecord();
            record.setIsEligible(true);
            recordRepo.save(record);
        }

    }


}
