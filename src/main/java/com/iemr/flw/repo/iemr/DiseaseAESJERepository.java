package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.ScreeningAesje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiseaseAESJERepository extends JpaRepository<ScreeningAesje, Long> {
    Optional<ScreeningAesje> findByBenId(Long benId);
    // Custom queries can be added here if needed
}