package com.iemr.flw.dto.iemr;

import lombok.Data;

@Data
public class GeneralCheckUpDto {
    private Long id;
    private Long beneficiaryId;
    private Long houseHoldId;
    private String visitDate;
    private GeneralCheckUpListDto fields;
}
