package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.EligibleCoupleTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface EligibleCoupleTrackingRepo extends JpaRepository<EligibleCoupleTracking, Long> {

    @Query(" SELECT ect FROM EligibleCoupleTracking ect WHERE ect.createdBy = :userId and ect.createdDate >= :fromDate and ect.createdDate <= :toDate")
    List<EligibleCoupleTracking> getECTrackRecords(@Param("userId") String userId,
                                                   @Param("fromDate") Timestamp fromDate, @Param("toDate") Timestamp toDate);

    @Query(value = "SELECT * FROM db_iemr.t_eligible_couple_tracking where ben_id = :benId and is_active = true and MONTH(visit_date) = MONTH(:visitDate) limit 1", nativeQuery = true)
    EligibleCoupleTracking findActiveEligibleCoupleTrackingByBenId(@Param("benId") Long benId, @Param("visitDate") Timestamp visitDate);
}
