package com.iemr.flw.dto.iemr;

import lombok.Data;

@Data
public class FilariasisResponseDTO {
    private Long id;
    private Long houseHoldId;
    private FilariasisCampaignListResponseDTO fields;
}
