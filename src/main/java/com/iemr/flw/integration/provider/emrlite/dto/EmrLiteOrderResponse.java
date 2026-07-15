package com.iemr.flw.integration.provider.emrlite.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmrLiteOrderResponse {
    private String externalPatientId;
    private String externalVisitId;
    private String externalOrderId;
    private String orderType;
    private String status;
}
