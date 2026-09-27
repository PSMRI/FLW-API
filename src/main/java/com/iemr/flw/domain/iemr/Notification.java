package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

/**
 * Persisted record of a notification that was sent (or attempted) to a user.
 * Backs the "notification list with unread count" feature.
 */
@Data
@Entity
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "app_type")
    private String appType;

    @Column(name = "sender_user_id")
    private Integer senderUserId;

    @Column(name = "receiver_user_id", nullable = false)
    private Integer receiverUserId;

    @Column(name = "title")
    private String title;

    @Column(name = "body")
    private String body;

    @Column(name = "notification_type")
    private String notificationType;

    @Column(name = "nav_id")
    private String navId;

    @Column(name = "redirect")
    private String redirect;

    @Column(name = "priority")
    private String priority;

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date", nullable = false, updatable = false)
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "read_date")
    private Date readDate;

    @PrePersist
    protected void onCreate() {
        this.createdDate = new Date();
        if (!isRead) {
            this.readDate = null;
        }
    }
}