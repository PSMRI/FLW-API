package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.DeliveryOutcome;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryOutcomeRepo extends JpaRepository<DeliveryOutcome, Long> {

    DeliveryOutcome getDeliveryOutcomeByBenId(Long benId);
}
