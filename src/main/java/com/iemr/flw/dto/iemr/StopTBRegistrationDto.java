package com.iemr.flw.dto.iemr;

import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.Data;

@Data
public class StopTBRegistrationDto {

    // Standard beneficiary fields — forwarded to Common-API
    @JsonRawValue
    private String benD;

    // Stop TB specific fields
    private Integer providerServiceMapID;
    private String personFrom;
    private String caseFindingType;
    private Integer tuId;
    private String tuName;
    private Integer healthFacilityId;
    private String healthFacilityName;
    private Boolean isMobileAvailable;

    // Anthropometry fields
    private Double weight;
    private Double height;
    private Double bmi;
    private Double temperatureValue;

    private String createdBy;

    // For get/update
    private Long benRegID;
    private String modifiedBy;
}
