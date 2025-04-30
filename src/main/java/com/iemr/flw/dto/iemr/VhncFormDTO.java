package com.iemr.flw.dto.iemr;

import lombok.Data;

@Data
public class VhncFormDTO {
    private int id;
    private String vhncDate;
    private String place;
    private Integer noOfBeneficiariesAttended;
    private String image1;
    private String image2;
}