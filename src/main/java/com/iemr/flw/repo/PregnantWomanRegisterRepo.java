package com.iemr.flw.repo;

import com.iemr.flw.domain.PregnantWomanRegister;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PregnantWomanRegisterRepo extends JpaRepository<PregnantWomanRegister, Long> {

    @Query(" SELECT p FROM PregnantWomanRegister p WHERE p.benId = :benId")
    PregnantWomanRegister getPWRWithBen(@Param("benId") Long benId);

}
