package com.iemr.flw.repo;

import com.iemr.flw.domain.InfantRegister;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InfantRegisterRepo extends JpaRepository<InfantRegister, Long> {

    @Query(" SELECT ir FROM InfantRegister ir WHERE ir.benId = :benId")
    InfantRegister getInfantDetailsWithBen(@Param("benId") Long benId);
}
