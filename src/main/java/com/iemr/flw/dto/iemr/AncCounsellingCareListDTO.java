package com.iemr.flw.dto.iemr;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class AncCounsellingCareListDTO {

        @JsonProperty("home_visit_date")
        @SerializedName("home_visit_date")
        private String homeVisitDate;

        @JsonProperty("visit_number")
        @SerializedName("visit_number")
        private String visitNumber;

        @JsonProperty("select_all")
        @SerializedName("select_all")
        private String selectAll;

        private String swelling;

        @JsonProperty("high_bp")
        @SerializedName("high_bp")
        private String highBp;

        private String convulsions;

        private String anemia;

        @JsonProperty("reduced_fetal_movement")
        @SerializedName("reduced_fetal_movement")
        private String reducedFetalMovement;

        @JsonProperty("age_risk")
        @SerializedName("age_risk")
        private String ageRisk;

        @JsonProperty("child_gap")
        @SerializedName("child_gap")
        private String childGap;

        @JsonProperty("short_height")
        @SerializedName("short_height")
        private String shortHeight;

        @JsonProperty("pre_preg_weight")
        @SerializedName("pre_preg_weight")
        private String prePregWeight;

        private String bleeding;

        @JsonProperty("miscarriage_history")
        @SerializedName("miscarriage_history")
        private String miscarriageHistory;

        @JsonProperty("four_plus_delivery")
        @SerializedName("four_plus_delivery")
        private String fourPlusDelivery;

        @JsonProperty("first_delivery")
        @SerializedName("first_delivery")
        private String firstDelivery;

        @JsonProperty("twin_pregnancy")
        @SerializedName("twin_pregnancy")
        private String twinPregnancy;

        @JsonProperty("c_section_history")
        @SerializedName("c_section_history")
        private String cSectionHistory;

        @JsonProperty("pre_existing_disease")
        @SerializedName("pre_existing_disease")
        private String preExistingDisease;

        @JsonProperty("fever_malaria")
        @SerializedName("fever_malaria")
        private String feverMalaria;

        private String jaundice;

        @JsonProperty("sickle_cell")
        @SerializedName("sickle_cell")
        private String sickleCell;

        @JsonProperty("prolonged_labor")
        @SerializedName("prolonged_labor")
        private String prolongedLabor;

        private String malpresentation;



}
