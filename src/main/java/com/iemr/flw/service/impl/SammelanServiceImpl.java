package com.iemr.flw.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iemr.flw.domain.iemr.IncentiveActivity;
import com.iemr.flw.domain.iemr.IncentiveActivityRecord;
import com.iemr.flw.domain.iemr.SammelanAttachment;
import com.iemr.flw.domain.iemr.SammelanRecord;
import com.iemr.flw.dto.iemr.*;
import com.iemr.flw.masterEnum.GroupName;
import com.iemr.flw.repo.iemr.*;
import com.iemr.flw.service.SammelanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SammelanServiceImpl implements SammelanService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @Autowired
    private SammelanRecordRepository recordRepo;
    @Autowired
    private SammelanAttachmentRepository attachmentRepo;

    private SammelanRecord record;

    @Autowired
    private IncentiveRecordRepo incentiveRecordRepo;

    @Autowired
    private ObjectMapper objectMapper;


    @Autowired
    private UserServiceRoleRepo userRepo;

    @Autowired
    private IncentivesRepo incentivesRepo;

    @Autowired
    private UpdateIncentivePendindDocService updateIncentivePendindDocService;


    @Override
    public SammelanResponseDTO submitSammelan(SammelanRequestDTO sammelanRequestDTO) {
        SammelanResponseDTO response = new SammelanResponseDTO();

        try {

            // Save Sammelan record
            record = new SammelanRecord();
            record.setAshaId(sammelanRequestDTO.getAshaId());
            logger.info("Meeting Date:" + sammelanRequestDTO.getDate());
            Timestamp timestamp = new Timestamp(sammelanRequestDTO.getDate());
            record.setMeetingDate(timestamp);
            record.setPlace(sammelanRequestDTO.getPlace());
            record.setParticipants(sammelanRequestDTO.getParticipants());


            if (sammelanRequestDTO.getSammelanImages() != null && sammelanRequestDTO.getSammelanImages().length > 0) {
                List<String> base64Images = Arrays.stream(sammelanRequestDTO.getSammelanImages())
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
                record.setAttachments(imagesJson);
            }

            if (record != null) {
                record = recordRepo.save(record);
                checkIncentive(record);


            }
            // Prepare Response DTO
            response.setId(record.getId());
            response.setAshaId(record.getAshaId());
            Timestamp ts = Timestamp.valueOf(record.getMeetingDate().toLocalDateTime());
            long millis = ts.getTime();
            response.setDate(millis);
            response.setPlace(record.getPlace());
            response.setParticipants(record.getParticipants());


        } catch (Exception e) {

            logger.info("SammelanRecord Exception: " + e.getMessage());
        }
        return response;


    }

    private void checkIncentive(SammelanRecord record) {
        IncentiveActivity incentiveActivity = incentivesRepo.findIncentiveMasterByNameAndGroup("FP_SAAS_BAHU", GroupName.FAMILY_PLANNING.getDisplayName());
        logger.info("SammelanRecord: " + incentiveActivity.getRate());
        if (incentiveActivity != null) {
            addSammelanIncentive(incentiveActivity, record);
        }
    }

    private void addSammelanIncentive(IncentiveActivity incentiveActivity, SammelanRecord record) {
        IncentiveActivityRecord incentiveActivityRecord = incentiveRecordRepo.findRecordByActivityIdCreatedDateBenId(incentiveActivity.getId(), record.getMeetingDate(), 0L, record.getAshaId());
        try {
            if (incentiveActivityRecord == null) {
                incentiveActivityRecord = new IncentiveActivityRecord();
                incentiveActivityRecord.setActivityId(incentiveActivity.getId());
                incentiveActivityRecord.setCreatedDate(record.getMeetingDate());
                incentiveActivityRecord.setCreatedBy(userRepo.getUserNamedByUserId(record.getAshaId()));
                incentiveActivityRecord.setStartDate(record.getMeetingDate());
                incentiveActivityRecord.setEndDate(record.getMeetingDate());
                incentiveActivityRecord.setUpdatedDate(record.getMeetingDate());
                incentiveActivityRecord.setUpdatedBy(userRepo.getUserNamedByUserId(record.getAshaId()));
                incentiveActivityRecord.setBenId(0L);
                incentiveActivityRecord.setAshaId(record.getAshaId());
                incentiveActivityRecord.setAmount(Long.valueOf(incentiveActivity.getRate()));
                if (record.getAttachments() != null) {
                    incentiveActivityRecord.setIsEligible(true);
                    incentiveRecordRepo.save(incentiveActivityRecord);

                } else {
                    incentiveActivityRecord.setIsEligible(false);
                    IncentiveActivityRecord activityRecord = incentiveRecordRepo.save(incentiveActivityRecord);
                    if (activityRecord != null) {
                        updateIncentivePendindDocService.updatePendingActivity(record.getAshaId(), record.getId(), activityRecord.getActivityId(), incentiveActivity.getId());

                    }
                }
            }
        } catch (Exception e) {
            logger.info("SammelanRecord save Record: ", e);

        }

    }


    @Override
    public List<SammelanResponseDTO> getSammelanHistory(Integer ashaId) {
        List<SammelanRecord> records = recordRepo.findByAshaId(ashaId);
        return records.stream().map(record -> {
            SammelanResponseDTO dto = new SammelanResponseDTO();
            dto.setId(record.getId());
            dto.setAshaId(record.getAshaId());
            Timestamp ts = Timestamp.valueOf(record.getMeetingDate().toLocalDateTime());
            long millis = ts.getTime();
            dto.setDate(millis);
            dto.setPlace(record.getPlace());
            dto.setParticipants(record.getParticipants());
            try {
                if (record.getAttachments() != null) {
                    List<String> images = objectMapper.readValue(
                            record.getAttachments(),
                            new TypeReference<List<String>>() {
                            }
                    );
                    dto.setImagePaths(images);
                }
            } catch (Exception e) {
                dto.setImagePaths(List.of());
            }

            return dto;
        }).collect(Collectors.toList());
    }

}
