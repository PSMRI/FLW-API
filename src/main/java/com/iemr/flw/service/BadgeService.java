package com.iemr.flw.service;

import com.iemr.flw.domain.iemr.BadgeConfig;
import com.iemr.flw.domain.iemr.BadgeEarned;
import com.iemr.flw.domain.iemr.BadgeStreakFreeze;
import com.iemr.flw.dto.iemr.BadgeEarnedDTO;
import com.iemr.flw.repo.iemr.BadgeConfigRepo;
import com.iemr.flw.repo.iemr.BadgeEarnedRepo;
import com.iemr.flw.repo.iemr.BadgeStreakFreezeRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Badges backend (Badge LLD §4). The device owns evaluation; the server only
 * distributes tuning + freeze windows and keeps the durable award log so a
 * reinstall restores progress.
 */
@Service
public class BadgeService {

    private final BadgeConfigRepo configRepo;
    private final BadgeStreakFreezeRepo freezeRepo;
    private final BadgeEarnedRepo earnedRepo;

    public BadgeService(BadgeConfigRepo configRepo, BadgeStreakFreezeRepo freezeRepo,
                        BadgeEarnedRepo earnedRepo) {
        this.configRepo = configRepo;
        this.freezeRepo = freezeRepo;
        this.earnedRepo = earnedRepo;
    }

    public Map<String, String> getConfig() {
        Map<String, String> out = new LinkedHashMap<>();
        for (BadgeConfig row : configRepo.findAll()) {
            out.put(row.getKey(), row.getValue());
        }
        return out;
    }

    public List<BadgeStreakFreeze> getFreezes(Integer userId) {
        return freezeRepo.findByUserIdIsNullOrUserId(userId);
    }

    public List<BadgeEarnedDTO> getEarned(Integer userId) {
        return earnedRepo.findByUserIdOrderByEarnedAtAsc(userId).stream()
                .map(e -> new BadgeEarnedDTO(e.getBadgeId(), e.getLevel(), e.getEarnedAt()))
                .toList();
    }

    /** Idempotent: rows already known for (user, badge, level) are skipped. Returns rows inserted. */
    @Transactional
    public int saveEarned(Integer userId, List<BadgeEarnedDTO> badges) {
        if (badges == null || badges.isEmpty()) return 0;
        long now = System.currentTimeMillis();
        int inserted = 0;
        for (BadgeEarnedDTO dto : badges) {
            if (dto == null || dto.getBadgeId() == null || dto.getLevel() == null) continue;
            if (earnedRepo.existsByUserIdAndBadgeIdAndLevel(userId, dto.getBadgeId(), dto.getLevel())) continue;
            BadgeEarned row = new BadgeEarned();
            row.setUserId(userId);
            row.setBadgeId(dto.getBadgeId());
            row.setLevel(dto.getLevel());
            row.setEarnedAt(dto.getEarnedAt() != null ? dto.getEarnedAt() : now);
            row.setReceivedAt(now);
            earnedRepo.save(row);
            inserted++;
        }
        return inserted;
    }
}
