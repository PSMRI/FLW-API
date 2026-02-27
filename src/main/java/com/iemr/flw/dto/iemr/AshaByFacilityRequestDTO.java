package com.iemr.flw.dto.iemr;

import lombok.Data;

@Data
public class AshaByFacilityRequestDTO {
    Integer facilityId;
    private Integer month;
    private Integer year;
}
