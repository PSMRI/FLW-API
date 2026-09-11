package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.BadgeStreakFreeze;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BadgeStreakFreezeRepo extends JpaRepository<BadgeStreakFreeze, Long> {
    /** Global windows (user_id IS NULL) plus this ASHA's own. */
    List<BadgeStreakFreeze> findByUserIdIsNullOrUserId(Integer userId);
}
