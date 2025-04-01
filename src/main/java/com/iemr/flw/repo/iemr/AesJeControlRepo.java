package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.AesJeData;
import com.iemr.flw.domain.iemr.KalaAzarData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Optional;

@Repository
public interface AesJeControlRepo extends JpaRepository<AesJeData,Long> {
    Optional<AesJeData>  findByBenId(BigInteger benId);
}
