package com.iemr.flw.dto.iemr;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Wire shape shared with the app's BadgeEarnedDTO (Moshi). */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BadgeEarnedDTO {
    private String badgeId;
    private Integer level;
    private Long earnedAt;

    /**
     * Stable discriminator for badges that can be earned more than once at the same level.
     * A quarter key, or an opaque digest for per-case awards; empty otherwise. Never a
     * beneficiary identifier — see {@link com.iemr.flw.domain.iemr.BadgeEarned#getAwardKey()}.
     */
    private String awardKey;
}
