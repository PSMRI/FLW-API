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

    // MTB / MTB_PLUS: the MTB test's result (e.g. "TB Positive", "TB Negative").
    private String resultSummary;

    // MTB / MTB_PLUS only: the MDR RIF result run on the same order after a positive MTB
    // (e.g. "DR TB", "Non DR TB"). Send it with resultSummary, or on its own in a later call
    // once the MTB result is stored.
    private String mdrRifResult;

    // Close instead of pushing/entering a result — send at most one, for the test that wasn't done:
    // reasonToCloseXray (XRAY_CHEST); reasonToCloseMTB (MTB/MTB_PLUS, MTB test not done);
    // reasonToCloseRif (MTB/MTB_PLUS, MTB was positive but the MDR RIF test wasn't done).
    private String reasonToCloseXray;
    private String reasonToCloseMTB;
    private String reasonToCloseRif;
}
