package com.iemr.flw.dto.iemr;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VaccineDTO {

    private short vaccineId;
    private String immunizationService;
    private String vaccineName;
    private Long minAllowedAgeInMillis;
    private Long maxAllowedAgeInMillis;
    private String category;
    private Long overdueDurationSinceMinInMillis;
    private Integer dependantVaccineId;
    private Long dependantCoolDuration;
}
