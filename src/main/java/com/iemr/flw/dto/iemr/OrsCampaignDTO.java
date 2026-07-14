package com.iemr.flw.dto.iemr;

import lombok.Data;

@Data
public class OrsCampaignDTO {
    private Long id;
    private String visitDate;
    private OrsCampaignListDTO fields;
}
