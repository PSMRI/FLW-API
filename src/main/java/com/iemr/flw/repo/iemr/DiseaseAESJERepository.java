package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.DiseaseAesje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Optional;

@Repository
public interface DiseaseAESJERepository extends JpaRepository<DiseaseAesje, Long> {
    Optional<DiseaseAesje> findByBenId(Long benId);
    // Custom queries can be added here if needed
}