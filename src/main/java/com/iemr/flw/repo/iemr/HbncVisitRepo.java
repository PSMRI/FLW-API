package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.HbncVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface HbncVisitRepo extends JpaRepository<HbncVisit, Long> {

//    @Query(value = "SELECT hbnc FROM  HbncVisit hbnc WHERE hbnc.createdBy = :userId and hbnc.createdDate >= :fromDate and hbnc.createdDate <= :toDate")
//    List<HbncVisit> getHbncVisitDetails(@Param("userId") String userId,
//                                        @Param("fromDate") Timestamp fromDate, @Param("toDate") Timestamp toDate);

    @Query("SELECT v FROM HbncVisit v WHERE v.beneficiaryId = :beneficiaryId AND v.visit_day = :visitDay")
    HbncVisit findByBeneficiaryIdAndVisit_day(@Param("beneficiaryId") Long beneficiaryId,
                                              @Param("visitDay") String visitDay);

}
