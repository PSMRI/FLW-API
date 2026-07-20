package com.iemr.flw.service.impl;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.iemr.flw.domain.iemr.IncentiveActivityRecord;
import com.iemr.flw.dto.iemr.UserServiceRoleDTO;
import com.iemr.flw.masterEnum.IncentiveApprovalStatus;
import com.iemr.flw.repo.iemr.*;
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

        // 2. Get all ASHAs with facility info
        List<Object[]> ashaRows = dashboardRepo.getAshasWithFacilityInfo(supervisorUserID);
        if (ashaRows == null || ashaRows.isEmpty()) {
            result.put("totalAshaCount", 0);
            result.put("incentiveSummary", buildEmptyIncentiveSummary());
            result.put("facilities", new JSONArray());
            return result.toString();
        }

        // Collect distinct facility IDs and ASHA IDs
        Set<Integer> facilityIDSet = new HashSet<>();
        Set<Integer> ashaIDSet = new HashSet<>();
        for (Object[] row : ashaRows) {
            if (row[3] != null)
                facilityIDSet.add((Integer) row[3]);
            if (row[0] != null)
                ashaIDSet.add((Integer) row[0]);
        }
        List<Integer> facilityIDs = new ArrayList<>(facilityIDSet);
        List<Integer> ashaIDs = new ArrayList<>(ashaIDSet);

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
            Integer facID = (Integer) row[3];
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
                    asha.put("gender", str(row[8]).isEmpty() ? JSONObject.NULL : str(row[8]));

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
        List<Object[]> rows;
        logger.info("approvalStatusID:" + approvalStatusID);

        List<Object[]> superVisorRow = ashaSupervisorLoginRepo.getAllMappedAshas(supervisorId);

        List<Map<String, Object>> ashaList = new ArrayList<>();

        LocalDate startLocalDate = LocalDate.of(year, month, 1);
        LocalDate endLocalDate = startLocalDate.plusMonths(1);

        Timestamp startDate = Timestamp.valueOf(startLocalDate.atStartOfDay());
        Timestamp endDate = Timestamp.valueOf(endLocalDate.atStartOfDay());

        if (facilityId.equals(0)) {
            rows = ashaSupervisorLoginRepo.getAshasAtFacility(
                    supervisorId, approvalStatusID, startDate, endDate);
        } else {
            rows = ashaSupervisorLoginRepo.getAshasAtFacility(
                    supervisorId, facilityId, approvalStatusID, startDate, endDate);
        }

        long overallVerified = 0, overallRejected = 0, overallPending = 0;

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

            List<Object[]> countList = incentiveRecordRepo.getStatusCountByAshaId(ashaId, startDate, endDate);
            Long totalAmount = null;
            if (userService.getUserDetail(ashaId) != null) {
                Integer stateCode = userService.getUserDetail(ashaId).getStateId();
                totalAmount = incentiveRecordRepo.getTotalAmountByAsha(
                        ashaId, startDate, endDate, approvalStatusID, stateCode);

            }


            List<IncentiveActivityRecord> incentiveActivityRecord =
                    incentiveRecordRepo.getRecordsByAsha(ashaId, startDate, endDate)
                            .stream()
                            .filter(r -> approvalStatusID == 0 ||
                                    approvalStatusID.equals(r.getApprovalStatus()))
                            .collect(Collectors.toList());
            List<Map<String, Object>> activityList = new ArrayList<>();
            for (IncentiveActivityRecord record : incentiveActivityRecord) {
                Map<String, Object> activity = new HashMap<>();
                activity.put("reason", record.getReason());
                activity.put("otherReason", record.getOtherReason());
                activity.put("approvalDate", record.getApprovalDate());
                activity.put("approvalStatus", record.getApprovalStatus());
                if(record.getVerifiedByUserId()!=null){
                    activity.put("verifiedByUserName", userService.getUserDetail(record.getVerifiedByUserId()).getName());

                }
                activity.put("verifiedByUserId", record.getVerifiedByUserId());
                activity.put("isClaimed", record.getIsClaimed());
                activity.put("claimedDate", record.getCalimedDate());

                UserServiceRoleDTO roles = userService.getUserDetail(record.getVerifiedByUserId());
                activity.put("role", (roles != null ) ? roles.getRoleName() : null);

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

            long pending = 0, verified = 0, rejected = 0;
            if (countList != null && !countList.isEmpty()) {
                Object[] counts = countList.get(0);
                verified = counts[0] != null ? ((Number) counts[0]).longValue() : 0;
                pending = counts[1] != null ? ((Number) counts[1]).longValue() : 0;
                rejected = counts[2] != null ? ((Number) counts[2]).longValue() : 0;
            }

            if (verified > 0) overallVerified += 1;
            if (rejected > 0) overallRejected += 1;
            if (pending > 0) overallPending += 1;

            asha.put("pending", pending);
            asha.put("verified", verified);
            asha.put("rejected", rejected);

            int approvalStatus = 0;

            if (!activityList.isEmpty()) {
                approvalStatus = (int) activityList.get(0).get("approvalStatus");
            }
            if (pending == 0 && verified == 0 && rejected == 0) continue;
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

        response.put("approvalStatus", approvalStatus);
        response.put("data", ashaList);
        response.put("statusCode", 200);

        return response;
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
            String ashaSupervisorUsername = userService.getUserDetail(ashaSupervisorUserId).getUserName();

            if (approvalStatus.equals(IncentiveApprovalStatus.REJECTED.getCode())) {
                int totalUpdated = 0;

                totalUpdated += incentiveRecordRepo.updateApprovalStatusById(
                        approvalStatus,
                        ashaSupervisorUserId,
                        ashaSupervisorUsername,
                        reason,
                        approvalDate,
                        otherReason
                );
                if (totalUpdated > 0) {

                    Map<String, String> data = new HashMap<>();
                    data.put("notification_type", "INCENTIVE_APPROVAL");
                    data.put("nav_id", "INCENTIVE_HISTORY");
                    data.put("sender_user_id", String.valueOf(ashaSupervisorUserId));
                    data.put("receiver_user_id", String.valueOf(ashaId));
                    data.put("month", String.valueOf(month));
                    data.put("year", String.valueOf(year));
                    data.put("approval_status", String.valueOf(approvalStatus));
                    data.put("priority", "HIGH");



                    if (approvalStatus.equals(IncentiveApprovalStatus.REJECTED.getCode())) {
                        title = "Incentive Rejected";
                        body = "Your incentive claim for " + Month.of(month).name() + " " + year + " has been rejected.";
                        data.put("reason", reason == null ? "" : reason);
                        data.put("other_reason", otherReason == null ? "" : otherReason);
                    }


                    notificationService.sendNotification(
                            "FLW",
                            "user_" + ashaId,   // ya user ka topic
                            title,
                            body+data,
                            "","",ashaId
                    );
                }
                return totalUpdated;
            }
            int updatedCount = incentiveRecordRepo.updateApprovalStatusByAshaAndDateRange(
                    ashaId,
                    approvalStatus,
                    startDate,
                    endDate,
                    approvalDate,
                    ashaSupervisorUserId,
                    ashaSupervisorUsername
            );
            if (updatedCount > 0) {

                Map<String, String> data = new HashMap<>();
                data.put("notification_type", "INCENTIVE_APPROVAL");
                data.put("nav_id", "INCENTIVE_HISTORY");
                data.put("sender_user_id", String.valueOf(ashaSupervisorUserId));
                data.put("receiver_user_id", String.valueOf(ashaId));
                data.put("month", String.valueOf(month));
                data.put("year", String.valueOf(year));
                data.put("approval_status", String.valueOf(approvalStatus));
                data.put("priority", "HIGH");
                title = "Incentive Approved";

                if (approvalStatus.equals(IncentiveApprovalStatus.VERIFIED.getCode())) {
                    body = "Your incentive claim for " + Month.of(month).name() + " " + year + " has been approved.";
                }


                notificationService.sendNotification(
                        "FLW",
                        "user_" + ashaId,   // ya user ka topic
                        title,
                        body,
                        "","",ashaId
                );
            }
             return updatedCount;
        } catch (Exception e) {
            logger.error("Update claim :" + e.getMessage());
            e.printStackTrace();
            return 0;
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