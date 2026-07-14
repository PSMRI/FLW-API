package com.iemr.flw.dto.iemr;

import lombok.Data;

@Data
public class AshaByFacilityRequestDTO {
    private Integer facilityId;
    private Integer month;
    private Integer year;
    private Integer approvalStatus;
}
