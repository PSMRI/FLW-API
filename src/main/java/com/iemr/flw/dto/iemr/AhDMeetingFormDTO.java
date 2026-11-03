package com.iemr.flw.dto.iemr;

import lombok.Data;

@Data
public class AhDMeetingFormDTO {
    private int id = 0;
    private String mobilizedForAHD;
    private String ahdPlace;
    private String ahdDate;
    private byte[] image1;
    private byte[] image2;
}