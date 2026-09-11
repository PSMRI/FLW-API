package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.util.List;

/**
 * POST /badges/earned body. The userId field is accepted for wire compatibility
 * but never trusted: the owner is always taken from the JWT.
 */
@Data
public class BadgeEarnedPushDTO {
    private Integer userId;
    private List<BadgeEarnedDTO> badges;
}
