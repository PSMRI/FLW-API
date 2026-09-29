package com.iemr.flw.dto.iemr;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class TbReferralFollowUpDTO {
    private Long benId;
    private Long houseHoldId;
    TbReferralFollowUpListDTO fields;
}
