
package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.IRSRound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public
interface IRSRoundRepo extends JpaRepository<IRSRound, Long> {
    List<IRSRound> findByHouseholdId(Long householdId);
}
