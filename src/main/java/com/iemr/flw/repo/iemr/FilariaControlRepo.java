package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.FilariaData;
import com.iemr.flw.domain.iemr.KalaAzarData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Optional;

@Repository
public interface FilariaControlRepo extends JpaRepository<FilariaData,Long> {
    Optional<FilariaData>  findByBenId(BigInteger benId);
}
