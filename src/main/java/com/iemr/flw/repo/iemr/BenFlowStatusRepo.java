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

    @Query("SELECT b FROM BenFlowStatus b WHERE b.providerServiceMapId = :psmId AND b.villageID = :villageId AND b.nurseFlag = 1 AND b.deleted = false ORDER BY b.registrationDate DESC")
    List<BenFlowStatus> getNurseWorklist(@Param("psmId") Integer psmId, @Param("villageId") Integer villageId);

    @Modifying
    @Query("UPDATE BenFlowStatus b SET b.nurseFlag = 9, b.pharmacistFlag = 1 WHERE b.benFlowID = :benFlowID")
    int updateAfterNurseSubmit(@Param("benFlowID") Long benFlowID);

    // Stop TB: nurse done, counsellor pending when diagnostic is positive (set by device push)
    @Modifying
    @Query("UPDATE BenFlowStatus b SET b.nurseFlag = 9 WHERE b.benFlowID = :benFlowID")
    int updateStopTBAfterNurseSubmit(@Param("benFlowID") Long benFlowID);

    // Stop TB: device operator pushes positive diagnostic → route to counsellor
    @Modifying
    @Query("UPDATE BenFlowStatus b SET b.doctorFlag = 1 WHERE b.beneficiaryRegID = :benRegID AND b.visitCategory = 'Stop TB' AND b.deleted = false")
    int routeToStopTBCounsellor(@Param("benRegID") Long benRegID);
}
