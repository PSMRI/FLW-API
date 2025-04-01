package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.KalaAzarData;
import com.iemr.flw.domain.iemr.LeprosyData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Optional;

@Repository
public interface LeprosyControlRepo extends JpaRepository<LeprosyData,Long> {
    Optional<LeprosyData>  findByBenId(BigInteger benId);
}
