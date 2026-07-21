package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.DiagnosticOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiagnosticOrderRepo extends JpaRepository<DiagnosticOrder, Long> {

    Optional<DiagnosticOrder> findByExternalOrderId(String externalOrderId);

    @Query("SELECT o FROM DiagnosticOrder o WHERE o.benRegID = :benRegID AND o.visitCode = :visitCode " +
            "AND o.orderType = :orderType AND o.deleted = false")
    Optional<DiagnosticOrder> findByBenRegIDAndVisitCodeAndOrderType(@Param("benRegID") Long benRegID,
                                                                      @Param("visitCode") Long visitCode,
                                                                      @Param("orderType") String orderType);

    Optional<DiagnosticOrder> findFirstByBenRegIDAndOrderTypeAndDeletedFalseOrderByCreatedDateDesc(
            Long benRegID, String orderType);

    @Query("SELECT o FROM DiagnosticOrder o WHERE o.benRegID = :benRegID AND o.deleted = false ORDER BY o.createdDate DESC")
    List<DiagnosticOrder> findByBenRegID(@Param("benRegID") Long benRegID);

    @Query("SELECT o FROM DiagnosticOrder o WHERE o.orderType = 'XRAY_CHEST' " +
            "AND o.status IN ('PENDING', 'IN_PROGRESS') " +
            "AND o.deleted = false AND o.testCompletedAt IS NOT NULL " +
            "ORDER BY o.lastPolledAt ASC NULLS FIRST")
    List<DiagnosticOrder> findXrayDueForPoll();

    @Query("SELECT o FROM DiagnosticOrder o WHERE o.orderType IN ('MTB', 'MTB_PLUS', 'MDR_RIF') " +
            "AND o.status IN ('PENDING', 'IN_PROGRESS') " +
            "AND o.deleted = false AND o.testCompletedAt IS NOT NULL " +
            "ORDER BY o.lastPolledAt ASC NULLS FIRST")
    List<DiagnosticOrder> findTrueNatDueForPoll();

    @Transactional
    @Modifying
    @Query("UPDATE DiagnosticOrder o SET o.vanSerialNo = o.id WHERE o.id = :id")
    void updateVanSerialNo(@Param("id") Long id);

    // Beneficiary-status-summary queries, filtered the same way as
    // FormResponseRepo.findBeneficiaryIdsByFormIdAndStatusFiltered - null-safe optional
    // villageId/providerServiceMapId via a correlated BenFlowStatus subquery.
    // Each also requires o.id to be the latest (max id) order for its benRegID+orderType, so a
    // beneficiary with an old terminal order and a new in-flight retest of the same orderType
    // is bucketed only by the retest, not both. o.benRegID already holds the beneficiaryId
    // (not a separate registration id), so it's matched against b.beneficiaryID, not b.beneficiaryRegID.
    @Query("SELECT o.benRegID FROM DiagnosticOrder o WHERE o.orderType = :orderType AND o.deleted = false " +
            "AND o.testCompletedAt IS NULL " +
            "AND o.id = (SELECT MAX(o2.id) FROM DiagnosticOrder o2 " +
            "WHERE o2.benRegID = o.benRegID AND o2.orderType = :orderType AND o2.deleted = false) " +
            "AND o.benRegID IN (SELECT b.beneficiaryID FROM BenFlowStatus b WHERE b.deleted = false " +
            "AND (:villageId IS NULL OR b.villageID = :villageId) " +
            "AND (:providerServiceMapId IS NULL OR b.providerServiceMapId = :providerServiceMapId))")
    List<Long> findBeneficiaryIdsAwaitingTestCompletion(@Param("orderType") String orderType,
            @Param("villageId") Integer villageId, @Param("providerServiceMapId") Integer providerServiceMapId);

    @Query("SELECT o.benRegID FROM DiagnosticOrder o WHERE o.orderType = :orderType AND o.deleted = false " +
            "AND o.testCompletedAt IS NOT NULL AND o.status NOT IN ('COMPLETED', 'EXPIRED', 'FAILED') " +
            "AND o.id = (SELECT MAX(o2.id) FROM DiagnosticOrder o2 " +
            "WHERE o2.benRegID = o.benRegID AND o2.orderType = :orderType AND o2.deleted = false) " +
            "AND o.benRegID IN (SELECT b.beneficiaryID FROM BenFlowStatus b WHERE b.deleted = false " +
            "AND (:villageId IS NULL OR b.villageID = :villageId) " +
            "AND (:providerServiceMapId IS NULL OR b.providerServiceMapId = :providerServiceMapId))")
    List<Long> findBeneficiaryIdsAwaitingProviderResult(@Param("orderType") String orderType,
            @Param("villageId") Integer villageId, @Param("providerServiceMapId") Integer providerServiceMapId);

    @Query("SELECT o.benRegID FROM DiagnosticOrder o WHERE o.orderType = :orderType AND o.deleted = false " +
            "AND o.status = 'COMPLETED' " +
            "AND o.id = (SELECT MAX(o2.id) FROM DiagnosticOrder o2 " +
            "WHERE o2.benRegID = o.benRegID AND o2.orderType = :orderType AND o2.deleted = false) " +
            "AND o.benRegID IN (SELECT b.beneficiaryID FROM BenFlowStatus b WHERE b.deleted = false " +
            "AND (:villageId IS NULL OR b.villageID = :villageId) " +
            "AND (:providerServiceMapId IS NULL OR b.providerServiceMapId = :providerServiceMapId))")
    List<Long> findBeneficiaryIdsCompleted(@Param("orderType") String orderType,
            @Param("villageId") Integer villageId, @Param("providerServiceMapId") Integer providerServiceMapId);

    @Query("SELECT o.benRegID FROM DiagnosticOrder o WHERE o.orderType = :orderType AND o.deleted = false " +
            "AND o.status = 'EXPIRED' " +
            "AND o.id = (SELECT MAX(o2.id) FROM DiagnosticOrder o2 " +
            "WHERE o2.benRegID = o.benRegID AND o2.orderType = :orderType AND o2.deleted = false) " +
            "AND o.benRegID IN (SELECT b.beneficiaryID FROM BenFlowStatus b WHERE b.deleted = false " +
            "AND (:villageId IS NULL OR b.villageID = :villageId) " +
            "AND (:providerServiceMapId IS NULL OR b.providerServiceMapId = :providerServiceMapId))")
    List<Long> findBeneficiaryIdsPollingTimedOut(@Param("orderType") String orderType,
            @Param("villageId") Integer villageId, @Param("providerServiceMapId") Integer providerServiceMapId);

    @Query("SELECT o.benRegID FROM DiagnosticOrder o WHERE o.orderType = :orderType AND o.deleted = false " +
            "AND o.status = 'FAILED' " +
            "AND o.id = (SELECT MAX(o2.id) FROM DiagnosticOrder o2 " +
            "WHERE o2.benRegID = o.benRegID AND o2.orderType = :orderType AND o2.deleted = false) " +
            "AND o.benRegID IN (SELECT b.beneficiaryID FROM BenFlowStatus b WHERE b.deleted = false " +
            "AND (:villageId IS NULL OR b.villageID = :villageId) " +
            "AND (:providerServiceMapId IS NULL OR b.providerServiceMapId = :providerServiceMapId))")
    List<Long> findBeneficiaryIdsFailed(@Param("orderType") String orderType,
            @Param("villageId") Integer villageId, @Param("providerServiceMapId") Integer providerServiceMapId);

    @Query("SELECT o.benRegID FROM DiagnosticOrder o WHERE o.orderType = :orderType AND o.deleted = false " +
            "AND o.status = 'EXPIRED' " +
            "AND o.id = (SELECT MAX(o2.id) FROM DiagnosticOrder o2 " +
            "WHERE o2.benRegID = o.benRegID AND o2.orderType = :orderType AND o2.deleted = false) " +
            "AND o.benRegID IN (SELECT b.beneficiaryRegID FROM BenFlowStatus b WHERE b.deleted = false " +
            "AND (:villageId IS NULL OR b.villageID = :villageId) " +
            "AND (:providerServiceMapId IS NULL OR b.providerServiceMapId = :providerServiceMapId))")
    List<Long> findBenRegIDsPollingTimedOut(@Param("orderType") String orderType,
            @Param("villageId") Integer villageId, @Param("providerServiceMapId") Integer providerServiceMapId);

    @Query("SELECT o.benRegID FROM DiagnosticOrder o WHERE o.orderType = :orderType AND o.deleted = false " +
            "AND o.status = 'FAILED' " +
            "AND o.id = (SELECT MAX(o2.id) FROM DiagnosticOrder o2 " +
            "WHERE o2.benRegID = o.benRegID AND o2.orderType = :orderType AND o2.deleted = false) " +
            "AND o.benRegID IN (SELECT b.beneficiaryRegID FROM BenFlowStatus b WHERE b.deleted = false " +
            "AND (:villageId IS NULL OR b.villageID = :villageId) " +
            "AND (:providerServiceMapId IS NULL OR b.providerServiceMapId = :providerServiceMapId))")
    List<Long> findBenRegIDsFailed(@Param("orderType") String orderType,
            @Param("villageId") Integer villageId, @Param("providerServiceMapId") Integer providerServiceMapId);
}
