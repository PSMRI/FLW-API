package com.iemr.flw.dto.iemr;

import lombok.Data;

@Data
public class FilariasisCampaignDTO {
    private Long id;
    private String visitDate;
    private FilariasisCampaignListDTO fields;
}
