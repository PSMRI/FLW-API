package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.StopTBGeneralOpd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StopTBGeneralOpdRepo extends JpaRepository<StopTBGeneralOpd, Long> {

    @Query("SELECT o FROM StopTBGeneralOpd o WHERE o.benRegID = :benRegID AND o.deleted = false")
    StopTBGeneralOpd findByBenRegID(@Param("benRegID") Long benRegID);

    @Query("SELECT o FROM StopTBGeneralOpd o WHERE o.providerServiceMapID = :psmId AND o.deleted = false ORDER BY o.createdDate DESC")
    List<StopTBGeneralOpd> findAllByProviderServiceMapID(@Param("psmId") Integer psmId);

    @Query("SELECT o FROM StopTBGeneralOpd o WHERE o.benRegID IN " +
            "(SELECT b.beneficiaryRegID FROM BenFlowStatus b WHERE b.providerServiceMapId = :psmId AND b.villageID = :villageId) " +
            "AND o.deleted = false ORDER BY o.createdDate DESC")
    List<StopTBGeneralOpd> findAllByProviderServiceMapIDAndVillageID(@Param("psmId") Integer psmId, @Param("villageId") Integer villageId);
}
