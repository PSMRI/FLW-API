package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.SamVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SamVisitRepository extends JpaRepository<SamVisit, Long> {

    // Find all visits for a particular beneficiary
    List<SamVisit> findByBeneficiaryId(Long beneficiaryId);

    // Optional: Get latest visit record for a beneficiary
    SamVisit findTopByBeneficiaryIdOrderByVisitDateDesc(Long beneficiaryId);

    // Optional: Check if a visit exists for a given date
    boolean existsByBeneficiaryIdAndVisitDate(Long beneficiaryId, java.time.LocalDate visitDate);

    List<SamVisit> findByUserId(Integer ashaId);

    Optional<SamVisit> findByBeneficiaryIdAndVisitDate(Long beneficiaryId, LocalDate visitDate);
}
