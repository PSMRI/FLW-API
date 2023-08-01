package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.PregnantWomanHighRiskAssess;
import com.iemr.flw.domain.iemr.TBScreening;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface HRPregnantAssessRepo extends JpaRepository<PregnantWomanHighRiskAssess, Long> {

    @Query(value = "SELECT hrpa FROM PregnantWomanHighRiskAssess hrpa WHERE hrpa.benId = :benId and hrpa.userId = :userId")
    PregnantWomanHighRiskAssess getByUserIdAndBenId(@Param("benId") Long benId, @Param("userId") Integer userId);

    @Query(value = "SELECT hrpa FROM PregnantWomanHighRiskAssess hrpa WHERE hrpa.userId = :userId and hrpa.visitDate >= :fromDate and hrpa.visitDate <= :toDate")
    List<PregnantWomanHighRiskAssess> getByUserId(@Param("userId") Integer userId, @Param("fromDate") Timestamp fromDate, @Param("toDate") Timestamp toDate);
}
