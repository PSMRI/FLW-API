package com.iemr.flw.repo.identity;

import com.iemr.flw.domain.identity.CbacDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface CbacRepo extends JpaRepository<CbacDetails, Long> {

    @Query(value = "SELECT cbac FROM CbacDetails cbac WHERE cbac.createdBy = :user and cbac.createdDate >= :fromDate and cbac.createdDate <= :toDate")
    List<CbacDetails> getAllByCreatedBy(@Param("user") String user, @Param("fromDate") Timestamp fromDate, @Param("toDate") Timestamp toDate);

    @Query(value = "Select * from db_identity.i_cbacdetails where beneficiaryRegId = :benRegId and DATE(createdDate) = DATE(:createdDate) limit 1", nativeQuery = true)
    CbacDetails findCbacDetailsByBeneficiaryRegIdAndCreatedDate(@Param("benRegId") Long benRegId, @Param("createdDate") Timestamp createdDate);
}
