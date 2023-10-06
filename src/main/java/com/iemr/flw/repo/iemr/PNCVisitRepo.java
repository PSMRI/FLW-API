package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.PNCVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface PNCVisitRepo extends JpaRepository<PNCVisit, Long> {

    @Query(value = "SELECT pnc FROM  PNCVisit pnc WHERE pnc.createdBy = :userId and pnc.isActive = true and pnc.createdDate >= :fromDate and pnc.createdDate <= :toDate")
    List<PNCVisit> getPNCForPW(@Param("userId") String userId,
                               @Param("fromDate") Timestamp fromDate, @Param("toDate") Timestamp toDate);

    PNCVisit findPNCVisitByBenIdAndPncPeriodAndIsActive(Long benId, Integer pncVisit, Boolean isActive);
}
