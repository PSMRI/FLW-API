package com.iemr.flw.service.impl;

import com.google.gson.Gson;
import com.iemr.flw.domain.iemr.Notification;
import com.iemr.flw.dto.iemr.NotificationListDTO;
import com.iemr.flw.repo.iemr.NotificationRepository;
import com.iemr.flw.service.NotificationService;
import com.iemr.flw.utils.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class NotificationServiceImpl implements NotificationService {
    final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    @Value("${common-api-base-url}")
    private String commonApiBaseUrl;

    @Value("${notificationurl}")
    private String notificationurl;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private NotificationRepository notificationRepository;


    private String NOTIFICATION_URL = commonApiBaseUrl+notificationurl;

    public String sendNotification(String appType, String topic, String title, String body, String redirect,String notificationType,Integer reciverID) {
        try {
            String authHeader = null;
            String jwtToken = null;
            Integer senderId = jwtUtil.extractUserId(jwtToken);

            // Check if we have HTTP request context
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest httpServletRequest = attributes.getRequest();

                authHeader = httpServletRequest.getHeader("Authorization");

                if (httpServletRequest.getCookies() != null) {
                    for (Cookie cookie : httpServletRequest.getCookies()) {
                        if ("Jwttoken".equals(cookie.getName())) {
                            jwtToken = cookie.getValue();
                        }
                    }
                }
            }

            // If no request context, set default (for scheduler/startup use)
            if (authHeader == null) {
                authHeader = "Bearer DEFAULT_TOKEN_IF_REQUIRED"; // or leave null if API allows
            }
            if (jwtToken == null) {
                jwtToken = "DEFAULT_JWT_IF_REQUIRED";
            }

            RestTemplate restTemplate = new RestTemplate();
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add("Content-Type", "application/json");

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("appType", appType);
            requestBody.put("token", topic);
            requestBody.put("title", title);
            requestBody.put("body", body);

            Map<String, Object> dataMap = new HashMap<>();

            dataMap.put("notification_id", notificationType);
            dataMap.put("notification_type", notificationType);
            dataMap.put("nav_id", "INCENTIVE_SCREEN");

            dataMap.put("sender_user_id", senderId);
            dataMap.put("receiver_user_id", reciverID);


            dataMap.put("priority", "HIGH");

            // Existing redirect field
            dataMap.put("redirect", redirect);

            requestBody.put("data", dataMap);

            String jsonRequest = new Gson().toJson(requestBody);

            HttpEntity<Object> request = new HttpEntity<>(jsonRequest, headers);

            ResponseEntity<String> response = restTemplate.exchange(NOTIFICATION_URL, HttpMethod.POST, request, String.class);
            // Persist locally regardless of downstream push result, so the
            // in-app notification list stays accurate even if push delivery
            // to a device fails.
            saveNotificationRecord(appType, senderId, reciverID, title, body,
                    notificationType, "INCENTIVE_APPROVAL", redirect, "HIGH");
            return response.getBody();
        }catch (Exception e){
            return e.getMessage();
        }

    }

        private void saveNotificationRecord(String appType, Integer senderUserId, Integer receiverUserId,
                                        String title, String body, String notificationType,
                                        String navId, String redirect, String priority) {
        try {
            Notification notification = new Notification();
            notification.setAppType(appType);
            notification.setSenderUserId(senderUserId);
            notification.setReceiverUserId(receiverUserId);
            notification.setTitle(title);
            notification.setBody(body);
            notification.setNotificationType(notificationType);
            notification.setNavId(navId);
            notification.setRedirect(redirect);
            notification.setPriority(priority);
            notification.setRead(false);
            notificationRepository.save(notification);
        } catch (Exception e) {
            logger.error("Failed to persist notification record for receiver {}", receiverUserId, e);
        }
    }

    /** Paged list of a user's notifications, newest first, with unread count attached. */
    public NotificationListDTO getNotifications(Integer receiverUserId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
        Page<Notification> result =
                notificationRepository.findByReceiverUserIdOrderByCreatedDateDesc(receiverUserId, pageable);
        long unreadCount = notificationRepository.countByReceiverUserIdAndIsReadFalse(receiverUserId);

        return new NotificationListDTO(
                result.getContent(),
                unreadCount,
                result.getTotalElements(),
                page,
                size
        );
    }

    /**
     * Lightweight call for a badge — no list, just the count.
     */
    public Long getUnreadCount(Integer receiverUserId) {
        return notificationRepository.countByReceiverUserIdAndIsReadFalse(receiverUserId);
    }

    /** Marks a single notification read. Returns true if a row was updated. */
    public boolean markAsRead(Long notificationId) {
        int updated = notificationRepository.markAsRead(notificationId, new Date());
        return updated > 0;
    }

    /** Marks all of a user's notifications read. Returns the number updated. */
    public int markAllAsRead(Integer receiverUserId) {
        return notificationRepository.markAllAsRead(receiverUserId, new Date());
    }
}
