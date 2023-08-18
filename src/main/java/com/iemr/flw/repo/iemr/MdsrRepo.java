package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.MDSR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface MdsrRepo extends JpaRepository<MDSR, Long> {

    MDSR findMDSRByBenIdAndCreatedDate(Long benId, Timestamp createdDate);

    @Query(" SELECT m FROM MDSR m WHERE m.createdBy = :userId and m.createdDate >= :fromDate and m.createdDate <= :toDate")
    List<MDSR> getAllMdsrByAshaId(@Param("userId") String userId,
                                  @Param("fromDate") Timestamp fromDate, @Param("toDate") Timestamp toDate);
}
