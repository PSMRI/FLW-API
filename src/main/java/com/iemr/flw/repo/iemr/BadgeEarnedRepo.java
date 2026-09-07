package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.BadgeEarned;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BadgeEarnedRepo extends JpaRepository<BadgeEarned, Long> {
    List<BadgeEarned> findByUserIdOrderByEarnedAtAsc(Integer userId);

    boolean existsByUserIdAndBadgeIdAndLevel(Integer userId, String badgeId, Integer level);
}
