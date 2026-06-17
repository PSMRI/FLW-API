package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.StopTBGeneralExamination;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface StopTBGeneralExaminationRepo extends JpaRepository<StopTBGeneralExamination, Long> {

    @Query("SELECT e FROM StopTBGeneralExamination e WHERE e.beneficiaryRegID = :beneficiaryRegID AND e.deleted = false")
    StopTBGeneralExamination findByBeneficiaryRegID(@Param("beneficiaryRegID") Long beneficiaryRegID);

    // Read-side fix: a beneficiary can now have one row per visit, so the old single-result
    // finder above throws NonUniqueResultException once a 2nd visit exists. Used by read/worklist
    // endpoints that want "current" status — pass PageRequest.of(0, 1) to get just the latest.
    @Query("SELECT e FROM StopTBGeneralExamination e WHERE e.beneficiaryRegID = :beneficiaryRegID " +
            "AND e.deleted = false ORDER BY e.createdDate DESC")
    List<StopTBGeneralExamination> findLatestByBeneficiaryRegID(@Param("beneficiaryRegID") Long beneficiaryRegID, Pageable pageable);

    @Query("SELECT e FROM StopTBGeneralExamination e WHERE e.beneficiaryRegID = :beneficiaryRegID " +
            "AND e.visitCode = :visitCode AND e.deleted = false")
    StopTBGeneralExamination findByBeneficiaryRegIDAndVisitCode(@Param("beneficiaryRegID") Long beneficiaryRegID,
                                                                  @Param("visitCode") Long visitCode);

    @Query("SELECT e FROM StopTBGeneralExamination e WHERE e.providerServiceMapID = :psmId AND e.deleted = false ORDER BY e.createdDate DESC")
    List<StopTBGeneralExamination> findAllByProviderServiceMapID(@Param("psmId") Integer psmId);

    @Query("SELECT e FROM StopTBGeneralExamination e WHERE e.beneficiaryRegID IN " +
            "(SELECT b.beneficiaryRegID FROM BenFlowStatus b WHERE b.providerServiceMapId = :psmId AND b.villageID = :villageId) " +
            "AND e.deleted = false ORDER BY e.createdDate DESC")
    List<StopTBGeneralExamination> findAllByProviderServiceMapIDAndVillageID(@Param("psmId") Integer psmId, @Param("villageId") Integer villageId);

    @Transactional
    @Modifying
    @Query("UPDATE StopTBGeneralExamination t SET t.vanSerialNo = t.id WHERE t.id = :id")
    void updateVanSerialNo(@Param("id") Long id);
}
