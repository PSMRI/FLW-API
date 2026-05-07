package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.BenFlowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface BenFlowStatusRepo extends JpaRepository<BenFlowStatus, Long> {

    @Query("SELECT b FROM BenFlowStatus b WHERE b.providerServiceMapId = :psmId AND b.nurseFlag = 1 AND b.visitDate >= :fromDate AND b.visitDate <= :toDate AND b.deleted = false")
    List<BenFlowStatus> getNurseWorklist(
            @Param("psmId") Integer psmId,
            @Param("fromDate") Timestamp fromDate,
            @Param("toDate") Timestamp toDate
    );

    @Modifying
    @Query("UPDATE BenFlowStatus b SET b.nurseFlag = 9, b.pharmacistFlag = 1 WHERE b.benFlowID = :benFlowID")
    int updateAfterNurseSubmit(@Param("benFlowID") Long benFlowID);
}
