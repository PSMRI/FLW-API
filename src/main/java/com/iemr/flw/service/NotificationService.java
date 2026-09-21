package com.iemr.flw.service;

import com.iemr.flw.dto.iemr.NotificationListDTO;

public interface NotificationService {
    public String sendNotification(String appType, String topic, String title, String body, String redirect,String notificationType,Integer reciverID);
    NotificationListDTO getNotifications(Integer receiverUserId);

    Long getUnreadCount(Integer receiverUserId);

    boolean markAsRead(Long notificationId);

    int markAllAsRead(Integer receiverUserId);
}
