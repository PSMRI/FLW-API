package com.iemr.flw.dto.iemr;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class HbncRequestDTO {

    private Long id;
    @SerializedName("benId")
    private Long beneficiaryId;
    @SerializedName("hhId")
    private Long houseHoldId;
    @SerializedName("visitDate")
    private String visitDate;
    @SerializedName("fields")
    private HbncVisitDTO fields;
}
