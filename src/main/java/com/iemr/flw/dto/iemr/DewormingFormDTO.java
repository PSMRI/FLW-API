package com.iemr.flw.dto.iemr;

import lombok.Data;

@Data
public class DewormingFormDTO {
    private int id = 0;
    private String dewormingDone;
    private String dewormingDate;
    private String dewormingLocation;
    private Integer ageGroup;
    private String image1;
    private String image2;
}