package com.iemr.flw.integration.provider.emrlite.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmrLiteLoginResponse {
    private String accessToken;
    private String refreshToken;
}
