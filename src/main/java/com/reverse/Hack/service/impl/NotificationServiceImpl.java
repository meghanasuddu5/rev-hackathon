package com.reverse.Hack.service.impl;

import com.reverse.Hack.enums.NotificationType;
import com.reverse.Hack.model.Notification;
import com.reverse.Hack.model.User;
import com.reverse.Hack.repository.NotificationRepository;
import com.reverse.Hack.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void send(User recipient, User actor, NotificationType type, String message, String link) {
        if (actor != null && recipient.getId().equals(actor.getId())) return;
        notificationRepository.save(Notification.builder()
                .recipient(recipient).actor(actor).type(type).message(message).link(link).build());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Notification> getNotifications(User user, Pageable pageable) {
        return notificationRepository.findByRecipientOrderByCreatedAtDesc(user, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(User user) {
        return notificationRepository.countByRecipientAndReadFalse(user);
    }

    @Override
    public void markAllRead(User user) {
        notificationRepository.markAllReadForUser(user);
    }
}
