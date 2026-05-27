package com.iemr.flw.dto.iemr;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;

@Data
public class SamListDTO {


    @SerializedName("visit_label")
    private String visit_label;

    @SerializedName("muac")
    private String muac;

    @SerializedName("weight_for_height_status")
    private String weight_for_height_status;

    @SerializedName("is_child_referred_nrc")
    private String is_child_referred_nrc;

    @SerializedName("is_child_admitted_nrc")
    private String is_child_admitted_nrc;

    @SerializedName("nrc_admission_date")
    private String nrc_admission_date;

    @SerializedName("is_child_discharged_nrc")
    private String is_child_discharged_nrc;

    @SerializedName("nrc_discharge_date")
    private String nrc_discharge_date;

    @SerializedName("follow_up_visit_date")
    private ArrayList<String> follow_up_visit_date;

    @SerializedName("sam_status")
    private String sam_status;

    @SerializedName("discharge_summary")
    private String discharge_summary;

    @SerializedName("view_discharge_docs")
    private String view_discharge_docs;
}
