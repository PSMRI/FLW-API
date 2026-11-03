package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /** Paged list for a receiver, newest first. */
    Page<Notification> findByReceiverUserIdOrderByCreatedDateDesc(Integer receiverUserId, Pageable pageable);

    /** Cheap count for badge display. */
    long countByReceiverUserIdAndIsReadFalse(Integer receiverUserId);

    /** Mark a single notification as read. */
    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true, n.readDate = :readDate " +
           "WHERE n.id = :id")
    int markAsRead(@Param("id") Long id, @Param("readDate") Date readDate);

    /** Mark every unread notification for a user as read. */
    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true, n.readDate = :readDate " +
           "WHERE n.receiverUserId = :receiverUserId AND n.isRead = false")
    int markAllAsRead(@Param("receiverUserId") Integer receiverUserId, @Param("readDate") Date readDate);
}