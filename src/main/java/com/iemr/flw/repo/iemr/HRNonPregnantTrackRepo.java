package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.NonPregnantWomanHighRiskTrack;
import com.iemr.flw.domain.iemr.PregnantWomanHighRiskTrack;
import com.iemr.flw.domain.iemr.TBScreening;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface HRNonPregnantTrackRepo extends JpaRepository<NonPregnantWomanHighRiskTrack, Long> {

    @Query(value = "SELECT nhrpt FROM NonPregnantWomanHighRiskTrack nhrpt WHERE nhrpt.benId = :benId and nhrpt.userId = :userId")
    TBScreening getByUserIdAndBenId(@Param("benId") Long benId, @Param("userId") Integer userId);

    @Query(value = "SELECT nhrpt FROM NonPregnantWomanHighRiskTrack nhrpt WHERE nhrpt.userId = :userId and nhrpt.visitDate >= :fromDate and nhrpt.visitDate <= :toDate")
    List<TBScreening> getByUserId(@Param("userId") Integer userId, @Param("fromDate") Timestamp fromDate, @Param("toDate") Timestamp toDate);
}
