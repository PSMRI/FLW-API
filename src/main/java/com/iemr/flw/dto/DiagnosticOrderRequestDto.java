package com.iemr.flw.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DiagnosticOrderRequestDto {

    @NotNull
    private Long beneficiaryId;

    @NotNull
    private String orderType;

    private String orderEvent;

    // Close instead of pushing/entering a result — send at most one, for the test that wasn't done:
    // reasonToCloseXray (XRAY_CHEST); reasonToCloseMTB (MTB/MTB_PLUS, MTB test not done);
    // reasonToCloseRif (MTB/MTB_PLUS, MTB was positive but the MDR RIF test wasn't done).
    private String reasonToCloseXray;
    private String reasonToCloseMTB;
    private String reasonToCloseRif;

    @NotNull
    @Valid
    private PatientDto patient;

    @Data
    @NoArgsConstructor
    public static class PatientDto {
        @NotBlank private String firstName;
        @NotBlank private String lastName;
        @NotBlank
        @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "dateOfBirth must be in yyyy-MM-dd format")
        private String dateOfBirth;
        @NotBlank
        @Pattern(regexp = "Male|Female|Other", message = "sex must be exactly one of Male, Female, Others")
        private String sex;
    }
}