package com.iemr.flw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ManualDiagnosticResultRequestDto {

    @NotNull
    private Long beneficiaryId;

    @NotNull
    private String orderType;

    @NotBlank
    private String resultSummary;
}
