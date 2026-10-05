package com.iemr.flw.dto.iemr;

import lombok.Data;

@Data
public class OrsDistributionDTO {
    private String syncedBy;
    private java.sql.Timestamp syncedDate;

    private Long id;
    private Long beneficiaryId;
    private Long houseHoldId;
    private String visitDate;
    private String  userName;
    private OrsDistributionListDTO fields;
}
