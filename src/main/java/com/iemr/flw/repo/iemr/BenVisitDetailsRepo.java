package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.BenVisitDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface BenVisitDetailsRepo extends JpaRepository<BenVisitDetail, Long> {

    List<BenVisitDetail> findByBeneficiaryRegId(Long benRegID);

    @Query("SELECT v FROM BenVisitDetail v WHERE v.beneficiaryRegId = :benRegID " +
           "AND v.visitCategory = 'Stop TB' " +
           "AND v.visitDateTime >= :dayStart AND v.visitDateTime < :dayEnd")
    BenVisitDetail findStopTBVisitForToday(@Param("benRegID") Long benRegID,
                                           @Param("dayStart") Timestamp dayStart,
                                           @Param("dayEnd") Timestamp dayEnd);

    @Query("SELECT COUNT(v) FROM BenVisitDetail v WHERE v.beneficiaryRegId = :benRegID " +
           "AND v.visitCategory = 'Stop TB'")
    Integer countStopTBVisits(@Param("benRegID") Long benRegID);
}
