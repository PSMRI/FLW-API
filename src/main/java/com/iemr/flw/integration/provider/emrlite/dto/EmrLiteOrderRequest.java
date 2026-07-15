package com.iemr.flw.integration.provider.emrlite.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmrLiteOrderRequest {

    private String externalPatientId;
    private String externalVisitId;
    private String externalOrderId;
    private String orderType;
    private String orderedAt;
    private PatientDto patient;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PatientDto {
        private String firstName;
        private String lastName;
        private String dateOfBirth;
        private String sex;
    }
}
