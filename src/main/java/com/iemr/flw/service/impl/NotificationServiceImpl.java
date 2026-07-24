package com.iemr.flw.service.impl;

import com.google.gson.Gson;
import com.iemr.flw.domain.iemr.Notification;
import com.iemr.flw.domain.iemr.UserFcmTokenData;
import com.iemr.flw.dto.iemr.NotificationListDTO;
import com.iemr.flw.repo.iemr.NotificationRepository;
import com.iemr.flw.repo.iemr.UserFcmTokenRepo;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
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
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserFcmTokenRepo userFcmTokenRepo;


    private String NOTIFICATION_URL = "https://uatamrit.piramalswasthya.org/common-api/"+"firebaseNotification/sendNotification";

    public String sendNotification(String appType, String topic, String title,
                                   String body, String redirect,
                                   String notificationType, Integer receiverId) {

        logger.info("Initiating notification. appType={}, receiverId={}, type={}",
                appType, receiverId, notificationType);

        try {
            String authHeader = null;
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                authHeader = request.getHeader("jwtToken");
            }

            Integer senderId = jwtUtil.extractUserId(authHeader);

            logger.debug("Sender Id: {}", senderId);

            UserFcmTokenData tokenData = userFcmTokenRepo.findByUserId(4009);

            if (tokenData == null) {
                logger.warn("FCM token not found for receiverId={}", receiverId);
                return "FCM token not found.";
            }

            String token = tokenData.getToken();



            RestTemplate restTemplate = new RestTemplate();

            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add("Content-Type", "application/json");
            headers.add("jwtToken", authHeader);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("appType", appType);
            requestBody.put("token", token);
            requestBody.put("title", title);
            requestBody.put("body", body);

            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("notification_id", notificationType);
            dataMap.put("notification_type", notificationType);
            dataMap.put("nav_id", "INCENTIVE_SCREEN");
            dataMap.put("sender_user_id", senderId);
            dataMap.put("receiver_user_id", receiverId);
            dataMap.put("priority", "HIGH");
            dataMap.put("redirect", redirect);

            requestBody.put("data", dataMap);

            String jsonRequest = new Gson().toJson(requestBody);

            logger.debug("Notification request payload: {}", NOTIFICATION_URL);
            HttpEntity<Object> request = new HttpEntity<>(jsonRequest, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    NOTIFICATION_URL,
                    HttpMethod.POST,
                    request,
                    String.class);

            logger.info("Notification API response. Status={}, Body={}",
                    response.getStatusCode(),
                    response.getBody());

            saveNotificationRecord(appType, senderId, receiverId, title, body,
                    notificationType, "INCENTIVE_APPROVAL", redirect, "HIGH");

            logger.info("Notification record saved successfully. receiverId={}", receiverId);

            return response.getBody();

        } catch (HttpClientErrorException | HttpServerErrorException ex) {

            logger.error("Notification API failed. Status={}, Response={}",
                    ex.getStatusCode(),
                    ex.getResponseBodyAsString(),
                    ex);

            throw ex;

        } catch (Exception ex) {

            logger.error("Unexpected error while sending notification. receiverId={}, type={}",
                    receiverId,
                    notificationType,
                    ex);

            throw new RuntimeException("Failed to send notification.", ex);
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
    public NotificationListDTO getNotifications(Integer receiverUserId) {

        int page = 0;   // First page
        int size = 20;  // Records per page

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdDate")
        );

        Page<Notification> result =
                notificationRepository.findByReceiverUserIdOrderByCreatedDateDesc(
                        receiverUserId,
                        pageable
                );

        long unreadCount =
                notificationRepository.countByReceiverUserIdAndIsReadFalse(receiverUserId);

        return new NotificationListDTO(
                result.getContent(),
                unreadCount,
                result.getTotalElements(),
                result.getNumber(),
                result.getSize()
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
