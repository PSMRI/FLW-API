package com.iemr.flw.dto.iemr;

import lombok.Data;

@Data
public class PhcReviewMeetingFormDTO {
    private int id;
    private String phcReviewDate;
    private String place;
    private Integer noOfBeneficiariesAttended;
    private String image1;
    private String image2;


}