package com.iemr.flw.dto.iemr;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TbReferralFollowUpListDTO {
    @JsonProperty("referred_on_date")
    private String referredOnDate;
    @JsonProperty("follow_up_date")
    private String followUpDate;
    @JsonProperty("follow_up_status")
    private String followUpStatus;
}
