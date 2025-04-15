package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.DiseaseFilariasis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiseaseFilariasisRepository extends JpaRepository<DiseaseFilariasis, Long> {
    Optional<DiseaseFilariasis> findByBenId(Long benId);
    // Custom queries can be added here if needed
}