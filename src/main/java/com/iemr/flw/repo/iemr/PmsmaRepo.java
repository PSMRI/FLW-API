package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.PMSMA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface PmsmaRepo extends JpaRepository<PMSMA, Long> {

    PMSMA findPMSMAByBenIdAndIsActive(Long benId, Boolean isActive);

    @Query(" SELECT pmsma FROM PMSMA pmsma WHERE pmsma.createdBy = :userId and pmsma.isActive = true and pmsma.createdDate >= :fromDate and pmsma.createdDate <= :toDate")
    List<PMSMA> getAllPmsmaByAshaId(@Param("userId") String userId,
                                 @Param("fromDate") Timestamp fromDate, @Param("toDate") Timestamp toDate);
}
