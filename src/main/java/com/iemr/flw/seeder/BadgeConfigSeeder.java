package com.iemr.flw.seeder;

import com.iemr.flw.domain.iemr.BadgeConfig;
import com.iemr.flw.repo.iemr.BadgeConfigRepo;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Seeds the default badge tuning once (table empty). Values mirror the app's
 * compiled defaults so a fresh server changes nothing until someone edits a row.
 */
@Component
public class BadgeConfigSeeder {

    private static final Logger log = LoggerFactory.getLogger(BadgeConfigSeeder.class);

    private final BadgeConfigRepo repo;

    public BadgeConfigSeeder(BadgeConfigRepo repo) {
        this.repo = repo;
    }

    @PostConstruct
    public void seed() {
        try {
            if (repo.count() > 0) return;
            repo.saveAll(List.of(
                    new BadgeConfig("feature_enabled", "true"),
                    new BadgeConfig("copy_version", "1"),
                    new BadgeConfig("milestones.steady_syncer", "2,4,6,8"),
                    new BadgeConfig("grace.steady_syncer", "1"),
                    new BadgeConfig("milestones.timely_reporter", "1,3,6,12"),
                    new BadgeConfig("milestones.complete_worker", "3"),
                    new BadgeConfig("milestones.community_voice", "2"),
                    new BadgeConfig("milestones.maternal_journey", "1,5,15,30"),
                    new BadgeConfig("milestones.child_fully_protected", "3,10,25,60"),
                    new BadgeConfig("milestones.digital_identity", "10,25,75,150"),
                    new BadgeConfig("enabled.critical_referral", "false")
            ));
            log.info("Badge config defaults seeded");
        } catch (Exception e) {
            log.warn("Badge config seeding skipped: {}", e.getMessage());
        }
    }
}
