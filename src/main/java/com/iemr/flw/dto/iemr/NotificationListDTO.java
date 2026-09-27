package com.iemr.flw.dto.iemr;

import com.iemr.flw.domain.iemr.Notification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response payload for the "list notifications" endpoint.
 * Bundles the page of notifications together with the receiver's
 * total unread count so the client can update a badge in one call.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationListDTO {
    private List<Notification> notifications;
    private long unreadCount;
    private long totalCount;
    private int page;
    private int size;
}