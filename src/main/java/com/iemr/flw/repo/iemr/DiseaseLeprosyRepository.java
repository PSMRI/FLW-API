package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.DiseaseLeprosy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiseaseLeprosyRepository extends JpaRepository<DiseaseLeprosy, Long> {
    Optional<DiseaseLeprosy> findByBenId(Long benId);
    // Custom queries can be added here if needed
}