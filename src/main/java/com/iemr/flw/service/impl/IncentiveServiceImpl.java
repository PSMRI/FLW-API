package com.iemr.flw.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iemr.flw.domain.identity.RMNCHMBeneficiarydetail;
import com.iemr.flw.domain.iemr.*;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.*;
import com.iemr.flw.masterEnum.GroupName;
import com.iemr.flw.masterEnum.IncentiveApprovalStatus;
import com.iemr.flw.masterEnum.IncentiveName;
import com.iemr.flw.masterEnum.StateCode;
import com.iemr.flw.repo.identity.BeneficiaryRepo;
import com.iemr.flw.repo.iemr.*;
import com.iemr.flw.service.IncentiveService;
import com.iemr.flw.utils.JwtUtil;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class IncentiveServiceImpl implements IncentiveService {

    private final Logger logger = LoggerFactory.getLogger(IncentiveServiceImpl.class);

    @Autowired private BeneficiaryRepo beneficiaryRepo;
    @Autowired private IncentiveActivityLangMappingRepo incentiveActivityLangMappingRepo;
    @Autowired private IncentivesRepo incentivesRepo;
    @Autowired private IncentiveRecordRepo recordRepo;
    @Autowired private IncentivePendingActivityRepository incentivePendingActivityRepository;
    @Autowired private UserServiceRoleRepo userRepo;

    private final ModelMapper modelMapper = new ModelMapper();

    // ================= MASTER SAVE =================
    @Override
    public String saveIncentivesMaster(List<IncentiveActivityDTO> activityDTOS) {
        try {
            for (IncentiveActivityDTO dto : activityDTOS) {
                IncentiveActivity activity =
                        incentivesRepo.findIncentiveMasterByNameAndGroup(dto.getName(), dto.getGroup());

                if (activity == null) {
                    activity = new IncentiveActivity();
                }

                modelMapper.map(dto, activity);
                incentivesRepo.save(activity);
            }
            return "Saved successfully";
        } catch (Exception e) {
            logger.error("Error saving incentives", e);
            return "Error";
        }
    }

    // ================= GET MASTER =================
    @Override
    public String getIncentiveMaster(IncentiveRequestDTO request) {
        try {
            Integer stateCode = request.getState();
            String langCode = request.getLangCode();

            boolean isCG = stateCode != null && stateCode.equals(StateCode.CG.getStateCode());

            List<IncentiveActivity> incs = isCG
                    ? incentivesRepo.findByGroupAndIsDeleted("ACTIVITY", false)
                    : incentivesRepo.findByGroupNotAndIsDeleted("ACTIVITY", false);

            if (incs.isEmpty()) {
                return new Gson().toJson(Collections.emptyList());
            }

            List<Long> ids = incs.stream().map(IncentiveActivity::getId).toList();

            Map<Long, IncentiveActivityLangMapping> mappingMap =
                    incentiveActivityLangMappingRepo.findAllByIdIn(ids)
                            .stream()
                            .collect(Collectors.toMap(
                                    IncentiveActivityLangMapping::getId,
                                    Function.identity()
                            ));

            List<IncentiveActivityDTO> dtos = incs.stream().map(inc -> {
                IncentiveActivityDTO dto = modelMapper.map(inc, IncentiveActivityDTO.class);
                IncentiveActivityLangMapping mapping = mappingMap.get(inc.getId());

                if (mapping != null) {
                    dto.setName(mapping.getName());

                    if ("hi".equals(langCode)) {
                        dto.setDescription(mapping.getHindiActivityDescription());
                    } else if ("as".equals(langCode)) {
                        dto.setDescription(mapping.getAssameActivityDescription());
                    } else {
                        dto.setDescription(inc.getDescription());
                    }
                }
                return dto;
            }).toList();

            return new GsonBuilder().setDateFormat("MMM dd, yyyy h:mm:ss a").create().toJson(dtos);

        } catch (Exception e) {
            logger.error("Error", e);
            return null;
        }
    }

    // ================= GET ALL INCENTIVES =================
    @Override
    public String getAllIncentivesByUserId(GetBenRequestHandler request) {

        try {
            List<IncentiveActivityRecord> records =
                    recordRepo.findRecordsByAsha(request.getAshaId());

            if (records.isEmpty()) {
                return new Gson().toJson(Collections.emptyList());
            }

            List<Long> benIds = records.stream()
                    .filter(r -> r.getBenId() != null && r.getBenId() > 0)
                    .map(IncentiveActivityRecord::getBenId)
                    .distinct()
                    .toList();

            Map<Long, String> nameMap = new HashMap<>();

            if (!benIds.isEmpty()) {
                List<Object[]> data = beneficiaryRepo.findBenNamesByBenIds(benIds);
                for (Object[] row : data) {
                    Long id = ((Number) row[0]).longValue();
                    String name = (row[1] != null ? row[1] : "") + " " +
                            (row[2] != null ? row[2] : "");
                    nameMap.put(id, name.trim());
                }
            }

            List<IncentiveRecordDTO> dtos = records.stream().map(r -> {
                if (r.getName() == null) {
                    r.setName(nameMap.getOrDefault(r.getBenId(), ""));
                }
                return modelMapper.map(r, IncentiveRecordDTO.class);
            }).toList();

            return new Gson().toJson(dtos);

        } catch (Exception e) {
            logger.error("Error", e);
            return null;
        }
    }

    // ================= GROUPED SUMMARY =================
    @Override
    public String getAllIncentivesGroupedSummary(GetBenRequestHandler request) {

        LocalDate start = LocalDate.of(request.getYear(), request.getMonth(), 1);
        LocalDate end = start.plusMonths(1);

        Timestamp startTs = Timestamp.valueOf(start.atStartOfDay());
        Timestamp endTs = Timestamp.valueOf(end.atStartOfDay());

        List<IncentiveActivityRecord> records =
                recordRepo.findRecordsByAsha(request.getUserId())
                        .stream()
                        .filter(r -> r.getCreatedDate() != null
                                && !r.getCreatedDate().before(startTs)
                                && r.getCreatedDate().before(endTs))
                        .toList();

        Map<Long, List<IncentiveActivityRecord>> grouped =
                records.stream().collect(Collectors.groupingBy(IncentiveActivityRecord::getActivityId));

        List<Map<String, Object>> result = new ArrayList<>();

        for (var entry : grouped.entrySet()) {

            Long activityId = entry.getKey();
            List<IncentiveActivityRecord> list = entry.getValue();

            IncentiveActivity activity =
                    incentivesRepo.findById(activityId).orElse(null);

            if (activity == null) continue;

            long total = list.stream()
                    .mapToLong(r -> r.getAmount() != null ? r.getAmount() : 0)
                    .sum();

            Map<String, Object> map = new HashMap<>();
            map.put("activityId", activityId);
            map.put("activity", activity.getDescription());
            map.put("count", list.size());
            map.put("totalAmount", total);

            result.add(map);
        }

        return new Gson().toJson(result);
    }

    @Override
    public String getAllIncentivesGroupedActivity(GetBenRequestHandler request) {
        try {

            LocalDate startDate = LocalDate.of(request.getYear(), request.getMonth(), 1);
            LocalDate endDate = startDate.plusMonths(1);

            Timestamp startTs = Timestamp.valueOf(startDate.atStartOfDay());
            Timestamp endTs = Timestamp.valueOf(endDate.atStartOfDay());

            List<IncentiveActivityRecord> records =
                    recordRepo.findRecordsByAsha(request.getUserId())
                            .stream()
                            .filter(r -> r.getActivityId() != null
                                    && r.getActivityId().equals(request.getActivityId())
                                    && r.getCreatedDate() != null
                                    && !r.getCreatedDate().before(startTs)
                                    && r.getCreatedDate().before(endTs))
                            .collect(Collectors.toList());

            if (records.isEmpty()) {
                return new Gson().toJson(new ArrayList<>());
            }

            // 🔹 Get beneficiary names
            Set<Long> benIds = records.stream()
                    .map(IncentiveActivityRecord::getBenId)
                    .filter(id -> id != null && id > 0)
                    .collect(Collectors.toSet());

            Map<Long, String> nameMap = new HashMap<>();

            if (!benIds.isEmpty()) {
                List<Object[]> data = beneficiaryRepo.findBenNamesByBenIds(new ArrayList<>(benIds));
                for (Object[] row : data) {
                    Long id = ((Number) row[0]).longValue();
                    String name = (row[1] != null ? row[1] : "") + " " +
                            (row[2] != null ? row[2] : "");
                    nameMap.put(id, name.trim());
                }
            }

            // 🔹 Activity details
            IncentiveActivity activity =
                    incentivesRepo.findById(request.getActivityId()).orElse(null);

            String groupName = activity != null ? activity.getGroup() : "";
            String description = activity != null ? activity.getDescription() : "";

            // 🔹 Map result
            List<IncentiveRecordDTO> dtos = records.stream().map(r -> {

                if (r.getName() == null) {
                    if (r.getBenId() != null && r.getBenId() > 0) {
                        r.setName(nameMap.getOrDefault(r.getBenId(), ""));
                    } else {
                        r.setName("");
                    }
                }

                r.setGroupName(groupName);
                r.setActivityDec(description);

                return modelMapper.map(r, IncentiveRecordDTO.class);

            }).collect(Collectors.toList());

            return new GsonBuilder()
                    .setDateFormat("MMM dd, yyyy h:mm:ss a")
                    .create()
                    .toJson(dtos);

        } catch (Exception e) {
            logger.error("Error in getAllIncentivesGroupedActivity", e);
            return null;
        }
    }
    @Override
    public String updateIncentive(PendingActivityDTO pendingActivityDTO) {
        try {

            if (pendingActivityDTO == null) {
                return "Invalid request";
            }

//            Optional<IncentivePendingActivity> optional =
//                    incentivePendingActivityRepository
//                            .findByUserIdAndModuleNameAndActivityId(
//                                    pendingActivityDTO.getUserId(),
//                                    pendingActivityDTO.getModuleName(),
//                                    pendingActivityDTO.getId()
//                            );
//
//            if (!optional.isPresent()) {
//                return "Record not found";
//            }

//            IncentivePendingActivity existing = optional.get();

            if (pendingActivityDTO.getImages() == null ||
                    pendingActivityDTO.getImages().isEmpty()) {
                return "No images provided";
            }

            String module = pendingActivityDTO.getModuleName();

            // 🔹 MAA MEETING
            if ("MAA_MEETING".equalsIgnoreCase(module)) {
                MaaMeetingRequestDTO dto = new MaaMeetingRequestDTO();
                dto.setMeetingImages(
                        pendingActivityDTO.getImages().toArray(new MultipartFile[0])
                );

                //maaMeetingService.updateMeeting(dto);
                return "Updated successfully";
            }

            // 🔹 HBNC
            if ("HBNC".equalsIgnoreCase(module)) {
//                childCareService.updateHbncFromFileUpload(
//                        pendingActivityDTO.getImages().toArray(new MultipartFile[0]),
//                        pendingActivityDTO.getId(),
//                        existing.getRecordId()
//                );
                return "Updated successfully";
            }

            // 🔹 UWIN SESSION
            if ("UWIN".equalsIgnoreCase(module)) {
                UwinSessionRequestDTO dto = new UwinSessionRequestDTO();
                dto.setAttachments(
                        pendingActivityDTO.getImages().toArray(new MultipartFile[0])
                );

//                uwinSessionService.updateSession(
//                        dto,
//                        existing.getRecordId(),
//                        pendingActivityDTO.getId()
//                );

                return "Updated successfully";
            }

            return "No matching module found";

        } catch (Exception e) {
            logger.error("Error in updateIncentive", e);
            return "Error: " + e.getMessage();
        }
    }

    @Override
    public String updateClaimStatus(Integer ashaId, Integer month, Integer year, Boolean isClaimed, String token) {
        return null;
    }

    // ================= UPDATE CLAIM =================
    @Transactional
    public String updateClaimStatus(Integer ashaId, Integer month, Integer year, Boolean isClaimed) {
        try {
            LocalDate start = LocalDate.of(year, month, 1);
            LocalDate end = start.plusMonths(1);

            int updated = recordRepo.updateClaimStatusByAshaAndDateRange(
                    ashaId,
                    isClaimed,
                    Timestamp.valueOf(LocalDateTime.now()),
                    Timestamp.valueOf(start.atStartOfDay()),
                    Timestamp.valueOf(end.atStartOfDay())
            );

            return updated > 0 ? "Success" : "No records";

        } catch (Exception e) {
            logger.error("Error", e);
            return "Error";
        }
    }
}
