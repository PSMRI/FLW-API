package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.TBScreening;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface TBScreeningRepo extends JpaRepository<TBScreening, Long> {

    @Query(value = "SELECT tbs FROM TBScreening tbs WHERE tbs.benId = :benId and tbs.userId = :userId")
    TBScreening getByUserIdAndBenId(@Param("benId") Long benId, @Param("userId") Integer userId);

    @Query(value = "SELECT tbs FROM TBScreening tbs WHERE tbs.userId = :userId")
    List<TBScreening> getByUserId(@Param("userId") Integer userId);

    @Query("SELECT tbs FROM TBScreening tbs WHERE tbs.benRegID = :benRegID AND tbs.deleted = false")
    TBScreening findByBenRegID(@Param("benRegID") Long benRegID);

    // Read-side fix: same NonUniqueResultException risk as StopTBGeneralExaminationRepo once a
    // beneficiary has more than one visit. Pass PageRequest.of(0, 1) for the latest only.
    @Query("SELECT tbs FROM TBScreening tbs WHERE tbs.benRegID = :benRegID " +
            "AND tbs.deleted = false ORDER BY tbs.visitDate DESC")
    List<TBScreening> findLatestByBenRegID(@Param("benRegID") Long benRegID, Pageable pageable);

    @Query("SELECT tbs FROM TBScreening tbs WHERE tbs.benRegID = :benRegID " +
            "AND tbs.visitCode = :visitCode AND tbs.deleted = false")
    TBScreening findByBenRegIDAndVisitCode(@Param("benRegID") Long benRegID, @Param("visitCode") Long visitCode);

    @Query("SELECT tbs FROM TBScreening tbs WHERE tbs.providerServiceMapID = :psmId AND tbs.deleted = false ORDER BY tbs.visitDate DESC")
    List<TBScreening> findAllByProviderServiceMapID(@Param("psmId") Integer psmId);

    @Query("SELECT tbs FROM TBScreening tbs WHERE tbs.benRegID IN " +
            "(SELECT b.beneficiaryRegID FROM BenFlowStatus b WHERE b.providerServiceMapId = :psmId AND b.villageID = :villageId) " +
            "AND tbs.deleted = false ORDER BY tbs.visitDate DESC")
    List<TBScreening> findAllByProviderServiceMapIDAndVillageID(@Param("psmId") Integer psmId, @Param("villageId") Integer villageId);

    @Transactional
    @Modifying
    @Query("UPDATE TBScreening t SET t.vanSerialNo = t.id WHERE t.id = :id")
    void updateVanSerialNo(@Param("id") Long id);
}
