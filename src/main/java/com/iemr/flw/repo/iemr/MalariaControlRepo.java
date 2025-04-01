package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.DiseaseControl;
import com.iemr.flw.domain.iemr.MalariaData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Optional;

@Repository
public interface MalariaControlRepo extends JpaRepository<MalariaData,Long> {
    Optional<MalariaData>  findByBenId(BigInteger benId);
}
