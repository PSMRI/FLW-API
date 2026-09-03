package com.iemr.flw.integration.provider.emrlite.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmrLiteLoginRequest {
    private String username;
    private String password;
}
