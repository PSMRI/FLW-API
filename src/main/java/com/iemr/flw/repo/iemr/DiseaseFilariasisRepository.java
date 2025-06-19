package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.ScreeningFilariasis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiseaseFilariasisRepository extends JpaRepository<ScreeningFilariasis, Long> {
    Optional<ScreeningFilariasis> findByBenId(Long benId);
    // Custom queries can be added here if needed
}