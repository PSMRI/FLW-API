package com.iemr.flw.dto.iemr;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class IfaDistributionDTO {

    @SerializedName("beneficiaryId")
    private Long beneficiaryId;

    @SerializedName("houseHoldId")
    private Long houseHoldId;

    @SerializedName("formId")
    private String formId;

    @SerializedName("visitDate")
    private String visitDate; // can be "N/A" or date string

    @SerializedName("fields")
    private FieldsDTO fields;

    @Data
    public static class FieldsDTO {
        @SerializedName("ifa_provision_date")
        private String ifa_provision_date;

        @SerializedName("mcp_card_upload")
        private String mcp_card_upload;

        @SerializedName("ifa_bottle_count")
        private Double ifa_bottle_count;
    }
}
