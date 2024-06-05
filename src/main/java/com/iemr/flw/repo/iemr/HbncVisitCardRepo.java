package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.HbncVisitCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface HbncVisitCardRepo extends JpaRepository<HbncVisitCard, Long> {

    @Query(value = "SELECT hbnc FROM  HbncVisitCard hbnc WHERE hbnc.createdBy = :userId and hbnc.createdDate >= :fromDate and hbnc.createdDate <= :toDate")
    List<HbncVisitCard> getHbncVisitCardDetails(@Param("userId") String userId,
                                      @Param("fromDate") Timestamp fromDate, @Param("toDate") Timestamp toDate);

    HbncVisitCard findHbncVisitCardByBenIdAndVisitNo(Long benId, Integer visitNumber);

}