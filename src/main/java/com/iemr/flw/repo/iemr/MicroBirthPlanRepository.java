package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.MicroBirthPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MicroBirthPlanRepository extends JpaRepository<MicroBirthPlan, Long> {
    Optional<MicroBirthPlan> findByBenId(Long benId);

    List<MicroBirthPlan> findByUserId(Integer userId);
}