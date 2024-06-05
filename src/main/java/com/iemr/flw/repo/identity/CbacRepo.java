package com.iemr.flw.repo.identity;

import com.iemr.flw.domain.identity.CbacDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
public interface CbacRepo extends JpaRepository<CbacDetails, Long> {

    @Query(value = "SELECT cbac FROM CbacDetails cbac WHERE cbac.createdBy = :user and cbac.createdDate >= :fromDate and cbac.createdDate <= :toDate")
    Page<CbacDetails> getAllByCreatedBy(@Param("user") String user,
                                        @Param("fromDate") Timestamp fromDate,
                                        @Param("toDate") Timestamp toDate,
                                        Pageable pageable);

    @Query(value = "Select * from db_identity.i_cbacdetails where beneficiaryRegId = :benRegId and createdDate = :createdDate limit 1", nativeQuery = true)
    CbacDetails findCbacDetailsByBeneficiaryRegIdAndCreatedDate(@Param("benRegId") Long benRegId, @Param("createdDate") Timestamp createdDate);
}
