package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.util.Map;

@Data
public class NotificationDTO {
    private String syncedBy;
    private java.sql.Timestamp syncedDate;

    private String title;
    private String body;
    private String token;
    private Map<String, String> data;
}
