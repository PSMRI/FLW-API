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
import com.iemr.flw.service.MaaMeetingService;
import com.iemr.flw.service.UserService;
import com.iemr.flw.utils.JwtUtil;
import jakarta.persistence.criteria.CriteriaBuilder;
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
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class IncentiveServiceImpl implements IncentiveService {

    private final Logger logger = LoggerFactory.getLogger(IncentiveServiceImpl.class);

    @Autowired
    private BeneficiaryRepo beneficiaryRepo;
    @Autowired
    private IncentiveActivityLangMappingRepo incentiveActivityLangMappingRepo;
    @Autowired
    private IncentivesRepo incentivesRepo;
    @Autowired
    private IncentiveRecordRepo recordRepo;
    @Autowired
    private IncentivePendingActivityRepository incentivePendingActivityRepository;
    @Autowired
    private UserServiceRoleRepo userRepo;
    ;
    ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private JwtUtil jwtUtil;


    @Autowired
    private MaaMeetingService maaMeetingService;

    @Autowired
    private BenReferDetailsRepo benReferDetailsRepo;

    @Autowired
    private CbacIemrDetailsRepo cbacIemrDetailsRepo;

    @Autowired
    private UserService userService;

    @Autowired
    private EligibleCoupleRegisterRepo eligibleCoupleRegisterRepo;

    @Autowired
    private IFAFormSubmissionRepository ifaFormSubmissionRepository;

    private final ConcurrentHashMap<String, Object> lockMap = new ConcurrentHashMap<>();



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
                    if (isCG) {
                        dto.setGroupName("");

                    } else {
                        dto.setGroupName(inc.getGroup());

                    }
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

        Integer stateCode = userService.getUserDetail(request.getAshaId()).getStateId();
        String userName = userService.getUserDetail(request.getAshaId()).getUserName();
        try {

            if (stateCode.equals(StateCode.AM.getStateCode())) {
                checkMonthlyAshaIncentive(request.getAshaId());
            }
            if (stateCode.equals(StateCode.CG.getStateCode())) {
                checkMonthlyAshaIncentiveForCg(request.getAshaId());

            }
            checkIncentiveForChildGap(stateCode, userName);

        } catch (Exception e) {
            logger.error("Error in checkMonthlyAshaIncentive: ", e);
        }

        try {
            addIncentiveForIronTablets(request.getAshaId());
            incentiveOfNcdReferal(request.getAshaId(), request.getVillageID());

        } catch (Exception e) {
            logger.error("Error in incentiveOfNcdReferal: ", e);
        }

        boolean isCG = stateCode != null && stateCode.equals(StateCode.CG.getStateCode());

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
                Long benId = ((Number) row[0]).longValue();
                String first = row[1] != null ? (String) row[1] : "";
                String last = row[2] != null ? (String) row[2] : "";
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
                if (entry.getVerifiedByUserId() != null) {
                    entry.setSupervisorRole(userRepo.getUserRole(entry.getVerifiedByUserId()).get(0).getRoleName());
                    entry.setVerifiedByUserName(userRepo.getUserRole(entry.getVerifiedByUserId()).get(0).getName());

                }
                if (entry.getAshaId() != null) {
                    if (entry.getCreatedBy() == null) {
                        entry.setCreatedBy(userName);
                    }

                    if (entry.getUpdatedBy() == null) {
                        entry.setUpdatedBy(userName);
                    }

                    if (entry.getIsEligible() == null) {
                        entry.setIsEligible(true);
                    }
                }


            }
            return modelMapper.map(entry, IncentiveRecordDTO.class);
        }).collect(Collectors.toList());

        Gson gson = new GsonBuilder().setDateFormat("MMM dd, yyyy h:mm:ss a").create();
        return gson.toJson(dtos);
    }

    // ================= GROUPED SUMMARY =================
    @Override
    public String getAllIncentivesGroupedSummary(GetBenRequestHandler request) {

        LocalDate start = LocalDate.of(request.getYear(), request.getMonth(), 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        Timestamp startTs = Timestamp.valueOf(start.atStartOfDay());
        Timestamp endTs = Timestamp.valueOf(end.atTime(23, 59, 59));

        Integer villageID = userRepo.getUserRole(request.getUserId()).get(0).getStateId();
        boolean isCG = villageID != null && villageID.intValue() == StateCode.CG.getStateCode();

        List<IncentiveActivityRecord> records =
                recordRepo.findRecordsByAsha(request.getUserId())
                        .stream()
                        .filter(r -> r.getCreatedDate() != null
                                && r.getEndDate() != null
                                && r.getEndDate().toLocalDateTime().getMonthValue() == request.getMonth()
                                && r.getEndDate().toLocalDateTime().getYear() == request.getYear()
                                && r.getIsClaimed())
                        .toList();


        // Bulk fetch valid activity IDs — state wise
        List<Long> activityIds = records.stream()
                .map(IncentiveActivityRecord::getActivityId)
                .collect(Collectors.toList());

        Set<Long> validActivityIds = isCG
                ? incentivesRepo.findValidActivityIds(activityIds, true)
                : incentivesRepo.findValidActivityIds(activityIds, false);

        // Filter records based on valid activity IDs
        records = records.stream()
                .filter(r -> validActivityIds.contains(r.getActivityId()))
                .collect(Collectors.toList());

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
            map.put("activityDec", activity.getDescription());
            map.put("groupName", activity.getGroup());
            map.put("claimCount", list.size());
            map.put("totalAmount", total);
            map.put("amount", activity.getRate());

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


    // ================= UPDATE CLAIM =================
    @Transactional
    public String updateClaimStatus(Integer ashaId, Integer month, Integer year, Boolean isClaimed, String token) {
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

    private void incentiveOfNcdReferal(Integer ashaId, Integer stateId) {
        try {
            String userName = userRepo.getUserNamedByUserId(ashaId);
            String groupName = resolveGroupName(stateId);

            Map<String, IncentiveActivity> activityMap =
                    incentivesRepo.findIncentiveMasterByNameAndGroup(
                            List.of("NCD_POP_ENUMERATION", "HWC_REFERRAL_10_CASES"), groupName
                    ).stream().collect(Collectors.toMap(IncentiveActivity::getName, Function.identity()));

            IncentiveActivity ncdPopEnumeration = activityMap.get("NCD_POP_ENUMERATION");

            IncentiveActivity hwcReferralEnumeration = activityMap.get("HWC_REFERRAL_10_CASES");

            CompletableFuture<List<BenReferDetails>> benReferFuture =
                    CompletableFuture.supplyAsync(() -> benReferDetailsRepo.findByCreatedBy(userName));
            CompletableFuture<List<CbacDetailsImer>> cbacFuture =
                    CompletableFuture.supplyAsync(() -> cbacIemrDetailsRepo.findByCreatedBy(userName));

            List<CbacDetailsImer> cbacDetailsImer = cbacFuture.get();

            List<IncentiveActivityRecord> recordsToSave = new ArrayList<>();


            if (ncdPopEnumeration != null && !cbacDetailsImer.isEmpty()) {

                final IncentiveActivity activity = ncdPopEnumeration;
                cbacDetailsImer.forEach(cbacDetailsImer1 -> {
                    if(cbacDetailsImer1!=null){
                        addNCDandCBACIncentiveRecord(activity, ashaId, cbacDetailsImer1.getBeneficiaryRegId(), cbacDetailsImer1.getCreatedDate(), userName);
                    }
                });
            }

            if (hwcReferralEnumeration != null) {
                logger.info("hwcReferralEnumeration :" + hwcReferralEnumeration.getDescription());
                logger.info("userName={}", userName);

                LocalDate now = LocalDate.now();

                Long referralCount = benReferDetailsRepo.countMonthlyReferrals(
                        userName,
                        now.getMonthValue(),
                        now.getYear());

                logger.info("referralCount :" + referralCount);


                if (referralCount >= 10) {

                    addHwcReferalAshaIncentiveRecord(
                            hwcReferralEnumeration,
                            ashaId,
                            userName
                    );
                }
            }

            // ---- Single batch insert instead of N individual saves ----
            if (!recordsToSave.isEmpty()) {
                recordRepo.saveAll(recordsToSave);
            }

        } catch (Exception e) {
            logger.error("Error in incentiveOfNcdReferal for ashaId={}, stateId={}", ashaId, stateId, e);
        }
    }

    // Helper — record banana
    private void addNCDandCBACIncentiveRecord(IncentiveActivity activity, Integer ashaId,
                                                                 Long benId, Timestamp createdDate, String userName) {

        String lockKey = activity.getId() + "_" + benId + "_" + createdDate;

        Object lock = lockMap.computeIfAbsent(lockKey, k -> new Object());
        synchronized (lock){
            IncentiveActivityRecord incentiveActivityRecord = recordRepo.findRecordByActivityIdCreatedDateBenId(activity.getId(),createdDate,benId,ashaId);
            if(incentiveActivityRecord==null){
                Timestamp now = Timestamp.valueOf(LocalDateTime.now());
                IncentiveActivityRecord record = new IncentiveActivityRecord();
                record.setActivityId(activity.getId());
                record.setCreatedDate(createdDate);
                record.setCreatedBy(userName);
                record.setStartDate(createdDate);
                record.setEndDate(createdDate);
                record.setUpdatedDate(now);
                record.setUpdatedBy(userName);
                record.setBenId(benId);
                record.setAshaId(ashaId);
                record.setAmount(Long.valueOf(activity.getRate()));
                recordRepo.save(record);
            }
            lockMap.remove(lockKey, lock);

        }


    }

    private String resolveGroupName(Integer stateId) {
        if (stateId.equals(StateCode.CG.getStateCode())) {
            logger.info("State id: {}", StateCode.CG.getStateCode());
            return GroupName.ACTIVITY.getDisplayName();
        }
        logger.info("State id: {}", stateId);
        return GroupName.NCD.getDisplayName();
    }

    private void checkMonthlyAshaIncentive(Integer ashaId) {
        try {
            String userName = userRepo.getUserNamedByUserId(ashaId);

            IncentiveActivity MOBILEBILLREIMB_ACTIVITY = incentivesRepo.findIncentiveMasterByNameAndGroup("MOBILE_BILL_REIMB", GroupName.OTHER_INCENTIVES.getDisplayName());
            IncentiveActivity ADDITIONAL_ASHA_INCENTIVE = incentivesRepo.findIncentiveMasterByNameAndGroup("ADDITIONAL_ASHA_INCENTIVE", GroupName.ADDITIONAL_INCENTIVE.getDisplayName());
            IncentiveActivity ASHA_MONTHLY_ROUTINE = incentivesRepo.findIncentiveMasterByNameAndGroup("ASHA_MONTHLY_ROUTINE", GroupName.ASHA_MONTHLY_ROUTINE.getDisplayName());
            if (MOBILEBILLREIMB_ACTIVITY != null) {
                addMonthlyAshaIncentiveRecord(MOBILEBILLREIMB_ACTIVITY, ashaId, userName);
            }
            if (ADDITIONAL_ASHA_INCENTIVE != null) {
                addMonthlyAshaIncentiveRecord(ADDITIONAL_ASHA_INCENTIVE, ashaId, userName);

            }

            if (ASHA_MONTHLY_ROUTINE != null) {
                addMonthlyAshaIncentiveRecord(ASHA_MONTHLY_ROUTINE, ashaId, userName);

            }
        } catch (Exception e) {
            logger.error("Error in addMonthlyAshaIncentiveRecord", e);

        }

    }

    private void checkMonthlyAshaIncentiveForCg(Integer ashaId) {
        try {
            String userName = userRepo.getUserNamedByUserId(ashaId);

            IncentiveActivity MONTHLY_HONORARIUM = incentivesRepo.findIncentiveMasterByNameAndGroup("MONTHLY_HONORARIUM", GroupName.ACTIVITY.getDisplayName());
            IncentiveActivity MITANIN_REGISTER_5_INFO_FILL = incentivesRepo.findIncentiveMasterByNameAndGroup("MITANIN_REGISTER_5_INFO_FILL", GroupName.ACTIVITY.getDisplayName());
            IncentiveActivity MITANIN_REGISTER = incentivesRepo.findIncentiveMasterByNameAndGroup("MITANIN_REGISTER", GroupName.ACTIVITY.getDisplayName());
            if (MONTHLY_HONORARIUM != null) {
                addMonthlyAshaIncentiveRecord(MONTHLY_HONORARIUM, ashaId, userName);
            }
            if (MITANIN_REGISTER_5_INFO_FILL != null) {
                addMonthlyAshaIncentiveRecord(MITANIN_REGISTER_5_INFO_FILL, ashaId, userName);

            }

            if (MITANIN_REGISTER != null) {
                addMonthlyAshaIncentiveRecord(MITANIN_REGISTER, ashaId, userName);

            }
        } catch (Exception e) {
            logger.error("Error in addMonthlyAshaIncentiveRecord", e);

        }

    }

    private void addMonthlyAshaIncentiveRecord(IncentiveActivity incentiveActivity, Integer ashaId, String userName) {
        try {
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
                record.setCreatedBy(userName);
                record.setStartDate(timestamp);
                record.setEndDate(timestamp);
                record.setUpdatedDate(timestamp);
                record.setUpdatedBy(userName);
                record.setBenId(0L);
                record.setAshaId(ashaId);
                record.setIsEligible(true);
                record.setIsDefaultActivity(true);
                record.setAmount(Long.valueOf(incentiveActivity.getRate()));
                recordRepo.save(record);
            }
        } catch (Exception e) {
            logger.error("Error in addMonthlyAshaIncentiveRecord", e);

        }

    }

    private void addHwcReferalAshaIncentiveRecord(IncentiveActivity incentiveActivity, Integer ashaId, String userName) {
        try {
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
                record.setCreatedBy(userName);
                record.setStartDate(timestamp);
                record.setEndDate(timestamp);
                record.setUpdatedDate(timestamp);
                record.setUpdatedBy(userName);
                record.setBenId(0L);
                record.setAshaId(ashaId);
                record.setIsEligible(true);
                record.setIsDefaultActivity(false);
                record.setAmount(Long.valueOf(incentiveActivity.getRate()));
                recordRepo.save(record);
            }
        } catch (Exception e) {
            logger.error("Error in addMonthlyAshaIncentiveRecord", e);

        }

    }

    private void addIncentiveForIronTablets(Integer userId) {
        IncentiveActivity incentiveActivityAM = incentivesRepo.findIncentiveMasterByNameAndGroup("NATIONAL_IRON_PLUS", GroupName.CHILD_HEALTH.getDisplayName());
        IncentiveActivity incentiveActivityCG = incentivesRepo.findIncentiveMasterByNameAndGroup("NATIONAL_IRON_PLUS", GroupName.ACTIVITY.getDisplayName());

        String userName = userService.getUserDetail(userId).getUserName();
        Integer stateId = userService.getUserDetail(userId).getStateId();
        if (userName != null) {
            List<EligibleCoupleRegister> eligibleCoupleRegisters = eligibleCoupleRegisterRepo.findByCreatedBy(userName);
            List<IFAFormSubmissionData> ifaFormSubmissionData = ifaFormSubmissionRepository.findByUserId(userId);
            logger.info("eligibleCoupleRegisters :" + eligibleCoupleRegisters.size());
            logger.info("ifaFormSubmissionData :" + ifaFormSubmissionData.size());

            if (!eligibleCoupleRegisters.isEmpty() && !ifaFormSubmissionData.isEmpty()) {

                int percentage = (int) (((double) ifaFormSubmissionData.size()
                        / eligibleCoupleRegisters.size()) * 100);

                logger.info("IFA Count : {}", ifaFormSubmissionData.size());
                logger.info("Eligible Couple Count : {}", eligibleCoupleRegisters.size());
                logger.info("Percentage : {}", percentage);

                if (percentage >= 50) {

                    if (stateId.equals(StateCode.AM.getStateCode())) {
                        addIFAIncentive(
                                ifaFormSubmissionData.get(ifaFormSubmissionData.size() - 1),
                                incentiveActivityAM,
                                userId,
                                userName);
                    }

                    if (stateId.equals(StateCode.CG.getStateCode())) {
                        addIFAIncentive(
                                ifaFormSubmissionData.get(ifaFormSubmissionData.size() - 1),
                                incentiveActivityCG,
                                userId,
                                userName);
                    }
                }
            }

        }


    }

    private void addIFAIncentive(IFAFormSubmissionData ifaFormSubmissionData, IncentiveActivity incentiveActivityAM, Integer userId, String userName) {
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());

        Timestamp startOfMonth = Timestamp.valueOf(LocalDate.now().withDayOfMonth(1).atStartOfDay());
        Timestamp endOfMonth = Timestamp.valueOf(LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()).atTime(23, 59, 59));

        logger.info("IFA incentive");
        IncentiveActivityRecord incentiveActivityRecord = recordRepo.findRecordByActivityIdCreatedDateBenId(
                incentiveActivityAM.getId(),
                startOfMonth,
                endOfMonth,
                0L,
                userId
        );
        if (incentiveActivityRecord == null) {
            incentiveActivityRecord = new IncentiveActivityRecord();
            incentiveActivityRecord.setActivityId(incentiveActivityAM.getId());
            incentiveActivityRecord.setCreatedDate(timestamp);
            incentiveActivityRecord.setStartDate(timestamp);
            incentiveActivityRecord.setEndDate(timestamp);
            incentiveActivityRecord.setUpdatedDate(timestamp);
            incentiveActivityRecord.setUpdatedBy(ifaFormSubmissionData.getUserName());
            incentiveActivityRecord.setCreatedBy(ifaFormSubmissionData.getUserName());
            incentiveActivityRecord.setBenId(0L);
            incentiveActivityRecord.setAshaId(userId);
            incentiveActivityRecord.setAmount(Long.valueOf(incentiveActivityAM.getRate()));
            recordRepo.save(incentiveActivityRecord);
            logger.info("saved IFA incentive");

        }
    }

    private void checkIncentiveForChildGap(Integer stateId, String userName) {

        logger.info("Checking Child Gap Incentive for user: {}, stateId: {}", userName, stateId);

        List<EligibleCoupleRegister> eligibleCoupleRegisters = eligibleCoupleRegisterRepo.findByCreatedBy(userName);

        logger.info("Eligible Couple Records Found: {}", eligibleCoupleRegisters.size());

        if (!eligibleCoupleRegisters.isEmpty()) {

            eligibleCoupleRegisters.forEach(eligibleCoupleRegister -> {

                logger.info(
                        "Processing EligibleCoupleRegister -> BenId: {}, NumChildren: {}, MarriageFirstChildGap: {}, FirstAndSecondChildGap: {}",
                        eligibleCoupleRegister.getBenId(),
                        eligibleCoupleRegister.getNumChildren(),
                        eligibleCoupleRegister.getMarriageFirstChildGap(),
                        eligibleCoupleRegister.getFirstAndSecondChildGap());

                // Marriage -> First Child Gap
                if(eligibleCoupleRegister.getFirstAndSecondChildGap()!=null){
                    if (eligibleCoupleRegister.getFirstAndSecondChildGap()>=3 ) {

                        logger.info("Marriage -> First Child Gap condition matched.");

                        if (stateId.equals(StateCode.AM.getStateCode())) {

                            logger.info("Fetching incentive for Assam.");

                            IncentiveActivity activity1 =
                                    incentivesRepo.findIncentiveMasterByNameAndGroup(
                                            "1st_2nd_CHILD_GAP",
                                            GroupName.FAMILY_PLANNING.getDisplayName());

                            logger.info("Incentive Activity: {}", activity1);

                            createIncentiveRecord(eligibleCoupleRegister, activity1);

                            logger.info("Marriage -> First Child Gap incentive created.");
                        }

                        if (stateId.equals(StateCode.CG.getStateCode())) {

                            logger.info("Fetching incentive for Chhattisgarh.");

                            IncentiveActivity activityCH =
                                    incentivesRepo.findIncentiveMasterByNameAndGroup(
                                            "1st_2nd_CHILD_GAP",
                                            GroupName.ACTIVITY.getDisplayName());

                            logger.info("Incentive Activity: {}", activityCH);

                            createIncentiveRecord(eligibleCoupleRegister, activityCH);

                            logger.info("Marriage -> First Child Gap incentive created.");
                        }
                    }
                }


                // First -> Second Child Gap
                if(eligibleCoupleRegister.getMarriageFirstChildGap()!=null){
                    if (eligibleCoupleRegister.getMarriageFirstChildGap()>=2) {

                        logger.info("1st -> 2nd Child Gap condition matched.");

                        if (stateId.equals(StateCode.AM.getStateCode())) {

                            logger.info("Fetching incentive for Assam.");

                            IncentiveActivity activity2 =
                                    incentivesRepo.findIncentiveMasterByNameAndGroup(
                                            "FP_DELAY_2Y",
                                            GroupName.FAMILY_PLANNING.getDisplayName());

                            logger.info("Incentive Activity: {}", activity2);

                            createIncentiveRecord(eligibleCoupleRegister, activity2);

                            logger.info("1st -> 2nd Child Gap incentive created.");
                        }

                        if (stateId.equals(StateCode.CG.getStateCode())) {

                            logger.info("Fetching incentive for Chhattisgarh.");

                            IncentiveActivity activityCH =
                                    incentivesRepo.findIncentiveMasterByNameAndGroup(
                                            "FP_DELAY_2Y",
                                            GroupName.ACTIVITY.getDisplayName());

                            logger.info("Incentive Activity: {}", activityCH);

                            createIncentiveRecord(eligibleCoupleRegister, activityCH);

                            logger.info("1st -> 2nd Child Gap incentive created.");
                        }
                    }
                }

            });

        } else {
            logger.info("No Eligible Couple Register records found for user: {}", userName);
        }

        logger.info("Completed Child Gap Incentive check for user: {}", userName);
    }

    private void createIncentiveRecord(EligibleCoupleRegister eligibleCoupleRegister, IncentiveActivity activity) {
        if (activity != null) {
            Integer userId = userRepo.getUserIdByName(eligibleCoupleRegister.getCreatedBy());

            IncentiveActivityRecord record = recordRepo
                    .findRecordByActivityIdCreatedDateBenId(activity.getId(), eligibleCoupleRegister.getCreatedDate(), eligibleCoupleRegister.getBenId(),userId);
            if (record == null) {
                record = new IncentiveActivityRecord();
                record.setActivityId(activity.getId());
                record.setCreatedDate(eligibleCoupleRegister.getCreatedDate());
                record.setCreatedBy(eligibleCoupleRegister.getCreatedBy());
                record.setStartDate(eligibleCoupleRegister.getCreatedDate());
                record.setEndDate(eligibleCoupleRegister.getCreatedDate());
                record.setUpdatedDate(eligibleCoupleRegister.getCreatedDate());
                record.setUpdatedBy(eligibleCoupleRegister.getCreatedBy());
                record.setBenId(eligibleCoupleRegister.getBenId());
                record.setAshaId(userId);
                record.setAmount(Long.valueOf(activity.getRate()));
                recordRepo.save(record);
            }
        }
    }

}
