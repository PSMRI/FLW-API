package com.iemr.flw.dto.iemr;

import lombok.Data;

@Data
public class GeneralCheckUpFollowUpDto {
    private Long id;
    private Long beneficiaryId;
    private Long houseHoldId;
    private String visitDate;
    private GeneralCheckUpFollowUpListDto fields;
}
