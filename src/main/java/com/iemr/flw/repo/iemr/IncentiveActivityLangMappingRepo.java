package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.IncentiveActivityLangMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IncentiveActivityLangMappingRepo extends JpaRepository<IncentiveActivityLangMapping, Long> {
    IncentiveActivityLangMapping findByIdAndName(Long activityId,String activityName);
    List<IncentiveActivityLangMapping> findAllByIdIn(List<Long> ids);


}
