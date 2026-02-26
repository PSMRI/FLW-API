package com.iemr.flw.repo.iemr;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iemr.flw.domain.iemr.IncentiveActivityRecord;

@Repository
public interface SupervisorDashboardRepo extends JpaRepository<IncentiveActivityRecord, Long> {

	// Get all ASHA userIDs mapped to a supervisor
	@Query(value = "SELECT DISTINCT asm.ashaUserID "
			+ "FROM asha_supervisor_mapping asm "
			+ "WHERE asm.supervisorUserID = :supervisorUserID "
			+ "AND asm.deleted = false", nativeQuery = true)
	List<Integer> getAshaIdsBySupervisor(@Param("supervisorUserID") Integer supervisorUserID);

	// Get ASHAs with facility info for a supervisor
	@Query(value = "SELECT DISTINCT asm.ashaUserID, u.FirstName, u.LastName, "
			+ "f.FacilityID, f.FacilityName, "
			+ "COALESCE(ft.FacilityTypeName,'') AS facilityTypeName, "
			+ "COALESCE(u.AgentID,'') AS agentID, "
			+ "COALESCE(u.EmergencyContactNo,'') AS mobile, "
			+ "COALESCE(g.GenderName,'') AS gender "
			+ "FROM asha_supervisor_mapping asm "
			+ "JOIN m_User u ON u.UserID = asm.ashaUserID AND u.Deleted = false "
			+ "JOIN m_facility f ON f.FacilityID = asm.facilityID AND f.Deleted = false "
			+ "LEFT JOIN m_facilitytype ft ON ft.FacilityTypeID = f.FacilityTypeID "
			+ "LEFT JOIN m_gender g ON g.GenderID = u.GenderID "
			+ "WHERE asm.supervisorUserID = :supervisorUserID "
			+ "AND asm.deleted = false", nativeQuery = true)
	List<Object[]> getAshasWithFacilityInfo(@Param("supervisorUserID") Integer supervisorUserID);

	// Get facility details with geo names
	@Query(value = "SELECT DISTINCT f.FacilityID, f.FacilityName, "
			+ "COALESCE(s.StateName,'') AS stateName, "
			+ "COALESCE(d.DistrictName,'') AS districtName, "
			+ "COALESCE(b.BlockName,'') AS blockName, "
			+ "COALESCE(f.RuralUrban,'') AS ruralUrban, "
			+ "COALESCE(ft.FacilityTypeName,'') AS facilityTypeName "
			+ "FROM m_facility f "
			+ "LEFT JOIN m_state s ON s.StateID = f.StateID "
			+ "LEFT JOIN m_district d ON d.DistrictID = f.DistrictID "
			+ "LEFT JOIN m_districtblock b ON b.BlockID = f.BlockID "
			+ "LEFT JOIN m_facilitytype ft ON ft.FacilityTypeID = f.FacilityTypeID "
			+ "WHERE f.FacilityID IN :facilityIDs AND f.Deleted = false", nativeQuery = true)
	List<Object[]> getFacilityDetails(@Param("facilityIDs") List<Integer> facilityIDs);

	// Villages mapped to facilities
	@Query(value = "SELECT fvm.FacilityID, fvm.DistrictBranchID, dbm.VillageName "
			+ "FROM facility_village_mapping fvm "
			+ "JOIN m_DistrictBranchMapping dbm ON dbm.DistrictBranchID = fvm.DistrictBranchID "
			+ "WHERE fvm.FacilityID IN :facilityIDs AND fvm.Deleted = false", nativeQuery = true)
	List<Object[]> getVillagesForFacilities(@Param("facilityIDs") List<Integer> facilityIDs);

	// Incentive status counts per ASHA: verified, rejected, pending, total amount
	@Query(value = "SELECT iar.asha_id, "
			+ "COUNT(*) AS totalRecords, "
			+ "SUM(CASE WHEN iar.is_eligible = true THEN 1 ELSE 0 END) AS verified, "
			+ "SUM(CASE WHEN iar.is_eligible = false THEN 1 ELSE 0 END) AS rejected, "
			+ "SUM(CASE WHEN iar.is_eligible IS NULL THEN 1 ELSE 0 END) AS pending, "
			+ "COALESCE(SUM(CASE WHEN iar.is_eligible = true THEN iar.amount ELSE 0 END), 0) AS totalAmount "
			+ "FROM incentive_activity_record iar "
			+ "WHERE iar.asha_id IN :ashaIds "
			+ "GROUP BY iar.asha_id", nativeQuery = true)
	List<Object[]> getIncentiveStatusByAshaIds(@Param("ashaIds") List<Integer> ashaIds);

	// Incentive activity history per ASHA (recent records)
	@Query(value = "SELECT iar.asha_id, iar.name AS activityName, "
			+ "iar.amount, iar.is_eligible, iar.created_date "
			+ "FROM incentive_activity_record iar "
			+ "WHERE iar.asha_id IN :ashaIds "
			+ "ORDER BY iar.created_date DESC", nativeQuery = true)
	List<Object[]> getIncentiveHistoryByAshaIds(@Param("ashaIds") List<Integer> ashaIds);

	// Supervisor user details
	@Query(value = "SELECT u.UserID, u.FirstName, u.LastName, "
			+ "COALESCE(u.AgentID,'') AS agentID, "
			+ "COALESCE(u.EmergencyContactNo,'') AS mobile, "
			+ "COALESCE(g.GenderName,'') AS gender "
			+ "FROM m_User u "
			+ "LEFT JOIN m_gender g ON g.GenderID = u.GenderID "
			+ "WHERE u.UserID = :userId AND u.Deleted = false", nativeQuery = true)
	List<Object[]> getSupervisorUserDetails(@Param("userId") Integer userId);
}
