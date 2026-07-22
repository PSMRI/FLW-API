package com.iemr.flw.controller;

import com.iemr.flw.dto.iemr.NotificationListDTO;
import com.iemr.flw.dto.iemr.SendNotificationRequestDTO;
import com.iemr.flw.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    private NotificationService notificationService;

    /** GET /notifications/{receiverUserId}?page=0&size=20 */
    @GetMapping("/{receiverUserId}")
    public Map<String, Object> listNotifications(
            @PathVariable Integer receiverUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (receiverUserId != null) {
                NotificationListDTO notificationListDTO =
                        notificationService.getNotifications(receiverUserId, page, size);

                if (notificationListDTO != null) {
                    response.put("statusCode", 200);
                    response.put("data", notificationListDTO);
                } else {
                    response.put("statusCode", 5000);
                    response.put("error", "Invalid/NULL request obj");
                }
            } else {
                response.put("statusCode", 5000);
                response.put("error", "Invalid/NULL request obj");
            }
        } catch (Exception e) {
            logger.error("Error in get data : " + e);
            response.put("statusCode", 5000);
            response.put("error", "Error in get data : " + e);
        }
        return response;
    }

    /** GET /notifications/{receiverUserId}/unread-count */
    @GetMapping("/{receiverUserId}/unread-count")
    public Map<String, Object> unreadCount(@PathVariable Integer receiverUserId) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (receiverUserId != null) {
                Long unreadCount = notificationService.getUnreadCount(receiverUserId);

                if (unreadCount != null) {
                    response.put("statusCode", 200);
                    response.put("data", unreadCount);
                } else {
                    response.put("statusCode", 5000);
                    response.put("error", "Invalid/NULL request obj");
                }
            } else {
                response.put("statusCode", 5000);
                response.put("error", "Invalid/NULL request obj");
            }
        } catch (Exception e) {
            logger.error("Error in get data : " + e);
            response.put("statusCode", 5000);
            response.put("error", "Error in get data : " + e);
        }
        return response;
    }

    /** PUT /notifications/{notificationId}/read */
    @PutMapping("/{notificationId}/read")
    public Map<String, Object> markAsRead(@PathVariable Long notificationId) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (notificationId != null) {
                boolean success = notificationService.markAsRead(notificationId);

                response.put("statusCode", 200);
                response.put("data", success);
            } else {
                response.put("statusCode", 5000);
                response.put("error", "Invalid/NULL request obj");
            }
        } catch (Exception e) {
            logger.error("Error in update data : " + e);
            response.put("statusCode", 5000);
            response.put("error", "Error in update data : " + e);
        }
        return response;
    }

    /** POST /notifications/send */
    @PostMapping("/send")
    public Map<String, Object> sendNotification(@RequestBody SendNotificationRequestDTO request) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (request != null) {
                String result = notificationService.sendNotification(
                        request.getAppType(),
                        request.getTopic(),
                        request.getTitle(),
                        request.getBody(),
                        request.getRedirect(),
                        request.getNotificationType(),
                        request.getReceiverId());

                if (result != null) {
                    response.put("statusCode", 200);
                    response.put("data", result);
                } else {
                    response.put("statusCode", 5000);
                    response.put("error", "Invalid/NULL request obj");
                }
            } else {
                response.put("statusCode", 5000);
                response.put("error", "Invalid/NULL request obj");
            }
        } catch (Exception e) {
            logger.error("Error in send notification : " + e);
            response.put("statusCode", 5000);
            response.put("error", "Error in send notification : " + e);
        }
        return response;
    }

    /** PUT /notifications/{receiverUserId}/read-all */
    @PutMapping("/{receiverUserId}/read-all")
    public Map<String, Object> markAllAsRead(@PathVariable Integer receiverUserId) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (receiverUserId != null) {
                int updatedCount = notificationService.markAllAsRead(receiverUserId);

                response.put("statusCode", 200);
                response.put("data", updatedCount);
            } else {
                response.put("statusCode", 5000);
                response.put("error", "Invalid/NULL request obj");
            }
        } catch (Exception e) {
            logger.error("Error in update data : " + e);
            response.put("statusCode", 5000);
            response.put("error", "Error in update data : " + e);
        }
        return response;
    }
}