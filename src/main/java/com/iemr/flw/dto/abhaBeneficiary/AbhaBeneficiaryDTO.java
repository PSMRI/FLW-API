package com.iemr.flw.dto.abhaBeneficiary;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AbhaBeneficiaryDTO {
    @JsonProperty("personName")
    private String personName;

    private String firstName;

    private String lastName;

    @JsonProperty("age")
    private String age;

    @JsonProperty("address")
    private String address;

    @JsonProperty("district")
    private String district;

    @JsonProperty("mobileNo")
    private String mobileNo;

    @JsonProperty("block")
    private String block;

    @JsonProperty("cardNo")
    private String cardNo;

    @JsonProperty("villagename")
    private String villagename;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("district_Code")
    private String districtCode;

    @JsonProperty("block_Code")
    private String blockCode;

    @JsonProperty("village_Code")
    private String villageCode;

    @JsonProperty("rural_Urban")
    private String ruralUrban;

    @JsonProperty("abhaId")
    private String abhaId;

    @JsonProperty("vvs")
    private String vvs;

    @JsonProperty("familyid")
    private String familyid;

    @JsonProperty("dob")
    @JsonAlias("dob_secc")
    private String dob;

}
