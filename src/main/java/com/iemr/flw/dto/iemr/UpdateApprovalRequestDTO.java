package com.iemr.flw.dto.iemr;

import lombok.Data;

@Data
public class UpdateApprovalRequestDTO {
    private Integer ashaId;
    private Integer month;
    private Integer year;
    private Integer approvalStatus;
    private String incentiveIds;
    private String remarks;
}
