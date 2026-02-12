package com.iemr.flw.repo.iemr;


import com.iemr.flw.domain.iemr.IncentiveActivityRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface IncentiveRecordRepo extends JpaRepository<IncentiveActivityRecord, Long> {

    @Query("select record from IncentiveActivityRecord record where record.activityId = :id and record.createdDate = :createdDate and record.benId = :benId")
    IncentiveActivityRecord findRecordByActivityIdCreatedDateBenId(@Param("id") Long id, @Param("createdDate") Timestamp createdDate, @Param("benId") Long benId);

    Optional<IncentiveActivityRecord> findById(Long id);


    @Query("select record from IncentiveActivityRecord record where record.activityId = :id and record.createdDate = :createdDate and record.benId = :benId and record.ashaId = :ashaId")
    IncentiveActivityRecord findRecordByActivityIdCreatedDateBenId(@Param("id") Long id, @Param("createdDate") Timestamp createdDate, @Param("benId") Long benId,@Param("ashaId") Integer ashaId);


    @Query("SELECT record FROM IncentiveActivityRecord record " +
            "WHERE record.activityId = :id " +
            "AND record.createdDate BETWEEN :startDate AND :endDate " +
            "AND record.benId = :benId " +   // ← space added here
            "AND record.ashaId = :ashaId")
    IncentiveActivityRecord findRecordByActivityIdCreatedDateBenId(
            @Param("id") Long id,
            @Param("startDate") Timestamp startDate,
            @Param("endDate") Timestamp endDate,
            @Param("benId") Long benId,
            @Param("ashaId") Integer ashaId
    );

    @Query("select record from IncentiveActivityRecord record where record.ashaId = :ashaId and record.startDate >= :fromDate and record.startDate <= :toDate and record.endDate >= :fromDate and record.endDate <= :toDate ")
    List<IncentiveActivityRecord> findRecordsByAsha(@Param("ashaId") Integer ashaId, @Param("fromDate") Timestamp fromDate,@Param("toDate") Timestamp toDate);



    @Query("select record from IncentiveActivityRecord record where record.ashaId = :ashaId")
    List<IncentiveActivityRecord> findRecordsByAsha(@Param("ashaId") Integer ashaId);
}
