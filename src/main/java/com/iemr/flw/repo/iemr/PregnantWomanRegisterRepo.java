package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.PregnantWomanRegister;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface PregnantWomanRegisterRepo extends JpaRepository<PregnantWomanRegister, Long> {

    @Query(" SELECT pw FROM PregnantWomanRegister pw WHERE pw.createdBy = :userId and pw.createdDate >= :fromDate and pw.createdDate <= :toDate")
    List<PregnantWomanRegister> getPWRWithBen(@Param("userId") String userId,
                                              @Param("fromDate") Timestamp fromDate, @Param("toDate") Timestamp toDate);

    PregnantWomanRegister findPregnantWomanRegisterByBenIdAndIsActive(Long benId, Boolean isActive);

}
