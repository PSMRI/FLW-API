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

    @Query("SELECT t FROM StopTBRegistration t WHERE t.providerServiceMapID = :psmId AND t.villageId = :villageId AND t.deleted = false ORDER BY t.createdDate DESC")
    List<StopTBRegistration> getRegistrarWorklist(@Param("psmId") Integer psmId, @Param("villageId") Integer villageId);
}
