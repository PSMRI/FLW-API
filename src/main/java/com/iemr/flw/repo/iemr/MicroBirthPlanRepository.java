package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.MicroBirthPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MicroBirthPlanRepository extends JpaRepository<MicroBirthPlan, Long> {
}
