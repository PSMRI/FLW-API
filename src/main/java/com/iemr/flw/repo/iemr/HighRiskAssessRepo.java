package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.HighRiskAssess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;

public interface HighRiskAssessRepo extends JpaRepository<HighRiskAssess, Long> {

    @Query(value = "SELECT hra FROM HighRiskAssess hra WHERE hra.benId = :benId and hra.userId = :userId")
    HighRiskAssess getByUserIdAndBenId(@Param("benId") Long benId, @Param("userId") Integer userId);

    @Query(value = "SELECT nhrpa FROM HighRiskAssess hra WHERE hra.userId = :userId and hra.visitDate >= :fromDate and hra.visitDate <= :toDate")
    List<HighRiskAssess> getByUserId(@Param("userId") Integer userId, @Param("fromDate") Timestamp fromDate, @Param("toDate") Timestamp toDate);

}
