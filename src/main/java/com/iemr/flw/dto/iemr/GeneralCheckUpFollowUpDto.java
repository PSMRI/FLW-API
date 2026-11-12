package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.time.LocalDate;

@Data
public class GeneralCheckUpFollowUpDto {
    private Long id;
    private Long beneficiaryId;
    private Long houseHoldId;
    private String visitDate;
    private String referralReason;
    private LocalDate referredOnDate;
    private GeneralCheckUpFollowUpListDto fields;
}
