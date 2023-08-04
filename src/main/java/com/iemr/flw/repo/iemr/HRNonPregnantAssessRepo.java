package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.NonPregnantWomanHighRiskAssess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface HRNonPregnantAssessRepo extends JpaRepository<NonPregnantWomanHighRiskAssess, Long> {

    @Query(value = "SELECT nhrpa FROM NonPregnantWomanHighRiskAssess nhrpa WHERE nhrpa.benId = :benId and nhrpa.userId = :userId")
    NonPregnantWomanHighRiskAssess getByUserIdAndBenId(@Param("benId") Long benId, @Param("userId") Integer userId);

    @Query(value = "SELECT nhrpa FROM NonPregnantWomanHighRiskAssess nhrpa WHERE nhrpa.userId = :userId and nhrpa.visitDate >= :fromDate and nhrpa.visitDate <= :toDate")
    List<NonPregnantWomanHighRiskAssess> getByUserId(@Param("userId") Integer userId, @Param("fromDate") Timestamp fromDate, @Param("toDate") Timestamp toDate);
}
