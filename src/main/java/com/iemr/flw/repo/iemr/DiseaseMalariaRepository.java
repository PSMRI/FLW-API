package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.DiseaseMalaria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiseaseMalariaRepository extends JpaRepository<DiseaseMalaria, Long> {
    Optional<DiseaseMalaria> findByBenId(Long benId);
    // Custom queries can be added here if needed
}