package com.iemr.flw.repo.iemr;


import com.iemr.flw.domain.iemr.IncentiveActivityRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface IncentiveRecordRepo extends JpaRepository<IncentiveActivityRecord, Long> {

    @Query("select record from IncentiveActivityRecord record where record.activityId = :id and record.createdDate = :createdDate and record.benId = :benId")
    IncentiveActivityRecord findRecordByActivityIdCreatedDateBenId(@Param("id") Long id, @Param("createdDate") Timestamp createdDate, @Param("benId") Long benId);

    @Query("select record from IncentiveActivityRecord record where record.userId = :ashaId and record.startDate >= :fromDate and record.startDate <= :toDate and record.endDate >= :fromDate and record.endDate <= :toDate ")
    List<IncentiveActivityRecord> findRecordsByAsha(@Param("asha_id") Integer ashaId, @Param("fromDate") Timestamp fromDate,@Param("toDate") Timestamp toDate);
}
