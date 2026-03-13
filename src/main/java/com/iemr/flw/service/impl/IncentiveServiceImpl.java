package com.iemr.flw.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iemr.flw.domain.identity.RMNCHMBeneficiarydetail;
import com.iemr.flw.domain.iemr.*;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.*;
import com.iemr.flw.masterEnum.GroupName;
import com.iemr.flw.masterEnum.IncentiveName;
import com.iemr.flw.masterEnum.StateCode;
import com.iemr.flw.repo.identity.BeneficiaryRepo;
import com.iemr.flw.repo.iemr.*;
import com.iemr.flw.service.IncentiveService;
import com.iemr.flw.service.MaaMeetingService;
import com.iemr.flw.service.UwinSessionService;
import com.iemr.flw.utils.JwtUtil;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class IncentiveServiceImpl implements IncentiveService {
    private final Logger logger = LoggerFactory.getLogger(ChildCareServiceImpl.class);

    @Autowired
    private BeneficiaryRepo beneficiaryRepo;
    ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private IncentiveActivityLangMappingRepo incentiveActivityLangMappingRepo;

    @Autowired
    IncentivesRepo incentivesRepo;

    @Autowired
    IncentiveRecordRepo recordRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private IncentivePendingActivityRepository incentivePendingActivityRepository;

    @Autowired
    private UserServiceRoleRepo userRepo;

    @Autowired
    private MaaMeetingService maaMeetingService;

    @Autowired
    private ChildCareServiceImpl childCareService;

    @Autowired
    private UwinSessionService uwinSessionService;

    @Autowired
    private DeathReportsServiceImpl deathReportsService;

    @Override
    public String saveIncentivesMaster(List<IncentiveActivityDTO> activityDTOS) {
        try {
            activityDTOS.forEach(activityDTO -> {
                IncentiveActivity activity =
                        incentivesRepo.findIncentiveMasterByNameAndGroup(activityDTO.getName(), activityDTO.getGroup());

                if (activity == null) {
                    activity = new IncentiveActivity();
                    modelMapper.map(activityDTO, activity);
                } else {
                    Long id = activity.getId();
                    modelMapper.map(activityDTO, activity);
                    activity.setId(id);
                }
                incentivesRepo.save(activity);
            });
            String saved = "";
            activityDTOS.forEach(dto -> saved.concat(dto.getGroup() + ": " + dto.getName()));
            return "saved master data for " + saved;
        } catch (Exception e) {

        }
        return null;
    }

    @Override
    public String getIncentiveMaster(IncentiveRequestDTO incentiveRequestDTO) {
        try {
            Integer stateCode = incentiveRequestDTO.getState();
            String langCode = incentiveRequestDTO.getLangCode();

            // Integer comparison using intValue() — safe and reliable
            boolean isCG = stateCode != null && stateCode.intValue() == StateCode.CG.getStateCode();
            boolean isAM = stateCode != null && stateCode.intValue() == StateCode.AM.getStateCode();

            // Single filtered DB query instead of findAll() + stream filter
            List<IncentiveActivity> incs = isCG
                    ? incentivesRepo.findByGroupAndIsDeleted("ACTIVITY", false)
                    : incentivesRepo.findByGroupNotAndIsDeleted("ACTIVITY", false);

            if (incs.isEmpty()) {
                return new Gson().toJson(Collections.emptyList());
            }

            // Fetch ALL lang mappings in ONE query instead of N queries in loop
            List<Long> ids = incs.stream()
                    .map(IncentiveActivity::getId)
                    .collect(Collectors.toList());

            Map<Long, IncentiveActivityLangMapping> mappingById = incentiveActivityLangMappingRepo
                    .findAllByIdIn(ids)
                    .stream()
                    .collect(Collectors.toMap(
                            IncentiveActivityLangMapping::getId,
                            Function.identity()
                    ));

            List<IncentiveActivityDTO> dtos = incs.stream().map(inc -> {
                IncentiveActivityDTO dto = modelMapper.map(inc, IncentiveActivityDTO.class);
                IncentiveActivityLangMapping mapping = mappingById.get(inc.getId());

                if (mapping != null) {
                    dto.setName(mapping.getName());

                    if (isCG) {
                        dto.setGroupName("");
                    } else if (isAM) {
                        dto.setGroupName(mapping.getGroup());
                    }

                    // Set description based on language
                    if ("as".equals(langCode)) {
                        dto.setDescription(mapping.getAssameActivityDescription() != null
                                ? mapping.getAssameActivityDescription()
                                : mapping.getDescription());

                    } else if ("hi".equals(langCode)) {
                        dto.setDescription(mapping.getHindiActivityDescription() != null
                                ? mapping.getHindiActivityDescription()
                                : mapping.getDescription());

                    } else {
                        // default "en"
                        dto.setDescription(inc.getDescription());
                    }

                } else {
                    dto.setGroupName(inc.getGroup());
                }

                return dto;
            }).collect(Collectors.toList());

            Gson gson = new GsonBuilder().setDateFormat("MMM dd, yyyy h:mm:ss a").create();
            return gson.toJson(dtos);

        } catch (Exception e) {
            logger.error("Error in getIncentiveMaster: ", e);
        }
        return null;
    }

    @Override
    public String getAllIncentivesByUserId(GetBenRequestHandler request) {

        Integer villageID = request.getVillageID();
        boolean isCG = villageID != null && villageID.intValue() == StateCode.CG.getStateCode();

        if (!isCG) {
            checkMonthlyAshaIncentive(request.getAshaId());
        }

        // Step 1: Fetch all records for this ASHA
        List<IncentiveActivityRecord> entities = recordRepo.findRecordsByAsha(request.getAshaId());

        if (entities.isEmpty()) {
            return new Gson().toJson(Collections.emptyList());
        }

        // Step 2: Collect all activityIds — fetch valid ones in ONE query
        List<Long> activityIds = entities.stream()
                .map(IncentiveActivityRecord::getActivityId)
                .distinct()
                .collect(Collectors.toList());

        // Single bulk query instead of N individual findIncentiveMasterById() calls
        Set<Long> validActivityIds = isCG
                ? incentivesRepo.findValidActivityIds(activityIds, true)
                : incentivesRepo.findValidActivityIds(activityIds, false);

        // Filter entities based on valid activity IDs
        entities = entities.stream()
                .filter(e -> validActivityIds.contains(e.getActivityId()))
                .collect(Collectors.toList());

        // Step 3: Collect all benIds that need name lookup (name == null and benId > 0)
        List<Long> benIdsToFetch = entities.stream()
                .filter(e -> e.getName() == null && e.getBenId() != null && e.getBenId() > 0)
                .map(IncentiveActivityRecord::getBenId)
                .distinct()
                .collect(Collectors.toList());

        // Step 4: Bulk fetch all beneficiary names in ONE query instead of 3 queries per record
        Map<Long, String> benIdToNameMap = new HashMap<>();
        if (!benIdsToFetch.isEmpty()) {
            List<Object[]> benDetails = beneficiaryRepo.findBenNamesByBenIds(benIdsToFetch);
            for (Object[] row : benDetails) {
                Long benId    = ((Number) row[0]).longValue();
                String first  = row[1] != null ? (String) row[1] : "";
                String last   = row[2] != null ? (String) row[2] : "";
                benIdToNameMap.put(benId, (first + " " + last).trim());
            }
        }

        // Step 5: Map entities to DTOs
        List<IncentiveRecordDTO> dtos = entities.stream().map(entry -> {
            if (entry.getName() == null) {
                if (entry.getBenId() != null && entry.getBenId() > 0) {
                    String name = benIdToNameMap.getOrDefault(entry.getBenId(), "");
                    entry.setName(name);
                    if (isCG) {
                        entry.setIsEligible(true);
                    }
                } else {
                    entry.setName("");
                }
            }
            return modelMapper.map(entry, IncentiveRecordDTO.class);
        }).collect(Collectors.toList());

        Gson gson = new GsonBuilder().setDateFormat("MMM dd, yyyy h:mm:ss a").create();
        return gson.toJson(dtos);
    }

    @Override
    public String updateIncentive(PendingActivityDTO pendingActivityDTO) {
        logger.info("run--1");

        if (pendingActivityDTO == null) {
            return "Invalid request";
        }
        logger.info("run--2");

        IncentiveName incentiveName;
        try {
            incentiveName = IncentiveName.valueOf(pendingActivityDTO.getActivityName());
        } catch (IllegalArgumentException e) {
            return "Invalid incentive name";
        }
        logger.info("run--3");


        IncentiveActivity incentiveActivity =
                incentivesRepo.findIncentiveMasterByNameAndGroup(
                        pendingActivityDTO.getActivityName(),
                        pendingActivityDTO.getModuleName()
                );
        logger.info("run--3");


        if (incentiveActivity == null) {
            return null;
        }

        Optional<IncentivePendingActivity> optionalPendingActivity =
                incentivePendingActivityRepository
                        .findByMincentiveIdAndActivityId(
                                incentiveActivity.getId(),
                                pendingActivityDTO.getId()
                        );
        logger.info("run--4");

        if (!optionalPendingActivity.isPresent()) {
            return null;
        }
        logger.info("run--5");

        IncentivePendingActivity existingActivity = optionalPendingActivity.get();

        if (!existingActivity.getActivityId().equals(pendingActivityDTO.getId())) {
            return null;
        }
        logger.info("run--6");

        if (pendingActivityDTO.getImages() == null || pendingActivityDTO.getImages().isEmpty()) {
            return null;
        }
        logger.info("run--7");

        try {

            // ✅ ENUM BASED CHECK
            if(pendingActivityDTO.getModuleName().equals(GroupName.ACTIVITY)){
                if (Objects.equals(incentiveName.name(), IncentiveName.MAA_QUARTERLY_MEETING.name())) {
                    MaaMeetingRequestDTO maaMeetingRequestDTO = new MaaMeetingRequestDTO();
                    maaMeetingRequestDTO.setId(existingActivity.getRecordId());
                    maaMeetingRequestDTO.setMeetingImages(
                            pendingActivityDTO.getImages().toArray(new MultipartFile[0])
                    );
                    logger.info("Activity Name from request:"+incentiveName.name());
                    logger.info("Activity Name from enum:"+IncentiveName.MAA_QUARTERLY_MEETING.name());


                    MaaMeeting meeting=   maaMeetingService.updateMeetingFromFileUpload(
                            maaMeetingRequestDTO,
                            pendingActivityDTO.getId()
                    );
                    if(meeting!=null){
                        return  "Incentive update successfully";
                    }
                }

            }else {
                if (incentiveName.name().equals(IncentiveName.MAA_QUARTERLY_MEETING.name())) {
                    MaaMeetingRequestDTO maaMeetingRequestDTO = new MaaMeetingRequestDTO();
                    maaMeetingRequestDTO.setId(existingActivity.getRecordId());
                    maaMeetingRequestDTO.setMeetingImages(
                            pendingActivityDTO.getImages().toArray(new MultipartFile[0])
                    );
                    logger.info("Activity Name from request:"+incentiveName.name());
                    logger.info("Activity Name from enum:"+IncentiveName.MAA_QUARTERLY_MEETING.name());


                    MaaMeeting meeting =  maaMeetingService.updateMeetingFromFileUpload(
                            maaMeetingRequestDTO,
                            pendingActivityDTO.getId()
                    );
                  if(meeting!=null){
                      return  "Incentive update successfully";
                  }

                }

                if (incentiveName.name().equals(IncentiveName.HBNC_0_42_DAYS.name())) {

                    HbncVisit hbncVisit=   childCareService.updateHbncFromFileUpload(
                            pendingActivityDTO.getImages().toArray(new MultipartFile[0]),
                            pendingActivityDTO.getId(),
                            existingActivity.getRecordId()

                    );
                    if(hbncVisit!=null){
                        return  "Incentive update successfully";
                    }
                }
                if(incentiveName.name().equals(IncentiveName.CHILD_MOBILIZATION_SESSIONS.name())){
                    UwinSessionRequestDTO uwinSessionRequestDTO = new UwinSessionRequestDTO();
                    uwinSessionRequestDTO.setAttachments(pendingActivityDTO.getImages().toArray(new MultipartFile[0]));
                    UwinSession uwinSession = uwinSessionService.updateSession(uwinSessionRequestDTO,existingActivity.getRecordId(),pendingActivityDTO.getId());
                    if(uwinSession!=null){
                        return  "Incentive update successfully";

                    }


                }
                if (pendingActivityDTO != null
                        && incentiveName != null
                        && IncentiveName.CHILD_DEATH_REPORTING.name().equals(incentiveName.name())) {

                    CdrDTO cdrDTO = new CdrDTO();
                    cdrDTO.setId(existingActivity.getRecordId());

                    List<MultipartFile> images = pendingActivityDTO.getImages();

                    if (images != null && !images.isEmpty()) {

                        if (images.size() > 0 && images.get(0) != null && !images.get(0).isEmpty())
                            cdrDTO.setCdrImage(convertAttachmentsToJson(images.get(0)));

                        if (images.size() > 1 && images.get(1) != null && !images.get(1).isEmpty())
                            cdrDTO.setCdrImage2(convertAttachmentsToJson(images.get(1)));

                        if (images.size() > 2 && images.get(2) != null && !images.get(2).isEmpty())
                            cdrDTO.setDeathCertImage1(convertAttachmentsToJson(images.get(2)));

                        if (images.size() > 3 && images.get(3) != null && !images.get(3).isEmpty())
                            cdrDTO.setDeathCertImage2(convertAttachmentsToJson(images.get(3)));
                    }

                    deathReportsService.updateCDRUploadFiles(cdrDTO, pendingActivityDTO.getId());
                }

                if (pendingActivityDTO != null
                        && incentiveName != null
                        && IncentiveName.MATERNAL_DEATH_REPORT.name().equals(incentiveName.name())) {

                    MdsrDTO mdsrDTO = new MdsrDTO();
                    List<MultipartFile> images = pendingActivityDTO.getImages();
                  mdsrDTO.setId(existingActivity.getRecordId());
                    if (images != null && !images.isEmpty()) {

                        if (images.size() > 0 && images.get(0) != null && !images.get(0).isEmpty())
                            mdsrDTO.setMdsr1File(convertAttachmentsToJson(images.get(0)));

                        if (images.size() > 1 && images.get(1) != null && !images.get(1).isEmpty())
                            mdsrDTO.setMdsr2File(convertAttachmentsToJson(images.get(1)));

                        if (images.size() > 2 && images.get(2) != null && !images.get(2).isEmpty())
                            mdsrDTO.setMdsrDeathCertFile(convertAttachmentsToJson(images.get(2)));
                    }
                    deathReportsService.updateMDSR(mdsrDTO, pendingActivityDTO.getId());

                }


            }




        } catch (Exception e) {
            logger.info("run--last"+e.getMessage());

            return e.getMessage();
        }

        return null;
    }

    public String convertAttachmentsToJson(MultipartFile file) {
        try {
            String base64 = Base64.getEncoder().encodeToString(file.getBytes());
            return new ObjectMapper().writeValueAsString(base64);
        } catch (Exception e) {
            throw new RuntimeException("Error converting file", e);
        }
    }
    private void checkMonthlyAshaIncentive(Integer ashaId) {
        IncentiveActivity MOBILEBILLREIMB_ACTIVITY = incentivesRepo.findIncentiveMasterByNameAndGroup("MOBILE_BILL_REIMB", GroupName.OTHER_INCENTIVES.getDisplayName());
        IncentiveActivity ADDITIONAL_ASHA_INCENTIVE = incentivesRepo.findIncentiveMasterByNameAndGroup("ADDITIONAL_ASHA_INCENTIVE", GroupName.ADDITIONAL_INCENTIVE.getDisplayName());
        IncentiveActivity ASHA_MONTHLY_ROUTINE = incentivesRepo.findIncentiveMasterByNameAndGroup("ASHA_MONTHLY_ROUTINE", GroupName.ASHA_MONTHLY_ROUTINE.getDisplayName());
        if (MOBILEBILLREIMB_ACTIVITY != null) {
            addMonthlyAshaIncentiveRecord(MOBILEBILLREIMB_ACTIVITY, ashaId);
        }
        if (ADDITIONAL_ASHA_INCENTIVE != null) {
            addMonthlyAshaIncentiveRecord(ADDITIONAL_ASHA_INCENTIVE, ashaId);

        }

        if (ASHA_MONTHLY_ROUTINE != null) {
            addMonthlyAshaIncentiveRecord(ASHA_MONTHLY_ROUTINE, ashaId);

        }
    }

    private void addMonthlyAshaIncentiveRecord(IncentiveActivity incentiveActivity, Integer ashaId) {
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());

        Timestamp startOfMonth = Timestamp.valueOf(LocalDate.now().withDayOfMonth(1).atStartOfDay());
        Timestamp endOfMonth = Timestamp.valueOf(LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()).atTime(23, 59, 59));

        IncentiveActivityRecord record = recordRepo.findRecordByActivityIdCreatedDateBenId(
                incentiveActivity.getId(),
                startOfMonth,
                endOfMonth,
                0L,
                ashaId
        );


        if (record == null) {
            record = new IncentiveActivityRecord();
            record.setActivityId(incentiveActivity.getId());
            record.setCreatedDate(timestamp);
            record.setCreatedBy(userRepo.getUserNamedByUserId(ashaId));
            record.setStartDate(timestamp);
            record.setEndDate(timestamp);
            record.setUpdatedDate(timestamp);
            record.setUpdatedBy(userRepo.getUserNamedByUserId(ashaId));
            record.setBenId(0L);
            record.setAshaId(ashaId);
            record.setAmount(Long.valueOf(incentiveActivity.getRate()));
            recordRepo.save(record);
        }
    }


}
