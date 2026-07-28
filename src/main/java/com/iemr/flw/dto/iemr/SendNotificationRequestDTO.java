package com.iemr.flw.dto.iemr;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendNotificationRequestDTO {
    private String appType;
    private String topic;
    private String title;
    private String body;
    private String redirect;
    private String notificationType;
    private Integer receiverId;
}
