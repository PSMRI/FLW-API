package com.iemr.flw.dto.crashlogs;

import lombok.Data;

@Data
public class CrashLogRequest {
    private String appVersion;
    private String deviceId;
    private String timestamp;
}
