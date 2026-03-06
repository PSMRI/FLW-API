package com.iemr.flw.service;

import java.util.List;
import java.util.Map;

public interface SupervisorDashboardService {

	String getSupervisorDashboard(Integer supervisorUserID, Integer month, Integer year);

	Map<String, Object> getAshasAtFacility(Integer supervisorId, Integer facilityId, Integer month, Integer year, Integer approvalStatusID);

	public int updateApprovalStatus(Integer ashaId,
									Integer month,
									Integer year,
									Integer approvalStatus,
									String incentiveIds,
									String reason,
									String otherReason,
									String token);
}
