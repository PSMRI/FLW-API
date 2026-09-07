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
}
