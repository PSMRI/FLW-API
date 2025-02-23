package com.iemr.flw.repo.identity;

import com.iemr.flw.domain.identity.RMNCHHouseHoldDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HouseHoldRepo extends JpaRepository<RMNCHHouseHoldDetails, Long> {

    @Query(" SELECT t FROM RMNCHHouseHoldDetails t WHERE t.houseoldId =:houseoldId ")
    RMNCHHouseHoldDetails getByHouseHoldID(@Param("houseoldId") long houseoldId);
}
