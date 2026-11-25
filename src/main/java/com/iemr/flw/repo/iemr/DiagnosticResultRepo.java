package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.DiagnosticResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiagnosticResultRepo extends JpaRepository<DiagnosticResult, Long> {

    // One order has a row per test (resultType): its own test, plus MDR_RIF on MTB/MTB_PLUS orders.
    Optional<DiagnosticResult> findByExternalOrderIdAndResultTypeAndDeletedFalse(String externalOrderId, String resultType);

    // Including soft-deleted rows: the unique (external_order_id, result_type) key covers them too.
    Optional<DiagnosticResult> findByExternalOrderIdAndResultType(String externalOrderId, String resultType);

    List<DiagnosticResult> findAllByExternalOrderIdAndDeletedFalse(String externalOrderId);

    @Transactional
    @Modifying
    @Query("UPDATE DiagnosticResult r SET r.vanSerialNo = r.id WHERE r.id = :id")
    void updateVanSerialNo(@Param("id") Long id);

    // Saves the result and sets vanSerialNo = id in one transaction, so van sync never sees a committed
    // result row with vanSerialNo NULL. See DiagnosticOrderRepo.saveWithVanSerialNo for the full reason.
    @Transactional
    default DiagnosticResult saveWithVanSerialNo(DiagnosticResult result) {
        DiagnosticResult saved = save(result);
        if (saved.getVanSerialNo() == null) {
            updateVanSerialNo(saved.getId());
            saved.setVanSerialNo(saved.getId()); // bulk UPDATE doesn't touch the in-memory entity
        }
        return saved;
    }
}
