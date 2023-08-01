package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.PregnantWomanHighRiskTrack;
import com.iemr.flw.domain.iemr.TBScreening;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface HRPregnantTrackRepo extends JpaRepository<PregnantWomanHighRiskTrack, Long> {

    @Query(value = "SELECT hrpt FROM PregnantWomanHighRiskTrack hrpt WHERE hrpt.benId = :benId and hrpt.userId = :userId")
    TBScreening getByUserIdAndBenId(@Param("benId") Long benId, @Param("userId") Integer userId);

    @Query(value = "SELECT hrpt FROM PregnantWomanHighRiskTrack hrpt WHERE hrpt.userId = :userId and hrpt.visitDate >= :fromDate and hrpt.visitDate <= :toDate")
    List<TBScreening> getByUserId(@Param("userId") Integer userId, @Param("fromDate") Timestamp fromDate, @Param("toDate") Timestamp toDate);
}
