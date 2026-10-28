package com.iemr.flw.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iemr.flw.domain.iemr.*;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.*;
import com.iemr.flw.masterEnum.GroupName;
import com.iemr.flw.masterEnum.StateCode;
import com.iemr.flw.repo.identity.BeneficiaryRepo;
import com.iemr.flw.repo.iemr.*;
import com.iemr.flw.service.IncentiveService;
import com.iemr.flw.service.MaaMeetingService;
import com.iemr.flw.service.NotificationService;
import com.iemr.flw.service.UserService;
import com.iemr.flw.utils.JwtUtil;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.time.*;
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

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SupervisorDashboardRepo dashboardRepo;



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
                        if (inc.getGroupCategoryName() != null
                                && !inc.getGroupCategoryName().isEmpty()) {
                            dto.setGroupName(inc.getGroupCategoryName());
                        } else {
                            dto.setGroupName(inc.getGroup());
                        }
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
                        if (inc.getGroupCategoryName() != null
                                && !inc.getGroupCategoryName().isEmpty()) {
                            dto.setGroupName(inc.getGroupCategoryName());
                        } else {
                            dto.setGroupName(inc.getGroup());
                        }
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
        Integer ashaId = request.getAshaId();

        // Fetch user stateId and userName in ONE lightweight query instead of heavy getUserDetail() (which builds facilityData)
        List<UserServiceRoleDTO> userRoles = userRepo.getUserRole(ashaId);
        UserServiceRoleDTO userDetail = (userRoles != null && !userRoles.isEmpty())
                ? userRoles.get(0)
                : userService.getUserDetail(ashaId);
        Integer stateCode = userDetail != null ? userDetail.getStateId() : null;
        String userName = userDetail != null ? userDetail.getUserName() : null;

        try {
            if (stateCode != null && stateCode.equals(StateCode.AM.getStateCode())) {
                checkMonthlyAshaIncentive(ashaId, userName);
            } else if (stateCode != null && stateCode.equals(StateCode.CG.getStateCode())) {
                checkMonthlyAshaIncentiveForCg(ashaId, userName);
            }
        } catch (Exception e) {
            logger.error("Error in checkMonthlyAshaIncentive: ", e);
        }

        try {
            addIncentiveForIronTablets(ashaId, userName, stateCode);
            Integer effectiveStateId = request.getVillageID() != null ? request.getVillageID() : stateCode;
            incentiveOfNcdReferal(ashaId, effectiveStateId, request, userName);
        } catch (Exception e) {
            logger.error("Error in incentiveOfNcdReferal: ", e);
        }

        boolean isCG = stateCode != null && stateCode.equals(StateCode.CG.getStateCode());

        // Fetch all records for this ASHA in a single query ordered by id DESC (avoids Page COUNT(DISTINCT) + multi-page round trips)
        List<IncentiveActivityRecord> entities = recordRepo.findRecordsByAshaOrderByIdDesc(ashaId);

        if (entities.isEmpty()) {
            return new Gson().toJson(Collections.emptyList());
        }

        // Bulk fetch valid activity ids
        List<Long> activityIds = entities.stream()
                .map(IncentiveActivityRecord::getActivityId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        if (activityIds.isEmpty()) {
            return new Gson().toJson(Collections.emptyList());
        }

        Set<Long> validActivityIds = isCG
                ? incentivesRepo.findValidActivityIds(activityIds, true)
                : incentivesRepo.findValidActivityIds(activityIds, false);

        entities = entities.stream()
                .filter(e -> validActivityIds.contains(e.getActivityId()))
                .collect(Collectors.toList());

        if (entities.isEmpty()) {
            return new Gson().toJson(Collections.emptyList());
        }

        // Bulk fetch beneficiary names
        List<Long> benIdsToFetch = entities.stream()
                .filter(e -> e.getName() == null && e.getBenId() != null && e.getBenId() > 0)
                .map(IncentiveActivityRecord::getBenId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, String> benIdToNameMap = new HashMap<>();
        Map<Long, Long> regIdToBenIdMap = new HashMap<>();

        if (!benIdsToFetch.isEmpty()) {

            // First, fetch names using BeneficiaryId
            List<Object[]> benDetails =
                    beneficiaryRepo.findBenNamesByBenIds(benIdsToFetch);

            Set<Long> foundBenIds = new HashSet<>();

            for (Object[] row : benDetails) {
                Long benId = ((Number) row[0]).longValue();

                String first = row[1] != null ? row[1].toString() : "";
                String last = row[2] != null ? row[2].toString() : "";

                benIdToNameMap.put(benId, (first + " " + last).trim());
                foundBenIds.add(benId);
            }

            // IDs not found as BeneficiaryId
            List<Long> unresolvedIds = benIdsToFetch.stream()
                    .filter(Objects::nonNull)
                    .filter(id -> !foundBenIds.contains(id))
                    .distinct()
                    .toList();

            if (!unresolvedIds.isEmpty()) {

                // Resolve BenRegId -> BeneficiaryId
                List<Object[]> mappings =
                        beneficiaryRepo.getBenIdsFromRegIds(unresolvedIds);

                Set<Long> resolvedBenIds = new HashSet<>();

                for (Object[] row : mappings) {
                    Long regId = ((Number) row[0]).longValue();
                    Long benId = ((Number) row[1]).longValue();

                    regIdToBenIdMap.put(regId, benId);
                    resolvedBenIds.add(benId);
                }

                // Fetch names for resolved BeneficiaryIds
                if (!resolvedBenIds.isEmpty()) {
                    List<Object[]> resolvedDetails =
                            beneficiaryRepo.findBenNamesByBenIds(
                                    new ArrayList<>(resolvedBenIds));

                    for (Object[] row : resolvedDetails) {
                        Long benId = ((Number) row[0]).longValue();

                        String first = row[1] != null ? row[1].toString() : "";
                        String last = row[2] != null ? row[2].toString() : "";

                        benIdToNameMap.put(benId, (first + " " + last).trim());
                    }
                }
            }
        }

        // Bulk fetch verifier (supervisor) roles — replaces N+1 userRepo.getUserRole() calls
        List<Integer> verifierIds = entities.stream()
                .filter(e -> e.getName() == null && e.getVerifiedByUserId() != null)
                .map(IncentiveActivityRecord::getVerifiedByUserId)
                .distinct()
                .collect(Collectors.toList());

        Map<Integer, UserServiceRoleDTO> verifierRoleMap = new HashMap<>();
        if (!verifierIds.isEmpty()) {
            List<UserServiceRoleDTO> roles = userRepo.getUserRolesByIds(verifierIds);
            for (UserServiceRoleDTO r : roles) {
                verifierRoleMap.put(r.getUserId(), r);
            }
        }

        // Map entities to DTOs
        List<IncentiveRecordDTO> finalDtos = entities.stream().map(entry -> {
            if (entry.getName() == null) {
                Long originalId = entry.getBenId();

                if (originalId != null && originalId > 0) {

                    Long actualBenId = regIdToBenIdMap.getOrDefault(
                            originalId, originalId);

                    String name = benIdToNameMap.getOrDefault(actualBenId, "");

                    entry.setBenId(actualBenId);
                    entry.setName(name);

                    if (isCG) {
                        entry.setIsEligible(true);
                    }

                } else {
                    entry.setName("");
                }

                if (entry.getVerifiedByUserId() != null) {
                    UserServiceRoleDTO role = verifierRoleMap.get(entry.getVerifiedByUserId());
                    if (role != null) {
                        entry.setSupervisorRole(role.getRoleName());
                        entry.setVerifiedByUserName(role.getName());
                    }
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
        return gson.toJson(finalDtos);
    }


    @Transactional
    public void resetRejectedIncentivesForPreviousMonths() {

        ZoneId zoneId = ZoneId.of("Asia/Kolkata");

        LocalDate firstDayOfCurrentMonth =
                LocalDate.now(zoneId).withDayOfMonth(1);

        Timestamp currentMonthStart =
                Timestamp.valueOf(firstDayOfCurrentMonth.atStartOfDay());

        Timestamp now =
                Timestamp.from(ZonedDateTime.now(zoneId).toInstant());

        recordRepo.resetRejectedIncentivesForNewMonth(
                currentMonthStart,
                now,
                "SYSTEM"
        );
    }
    // ================= GROUPED SUMMARY =================
    @Transactional(readOnly = true)
    public String getAllIncentivesGroupedSummary(GetBenRequestHandler request, Integer userId) {

        List<UserServiceRoleDTO> callerRoles = userRepo.getUserRole(userId);
        String roleName = (callerRoles != null && !callerRoles.isEmpty())
                ? callerRoles.get(0).getRoleName()
                : userService.getUserDetail(userId).getRoleName();
        Integer reqStatus = request.getApprovalStatus();

        Integer stateId = userRepo.getUserRole(request.getUserId()).get(0).getStateId();
        boolean isCG = stateId != null && stateId.intValue() == StateCode.CG.getStateCode();

        logger.info("Request getAllIncentivesGroupedSummary: {}", reqStatus);

        LocalDate monthStart = LocalDate.of(request.getYear(), request.getMonth(), 1);
        Timestamp startTs = Timestamp.valueOf(monthStart.atStartOfDay());
        Timestamp endTs = Timestamp.valueOf(monthStart.plusMonths(1).atStartOfDay());

        // Filter by date range and isClaimed=true in DB instead of loading all historical records
        List<IncentiveActivityRecord> records =
                recordRepo.findClaimedRecordsByAshaAndCreatedDateRange(request.getUserId(), startTs, endTs)
                        .stream()
                        .filter(r -> matchesRequestedStatus(r.getApprovalStatus(), reqStatus))
                        .collect(Collectors.toList());

        if (records.isEmpty()) {
            return new Gson().toJson(Collections.emptyList());
        }

        List<Long> activityIds = records.stream()
                .map(IncentiveActivityRecord::getActivityId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        Set<Long> validActivityIds = activityIds.isEmpty()
                ? Collections.emptySet()
                : incentivesRepo.findValidActivityIds(activityIds, isCG);
        if (validActivityIds == null) {
            validActivityIds = Collections.emptySet();
        }
        final Set<Long> validIds = validActivityIds;

        if (isCG) {

            if ("ASHA Supervisor".equalsIgnoreCase(roleName)) {

                if (Integer.valueOf(104).equals(reqStatus)) {
                    records = records.stream()
                            .filter(r -> validIds.contains(r.getActivityId())
                                    && Boolean.TRUE.equals(r.getIsDefaultActivity())
                                    && Objects.equals(r.getApprovalStatus(), 102))
                            .collect(Collectors.toList());
                } else if (Integer.valueOf(102).equals(reqStatus)) {
                    records = records.stream()
                            .filter(r -> validIds.contains(r.getActivityId())
                                    && Boolean.TRUE.equals(r.getIsDefaultActivity())
                                    && Objects.equals(r.getApprovalStatus(), 102))
                            .collect(Collectors.toList());

                }else {
                    records = records.stream()
                            .filter(r -> validIds.contains(r.getActivityId())
                                    && Boolean.TRUE.equals(r.getIsDefaultActivity()))
                            .collect(Collectors.toList());
                }

            } else if ("ANM".equalsIgnoreCase(roleName) || "CHO".equalsIgnoreCase(roleName)) {

                if (Integer.valueOf(102).equals(reqStatus) || Integer.valueOf(105).equals(reqStatus)) {

                    records = records.stream()
                            .filter(r -> validIds.contains(r.getActivityId()))

                            // default + approved + 102  ->  105
                            .peek(r -> {
                                if (Objects.equals(r.getApprovalStatus(), 102)
                                        && Boolean.TRUE.equals(r.getIsDefaultActivity())
                                        && Boolean.TRUE.equals(r.getIsApproved())) {

                                    logger.info("Changing record status 102 to 105: id={}, activityId={}",
                                            r.getId(), r.getActivityId());
                                    r.setApprovalStatus(105);
                                }
                            })

                            .filter(r -> {
                                boolean isDefault = Boolean.TRUE.equals(r.getIsDefaultActivity());
                                boolean isClaimed = Boolean.TRUE.equals(r.getIsClaimed());
                                Integer recStatus = r.getApprovalStatus();

                                if (Integer.valueOf(102).equals(reqStatus)) {
                                    return (!isDefault && Objects.equals(recStatus, 102) && isClaimed)
                                            || Objects.equals(recStatus, 105);
                                }

                                return Objects.equals(recStatus, 101)
                                        || Objects.equals(recStatus, 105);
                            })
                            .collect(Collectors.toList());

                } else if (Integer.valueOf(104).equals(reqStatus)) {

                    records = records.stream()
                            .filter(r -> validIds.contains(r.getActivityId()))
                            .filter(r -> {
                                boolean isDefault = Boolean.TRUE.equals(r.getIsDefaultActivity());
                                boolean isApproved = Boolean.TRUE.equals(r.getIsApproved());

                                boolean status102 = Objects.equals(r.getApprovalStatus(), 102)
                                        && (!isDefault || isApproved);

                                boolean status105 = Objects.equals(r.getApprovalStatus(), 105)
                                        && isDefault;

                                boolean status104 = Objects.equals(r.getApprovalStatus(), 104);

                                return status102 || status105 || status104;
                            })
                            .collect(Collectors.toList());

                } else {
                    records = records.stream()
                            .filter(r -> validIds.contains(r.getActivityId()))
                            .collect(Collectors.toList());
                }

                logger.info("ANM/CHO records after default activity filter: {}", records.size());
            }

        } else {
            records = records.stream()
                    .filter(r -> validIds.contains(r.getActivityId()))
                    .collect(Collectors.toList());
        }

        Map<Long, List<IncentiveActivityRecord>> grouped =
                records.stream().collect(Collectors.groupingBy(IncentiveActivityRecord::getActivityId));

        // Bulk fetch all IncentiveActivity masters in ONE query instead of N findById() calls in loop
        Map<Long, IncentiveActivity> activityByIdMap = grouped.isEmpty()
                ? Collections.emptyMap()
                : incentivesRepo.findAllById(grouped.keySet()).stream()
                        .collect(Collectors.toMap(IncentiveActivity::getId, Function.identity()));

        List<Map<String, Object>> result = new ArrayList<>();

        for (Map.Entry<Long, List<IncentiveActivityRecord>> entry : grouped.entrySet()) {

            Long activityId = entry.getKey();
            List<IncentiveActivityRecord> list = entry.getValue();

            IncentiveActivity activity = activityByIdMap.get(activityId);
            if (activity == null) continue;

            IncentiveActivityRecord latest = list.stream()
                    .max(Comparator.comparing(IncentiveActivityRecord::getCreatedDate))
                    .orElse(null);
            if (latest == null) continue;

            Integer approvalStatus = latest.getApprovalStatus() != null ? latest.getApprovalStatus() : 0;
            boolean isApproved = Boolean.TRUE.equals(latest.getIsApproved());
            Long incentiveId = latest.getId();

            long total = list.stream()
                    .mapToLong(r -> r.getAmount() != null ? r.getAmount() : 0)
                    .sum();

            Map<String, Object> map = new HashMap<>();
            map.put("activityId", activityId);
            map.put("incentiveId", incentiveId);
            map.put("activityDec", activity.getDescription());
            map.put("claimCount", list.size());
            map.put("totalAmount", total);
            map.put("amount", activity.getRate());

            if (isCG) {
                if (activity.getGroupCategoryName() != null && !activity.getGroupCategoryName().isEmpty()) {
                    map.put("groupName", activity.getGroupCategoryName());
                } else {
                    map.put("groupName", activity.getGroup());
                }
                map.put("isDefault", activity.getIsDefaultActivity());
                map.put("approvalStatus", approvalStatus);
                map.put("isApproved", isApproved);
            } else {
                map.put("groupName", activity.getGroup());
            }

            result.add(map);
        }

        return new Gson().toJson(result);
    }

    /**
     * Request ke status ke hisaab se kaunse DB status allow hain.
     */
    private boolean matchesRequestedStatus(Integer recStatus, Integer reqStatus) {

        if (Integer.valueOf(104).equals(reqStatus)) {
            // Overdue tab: 102, 104, 105
            return Objects.equals(recStatus, 102)
                    || Objects.equals(recStatus, 104)
                    || Objects.equals(recStatus, 105);
        }

        if (Integer.valueOf(105).equals(reqStatus)) {
            // Verified tab: 101, 105
            return Objects.equals(recStatus, 101)
                    || Objects.equals(recStatus, 105);
        }

        if (Integer.valueOf(102).equals(reqStatus)) {
            return Objects.equals(recStatus, 102)
                    || Objects.equals(recStatus, 105);
        }

        return Objects.equals(recStatus, reqStatus);
    }

    private boolean isAfter24Hours(Timestamp claimedDate) {
        if (claimedDate == null) {
            return false;
        }

        long now = System.currentTimeMillis();
        long diff = now - claimedDate.getTime();

        logger.info("Now: {}", new Timestamp(now));
        logger.info("ClaimedDate: {}", claimedDate);
        logger.info("Diff(ms): {}", diff);
        logger.info("Diff(hours): {}", diff / (1000 * 60 * 60.0));

        return diff >= 24L * 60 * 60 * 1000;
    }
    @Override
    public String getAllIncentivesGroupedActivity(GetBenRequestHandler request) {
        try {

            LocalDate startDate = LocalDate.of(request.getYear(), request.getMonth(), 1);
            LocalDate endDate = startDate.plusMonths(1);

            Timestamp startTs = Timestamp.valueOf(startDate.atStartOfDay());
            Timestamp endTs = Timestamp.valueOf(endDate.atStartOfDay());

            // Filter by ashaId, activityId, and date range in DB instead of loading all records for the ASHA
            List<IncentiveActivityRecord> records =
                    recordRepo.findRecordsByAshaAndActivityAndCreatedDateRange(
                                    request.getUserId(),
                                    request.getActivityId(),
                                    startTs,
                                    endTs
                            )
                            .stream()
                            .filter(r -> {

                                Integer requestedStatus =
                                        request.getApprovalStatus();

                                Integer recordStatus =
                                        r.getApprovalStatus();

                                boolean isDefault =
                                        Boolean.TRUE.equals(
                                                r.getIsDefaultActivity()
                                        );

                                boolean isApproved =
                                        Boolean.TRUE.equals(
                                                r.getIsApproved()
                                        );

                                // Overdue
                                if (Objects.equals(requestedStatus, 104)) {

                                    // 102 non-default records OR approved default records
                                    boolean status102 =
                                            Objects.equals(recordStatus, 102)
                                                    && (!isDefault || isApproved);

                                    // 105 only for default activities
                                    boolean status105 =
                                            Objects.equals(recordStatus, 105)
                                                    && isDefault;

                                    // Existing overdue records
                                    boolean status104 =
                                            Objects.equals(recordStatus, 104);

                                    return status102
                                            || status105
                                            || status104;
                                }

                                // Verified: 101 or 105
                                if (Objects.equals(requestedStatus, 105)) {
                                    return Objects.equals(recordStatus, 101)
                                            || Objects.equals(recordStatus, 105);
                                }

                                // Pending, rejected or other status
                                return Objects.equals(
                                        recordStatus,
                                        requestedStatus
                                );
                            })
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
            String groupCategoryName = activity != null
                    ? (activity.getGroupCategoryName() != null ? activity.getGroupCategoryName() : activity.getGroup())
                    : "";
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

                r.setGroupName(groupCategoryName);
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
    public String updateClaimStatus(Integer ashaId, Integer month, Integer year, Boolean isClaimed, String token,String incentive) {
        String title = null;
        try {
            LocalDate start = LocalDate.of(year, month, 1);
            LocalDate end = start.plusMonths(1);

            int updated=0;

            Timestamp claimedDate = Timestamp.valueOf(LocalDateTime.now());

            if (incentive == null || incentive.isEmpty()) {

                // No incentive IDs → all incentives for selected month/date range
                updated = recordRepo.updateClaimStatusByAshaAndDateRange(
                        ashaId,
                        isClaimed,
                        claimedDate,
                        Timestamp.valueOf(start.atStartOfDay()),
                        Timestamp.valueOf(end.atStartOfDay())
                );

            } else {

                for (String incentiveId : incentive.split(",")) {

                    Long id = Long.parseLong(incentiveId.trim());
                    updated = recordRepo.updateClaimStatusByAshaAndIncentiveIds(
                            ashaId,
                            isClaimed,
                            claimedDate,id
                    );
                }
            }

            if (updated > 0) {

                Integer supervisorUserId = dashboardRepo.getSupervisorUserIdByAshaId(ashaId);

                Map<String, String> data = new HashMap<>();
                data.put("notification_type", "INCENTIVE_APPROVAL");
                data.put("nav_id", "INCENTIVE_HISTORY");
                data.put("sender_user_id", String.valueOf(ashaId));
                data.put("receiver_user_id", String.valueOf(supervisorUserId));
                data.put("month", String.valueOf(month));
                data.put("year", String.valueOf(year));
                data.put("approval_status", String.valueOf(102));
                data.put("priority", "HIGH");

                title = "Incentive Claimed";

                if (isClaimed) {

                    String body = "Your incentive successfully claimed for month of "
                            + Month.of(month).name() + " " + year;

                    notificationService.sendNotification(
                            "FLW",
                            "NA",
                            title,
                            body,
                            "INCENTIVE_CLAIMED",
                            "INCENTIVE",
                            supervisorUserId
                    );

                    List<UserServiceRoleDTO> ashaRoles = userRepo.getUserRole(ashaId);
                    String ashaName = (ashaRoles != null && !ashaRoles.isEmpty())
                            ? ashaRoles.get(0).getName()
                            : userService.getUserDetail(ashaId).getName();

                    String supervisorBody =
                            ashaName
                                    + " successfully claimed incentive for month of "
                                    + Month.of(month).name()
                                    + " "
                                    + year;

                    notificationService.sendNotification(
                            "FLW",
                            "NA",
                            title,
                            supervisorBody,
                            "INCENTIVE_CLAIMED",
                            "INCENTIVE",
                            supervisorUserId
                    );
                }
            }

            return updated > 0 ? "Success" : "No records";

        } catch (Exception e) {
            logger.error("Error", e);
            return "Error";
        }
    }

    private void incentiveOfNcdReferal(Integer ashaId, Integer stateId, GetBenRequestHandler request, String userName) {
        try {
            if (userName == null) {
                userName = userRepo.getUserNamedByUserId(ashaId);
            }
            if (userName == null) {
                return;
            }
            String groupName = resolveGroupName(stateId);

            Map<String, IncentiveActivity> activityMap =
                    incentivesRepo.findIncentiveMasterByNameAndGroup(
                            List.of("NCD_POP_ENUMERATION", "HWC_REFERRAL_10_CASES"), groupName
                    ).stream().collect(Collectors.toMap(IncentiveActivity::getName, Function.identity(), (a, b) -> a));

            IncentiveActivity ncdPopEnumeration = activityMap.get("NCD_POP_ENUMERATION");
            IncentiveActivity hwcReferralEnumeration = activityMap.get("HWC_REFERRAL_10_CASES");

            // 1. Optimize NCD_POP_ENUMERATION: fetch only (beneficiaryRegId, createdDate) instead of all 65 columns,
            //    check existing records in 1 bulk query instead of N individual queries, and batch insert.
            if (ncdPopEnumeration != null) {
                List<Object[]> cbacRows = cbacIemrDetailsRepo.findBenRegIdAndCreatedDateByCreatedBy(userName);
                if (cbacRows != null && !cbacRows.isEmpty()) {
                    List<Object[]> existingRows = recordRepo.findBenIdAndStartDateByActivityIdAndAshaId(
                            ncdPopEnumeration.getId(), ashaId);
                    Set<String> existingMonthKeys = new HashSet<>();
                    if (existingRows != null) {
                        for (Object[] row : existingRows) {
                            Long existingBenId = row[0] != null ? ((Number) row[0]).longValue() : null;
                            Timestamp existingStart = (Timestamp) row[1];
                            if (existingBenId != null && existingStart != null) {
                                LocalDateTime ldt = existingStart.toLocalDateTime();
                                existingMonthKeys.add(existingBenId + "_" + ldt.getYear() + "_" + ldt.getMonthValue());
                            }
                        }
                    }

                    Timestamp nowTs = Timestamp.valueOf(LocalDateTime.now());
                    Long rate = Long.valueOf(ncdPopEnumeration.getRate());
                    List<IncentiveActivityRecord> recordsToSave = new ArrayList<>();

                    for (Object[] cbacRow : cbacRows) {
                        if (cbacRow == null || cbacRow[0] == null || cbacRow[1] == null) continue;
                        Long benId = ((Number) cbacRow[0]).longValue();
                        Timestamp createdDate = (Timestamp) cbacRow[1];
                        LocalDateTime ldt = createdDate.toLocalDateTime();
                        String monthKey = benId + "_" + ldt.getYear() + "_" + ldt.getMonthValue();

                        if (existingMonthKeys.add(monthKey)) {
                            IncentiveActivityRecord record = new IncentiveActivityRecord();
                            record.setActivityId(ncdPopEnumeration.getId());
                            record.setCreatedDate(createdDate);
                            record.setCreatedBy(userName);
                            record.setStartDate(createdDate);
                            record.setEndDate(createdDate);
                            record.setUpdatedDate(nowTs);
                            record.setUpdatedBy(userName);
                            record.setBenId(benId);
                            record.setAshaId(ashaId);
                            record.setAmount(rate);
                            recordsToSave.add(record);
                        }
                    }

                    if (!recordsToSave.isEmpty()) {
                        recordRepo.saveAll(recordsToSave);
                    }
                }
            }

            // 2. Optimize HWC_REFERRAL_10_CASES: check if monthly incentive already exists FIRST,
            //    and use index-friendly date range query instead of MONTH()/YEAR() functions.
            if (hwcReferralEnumeration != null) {
                LocalDate now = LocalDate.now();
                Timestamp startOfMonth = Timestamp.valueOf(now.withDayOfMonth(1).atStartOfDay());
                Timestamp endOfMonth = Timestamp.valueOf(now.withDayOfMonth(now.lengthOfMonth()).atTime(23, 59, 59));

                IncentiveActivityRecord existingHwcRecord = recordRepo.findRecordByActivityIdCreatedDateBenId(
                        hwcReferralEnumeration.getId(),
                        0L,
                        ashaId,
                        startOfMonth,
                        endOfMonth
                );

                if (existingHwcRecord == null) {
                    LocalDate requestDate = (request != null && request.getFromDate() != null)
                            ? request.getFromDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                            : now;
                    LocalDate reqMonthFirst = requestDate.withDayOfMonth(1);
                    Timestamp reqStart = Timestamp.valueOf(reqMonthFirst.atStartOfDay());
                    Timestamp reqEnd = Timestamp.valueOf(reqMonthFirst.plusMonths(1).atStartOfDay());

                    Long referralCount = benReferDetailsRepo.countMonthlyReferralsByDateRange(
                            userName,
                            reqStart,
                            reqEnd
                    );

                    logger.info("referralCount: {}", referralCount);

                    if (referralCount != null && referralCount >= 10) {
                        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
                        IncentiveActivityRecord record = new IncentiveActivityRecord();
                        record.setActivityId(hwcReferralEnumeration.getId());
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
                        record.setAmount(Long.valueOf(hwcReferralEnumeration.getRate()));
                        recordRepo.save(record);
                    }
                }
            }

        } catch (Exception e) {
            logger.error("Error in incentiveOfNcdReferal for ashaId={}, stateId={}", ashaId, stateId, e);
        }
    }

    private String resolveGroupName(Integer stateId) {
        if (stateId != null && stateId.equals(StateCode.CG.getStateCode())) {
            logger.info("State id: {}", StateCode.CG.getStateCode());
            return GroupName.ACTIVITY.getDisplayName();
        }
        logger.info("State id: {}", stateId);
        return GroupName.NCD.getDisplayName();
    }

    private void checkMonthlyAshaIncentive(Integer ashaId, String userName) {
        try {
            if (userName == null) {
                userName = userRepo.getUserNamedByUserId(ashaId);
            }
            List<IncentiveActivity> candidates = incentivesRepo.findByNamesAndIsDeletedFalse(
                    List.of("MOBILE_BILL_REIMB", "ADDITIONAL_ASHA_INCENTIVE", "ASHA_MONTHLY_ROUTINE")
            );
            List<IncentiveActivity> validActivities = new ArrayList<>();
            for (IncentiveActivity act : candidates) {
                if (("MOBILE_BILL_REIMB".equals(act.getName()) && GroupName.OTHER_INCENTIVES.getDisplayName().equals(act.getGroup()))
                        || ("ADDITIONAL_ASHA_INCENTIVE".equals(act.getName()) && GroupName.ADDITIONAL_INCENTIVE.getDisplayName().equals(act.getGroup()))
                        || ("ASHA_MONTHLY_ROUTINE".equals(act.getName()) && GroupName.ASHA_MONTHLY_ROUTINE.getDisplayName().equals(act.getGroup()))) {
                    validActivities.add(act);
                }
            }
            addMonthlyAshaIncentiveRecordsBatch(validActivities, ashaId, userName, Collections.emptySet());
        } catch (Exception e) {
            logger.error("Error in checkMonthlyAshaIncentive", e);
        }
    }

    private void checkMonthlyAshaIncentiveForCg(Integer ashaId, String userName) {
        try {
            if (userName == null) {
                userName = userRepo.getUserNamedByUserId(ashaId);
            }
            List<IncentiveActivity> validActivities = incentivesRepo.findIncentiveMasterByNameAndGroup(
                    List.of("MONTHLY_HONORARIUM", "MITANIN_REGISTER_5_INFO_FILL", "MITANIN_REGISTER"),
                    GroupName.ACTIVITY.getDisplayName()
            );
            addMonthlyAshaIncentiveRecordsBatch(validActivities, ashaId, userName, Set.of("MONTHLY_HONORARIUM"));
        } catch (Exception e) {
            logger.error("Error in checkMonthlyAshaIncentiveForCg", e);
        }
    }

    private void addMonthlyAshaIncentiveRecordsBatch(
            List<IncentiveActivity> activities,
            Integer ashaId,
            String userName,
            Set<String> preApprovedNames) {
        if (activities == null || activities.isEmpty()) {
            return;
        }
        try {
            LocalDate now = LocalDate.now();
            Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
            Timestamp startOfMonth = Timestamp.valueOf(now.withDayOfMonth(1).atStartOfDay());
            Timestamp endOfMonth = Timestamp.valueOf(now.withDayOfMonth(now.lengthOfMonth()).atTime(23, 59, 59));

            List<Long> activityIds = activities.stream()
                    .map(IncentiveActivity::getId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            if (activityIds.isEmpty()) {
                return;
            }

            Set<Long> existingIds = new HashSet<>(
                    recordRepo.findExistingActivityIdsByMonth(activityIds, 0L, ashaId, startOfMonth, endOfMonth)
            );

            List<IncentiveActivityRecord> newRecords = new ArrayList<>();
            for (IncentiveActivity activity : activities) {
                if (activity != null && !existingIds.contains(activity.getId())) {
                    IncentiveActivityRecord record = new IncentiveActivityRecord();
                    record.setActivityId(activity.getId());
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
                    record.setIsApproved(preApprovedNames != null && preApprovedNames.contains(activity.getName()));
                    record.setAmount(Long.valueOf(activity.getRate()));
                    newRecords.add(record);
                }
            }

            if (!newRecords.isEmpty()) {
                recordRepo.saveAll(newRecords);
            }
        } catch (Exception e) {
            logger.error("Error in addMonthlyAshaIncentiveRecordsBatch", e);
        }
    }

    private void addIncentiveForIronTablets(Integer userId, String userName, Integer stateId) {
        if (userName == null || stateId == null) {
            return;
        }

        IncentiveActivity targetActivity = null;
        if (stateId.equals(StateCode.AM.getStateCode())) {
            targetActivity = incentivesRepo.findIncentiveMasterByNameAndGroup("NATIONAL_IRON_PLUS", GroupName.CHILD_HEALTH.getDisplayName());
        } else if (stateId.equals(StateCode.CG.getStateCode())) {
            targetActivity = incentivesRepo.findIncentiveMasterByNameAndGroup("NATIONAL_IRON_PLUS", GroupName.ACTIVITY.getDisplayName());
        }

        if (targetActivity == null) {
            return;
        }

        LocalDate now = LocalDate.now();
        Timestamp startOfMonth = Timestamp.valueOf(now.withDayOfMonth(1).atStartOfDay());
        Timestamp endOfMonth = Timestamp.valueOf(now.withDayOfMonth(now.lengthOfMonth()).atTime(23, 59, 59));

        // Check if IFA incentive record already exists for this month BEFORE querying EC & IFA tables
        IncentiveActivityRecord existingRecord = recordRepo.findRecordByActivityIdCreatedDateBenId(
                targetActivity.getId(),
                0L,
                userId,
                startOfMonth,
                endOfMonth
        );
        if (existingRecord != null) {
            return;
        }

        // Use COUNT(*) queries instead of loading full entities (and LONGTEXT photos) into memory
        long ecCount = eligibleCoupleRegisterRepo.countByCreatedBy(userName);
        if (ecCount <= 0) {
            return;
        }
        long ifaCount = ifaFormSubmissionRepository.countByUserId(userId);
        if (ifaCount <= 0) {
            return;
        }

        int percentage = (int) (((double) ifaCount / ecCount) * 100);
        logger.info("IFA Count : {}, Eligible Couple Count : {}, Percentage : {}", ifaCount, ecCount, percentage);

        if (percentage >= 50) {
            addIFAIncentive(targetActivity, userId, userName, startOfMonth, endOfMonth);
        }
    }

    private void addIFAIncentive(IncentiveActivity incentiveActivity, Integer userId, String userName,
                                 Timestamp startOfMonth, Timestamp endOfMonth) {
        String lockKey = incentiveActivity.getId() + "_" + 0 + "_" + startOfMonth;
        Object lock = lockMap.computeIfAbsent(lockKey, k -> new Object());
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());

        logger.info("IFA incentive");
        synchronized (lock) {
            IncentiveActivityRecord incentiveActivityRecord = recordRepo.findRecordByActivityIdCreatedDateBenId(
                    incentiveActivity.getId(),
                    0L,
                    userId,
                    startOfMonth,
                    endOfMonth
            );
            if (incentiveActivityRecord == null) {
                incentiveActivityRecord = new IncentiveActivityRecord();
                incentiveActivityRecord.setActivityId(incentiveActivity.getId());
                incentiveActivityRecord.setCreatedDate(timestamp);
                incentiveActivityRecord.setStartDate(timestamp);
                incentiveActivityRecord.setEndDate(timestamp);
                incentiveActivityRecord.setUpdatedDate(timestamp);
                incentiveActivityRecord.setUpdatedBy(userName);
                incentiveActivityRecord.setCreatedBy(userName);
                incentiveActivityRecord.setBenId(0L);
                incentiveActivityRecord.setAshaId(userId);
                incentiveActivityRecord.setAmount(Long.valueOf(incentiveActivity.getRate()));
                recordRepo.save(incentiveActivityRecord);
                logger.info("saved IFA incentive");
            }
        }
        lockMap.remove(lockKey, lock);
    }

}
