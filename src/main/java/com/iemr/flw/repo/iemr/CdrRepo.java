package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.CDR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface CdrRepo extends JpaRepository<CDR, Long> {

    CDR findCDRByBenIdAndCreatedDate(Long benId, Timestamp createdDate);

    @Query(" SELECT c FROM CDR c WHERE c.createdBy = :userId and c.createdDate >= :fromDate and c.createdDate <= :toDate")
    List<CDR> getAllCdrByBenId(@Param("userId") String userId,
                                                    @Param("fromDate") Timestamp fromDate, @Param("toDate") Timestamp toDate);
}
