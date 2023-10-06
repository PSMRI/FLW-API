package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.HbncPart1;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface HbncPart1Repo extends JpaRepository<HbncPart1, Long> {

    @Query(value = "SELECT hbnc FROM  HbncPart1 hbnc WHERE hbnc.createdBy = :userId and hbnc.createdDate >= :fromDate and hbnc.createdDate <= :toDate")
    List<HbncPart1> getHbncPart1Details(@Param("userId") String userId,
                                                @Param("fromDate") Timestamp fromDate, @Param("toDate") Timestamp toDate);

    HbncPart1 findHbncPart1ByBenIdAndVisitNo(Long benId, Integer visitNo);

}
