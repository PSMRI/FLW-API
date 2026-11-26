package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.DiagnosticDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface DiagnosticDocumentRepo extends JpaRepository<DiagnosticDocument, Long> {

    Optional<DiagnosticDocument> findByExternalOrderIdAndDocumentTypeAndDeletedFalse(String externalOrderId, String documentType);

    boolean existsByExternalOrderIdAndDeletedFalse(String externalOrderId);

    @Transactional
    @Modifying
    @Query("UPDATE DiagnosticDocument d SET d.vanSerialNo = d.id WHERE d.id = :id")
    void updateVanSerialNo(@Param("id") Long id);

    // Saves the document and sets vanSerialNo = id in one transaction, so van sync never sees a committed
    // document row with vanSerialNo NULL. See DiagnosticOrderRepo.saveWithVanSerialNo for the full reason.
    @Transactional
    default DiagnosticDocument saveWithVanSerialNo(DiagnosticDocument document) {
        DiagnosticDocument saved = save(document);
        if (saved.getVanSerialNo() == null) {
            updateVanSerialNo(saved.getId());
            saved.setVanSerialNo(saved.getId()); // bulk UPDATE doesn't touch the in-memory entity
        }
        return saved;
    }
}
