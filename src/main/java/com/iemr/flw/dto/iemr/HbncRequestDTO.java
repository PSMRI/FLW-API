package com.iemr.flw.dto.iemr;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class HbncRequestDTO {

    private Long id;
    private Long beneficiaryId;
    private Long houseHoldId;
    private String visitDate;
    private String  userName;
    private HbncVisitDTO fields;
}


