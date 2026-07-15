package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.DiagnosticDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiagnosticDocumentRepo extends JpaRepository<DiagnosticDocument, Long> {

    Optional<DiagnosticDocument> findByBenRegIDAndDocumentTypeAndDeletedFalse(Long benRegID, String documentType);
}
