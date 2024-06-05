package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.HbncPart2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface HbncPart2repo extends JpaRepository<HbncPart2, Long> {

    @Query(value = "SELECT hbnc FROM  HbncPart2 hbnc WHERE hbnc.createdBy = :userId and hbnc.createdDate >= :fromDate and hbnc.createdDate <= :toDate")
    List<HbncPart2> getHbncPart2Details(@Param("userId") String userId,
                                        @Param("fromDate") Timestamp fromDate, @Param("toDate") Timestamp toDate);

    HbncPart2 findHbncPart2ByBenIdAndVisitNo(Long benId, Integer visitNo);

}

