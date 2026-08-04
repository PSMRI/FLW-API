package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.DiagnosticOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface DiagnosticOrderRepo extends JpaRepository<DiagnosticOrder, Long> {

    Optional<DiagnosticOrder> findByExternalOrderId(String externalOrderId);

    @Query("SELECT o FROM DiagnosticOrder o WHERE o.beneficiaryId = :beneficiaryId AND o.visitCode = :visitCode " +
            "AND o.orderType = :orderType AND o.deleted = false")
    Optional<DiagnosticOrder> findByBeneficiaryIdAndVisitCodeAndOrderType(@Param("beneficiaryId") Long beneficiaryId,
                                                                      @Param("visitCode") Long visitCode,
                                                                      @Param("orderType") String orderType);

    Optional<DiagnosticOrder> findFirstByBeneficiaryIdAndOrderTypeAndDeletedFalseOrderByCreatedDateDesc(
            Long beneficiaryId, String orderType);

    @Query("SELECT o FROM DiagnosticOrder o WHERE o.beneficiaryId = :beneficiaryId AND o.deleted = false ORDER BY o.createdDate DESC")
    List<DiagnosticOrder> findByBeneficiaryId(@Param("beneficiaryId") Long beneficiaryId);

    @Query("SELECT o FROM DiagnosticOrder o WHERE o.orderType = 'XRAY_CHEST' " +
            "AND o.status IN ('PENDING', 'IN_PROGRESS') " +
            "AND o.deleted = false " +
            "ORDER BY o.lastPolledAt ASC NULLS FIRST")
    List<DiagnosticOrder> findXrayDueForPoll();

    @Query("SELECT o FROM DiagnosticOrder o WHERE o.orderType IN ('MTB', 'MTB_PLUS', 'MDR_RIF') " +
            "AND o.status IN ('PENDING', 'IN_PROGRESS') " +
            "AND o.deleted = false " +
            "ORDER BY o.lastPolledAt ASC NULLS FIRST")
    List<DiagnosticOrder> findTrueNatDueForPoll();

    @Transactional
    @Modifying
    @Query("UPDATE DiagnosticOrder o SET o.vanSerialNo = o.id WHERE o.id = :id")
    void updateVanSerialNo(@Param("id") Long id);

    // Beneficiary-status-summary queries, filtered the same way as
    // FormResponseRepo.findBeneficiaryIdsByFormIdAndStatusFiltered - null-safe optional
    // villageId/providerServiceMapId via a correlated BenFlowStatus subquery.
    // Each also requires o.id to be the latest (max id) order for its beneficiaryId+orderType, so a
    // beneficiary with an old terminal order and a new in-flight retest of the same orderType
    // is bucketed only by the retest, not both.
    @Query("SELECT o.beneficiaryId FROM DiagnosticOrder o WHERE o.orderType = :orderType AND o.deleted = false " +
            "AND o.status NOT IN ('REFUSED', 'FAILED') AND o.createdDate > :pollEligibleCutoff " +
            "AND o.id = (SELECT MAX(o2.id) FROM DiagnosticOrder o2 " +
            "WHERE o2.beneficiaryId = o.beneficiaryId AND o2.orderType = :orderType AND o2.deleted = false) " +
            "AND o.beneficiaryId IN (SELECT b.beneficiaryID FROM BenFlowStatus b WHERE b.deleted = false " +
            "AND (:villageId IS NULL OR b.villageID = :villageId) " +
            "AND (:providerServiceMapId IS NULL OR b.providerServiceMapId = :providerServiceMapId))")
    List<Long> findBeneficiaryIdsAwaitingTestCompletion(@Param("orderType") String orderType,
            @Param("pollEligibleCutoff") Timestamp pollEligibleCutoff,
            @Param("villageId") Integer villageId, @Param("providerServiceMapId") Integer providerServiceMapId);

    @Query("SELECT o.beneficiaryId FROM DiagnosticOrder o WHERE o.orderType = :orderType AND o.deleted = false " +
            "AND o.createdDate <= :pollEligibleCutoff AND o.status NOT IN ('COMPLETED', 'EXPIRED', 'FAILED', 'REFUSED') " +
            "AND o.id = (SELECT MAX(o2.id) FROM DiagnosticOrder o2 " +
            "WHERE o2.beneficiaryId = o.beneficiaryId AND o2.orderType = :orderType AND o2.deleted = false) " +
            "AND o.beneficiaryId IN (SELECT b.beneficiaryID FROM BenFlowStatus b WHERE b.deleted = false " +
            "AND (:villageId IS NULL OR b.villageID = :villageId) " +
            "AND (:providerServiceMapId IS NULL OR b.providerServiceMapId = :providerServiceMapId))")
    List<Long> findBeneficiaryIdsAwaitingProviderResult(@Param("orderType") String orderType,
            @Param("pollEligibleCutoff") Timestamp pollEligibleCutoff,
            @Param("villageId") Integer villageId, @Param("providerServiceMapId") Integer providerServiceMapId);

    @Query("SELECT o.beneficiaryId FROM DiagnosticOrder o WHERE o.orderType = :orderType AND o.deleted = false " +
            "AND o.status = 'COMPLETED' " +
            "AND o.id = (SELECT MAX(o2.id) FROM DiagnosticOrder o2 " +
            "WHERE o2.beneficiaryId = o.beneficiaryId AND o2.orderType = :orderType AND o2.deleted = false) " +
            "AND o.beneficiaryId IN (SELECT b.beneficiaryID FROM BenFlowStatus b WHERE b.deleted = false " +
            "AND (:villageId IS NULL OR b.villageID = :villageId) " +
            "AND (:providerServiceMapId IS NULL OR b.providerServiceMapId = :providerServiceMapId))")
    List<Long> findBeneficiaryIdsCompleted(@Param("orderType") String orderType,
            @Param("villageId") Integer villageId, @Param("providerServiceMapId") Integer providerServiceMapId);

    @Query("SELECT o.beneficiaryId FROM DiagnosticOrder o WHERE o.orderType = :orderType AND o.deleted = false " +
            "AND o.status = 'EXPIRED' " +
            "AND o.id = (SELECT MAX(o2.id) FROM DiagnosticOrder o2 " +
            "WHERE o2.beneficiaryId = o.beneficiaryId AND o2.orderType = :orderType AND o2.deleted = false) " +
            "AND o.beneficiaryId IN (SELECT b.beneficiaryID FROM BenFlowStatus b WHERE b.deleted = false " +
            "AND (:villageId IS NULL OR b.villageID = :villageId) " +
            "AND (:providerServiceMapId IS NULL OR b.providerServiceMapId = :providerServiceMapId))")
    List<Long> findBeneficiaryIdsPollingTimedOut(@Param("orderType") String orderType,
            @Param("villageId") Integer villageId, @Param("providerServiceMapId") Integer providerServiceMapId);

    @Query("SELECT o.beneficiaryId FROM DiagnosticOrder o WHERE o.orderType = :orderType AND o.deleted = false " +
            "AND o.status = 'FAILED' " +
            "AND o.id = (SELECT MAX(o2.id) FROM DiagnosticOrder o2 " +
            "WHERE o2.beneficiaryId = o.beneficiaryId AND o2.orderType = :orderType AND o2.deleted = false) " +
            "AND o.beneficiaryId IN (SELECT b.beneficiaryID FROM BenFlowStatus b WHERE b.deleted = false " +
            "AND (:villageId IS NULL OR b.villageID = :villageId) " +
            "AND (:providerServiceMapId IS NULL OR b.providerServiceMapId = :providerServiceMapId))")
    List<Long> findBeneficiaryIdsFailed(@Param("orderType") String orderType,
            @Param("villageId") Integer villageId, @Param("providerServiceMapId") Integer providerServiceMapId);

    @Query("SELECT o.beneficiaryId FROM DiagnosticOrder o WHERE o.orderType = :orderType AND o.deleted = false " +
            "AND o.status = 'REFUSED' " +
            "AND o.id = (SELECT MAX(o2.id) FROM DiagnosticOrder o2 " +
            "WHERE o2.beneficiaryId = o.beneficiaryId AND o2.orderType = :orderType AND o2.deleted = false) " +
            "AND o.beneficiaryId IN (SELECT b.beneficiaryID FROM BenFlowStatus b WHERE b.deleted = false " +
            "AND (:villageId IS NULL OR b.villageID = :villageId) " +
            "AND (:providerServiceMapId IS NULL OR b.providerServiceMapId = :providerServiceMapId))")
    List<Long> findBeneficiaryIdsRefused(@Param("orderType") String orderType,
            @Param("villageId") Integer villageId, @Param("providerServiceMapId") Integer providerServiceMapId);
}
