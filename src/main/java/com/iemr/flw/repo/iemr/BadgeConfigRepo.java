package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.BadgeConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BadgeConfigRepo extends JpaRepository<BadgeConfig, String> {
}
