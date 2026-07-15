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
    private Long benRegID;

    @NotNull
    private Long visitCode;

    @NotNull
    private Integer providerServiceMapID;

    @NotNull
    private String orderType;

    private String orderEvent;

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
        @NotBlank private String sex;
    }
}