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

    @Query("SELECT o FROM DiagnosticOrder o WHERE o.status IN ('PENDING', 'IN_PROGRESS') " +
            "AND o.deleted = false AND o.testCompletedAt IS NOT NULL " +
            "ORDER BY o.lastPolledAt ASC NULLS FIRST")
    List<DiagnosticOrder> findDueForPoll();

    @Transactional
    @Modifying
    @Query("UPDATE DiagnosticOrder o SET o.vanSerialNo = o.id WHERE o.id = :id")
    void updateVanSerialNo(@Param("id") Long id);
}
