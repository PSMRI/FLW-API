package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.GeneralOpdEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BeneficiaryRepository extends JpaRepository<GeneralOpdEntry, Long> {
    List<GeneralOpdEntry> findByAshaIdOrderByVisitDateDesc(String ashaId);
}
