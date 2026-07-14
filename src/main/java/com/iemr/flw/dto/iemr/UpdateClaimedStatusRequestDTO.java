package com.iemr.flw.dto.iemr;

import lombok.Data;

@Data
public class UpdateClaimedStatusRequestDTO {
    private Integer ashaId;
    private Integer month;
    private Integer year;
    private boolean isClaimed;
}
