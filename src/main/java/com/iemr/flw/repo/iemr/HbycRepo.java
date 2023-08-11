package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.HBYC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface HbycRepo extends JpaRepository<HBYC, Long> {

    HBYC findHBYCByBenIdAndCreatedDate(Long benId, Timestamp createdDate);

    @Query(" SELECT hbyc FROM HBYC hbyc WHERE hbyc.createdBy = :userId and hbyc.createdDate >= :fromDate and hbyc.createdDate <= :toDate")
    List<HBYC> getAllHbycByBenId(@Param("userId") String userId,
                                 @Param("fromDate") Timestamp fromDate, @Param("toDate") Timestamp toDate);
}
