package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.TBConfirmedCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface TBConfirmedTreatmentRepository
        extends JpaRepository<TBConfirmedCase, Integer> {

    List<TBConfirmedCase> findByBenId(Long benId);
    List<TBConfirmedCase> findByUserId(Integer benId);
    List<TBConfirmedCase> findByBenIdAndVisitCode(Long benId, Long visitCode);

    @Query("SELECT tc FROM TBConfirmedCase tc WHERE tc.benId IN " +
            "(SELECT b.beneficiaryID FROM BenFlowStatus b WHERE b.providerServiceMapId = :psmId AND b.villageID = :villageId)")
    List<TBConfirmedCase> getByProviderServiceMapIdAndVillageId(@Param("psmId") Integer psmId, @Param("villageId") Integer villageId);

    @Transactional
    @Modifying
    @Query("UPDATE TBConfirmedCase t SET t.vanSerialNo = t.id WHERE t.id = :id")
    void updateVanSerialNo(@Param("id") Integer id);
}
