package com.iemr.flw.dto.iemr;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TbTptFollowUpDTO {
    @JsonProperty("ben_id")
    private Long benId;
    @JsonProperty("user_id")
    private Integer userId;
    @JsonProperty("house_hold_id")
    private Long houseHoldId;
    TbTptFollowUpListDTO fields;
}
