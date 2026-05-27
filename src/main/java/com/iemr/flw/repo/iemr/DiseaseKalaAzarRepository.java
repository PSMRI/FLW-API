package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.ScreeningKalaAzar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiseaseKalaAzarRepository extends JpaRepository<ScreeningKalaAzar, Long> {
    Optional<ScreeningKalaAzar> findByBenId(Long benId);
    // Custom queries can be added here if needed
}