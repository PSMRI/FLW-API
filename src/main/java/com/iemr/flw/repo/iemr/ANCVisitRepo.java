package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.ANCVisit;
import com.iemr.flw.domain.iemr.EligibleCoupleTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface ANCVisitRepo extends JpaRepository<ANCVisit, Long> {

    @Query(value = "SELECT anc FROM  ANCVisit anc WHERE anc.createdBy = :userId and anc.isActive = true and anc.createdDate >= :fromDate and anc.createdDate <= :toDate")
    List<ANCVisit> getANCForPW(@Param("userId") String userId,
                               @Param("fromDate") Timestamp fromDate, @Param("toDate") Timestamp toDate);

    @Query
    ANCVisit findANCVisitByBenIdAndAncVisitAndIsActive(Long benId, Integer ancVisit, Boolean isActive);
}
