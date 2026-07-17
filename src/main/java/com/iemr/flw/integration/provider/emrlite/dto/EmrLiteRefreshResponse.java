package com.iemr.flw.integration.provider.emrlite.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmrLiteRefreshResponse {
    // Field name is "access" (not "accessToken") per the getAccessToken API contract
    private String access;
}
