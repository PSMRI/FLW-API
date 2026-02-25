package com.iemr.flw.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iemr.flw.domain.iemr.IncentiveActivity;
import com.iemr.flw.domain.iemr.IncentiveActivityRecord;
import com.iemr.flw.domain.iemr.UwinSession;
import com.iemr.flw.dto.iemr.UwinSessionRequestDTO;
import com.iemr.flw.dto.iemr.UwinSessionResponseDTO;
import com.iemr.flw.masterEnum.GroupName;
import com.iemr.flw.repo.iemr.IncentiveRecordRepo;
import com.iemr.flw.repo.iemr.IncentivesRepo;
import com.iemr.flw.repo.iemr.UserServiceRoleRepo;
import com.iemr.flw.repo.iemr.UwinSessionRepository;
import com.iemr.flw.service.UwinSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UwinSessionServiceImpl implements UwinSessionService {
    @Autowired
    private UwinSessionRepository repo;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private IncentivesRepo incentivesRepo;

    @Autowired
    private UserServiceRoleRepo userRepo;

    @Autowired
    private IncentiveRecordRepo recordRepo;

    @Autowired
    private UpdateIncentivePendindDocService updateIncentivePendindDocService;


    @Override
    public UwinSessionResponseDTO saveSession(UwinSessionRequestDTO req) throws Exception {

        // 🧩 Validations

        Timestamp sessionDate = req.getDate(); // Timestamp from request
        Timestamp now = Timestamp.from(Instant.now());

// 1️⃣ Future date check
        if (sessionDate.after(now)) {
            throw new RuntimeException("Date cannot be in future");
        }

// 2️⃣ Backdate beyond 2 months
        LocalDateTime twoMonthsAgo = LocalDateTime.now().minusMonths(2);
        if (sessionDate.toLocalDateTime().isBefore(twoMonthsAgo)) {
            throw new RuntimeException("Backdate not allowed beyond 2 months");
        }

// 3️⃣ Participants check
        if (req.getParticipants() == null || req.getParticipants() < 0 || req.getParticipants() > 999) {
            throw new RuntimeException("Participants count must be between 0–999");
        }



        // 🧩 Save record
        UwinSession session = new UwinSession();
        session.setAshaId(req.getAshaId());
        session.setDate(req.getDate());
        session.setPlace(req.getPlace());
        session.setParticipants(req.getParticipants());
        session.setCreatedBy(req.getCreatedBy());
        if (req.getAttachments() != null && req.getAttachments().length > 0) {
            List<String> base64Images = Arrays.stream(req.getAttachments())
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
            session.setAttachmentsJson(imagesJson);
        }

        repo.save(session);
        checkAndAddIncentive(session);

        // 🎯 Prepare response
        UwinSessionResponseDTO dto = new UwinSessionResponseDTO();
        dto.setId(session.getId());
        dto.setAshaId(session.getAshaId());
        dto.setDate(session.getDate());
        dto.setPlace(session.getPlace());
        dto.setParticipants(session.getParticipants());
        dto.setAttachments(Collections.singletonList(session.getAttachmentsJson()));

        return dto;
    }
   @Override
    public UwinSession updateSession(UwinSessionRequestDTO req,Long recordId,Long activityId) throws Exception {
        Optional<UwinSession> optionalUwinSession = repo.findById(recordId);
        if(optionalUwinSession.isPresent()){
            UwinSession session = new UwinSession();
            if (req.getAttachments() != null && req.getAttachments().length > 0) {
                List<String> base64Images = Arrays.stream(req.getAttachments())
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
                session.setAttachmentsJson(imagesJson);
            }

            repo.save(session);
            if(session.getAttachmentsJson()!=null){
                updateIncentivePendindDocService.updateIncentive(activityId);
            }
        }


        return optionalUwinSession.get();
    }

    @Override
    public List<UwinSessionResponseDTO> getSessionsByAsha(Integer ashaId) throws Exception {
        List<UwinSession> sessions = repo.findByAshaId(ashaId);
        return sessions.stream().map(s -> {
            UwinSessionResponseDTO dto = new UwinSessionResponseDTO();
            dto.setId(s.getId());
            dto.setAshaId(s.getAshaId());
            dto.setDate(s.getDate());
            dto.setPlace(s.getPlace());
            dto.setParticipants(s.getParticipants());
            try {
                List<String> att = objectMapper.readValue(s.getAttachmentsJson(), new TypeReference<List<String>>() {
                });
                dto.setAttachments(att);
            } catch (Exception e) {
                dto.setAttachments(List.of());
            }

            return dto;
        }).collect(Collectors.toList());
    }

    private void checkAndAddIncentive(UwinSession session) {
        IncentiveActivity incentiveActivityAM = incentivesRepo.findIncentiveMasterByNameAndGroup("CHILD_MOBILIZATION_SESSIONS", GroupName.IMMUNIZATION.getDisplayName());
        IncentiveActivity incentiveActivityCH = incentivesRepo.findIncentiveMasterByNameAndGroup("CHILD_MOBILIZATION_SESSIONS", GroupName.ACTIVITY.getDisplayName());


         if(incentiveActivityAM!=null){
             IncentiveActivityRecord record = recordRepo
                     .findRecordByActivityIdCreatedDateBenId(incentiveActivityAM.getId(), session.getDate(), 0L,session.getAshaId());
             if (record == null) {
                 record = new IncentiveActivityRecord();
                 record.setActivityId(incentiveActivityAM.getId());
                 record.setCreatedDate(session.getDate());
                 record.setCreatedBy(session.getCreatedBy());
                 record.setStartDate(session.getDate());
                 record.setEndDate(session.getDate());
                 record.setUpdatedDate(session.getDate());
                 record.setUpdatedBy(session.getCreatedBy());
                 record.setBenId(0L);
                 record.setAshaId(session.getAshaId());
                 record.setAmount(Long.valueOf(incentiveActivityAM.getRate()));
                 if(session.getAttachmentsJson()!=null){
                     record.setIsEligible(true);
                     recordRepo.save(record);

                 }else {
                     record.setIsEligible(false);
                   IncentiveActivityRecord incentiveActivityRecord =   recordRepo.save(record);
                     if(incentiveActivityRecord!=null){
                         updateIncentivePendindDocService.updatePendingActivity(session.getAshaId(),session.getId(),incentiveActivityRecord.getId(),incentiveActivityAM.getId());

                     }
                 }

             }
         }

        if(incentiveActivityCH!=null){
            IncentiveActivityRecord record = recordRepo
                    .findRecordByActivityIdCreatedDateBenId(incentiveActivityCH.getId(), session.getDate(), 0L,session.getAshaId());
            if (record == null) {
                record = new IncentiveActivityRecord();
                record.setActivityId(incentiveActivityCH.getId());
                record.setCreatedDate(session.getDate());
                record.setCreatedBy(session.getCreatedBy());
                record.setStartDate(session.getDate());
                record.setEndDate(session.getDate());
                record.setUpdatedDate(session.getDate());
                record.setUpdatedBy(session.getCreatedBy());
                record.setBenId(0L);
                record.setAshaId(session.getAshaId());
                record.setAmount(Long.valueOf(incentiveActivityCH.getRate()));
                if(session.getAttachmentsJson()!=null){
                    record.setIsEligible(true);
                }else {
                    record.setIsEligible(false);
                    updateIncentivePendindDocService.updatePendingActivity(session.getAshaId(),session.getId(),record.getId(),incentiveActivityCH.getId());
                }
                recordRepo.save(record);
            }
        }


    }
}
