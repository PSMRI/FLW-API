package com.iemr.flw.repo.iemr;

import java.util.Optional;
import com.iemr.flw.domain.iemr.IncentivePendingActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface IncentivePendingActivityRepository extends JpaRepository<IncentivePendingActivity, Long> {

    Optional<IncentivePendingActivity>
    findByUserIdAndModuleNameAndActivityId(
            Integer userId,
            String moduleName,
            Long activityId
    );
}
