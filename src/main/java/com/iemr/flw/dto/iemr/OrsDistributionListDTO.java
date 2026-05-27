package com.iemr.flw.dto.iemr;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class OrsDistributionListDTO {

    @SerializedName("visit_date")
    private String visit_date;

    @SerializedName("num_under5_children")
    private Double num_under5_children=1.0;

    @SerializedName("num_ors_packets")
    private Double num_ors_packets;


}
