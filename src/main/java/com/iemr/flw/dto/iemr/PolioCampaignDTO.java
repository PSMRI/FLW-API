package com.iemr.flw.dto.iemr;

import lombok.Data;

@Data
public class PolioCampaignDTO {
    private Long id;
    private String visitDate;
    private PolioCampaignListDTO fields;
}
