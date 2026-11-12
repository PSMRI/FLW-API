package com.iemr.flw.dto.iemr;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class PhcReviewMeetingFormDTO {
    private int id;
    private String phcReviewDate;
    private String place;
    private Integer noOfBeneficiariesAttended;
    private String villageName;
    private String mitaninHistory;
    private String mitaninActivityCheckList;
    private Integer placeId;
    private String image1;
    private String image2;



}