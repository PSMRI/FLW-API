package com.iemr.flw.repo.iemr;


import com.iemr.flw.domain.iemr.IncentiveActivityRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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

    @Query("SELECT " +
            "SUM(CASE WHEN record.approvalStatus = 101 THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN record.approvalStatus = 102 THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN record.approvalStatus = 103 THEN 1 ELSE 0 END) " +
            "FROM IncentiveActivityRecord record " +
            "WHERE record.ashaId = :ashaId " +
            "AND record.createdDate >= :startDate " +
            "AND record.createdDate < :endDate")
    List<Object[]> getStatusCountByAshaId(
            @Param("ashaId") Integer ashaId,
            @Param("startDate") Timestamp startDate,
            @Param("endDate") Timestamp endDate);


    @Query("SELECT COALESCE(SUM(record.amount), 0) " +
            "FROM IncentiveActivityRecord record " +
            "WHERE record.ashaId = :ashaId " +
            "AND record.startDate <= :toDate " +
            "AND record.endDate >= :fromDate")
    Long getTotalAmountByAsha(
            @Param("ashaId") Integer ashaId,
            @Param("fromDate") Timestamp fromDate,
            @Param("toDate") Timestamp toDate);

    @Modifying
    @Transactional
    @Query(value = "UPDATE incentive_activity_record iar "
            + "SET iar.approval_status = :approvalStatus "
            + "SET iar.verifiedByUserId = :ashaSupervisorUserId "
            + "SET iar.verifiedByUserName = :ashaSupervisorUserName "
            + "WHERE iar.asha_id = :ashaId "
            + "AND iar.created_date >= :startDate "
            + "AND iar.created_date < :endDate",
            nativeQuery = true)
    int updateApprovalStatusByAshaAndDateRange(
            @Param("ashaId") Integer ashaId,
            @Param("approvalStatus") Integer approvalStatus,
            @Param("startDate") Timestamp startDate,
            @Param("endDate") Timestamp endDate,
            @Param("ashaSupervisorUserId") Integer ashaSupervisorUserId,
            @Param("ashaSupervisorUserName") String ashaSupervisorUserName);


    @Modifying
    @Query("UPDATE IncentiveActivityRecord iar "
            + "SET iar.approvalStatus = :status, "
            + "iar.remarks = :remarks, "
            + "iar.verifiedByUserId = :ashaSupervisorUserId, "
            + "iar.verifiedByUserName = :ashaSupervisorUserName "
            + "WHERE iar.id = :id")
    int updateApprovalStatusById(
            @Param("id") Long id,
            @Param("status") Integer status,
            @Param("remarks") String remarks,
            @Param("ashaSupervisorUserId") Integer ashaSupervisorUserId,
            @Param("ashaSupervisorUserName") String ashaSupervisorUserName
    );
}
