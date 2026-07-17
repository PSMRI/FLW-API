package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.DiagnosticResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiagnosticResultRepo extends JpaRepository<DiagnosticResult, Long> {

    Optional<DiagnosticResult> findByDiagnosticOrderIdAndDeletedFalse(Long diagnosticOrderId);
}
