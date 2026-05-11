package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.StopTBGeneralExamination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StopTBGeneralExaminationRepo extends JpaRepository<StopTBGeneralExamination, Long> {

    @Query("SELECT e FROM StopTBGeneralExamination e WHERE e.beneficiaryRegID = :beneficiaryRegID AND e.deleted = false")
    StopTBGeneralExamination findByBeneficiaryRegID(@Param("beneficiaryRegID") Long beneficiaryRegID);

    @Query("SELECT e FROM StopTBGeneralExamination e WHERE e.providerServiceMapID = :psmId AND e.deleted = false ORDER BY e.createdDate DESC")
    List<StopTBGeneralExamination> findAllByProviderServiceMapID(@Param("psmId") Integer psmId);

    @Query("SELECT e FROM StopTBGeneralExamination e WHERE e.beneficiaryRegID IN " +
            "(SELECT b.beneficiaryRegID FROM BenFlowStatus b WHERE b.providerServiceMapId = :psmId AND b.villageID = :villageId) " +
            "AND e.deleted = false ORDER BY e.createdDate DESC")
    List<StopTBGeneralExamination> findAllByProviderServiceMapIDAndVillageID(@Param("psmId") Integer psmId, @Param("villageId") Integer villageId);
}
