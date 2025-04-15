package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.DiseaseKalaAzar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiseaseKalaAzarRepository extends JpaRepository<DiseaseKalaAzar, Long> {
    Optional<DiseaseKalaAzar> findByBenId(Long benId);
    // Custom queries can be added here if needed
}