package com.iemr.flw.service.impl;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.iemr.flw.domain.iemr.IncentiveActivityRecord;
import com.iemr.flw.dto.iemr.UserServiceRoleDTO;
import com.iemr.flw.masterEnum.IncentiveApprovalStatus;
import com.iemr.flw.masterEnum.StateCode;
import com.iemr.flw.repo.iemr.*;
import com.iemr.flw.service.NotificationService;
import com.iemr.flw.service.UserService;
import com.iemr.flw.utils.JwtUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iemr.flw.service.SupervisorDashboardService;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SupervisorDashboardServiceImpl implements SupervisorDashboardService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    private SupervisorDashboardRepo dashboardRepo;

    @Autowired
    private AshaSupervisorLoginRepo ashaSupervisorLoginRepo;

    @Autowired
    private FacilityLoginRepo facilityLoginRepo;

    @Autowired
    private IncentiveRecordRepo incentiveRecordRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    @Override
    public String getSupervisorDashboard(Integer supervisorUserID, Integer month, Integer year) {
        JSONObject result = new JSONObject();
        Integer stateId = userService.getUserDetail(supervisorUserID).getStateId();
        String rollName = userService.getUserDetail(supervisorUserID).getRoleName();

        // 1. Supervisor user details
        List<Object[]> supervisorRows = dashboardRepo.getSupervisorUserDetails(supervisorUserID);
        if (supervisorRows != null && !supervisorRows.isEmpty()) {
            Object[] sRow = supervisorRows.get(0);
            JSONObject supervisor = new JSONObject();
            supervisor.put("userId", sRow[0]);
            supervisor.put("fullName", fullName(sRow[1], sRow[2]));
            supervisor.put("employeeId", str(sRow[3]).isEmpty() ? JSONObject.NULL : str(sRow[3]));
            supervisor.put("mobile", str(sRow[4]).isEmpty() ? JSONObject.NULL : str(sRow[4]));
            supervisor.put("gender", str(sRow[5]).isEmpty() ? JSONObject.NULL : str(sRow[5]));
            result.put("supervisor", supervisor);
        }
        logger.info("Supervisor id"+supervisorUserID);

        // 2. Get all ASHAs with facility info
        logger.info("Fetching ASHA details for supervisorUserID: {}", supervisorUserID);



        List<Object[]> ashaRows;

        if ("ANM".equalsIgnoreCase(rollName) || "CHO".equalsIgnoreCase(rollName)) {

            List<Integer> facilityIDs =
                    facilityLoginRepo.getUserFacilityIDs(supervisorUserID);

            ashaRows =
                    facilityLoginRepo.getAshaListByFacilities(facilityIDs);

        } else {

            ashaRows =
                    dashboardRepo.getAshasWithFacilityInfo(supervisorUserID);

        }

        if (ashaRows == null || ashaRows.isEmpty()) {
            logger.warn("No ASHA records found for supervisorUserID: {}", supervisorUserID);

            result.put("totalAshaCount", 0);
            result.put("incentiveSummary", buildEmptyIncentiveSummary());
            result.put("facilities", new JSONArray());

            logger.info("Returning empty dashboard response for supervisorUserID: {}", supervisorUserID);

            return result.toString();
        }



        // Collect distinct facility IDs and ASHA IDs

        logger.info("Processing {} ASHA records", ashaRows.size());

        Set<Integer> facilityIDSet = new HashSet<>();
        Set<Integer> ashaIDSet = new HashSet<>();

        for (Object[] row : ashaRows) {

            logger.info("Complete Row: {}", Arrays.toString(row));

            for (int i = 0; i < row.length; i++) {
                logger.info("row[{}] = {} ({})",
                        i,
                        row[i],
                        row[i] != null ? row[i].getClass().getName() : "null");
            }


            if (row[4] != null)
                facilityIDSet.add((Integer) row[4]);

            if (row[0] != null)
                ashaIDSet.add((Integer) row[0]);

        }

        List<Integer> facilityIDs = new ArrayList<>(facilityIDSet);
        List<Integer> ashaIDs = new ArrayList<>(ashaIDSet);

        logger.info("Facility IDs: {}", facilityIDs);
        logger.info("ASHA IDs: {}", ashaIDs);
        result.put("totalAshaCount", ashaIDs.size());

        // 3. Location from first facility
        List<Object[]> facilityRows = dashboardRepo.getFacilityDetails(facilityIDs);
        if (facilityRows != null && !facilityRows.isEmpty()) {
            Object[] fRow = facilityRows.get(0);
            JSONObject location = new JSONObject();
            location.put("state", str(fRow[2]));
            location.put("district", str(fRow[3]));
            location.put("blockOrUlb", str(fRow[4]));
            location.put("locationType", str(fRow[5]));
            result.put("location", location);
        }

        // 4. Build village map (facilityID -> villages)
        Map<Integer, List<JSONObject>> villageMap = new HashMap<>();
        List<Object[]> villageRows = dashboardRepo.getVillagesForFacilities(facilityIDs);
        if (villageRows != null) {
            for (Object[] vRow : villageRows) {
                Integer facID = (Integer) vRow[0];
                JSONObject village = new JSONObject();
                village.put("villageId", vRow[1]);
                village.put("villageName", str(vRow[2]));
                villageMap.computeIfAbsent(facID, k -> new ArrayList<>()).add(village);
            }
        }

        // 5. Get incentive status per ASHA (verified, rejected, pending, totalAmount)
        long overallVerified = 0, overallRejected = 0, overallPending = 0;
        long overallUnclaimed = 0;

        try {
            logger.info("Month: {}", month);
            logger.info("Year: {}", year);

            LocalDate startLocalDate = LocalDate.of(year, month, 1);
            LocalDate endLocalDate = startLocalDate.plusMonths(1);

            logger.info("startLocalDate {}", startLocalDate);
            logger.info("endLocalDate {}", endLocalDate);

            Timestamp startDate = Timestamp.valueOf(startLocalDate.atStartOfDay());
            Timestamp endDate = Timestamp.valueOf(endLocalDate.atStartOfDay());
            logger.info("Asha ID" + ashaIDs);

            if(stateId.equals(StateCode.AM.getStateCode())){
                List<Object[]> statusRows = dashboardRepo.getIncentiveStatusByAshaIds(ashaIDs, startDate, endDate);
                if (statusRows != null) {
                    for (Object[] sRow : statusRows) {
                        long verified = ((Number) sRow[2]).longValue();
                        long rejected = ((Number) sRow[3]).longValue();
                        long pending = ((Number) sRow[4]).longValue();

                        if (verified > 0) overallVerified += 1;
                        if (rejected > 0) overallRejected += 1;
                        if (pending > 0) overallPending += 1;
                    }
                }
                List<Object[]> unclaimedRows = dashboardRepo.getUnclaimedCountByAshaIds(ashaIDs, startDate, endDate);
                if (unclaimedRows != null) {
                    for (Object[] uRow : unclaimedRows) {
                        long count = ((Number) uRow[1]).longValue();
                        if (count > 0) overallUnclaimed += 1;
                    }
                }
            }else  if(stateId.equals(StateCode.CG.getStateCode())){
                if("ASHA Supervisor".equalsIgnoreCase(rollName)){
                    List<Object[]> statusRows = dashboardRepo.getDefaultIncentiveStatusByAshaIds(ashaIDs, startDate, endDate);
                    if (statusRows != null) {
                        for (Object[] sRow : statusRows) {
                            long verified = ((Number) sRow[5]).longValue();
                            long rejected = ((Number) sRow[3]).longValue();
                            long pending = ((Number) sRow[4]).longValue();


                            if (verified > 0) overallVerified += 1;
                            if (rejected > 0) overallRejected += 1;
                            if (pending > 0) overallPending += 1;
                        }
                    }
                }else if("ANM".equalsIgnoreCase(rollName)){
                    List<Object[]> statusRows = dashboardRepo.getIncentiveStatusByAshaIdsForAnm(ashaIDs, startDate, endDate);
                    if (statusRows != null) {
                        for (Object[] sRow : statusRows) {
                            long verified = ((Number) sRow[2]).longValue();
                            long rejected = ((Number) sRow[3]).longValue();
                            long pending = ((Number) sRow[5]).longValue();

                            if (verified > 0) overallVerified += 1;
                            if (rejected > 0) overallRejected += 1;
                            if (pending > 0) overallPending += 1;
                        }
                    }
                }

                List<Object[]> unclaimedRows = dashboardRepo.getUnclaimedCountByAshaIds(ashaIDs, startDate, endDate);
                if (unclaimedRows != null) {
                    for (Object[] uRow : unclaimedRows) {
                        long count = ((Number) uRow[1]).longValue();
                        if (count > 0) overallUnclaimed += 1;
                    }
                }
            }


        } catch (Exception e) {
            logger.error("Error fetching incentive status: " + e.getMessage(), e);

        }



        // Overall incentive summary across all ASHAs
        JSONObject overallSummary = new JSONObject();
        overallSummary.put("verified", overallVerified);
        overallSummary.put("rejected", overallRejected);
        overallSummary.put("pending", overallPending);
        overallSummary.put("overDue", 0);
        overallSummary.put("unclaimed", overallUnclaimed);
        result.put("incentiveSummary", overallSummary);

        // 7. Build facilities array with nested ASHAs
        Map<Integer, List<Object[]>> ashasByFacility = new HashMap<>();
        for (Object[] row : ashaRows) {
            Integer facID = (Integer) row[4];
            ashasByFacility.computeIfAbsent(facID, k -> new ArrayList<>()).add(row);
        }

        Map<Integer, Object[]> facilityDetailsMap = new HashMap<>();
        if (facilityRows != null) {
            for (Object[] fRow : facilityRows) {
                facilityDetailsMap.put((Integer) fRow[0], fRow);
            }
        }

        JSONArray facilitiesArray = new JSONArray();
        for (Integer facID : facilityIDs) {
            JSONObject facility = new JSONObject();
            facility.put("facilityId", facID);

            Object[] fDetails = facilityDetailsMap.get(facID);
            if (fDetails != null) {
                facility.put("facilityName", str(fDetails[1]));
                facility.put("facilityType", str(fDetails[6]));
            }


            // ASHAs at this facility
            JSONArray ashasArray = new JSONArray();
            List<Object[]> facAshaRows = ashasByFacility.get(facID);
            if (facAshaRows != null) {
                for (Object[] row : facAshaRows) {
                    Integer ashaId = (Integer) row[0];
                    JSONObject asha = new JSONObject();
                    asha.put("userId", ashaId);
                    asha.put("fullName", fullName(row[1], row[2]));
                    asha.put("employeeId", str(row[6]).isEmpty() ? JSONObject.NULL : str(row[6]));
                    asha.put("mobile", str(row[7]).isEmpty() ? JSONObject.NULL : str(row[7]));

                    ashasArray.put(asha);
                }
            }

            facility.put("ashaCount", ashasArray.length());
            facilitiesArray.put(facility);
        }

        result.put("facilities", facilitiesArray);
        return result.toString();
    }



    @Override
    public Map<String, Object> getAshasAtFacility(Integer supervisorId, Integer facilityId,
                                                  Integer month, Integer year, Integer approvalStatusID) {
        Integer supervisorstateCode = userService.getUserDetail(supervisorId).getStateId();
        List<Object[]> rows = null;
        LocalDate today = LocalDate.now();


        String roleName = userService.getUserDetail(supervisorId).getRoleName();

        LocalDate currentMonthStartDate = LocalDate.of(year, month, 1);
        LocalDate currentMonthendLocalDate = currentMonthStartDate.plusMonths(1);

        logger.info("Login user role:"+ roleName);

        boolean isCurrentMonth =
                today.getYear() == year &&
                        today.getMonthValue() == month;
        logger.info("approvalStatusID:" + approvalStatusID);

        List<Object[]> superVisorRow = ashaSupervisorLoginRepo.getAllMappedAshas(supervisorId);

        List<Map<String, Object>> ashaList = new ArrayList<>();

        LocalDate startLocalDate = LocalDate.of(year, month, 1);
        LocalDate endLocalDate = startLocalDate.plusMonths(1);

        Timestamp startDate = Timestamp.valueOf(startLocalDate.atStartOfDay());
        Timestamp endDate = Timestamp.valueOf(endLocalDate.atStartOfDay());




        if ("ANM".equalsIgnoreCase(roleName) || "CHO".equalsIgnoreCase(roleName)) {
            logger.info("ANM:" + roleName);

            if (facilityId.equals(0)) {

                List<Integer> facilityIDs = facilityLoginRepo.getUserFacilityIDs(supervisorId);
                rows = facilityLoginRepo.getAshaListByFacilities(facilityIDs);

            } else {

                rows = facilityLoginRepo.getAshaListByFacilities(
                        Collections.singletonList(facilityId));

            }

        } else {
            logger.info("Other:" + roleName);

            if (facilityId.equals(0)) {
                if(supervisorstateCode.equals(StateCode.CG.getStateCode())){
                    if("ASHA Supervisor".equalsIgnoreCase(roleName)){
                        rows = ashaSupervisorLoginRepo.getAshasAtFacilityCg(
                                supervisorId, approvalStatusID, startDate, endDate);
                    }
                }else if(supervisorstateCode.equals(StateCode.AM.getStateCode())){
                    rows = ashaSupervisorLoginRepo.getAshasAtFacility(
                            supervisorId, approvalStatusID, startDate, endDate);
                }
            } else {
                if(supervisorstateCode.equals(StateCode.CG.getStateCode())){
                    if("ASHA Supervisor".equalsIgnoreCase(roleName)){
                        rows = ashaSupervisorLoginRepo.getAshasAtFacilityCg(
                                supervisorId, facilityId, approvalStatusID, startDate, endDate);
                    }
                }else if(supervisorstateCode.equals(StateCode.AM.getStateCode())){
                    rows = ashaSupervisorLoginRepo.getAshasAtFacility(
                            supervisorId, facilityId, approvalStatusID, startDate, endDate);
                }


            }

        }
        logger.info("Other:" + rows);

        long overallVerified = 0, overallRejected = 0, overallPending = 0 , overallUnclaimed=0;

        String facilityName = "";
        String facilityType = "";
        Integer facilityID = null;

        for (Object[] row : superVisorRow) {
            facilityID = (Integer) row[3];
            facilityName = str(row[4]);
            facilityType = str(row[5]);
        }

        for (Object[] row : rows) {

            Map<String, Object> asha = new HashMap<>();

            Integer ashaId = ((Number) row[0]).intValue();
            logger.info("ASHA_ID"+ashaId);
            List<Object[]> countList =null;

            if(supervisorstateCode.equals(StateCode.CG.getStateCode())){
                if("ASHA Supervisor".equalsIgnoreCase(roleName)){
                    if(approvalStatusID.equals(106)){
                        countList = incentiveRecordRepo.getStatusUnclaimedCountByAshaId(ashaId, startDate, endDate);

                    }else {
                        countList = incentiveRecordRepo.getStatusCountByAshaIdOfDefaultActivity(ashaId, startDate, endDate);

                    }

                    logger.info("countList = {}", Arrays.deepToString(countList.toArray()));



                }else  if(("ANM".equalsIgnoreCase(roleName)  || "CHO".equalsIgnoreCase(roleName) )){
                    logger.info("ASHA_ID = {}", ashaId);
                    logger.info("Role = {}", roleName);

                    if(approvalStatusID.equals(106)){
                        countList = incentiveRecordRepo.getStatusUnclaimedCountByAshaId(ashaId, startDate, endDate);

                    }else {
                        countList = incentiveRecordRepo.getStatusCountByAshaId(ashaId, startDate, endDate);

                    }


                    logger.info("Count List = {}", countList);

                }

            }else  if(supervisorstateCode.equals(StateCode.AM.getStateCode())){
                countList = incentiveRecordRepo.getStatusCountByAshaId(ashaId, startDate, endDate);

            }

            Long totalAmount = null;
            if (userService.getUserDetail(ashaId) != null) {
                Integer stateCode = userService.getUserDetail(ashaId).getStateId();
                if(stateCode.equals(StateCode.AM.getStateCode())){
                    totalAmount = incentiveRecordRepo.getTotalAmountByAsha(
                            ashaId, startDate, endDate, approvalStatusID, stateCode);
                }else if(stateCode.equals(StateCode.CG.getStateCode())){
                    if("ASHA Supervisor".equalsIgnoreCase(roleName)){
                       if(approvalStatusID.equals(105)){
                           totalAmount = incentiveRecordRepo.getDefaultActivityTotalAmountByAsha(
                                   ashaId, startDate, endDate, 101, stateCode);
                       }else {
                           totalAmount = incentiveRecordRepo.getDefaultActivityTotalAmountByAsha(
                                   ashaId, startDate, endDate, approvalStatusID, stateCode);
                       }

                    }else if("ANM".equalsIgnoreCase(roleName) || "CHO".equalsIgnoreCase(roleName) ){
                        if(approvalStatusID.equals(102)){
                            totalAmount = incentiveRecordRepo.getTotalAmountByAshaANM(
                                    ashaId, startDate, endDate, approvalStatusID, stateCode);
                        }else {
                            totalAmount = incentiveRecordRepo.getTotalAmountByAsha(
                                    ashaId, startDate, endDate, approvalStatusID, stateCode);
                        }

                    }

                }


            }
            Integer stateCode = userService.getUserDetail(ashaId).getStateId();


            List<IncentiveActivityRecord> incentiveActivityRecord = null;
             if(stateCode.equals(StateCode.AM.getStateCode())){
                 List<IncentiveActivityRecord> dbRecords =
                         incentiveRecordRepo.getRecordsByAsha(ashaId, startDate, endDate);
                 incentiveActivityRecord=   dbRecords
                         .stream()
                         .filter(r -> approvalStatusID == 0 ||
                                 approvalStatusID.equals(r.getApprovalStatus()))
                         .collect(Collectors.toList());
             }else if(stateCode.equals(StateCode.CG.getStateCode())){
                 List<IncentiveActivityRecord> dbRecords =
                         incentiveRecordRepo.getRecordsByAsha(ashaId, startDate, endDate);
                 if("ASHA Supervisor".equalsIgnoreCase(roleName)){
                     if(approvalStatusID.equals(105)){
                         incentiveActivityRecord = dbRecords.stream()
                                 .filter(r -> (r.getApprovalStatus().equals(101) || r.getApprovalStatus().equals(105)) && r.getIsDefaultActivity())
                                 .collect(Collectors.toList());
                     }else if(approvalStatusID.equals(106)){
                         incentiveActivityRecord = dbRecords.stream()
                                 .filter(r ->r.getApprovalStatus().equals(102) && r.getIsDefaultActivity() && !r.getIsClaimed())
                                 .collect(Collectors.toList());
                     } else {
                         incentiveActivityRecord = dbRecords.stream()
                                 .filter(r ->( approvalStatusID == 0 ||
                                         approvalStatusID.equals(r.getApprovalStatus())) && r.getIsDefaultActivity()  && isWithin24Hours(r.getCalimedDate()))
                                 .collect(Collectors.toList());
                     }

                 }else if ("ANM".equalsIgnoreCase(roleName) || "CHO".equalsIgnoreCase(roleName)) {
                     if (approvalStatusID.equals(102)) {
                         incentiveActivityRecord = dbRecords.stream()
                                 .filter(r ->
                                         r.getApprovalStatus().equals(105)
                                                 || (r.getApprovalStatus().equals(102)))
                                 .peek(r -> {
                                     if (r.getApprovalStatus().equals(102)
                                             && isAfter24Hours(r.getCalimedDate())) {
                                         r.setApprovalStatus(105);
                                     }
                                 })
                                 .collect(Collectors.toList());

                     }else if(approvalStatusID.equals(106)){
                         incentiveActivityRecord = dbRecords.stream()
                                 .filter(r->!r.getIsClaimed() && r.getApprovalStatus().equals(102)).collect(Collectors.toList());
                     } else{
                         incentiveActivityRecord = dbRecords.stream()
                             .filter(r -> {

                                 if (approvalStatusID != 0
                                         && !approvalStatusID.equals(r.getApprovalStatus())) {
                                     return false;
                                 }

                                 // 102 should be visible only after 24 hours
                                 if (r.getApprovalStatus().equals(102)) {
                                     return isAfter24Hours(r.getCalimedDate());
                                 }

                                 // 105 should always be visible
                                 return true;
                             })
                             .peek(r -> {
                                 if (r.getApprovalStatus().equals(102)
                                         && isAfter24Hours(r.getCalimedDate())) {
                                     r.setApprovalStatus(105);
                                 }
                             })
                             .collect(Collectors.toList());
                     }

                 }


                 logger.info("CG - Records after approvalStatus filter: {}", incentiveActivityRecord.size());

             }
            logger.info("Final incentiveActivityRecord count: {}", incentiveActivityRecord.size());
            logger.info("Final incentiveActivityRecord: {}",
                    new Gson().toJson(incentiveActivityRecord));

            List<Map<String, Object>> activityList = new ArrayList<>();
            for (IncentiveActivityRecord record : incentiveActivityRecord) {
                Map<String, Object> activity = new HashMap<>();
                activity.put("reason", record.getReason());
                activity.put("otherReason", record.getOtherReason());
                activity.put("approvalDate", record.getApprovalDate());
                activity.put("approvalStatus", record.getApprovalStatus());
                if(record.getVerifiedByUserId()!=null){
                    activity.put("verifiedByUserName", userService.getUserDetail(record.getVerifiedByUserId()).getName());
                    activity.put("verifiedByUserId", record.getVerifiedByUserId());
                    UserServiceRoleDTO roles = userService.getUserDetail(record.getVerifiedByUserId());
                    activity.put("role", (roles != null ) ? roles.getRoleName() : null);


                }
                activity.put("isClaimed", record.getIsClaimed());
                activity.put("claimedDate", record.getCalimedDate());



                activityList.add(activity);
            }

            asha.put("facilityId", facilityID);
            asha.put("facilityName", facilityName);
            asha.put("facilityType", facilityType);
            asha.put("userId", row[0]);
            asha.put("fullName", fullName(row[1], row[2]));
            asha.put("employeeId", str(row[3]).isEmpty() ? null : str(row[3]));
            asha.put("mobile", str(row[4]).isEmpty() ? null : str(row[4]));
            asha.put("gender", str(row[5]).isEmpty() ? null : str(row[5]));
            asha.put("totalAmount", totalAmount);
            asha.put("activities", activityList);



            long pending = 0, verified = 0, rejected = 0 , unclaimedCount = 0 ;


            if (countList != null && !countList.isEmpty()) {
                Object[] counts = countList.get(0);
                logger.info("Count List = {}", Arrays.deepToString(countList.toArray()));
                logger.info("101={}, 102={}, 103={}, 101+105={}",
                        counts[0], counts[1], counts[2], counts[3]);

                if(stateCode.equals(StateCode.CG.getStateCode())){
                    if(roleName.equalsIgnoreCase("ANM")){
                        pending = counts[3] != null ? ((Number) counts[3]).longValue() : 0;
                        verified = counts[0] != null ? ((Number) counts[0]).longValue() : 0;
                        rejected = counts[2] != null ? ((Number) counts[2]).longValue() : 0;


                    }else if(roleName.equalsIgnoreCase("ASHA Supervisor")) {
                        if (approvalStatusID.equals(105)) {
                            // 101 + 105
                            verified = counts[3] != null ? ((Number) counts[3]).longValue() : 0;
                        } else {
                            // only 101
                            verified = counts[0] != null ? ((Number) counts[0]).longValue() : 0;
                        }
                        if(approvalStatusID.equals(106)){
                            unclaimedCount = counts[1] != null ? ((Number) counts[1]).longValue() : 0;

                        }else {
                            pending = counts[1] != null ? ((Number) counts[1]).longValue() : 0;

                        }
                        rejected = counts[2] != null ? ((Number) counts[2]).longValue() : 0;


                    }
                }else {
                    verified = counts[0] != null ? ((Number) counts[0]).longValue() : 0;
                    pending = counts[1] != null ? ((Number) counts[1]).longValue() : 0;
                    rejected = counts[2] != null ? ((Number) counts[2]).longValue() : 0;
                }

            }



            if (verified > 0) overallVerified += 1;
            if (rejected > 0) overallRejected += 1;
            if (pending > 0) overallPending += 1;
            if (unclaimedCount > 0) overallUnclaimed += 1;

            asha.put("pending", pending);
            asha.put("verified", verified);
            asha.put("rejected", rejected);
            asha.put("unClaimed", unclaimedCount);

            int approvalStatus = 0;

            if (!activityList.isEmpty()) {
                approvalStatus = (int) activityList.get(0).get("approvalStatus");
            }
            if (totalAmount == null || totalAmount <= 0) continue;

            if (pending == 0 && verified == 0 && rejected == 0 && unclaimedCount == 0) continue;
            if (approvalStatusID.equals(0)) {
                asha.put("approvalStatus", approvalStatus);

            } else {
                asha.put("approvalStatus", approvalStatusID);

            }

            ashaList.add(asha);
        }

        Map<String, Object> response = new HashMap<>();

        Map<String, Object> approvalStatus = new HashMap<>();
        approvalStatus.put("verified", overallVerified);
        approvalStatus.put("pending", overallPending);
        approvalStatus.put("rejected", overallRejected);
        approvalStatus.put("unClaimed", overallUnclaimed);

        response.put("approvalStatus", approvalStatus);
        response.put("data", ashaList);
        response.put("statusCode", 200);

        return response;
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
    private boolean isWithin24Hours(Timestamp claimedDate) {
        if (claimedDate == null) {
            return false;
        }

        long diff = System.currentTimeMillis() - claimedDate.getTime();
        return diff <= 24 * 60 * 60 * 1000L;
    }


    private String getGroupNameByState(Integer stateCode) {
        switch (stateCode) {
            case 5:
                return "! ACTIVITY";
            default:
                return "ACTIVITY";
        }
    }

    @Transactional
    public int updateApprovalStatus(Integer ashaId,
                                    Integer month,
                                    Integer year,
                                    Integer approvalStatus,
                                    String incentiveIds,
                                    String reason,
                                    String otherReason,
                                    String token) {
        try {
            String title = null;
            String body = null;
            Timestamp approvalDate = Timestamp.valueOf(LocalDateTime.now());

            LocalDate startLocalDate = LocalDate.of(year, month, 1);
            LocalDate endLocalDate = startLocalDate.plusMonths(1);

            Timestamp startDate = Timestamp.valueOf(startLocalDate.atStartOfDay());
            Timestamp endDate = Timestamp.valueOf(endLocalDate.atStartOfDay());

            Integer ashaSupervisorUserId = jwtUtil.extractUserId(token);
            logger.info("Asha Supervisor User Id : {}", ashaSupervisorUserId);
            UserServiceRoleDTO ashaSupervisorDetails = userService.getUserDetail(ashaSupervisorUserId);

            int updatedCount = 0;

            if (approvalStatus.equals(IncentiveApprovalStatus.REJECTED.getCode())) {
                updatedCount = incentiveRecordRepo.updateApprovalStatusById(
                        approvalStatus,
                        ashaSupervisorUserId,
                        ashaSupervisorDetails.getUserName(),
                        reason,
                        approvalDate,
                        otherReason
                );
            } else {

                if (ashaSupervisorDetails.getStateId().equals(StateCode.AM.getStateCode())) {

                    updatedCount = incentiveRecordRepo.updateApprovalStatusByAshaAndDateRange(
                            ashaId, approvalStatus, startDate, endDate,
                            approvalDate, ashaSupervisorUserId,
                            ashaSupervisorDetails.getUserName());

                } else if(ashaSupervisorDetails.getStateId().equals(StateCode.CG.getStateCode())){

                    if ("ASHA Supervisor".equalsIgnoreCase(ashaSupervisorDetails.getRoleName())) {

                        updatedCount = incentiveRecordRepo.updateApprovalStatusByAshaAndDateRange(
                                ashaId, 105, startDate, endDate,
                                approvalDate, ashaSupervisorUserId,
                                ashaSupervisorDetails.getUserName());

                    } else if("ANM".equalsIgnoreCase(ashaSupervisorDetails.getRoleName())){

                        updatedCount = incentiveRecordRepo.updateApprovalStatusByAshaAndDateRangeForDefaultActivity(
                                ashaId, approvalStatus, startDate, endDate,
                                approvalDate, ashaSupervisorUserId,
                                ashaSupervisorDetails.getUserName());

                    }
                }
            }

            if (updatedCount > 0) {
                sendApprovalNotification(
                        approvalStatus,
                        ashaId,
                        month,
                        year,
                        reason,
                        otherReason,
                        ashaSupervisorUserId,
                        ashaSupervisorDetails
                );
            }

            return updatedCount;

        } catch (Exception e) {
            logger.error("Update claim :" + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }


    private void sendApprovalNotification(
            Integer approvalStatus,
            Integer ashaId,
            Integer month,
            Integer year,
            String reason,
            String otherReason,
            Integer supervisorId,
            UserServiceRoleDTO supervisor) {

        String title;
        String body;

        if (approvalStatus.equals(IncentiveApprovalStatus.REJECTED.getCode())) {

            title = "Incentive Rejected";

            body = "Incentive claim for " + Month.of(month).name() + " " + year + " has been rejected.";

            notificationService.sendNotification(
                    "FLW", "NA",
                    title,
                    body,
                    "INCENTIVE_REJECTED",
                    "INCENTIVE",
                    supervisorId);

            body = supervisor.getName() + " has rejected your incentive claim for "
                    + Month.of(month).name() + " " + year + "due to "+reason+" "+otherReason;

            notificationService.sendNotification(
                    "FLW", "NA",
                    title,
                    body,
                    "INCENTIVE_REJECTED",
                    "INCENTIVE",
                    ashaId);

        } else {

            title = "Incentive Approved";

            body = "Incentive claim for " + Month.of(month).name() + " " + year + " has been approved.";

            notificationService.sendNotification(
                    "FLW", "NA",
                    title,
                    body,
                    "INCENTIVE_CLAIMED",
                    "INCENTIVE",
                    supervisorId);

            body = supervisor.getName() + " has approved your incentive claim for "
                    + Month.of(month).name() + " " + year;

            notificationService.sendNotification(
                    "FLW", "NA",
                    title,
                    body,
                    "INCENTIVE_CLAIMED",
                    "INCENTIVE",
                    ashaId);
        }
    }

    private JSONObject buildEmptyIncentiveSummary() {
        JSONObject summary = new JSONObject();
        summary.put("verified", 0);
        summary.put("rejected", 0);
        summary.put("pending", 0);
        summary.put("totalAmount", 0);
        return summary;
    }

    private String str(Object val) {
        return val != null ? val.toString() : "";
    }

    private String fullName(Object first, Object last) {
        return (str(first) + " " + str(last)).trim();
    }
}