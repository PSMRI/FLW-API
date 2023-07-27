package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.ANCVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ANCVisitRepo extends JpaRepository<ANCVisit, Long> {

    @Query(value = "SELECT * FROM ELIGIBLE_COUPLE_REGISTER ecr WHERE ecr.benId = :benId", nativeQuery = true)
    List<ANCVisit> getANCForPW(Long pwrId);
}
