package com.iemr.flw.dto.iemr;

import lombok.Data;

@Data
public class FilariasisCampaignDTO {
    private String syncedBy;
    private java.sql.Timestamp syncedDate;

    private Long id;
    private String visitDate;
    private FilariasisCampaignListDTO fields;
}
