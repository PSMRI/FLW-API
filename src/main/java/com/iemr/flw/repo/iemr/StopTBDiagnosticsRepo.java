package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.StopTBDiagnostics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import jakarta.persistence.QueryHint;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface StopTBDiagnosticsRepo extends JpaRepository<StopTBDiagnostics, Long> {

    @Query("SELECT d FROM StopTBDiagnostics d WHERE d.benRegID = :benRegID AND d.deleted = false")
    StopTBDiagnostics findByBenRegID(@Param("benRegID") Long benRegID);

    @Query("SELECT d FROM StopTBDiagnostics d WHERE d.providerServiceMapID = :psmId AND d.deleted = false ORDER BY d.visitDate DESC")
    List<StopTBDiagnostics> findAllByProviderServiceMapID(@Param("psmId") Integer psmId);

    // Read-only: getAll only reads these rows, so Hibernate keeps no change-tracking snapshot per row
    @QueryHints(@QueryHint(name = "org.hibernate.readOnly", value = "true"))
    @Query("SELECT d FROM StopTBDiagnostics d WHERE d.benRegID IN " +
            "(SELECT b.beneficiaryRegID FROM BenFlowStatus b WHERE b.providerServiceMapId = :psmId AND b.villageID = :villageId) " +
            "AND d.deleted = false ORDER BY d.visitDate DESC")
    List<StopTBDiagnostics> findAllByProviderServiceMapIDAndVillageID(@Param("psmId") Integer psmId, @Param("villageId") Integer villageId);

    @Transactional
    @Modifying
    @Query("UPDATE StopTBDiagnostics t SET t.vanSerialNo = t.id WHERE t.id = :id")
    void updateVanSerialNo(@Param("id") Long id);
}
