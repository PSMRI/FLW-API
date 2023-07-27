package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.EligibleCoupleRegister;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface EligibleCoupleRegisterRepo extends JpaRepository<EligibleCoupleRegister, Long> {

    @Query(" SELECT ecr FROM EligibleCoupleRegister ecr WHERE ecr.benId = :benId")
    EligibleCoupleRegister getECRWithBen(@Param("benId") Long benId);

    @Query(" SELECT ecr FROM EligibleCoupleRegister ecr WHERE ecr.createdBy = :userId and ecr.createdDate >= :fromDate and ecr.createdDate <= :toDate")
    List<EligibleCoupleRegister> getECRegRecords(@Param("userId") Integer userId,
                                                 @Param("fromDate") Timestamp fromDate, @Param("toDate") Timestamp toDate);
}
