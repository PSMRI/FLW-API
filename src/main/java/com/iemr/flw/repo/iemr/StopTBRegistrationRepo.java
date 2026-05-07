package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.StopTBRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface StopTBRegistrationRepo extends JpaRepository<StopTBRegistration, Long> {

    @Query("SELECT t FROM StopTBRegistration t WHERE t.benRegID = :benRegID AND t.deleted = false")
    StopTBRegistration findByBenRegID(@Param("benRegID") Long benRegID);

    @Query("SELECT t FROM StopTBRegistration t WHERE t.providerServiceMapID = :psmId AND t.createdBy = :createdBy AND t.createdDate >= :fromDate AND t.createdDate <= :toDate AND t.deleted = false")
    List<StopTBRegistration> getRegistrarWorklist(
            @Param("psmId") Integer psmId,
            @Param("createdBy") String createdBy,
            @Param("fromDate") Timestamp fromDate,
            @Param("toDate") Timestamp toDate
    );
}
