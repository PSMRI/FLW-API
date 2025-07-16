package com.iemr.flw.dto.iemr;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class HbncRequestDTO {

    private Long id;
    @SerializedName("beneficiaryId")
    private Long beneficiaryId;
    @SerializedName("houseHoldId")
    private Long houseHoldId;
    @SerializedName("visitDate")
    private String visitDate;
    @SerializedName("fields")
    private HbncVisitDTO fields;
}
