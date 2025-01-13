package com.example.sellcourse.service;

import com.example.sellcourse.entities.Notification;
import com.example.sellcourse.entities.User;
import com.example.sellcourse.exception.AppException;
import com.example.sellcourse.exception.ErrorCode;
import com.example.sellcourse.repository.NotificationRepository;
import com.example.sellcourse.repository.user.UserRepository;
import com.example.sellcourse.utils.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class NotificationService {

    NotificationRepository notificationRepository;
    SimpMessagingTemplate simpMessagingTemplate;
    UserRepository userRepository;

    @PreAuthorize("isAuthenticated()")
    public List<Notification> getNotificationsForCurrentUser() {
        String email = SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_INVALID));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        List<Notification> notifications = notificationRepository.findByUserIdOrderByIdDesc(user.getId());

        if (notifications == null || notifications.isEmpty()) {
            return Collections.emptyList();
        }

        for(Notification notification : notifications) {
            simpMessagingTemplate.convertAndSendToUser(
                    user.getEmail(),
                    "/queue/notifications",
                    notification
            );

        }
        return notifications;
    }

    @Transactional
    public void createNotification(User receiver, User sender, String message, String title, String url){
        Notification notification = Notification.builder()
                .user(receiver)
                .message(message)
                .title(title)
                .url(url)
                .isRead(false)
                .avatarUrl(sender.getAvatar())
                .build();
        notificationRepository.save(notification);
        simpMessagingTemplate.convertAndSendToUser(receiver.getEmail(), "/queue/notifications", notification);
        log.info("Sending notification to user: {}", receiver.getEmail());

    }

    @Transactional
    public void deleteNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_EXISTED));

        notificationRepository.delete(notification);
    }

    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_EXISTED));

        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

}
