package com.iemr.flw.dto.iemr;

import lombok.Data;

@Data
public class DewormingFormDTO {
    private String syncedBy;
    private java.sql.Timestamp syncedDate;

    private int id = 0;
    private String dewormingDone;
    private String dewormingDate;
    private String dewormingLocation;
    private Integer ageGroup;
    private byte[] image1;
    private byte[] image2;
}