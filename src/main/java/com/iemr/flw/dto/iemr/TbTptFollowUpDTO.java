package com.iemr.flw.dto.iemr;

import lombok.Data;

@Data
public class TbTptFollowUpDTO {
    private Long benId;
    private Integer userId;
    private Long houseHoldId;
    TbTptFollowUpListDTO fields;
}
