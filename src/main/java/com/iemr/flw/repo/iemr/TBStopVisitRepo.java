package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.TBStopVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

@Repository
public interface TBStopVisitRepo extends JpaRepository<TBStopVisit, Long> {

    @Query("SELECT v FROM TBStopVisit v WHERE v.beneficiaryRegID = :beneficiaryRegID " +
            "AND v.visitDate BETWEEN :start AND :end")
    TBStopVisit findByBeneficiaryRegIDAndVisitDateBetween(@Param("beneficiaryRegID") Long beneficiaryRegID,
                                                            @Param("start") Timestamp start,
                                                            @Param("end") Timestamp end);

    @Query("SELECT COUNT(v) FROM TBStopVisit v WHERE v.beneficiaryRegID = :beneficiaryRegID")
    Integer getVisitCountForBeneficiary(@Param("beneficiaryRegID") Long beneficiaryRegID);

    @Transactional
    @Modifying
    @Query("UPDATE TBStopVisit t SET t.vanSerialNo = t.id WHERE t.id = :id")
    void updateVanSerialNo(@Param("id") Long id);
}
