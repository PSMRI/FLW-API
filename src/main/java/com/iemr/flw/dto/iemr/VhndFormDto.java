package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.sql.Date;

@Data
public class VhndFormDto {
    private String ashaId;
    private Date date;
    private String place;
    private int participantCount;
    private String imageUrls; // Comma separated URLs or Base64 if needed
}

