package com.iemr.flw.dto.iemr;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class GeneralCheckUpFollowUpListDto {
    @JsonProperty("follow_up_date")
    private String followUpDate;

    @JsonProperty("followup_status")
    private String followupStatus;

    @JsonProperty("referred_to")
    private String referredTo;

}
