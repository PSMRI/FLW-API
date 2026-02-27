package com.iemr.flw.repo.iemr;


import com.iemr.flw.domain.iemr.IncentiveActivity;
import com.iemr.flw.domain.iemr.IncentiveActivityRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface IncentivesRepo extends JpaRepository<IncentiveActivity, Long> {

    @Query("select inc from IncentiveActivity inc where inc.name = :name and inc.group = :group and inc.isDeleted = false")
    IncentiveActivity findIncentiveMasterByNameAndGroup(@Param("name") String name, @Param("group") String group);

    @Query("SELECT a FROM IncentiveActivity a " +
            "WHERE a.id = :activityId AND " +
            "((:isActivity = true AND a.group = 'ACTIVITY') OR (:isActivity = false AND a.group <> 'ACTIVITY'))")
    IncentiveActivity findIncentiveMasterById(
            @Param("activityId") Long activityId,
            @Param("isActivity") boolean isActivity
    );


    @Query("select record from IncentiveActivityRecord record where record.activityId = :id and record.createdDate = :createdDate and record.benId = :benId")
    IncentiveActivityRecord findRecordByActivityIdCreatedDateBenId(@Param("id") Long id, @Param("createdDate") Timestamp createdDate, @Param("benId") Long benId);

    @Query(value = "SELECT " +
            "SUM(CASE WHEN i.approval_status = 101 THEN 1 ELSE 0 END) as pendingCount, " +
            "SUM(CASE WHEN i.approval_status = 102 THEN 1 ELSE 0 END) as approvalCount, " +
            "SUM(CASE WHEN i.approval_status = 103 THEN 1 ELSE 0 END) as rejectCount " +
            "FROM m_incentive_activity i " +
            "WHERE i.asha_id = :ashaId " +
            "AND i.is_deleted = false",
            nativeQuery = true)
    Object[] getStatusCountByAshaId(@Param("ashaId") Integer ashaId);
}
