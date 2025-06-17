package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.MalariaFollowUp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface MalariaFollowUpRepository extends JpaRepository<MalariaFollowUp, Long> {
    List<MalariaFollowUp> findByUserId(Integer userId);
}
