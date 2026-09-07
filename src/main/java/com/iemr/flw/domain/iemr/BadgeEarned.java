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
 */
@Entity
@Data
@Table(name = "badge_earned", schema = "db_iemr",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_badge_earned_user_badge_level",
                columnNames = {"user_id", "badge_id", "level"}))
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

    /** Epoch millis when the device awarded it. */
    @Column(name = "earned_at", nullable = false)
    private Long earnedAt;

    /** Epoch millis when the server first received it. */
    @Column(name = "received_at", nullable = false)
    private Long receivedAt;
}
