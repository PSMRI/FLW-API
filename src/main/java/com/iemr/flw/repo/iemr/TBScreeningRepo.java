package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.TBScreening;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface TBScreeningRepo extends JpaRepository<TBScreening, Long> {

    @Query(value = "SELECT tbs FROM TBScreening tbs WHERE tbs.benId = :benId and tbs.userId = :userId")
    TBScreening getByUserIdAndBenId(@Param("benId") Long benId, @Param("userId") Integer userId);

    @Query(value = "SELECT tbs FROM TBScreening tbs WHERE tbs.userId = :userId and tbs.visitDate >= :fromDate and tbs.visitDate <= :toDate")
    List<TBScreening> getByUserId(@Param("userId") Integer userId, @Param("fromDate") Timestamp fromDate, @Param("toDate") Timestamp toDate);
}
