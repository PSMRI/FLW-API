package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.TBSuspected;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface TBSuspectedRepo extends JpaRepository<TBSuspected, Long> {

    @Query("SELECT tbs FROM TBSuspected tbs WHERE tbs.benId = :benId")
    List<TBSuspected> getByBenId(@Param("benId") Long benId);

    @Query("SELECT tbs FROM TBSuspected tbs WHERE tbs.benId = :benId and tbs.userId = :userId")
    TBSuspected getByUserIdAndBenId(@Param("benId") Long benId, @Param("userId") Integer userId);

    @Query("SELECT tbs FROM TBSuspected tbs WHERE tbs.userId = :userId and tbs.visitDate >= :fromDate and tbs.visitDate <= :toDate")
    List<TBSuspected> getByUserId(@Param("userId") Integer userId, @Param("fromDate") Timestamp fromDate, @Param("toDate") Timestamp toDate);

    @Transactional
    @Modifying
    @Query("UPDATE TBSuspected t SET t.vanSerialNo = t.id WHERE t.id = :id")
    void updateVanSerialNo(@Param("id") Long id);
}
