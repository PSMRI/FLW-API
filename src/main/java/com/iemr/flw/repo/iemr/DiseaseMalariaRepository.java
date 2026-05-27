package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.ScreeningMalaria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiseaseMalariaRepository extends JpaRepository<ScreeningMalaria, Long> {
    Optional<ScreeningMalaria> findByBenId(Long benId);
    // Custom queries can be added here if needed
}