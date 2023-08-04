package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.NonPregnantWomanHighRiskTrack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface HRNonPregnantTrackRepo extends JpaRepository<NonPregnantWomanHighRiskTrack, Long> {

    @Query(value = "SELECT npt FROM NonPregnantWomanHighRiskTrack npt WHERE npt.benId = :benId and npt.userId = :userId and npt.visitDate = :visitDate")
    NonPregnantWomanHighRiskTrack getByUserIdAndBenId(@Param("benId") Long benId, @Param("userId") Integer userId, @Param("visitDate") Timestamp visitDate);

    @Query(value = "SELECT npt FROM NonPregnantWomanHighRiskTrack npt WHERE npt.userId = :userId and npt.visitDate >= :fromDate and npt.visitDate <= :toDate")
    List<NonPregnantWomanHighRiskTrack> getByUserId(@Param("userId") Integer userId, @Param("fromDate") Timestamp fromDate, @Param("toDate") Timestamp toDate);
}
