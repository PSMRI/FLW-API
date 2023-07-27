package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.DeliveryOutcome;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface DeliveryOutcomeRepo extends JpaRepository<DeliveryOutcome, Long> {

    @Query(" SELECT do FROM DeliveryOutcome do WHERE do.createdBy = :userId and do.createdDate >= :fromDate and do.createdDate <= :toDate")
    List<DeliveryOutcome> getDeliveryOutcomeByBenId(@Param("userId") Integer userId,
                                                    @Param("fromDate") Timestamp fromDate, @Param("toDate") Timestamp toDate);
}
