package com.iemr.flw.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iemr.flw.repo.iemr.SupervisorDashboardRepo;
import com.iemr.flw.service.SupervisorDashboardService;

@Service
public class SupervisorDashboardServiceImpl implements SupervisorDashboardService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	@Autowired
	private SupervisorDashboardRepo dashboardRepo;

	@Override
	public String getSupervisorDashboard(Integer supervisorUserID) {
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
		Map<Integer, JSONObject> incentiveStatusMap = new HashMap<>();
		long overallVerified = 0, overallRejected = 0, overallPending = 0, overallTotalAmount = 0;
		try {
			List<Object[]> statusRows = dashboardRepo.getIncentiveStatusByAshaIds(ashaIDs);
			if (statusRows != null) {
				for (Object[] sRow : statusRows) {
					Integer ashaId = (Integer) sRow[0];
					long total = ((Number) sRow[1]).longValue();
					long verified = ((Number) sRow[2]).longValue();
					long rejected = ((Number) sRow[3]).longValue();
					long pending = ((Number) sRow[4]).longValue();
					long totalAmount = ((Number) sRow[5]).longValue();

					JSONObject status = new JSONObject();
					status.put("totalRecords", total);
					status.put("verified", verified);
					status.put("rejected", rejected);
					status.put("pending", pending);
					status.put("totalAmount", totalAmount);
					incentiveStatusMap.put(ashaId, status);

					overallVerified += verified;
					overallRejected += rejected;
					overallPending += pending;
					overallTotalAmount += totalAmount;
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
		overallSummary.put("totalAmount", overallTotalAmount);
		result.put("incentiveSummary", overallSummary);

		// 6. Get incentive history per ASHA
		Map<Integer, List<JSONObject>> historyMap = new HashMap<>();
		try {
			List<Object[]> historyRows = dashboardRepo.getIncentiveHistoryByAshaIds(ashaIDs);
			if (historyRows != null) {
				for (Object[] hRow : historyRows) {
					Integer ashaId = (Integer) hRow[0];
					JSONObject record = new JSONObject();
					record.put("activityName", str(hRow[1]));
					record.put("amount", hRow[2]);
					String status;
					if (hRow[3] == null) {
						status = "pending";
					} else if ((Boolean) hRow[3]) {
						status = "verified";
					} else {
						status = "rejected";
					}
					record.put("status", status);
					record.put("createdDate", hRow[4] != null ? hRow[4].toString() : JSONObject.NULL);
					historyMap.computeIfAbsent(ashaId, k -> new ArrayList<>()).add(record);
				}
			}
		} catch (Exception e) {
			logger.error("Error fetching incentive history: " + e.getMessage(), e);
		}

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

			// Villages
			JSONArray villages = new JSONArray();
			List<JSONObject> vList = villageMap.get(facID);
			if (vList != null) {
				for (JSONObject v : vList)
					villages.put(v);
			}
			facility.put("villages", villages);

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

					// Individual ASHA incentive status
					JSONObject incentive = new JSONObject();
					JSONObject ashaStatus = incentiveStatusMap.get(ashaId);
					if (ashaStatus != null) {
						incentive.put("verified", ashaStatus.get("verified"));
						incentive.put("rejected", ashaStatus.get("rejected"));
						incentive.put("pending", ashaStatus.get("pending"));
						incentive.put("totalAmount", ashaStatus.get("totalAmount"));
					} else {
						incentive.put("verified", 0);
						incentive.put("rejected", 0);
						incentive.put("pending", 0);
						incentive.put("totalAmount", 0);
					}

					// History with status per record
					JSONArray history = new JSONArray();
					List<JSONObject> hList = historyMap.get(ashaId);
					if (hList != null) {
						for (JSONObject h : hList)
							history.put(h);
					}
					incentive.put("history", history);

					asha.put("incentive", incentive);
					ashasArray.put(asha);
				}
			}

			facility.put("ashaCount", ashasArray.length());
			facility.put("ashas", ashasArray);
			facilitiesArray.put(facility);
		}

		result.put("facilities", facilitiesArray);
		return result.toString();
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
