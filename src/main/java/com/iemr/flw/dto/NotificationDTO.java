package com.iemr.flw.dto;

import lombok.Data;

import java.util.Map;

@Data
public class NotificationDTO {
    private String title;
    private String body;
    private String token;
    private Map<String, String> data;
}
