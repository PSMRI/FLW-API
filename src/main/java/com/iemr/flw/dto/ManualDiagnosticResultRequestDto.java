package com.iemr.flw.dto;

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

    private String resultSummary;

    private String reasonToClose;
}
