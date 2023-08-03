package com.iemr.flw.repo.identity;

import com.iemr.flw.domain.identity.CbacAdditionalDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CbacAdditionalDetailRepo extends JpaRepository<CbacAdditionalDetails, Long> {

    @Query(value = "select t from CbacAdditionalDetails t where t.cbacDetailsId = :cbacId")
    CbacAdditionalDetails findCbacAdditionalDetail(@Param("cbacId") Long cbacId);
}
