package com.iemr.flw.domain.iemr;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Streak-freeze window (Badge LLD §3.3): illness / alternate duty. A null userId
 * applies to every ASHA; a null or empty badgeId applies to every streak badge.
 */
@Entity
@Data
@Table(name = "badge_streak_freeze", schema = "db_iemr")
public class BadgeStreakFreeze {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "badge_id", length = 50)
    private String badgeId;

    /** Epoch millis, inclusive. */
    @Column(name = "start_date", nullable = false)
    private Long startDate;

    /** Epoch millis, inclusive. */
    @Column(name = "end_date", nullable = false)
    private Long endDate;
}
