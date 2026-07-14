package com.iemr.flw.dto.iemr;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class AncCounsellingCareListDTO {

        @SerializedName("home_visit_date")
        private String homeVisitDate;

        @SerializedName("select_all")
        private String selectAll;

        @SerializedName("swelling")
        private String swelling;

        @SerializedName("high_bp")
        private String highBp;

        @SerializedName("convulsions")
        private String convulsions;

        @SerializedName("anemia")
        private String anemia;

        @SerializedName("reduced_fetal_movement")
        private String reducedFetalMovement;

        @SerializedName("age_risk")
        private String ageRisk;

        @SerializedName("child_gap")
        private String childGap;

        @SerializedName("short_height")
        private String shortHeight;

        @SerializedName("pre_preg_weight")
        private String prePregWeight;

        @SerializedName("bleeding")
        private String bleeding;

        @SerializedName("miscarriage_history")
        private String miscarriageHistory;

        @SerializedName("four_plus_delivery")
        private String fourPlusDelivery;

        @SerializedName("first_delivery")
        private String firstDelivery;

        @SerializedName("twin_pregnancy")
        private String twinPregnancy;

        @SerializedName("c_section_history")
        private String cSectionHistory;

        @SerializedName("pre_existing_disease")
        private String preExistingDisease;

        @SerializedName("fever_malaria")
        private String feverMalaria;

        @SerializedName("jaundice")
        private String jaundice;

        @SerializedName("sickle_cell")
        private String sickleCell;

        @SerializedName("prolonged_labor")
        private String prolongedLabor;

        @SerializedName("malpresentation")
        private String malpresentation;


}
