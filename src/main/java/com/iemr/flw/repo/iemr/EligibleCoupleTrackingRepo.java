package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.EligibleCoupleTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EligibleCoupleTrackingRepo extends JpaRepository<EligibleCoupleTracking, Long> {

    @Query(" SELECT ect FROM EligibleCoupleTracking ect WHERE ect.ecrId = :ecrId")
    List<EligibleCoupleTracking> getDetailsForEC(Long ecrId);
}
