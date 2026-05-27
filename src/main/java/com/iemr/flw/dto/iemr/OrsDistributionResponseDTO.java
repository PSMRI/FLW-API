package com.iemr.flw.dto.iemr;

import lombok.Data;

@Data
public class OrsDistributionResponseDTO {
    private Long id;
    private Long beneficiaryId;
    private Long houseHoldId;
    private String visitDate;
    private String  userName;
    private OrsDistributionResponseListDTO fields;
}
