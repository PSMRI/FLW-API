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

    @Query("select record from IncentiveActivityRecord record where record.activityId = :id and record.createdDate = :createdDate and record.benId = :benId")
    IncentiveActivityRecord findRecordByActivityIdCreatedDateBenId(@Param("id") Long id, @Param("createdDate") Timestamp createdDate, @Param("benId") Long benId);
}
