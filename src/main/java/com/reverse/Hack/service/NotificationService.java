package com.reverse.Hack.service;

import com.reverse.Hack.enums.NotificationType;
import com.reverse.Hack.model.Notification;
import com.reverse.Hack.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {

    void send(User recipient, User actor, NotificationType type, String message, String link);

    Page<Notification> getNotifications(User user, Pageable pageable);

    long getUnreadCount(User user);

    void markAllRead(User user);
}
