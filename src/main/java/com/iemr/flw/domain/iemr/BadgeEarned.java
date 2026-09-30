package com.iemr.flw.domain.iemr;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

/**
 * Append-only badge award log (Badge LLD §4.1, "once earned, never revoked").
 * The unique key mirrors the device's local constraint so re-uploads after a
 * reinstall or a retried sync are idempotent.
 *
 * The key includes award_key because (user, badge, level) is not unique for every badge.
 * Quarterly badges are re-earned each quarter at the same level, and per-case badges are
 * earned once per beneficiary, so without it the second and later awards collapse into the
 * first on upload and cannot be told apart on restore.
 */
@Entity
@Data
@Table(name = "badge_earned", schema = "db_iemr",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_badge_earned_user_badge_level_key",
                columnNames = {"user_id", "badge_id", "level", "award_key"}))
public class BadgeEarned {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "badge_id", length = 50, nullable = false)
    private String badgeId;

    @Column(name = "level", nullable = false)
    private Integer level;

    /**
     * What makes two awards of the same badge and level different: a quarter key such as
     * "2026-Q3", or an opaque digest for per-case badges. Empty for streak and cumulative
     * badges, which are earned once per level.
     *
     * Never a beneficiary identifier. The device hashes those before they are sent, so this
     * column can separate two awards without the server learning who either was about
     * (Badge LLD §4).
     */
    @Column(name = "award_key", length = 64, nullable = false)
    private String awardKey = "";

    /** Epoch millis when the device awarded it. */
    @Column(name = "earned_at", nullable = false)
    private Long earnedAt;

    /** Epoch millis when the server first received it. */
    @Column(name = "received_at", nullable = false)
    private Long receivedAt;
}
