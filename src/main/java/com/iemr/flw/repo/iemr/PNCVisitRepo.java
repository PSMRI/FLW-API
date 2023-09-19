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

    @Query(value = "SELECT anc FROM  PNCVisit anc WHERE anc.createdBy = :userId and anc.createdDate >= :fromDate and anc.createdDate <= :toDate")
    List<PNCVisit> getPNCForPW(@Param("userId") String userId,
                               @Param("fromDate") Timestamp fromDate, @Param("toDate") Timestamp toDate);

    PNCVisit findPNCVisitByBenIdAndCreatedDateAndPncPeriod(Long benId, Timestamp createdDate, String ancVisit);
}
