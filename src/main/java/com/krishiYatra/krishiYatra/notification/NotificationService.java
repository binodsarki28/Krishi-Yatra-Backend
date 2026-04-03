package com.krishiYatra.krishiYatra.notification;

import com.krishiYatra.krishiYatra.common.enums.NotificationCategory;
import com.krishiYatra.krishiYatra.common.enums.NotificationType;
import com.krishiYatra.krishiYatra.common.response.ServerResponse;
import com.krishiYatra.krishiYatra.notification.dto.NotificationResponse;
import com.krishiYatra.krishiYatra.user.UserEntity;
import com.krishiYatra.krishiYatra.user.UserRepo;
import com.krishiYatra.krishiYatra.verification.EmailService;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.krishiYatra.krishiYatra.utils.UserUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final FirebaseRepo firebaseRepo;
    private final UserRepo userRepo;
    private final EmailService emailService;
    private final NotificationRepo notificationRepo;

    @Transactional
    public ServerResponse saveFcmToken(String username, String fcmToken, String deviceName) {
        UserEntity user = UserUtil.getCurrentUser();
        if (user == null) {
            return ServerResponse.failureResponse(NotificationConst.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        try {
            Optional<FirebaseEntity> existingToken = firebaseRepo.findByFcmToken(fcmToken);
            
            if (existingToken.isPresent()) {
                FirebaseEntity tokenEntity = existingToken.get();
                tokenEntity.setUser(user);
                tokenEntity.setDeviceName(deviceName);
                firebaseRepo.save(tokenEntity);
            } else {
                FirebaseEntity newToken = new FirebaseEntity();
                newToken.setUser(user);
                newToken.setFcmToken(fcmToken);
                newToken.setDeviceName(deviceName);
                firebaseRepo.save(newToken);
            }
            return ServerResponse.successResponse(NotificationConst.TOKEN_SAVED, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error saving FCM token: {}", e.getMessage());
            return ServerResponse.failureResponse(NotificationConst.TOKEN_SAVE_FAILED, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public String sendPushNotification(String title, String body, String fcmToken) {
        Message message = Message.builder()
                .setToken(fcmToken)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .setWebpushConfig(WebpushConfig.builder()
                        .setFcmOptions(WebpushFcmOptions.builder()
                                .setLink(null) // Can add a default link here
                                .build())
                        .build())
                .setAndroidConfig(AndroidConfig.builder()
                        .setPriority(AndroidConfig.Priority.HIGH)
                        .build())
                .setApnsConfig(ApnsConfig.builder()
                        .setAps(Aps.builder()
                                .setContentAvailable(true)
                                .build())
                        .build())
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("Successfully sent message: {}", response);
            return response;
        } catch (FirebaseMessagingException e) {
            log.error("Error sending push notification: {}", e.getMessage());
            return null;
        }
    }

    public ServerResponse sendToUser(String username, String title, String body, NotificationType type, NotificationCategory category) {
        UserEntity user = userRepo.findByUsername(username).orElse(null);
        if (user == null) {
            return ServerResponse.failureResponse(NotificationConst.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        // 1. Save to Database for In-App Notifications
        NotificationEntity notification = NotificationEntity.builder()
                .user(user)
                .title(title)
                .body(body)
                .category(category)
                .read(false)
                .build();
        notificationRepo.save(notification);

        // 2. Handle Email Notification
        if (type == NotificationType.EMAIL || type == NotificationType.BOTH) {
            try {
                emailService.sendNotificationEmail(user.getEmail(), title, body);
            } catch (Exception e) {
                log.error("Failed to send email notification to {}: {}", user.getEmail(), e.getMessage());
            }
        }

        // 3. Handle Push Notification
        if (type == NotificationType.PUSH || type == NotificationType.BOTH) {
            List<FirebaseEntity> tokens = firebaseRepo.findAllByUser(user);
            if (!tokens.isEmpty()) {
                for (FirebaseEntity token : tokens) {
                    sendPushNotification(title, body, token.getFcmToken());
                }
            }
        }

        return ServerResponse.successResponse(NotificationConst.NOTIFICATION_SENT, HttpStatus.OK);
    }

    public ServerResponse sendToUser(String username, String title, String body, NotificationType type) {
        return sendToUser(username, title, body, type, NotificationCategory.GENERAL);
    }

    public ServerResponse sendToUser(String username, String title, String body) {
        return sendToUser(username, title, body, NotificationType.PUSH, NotificationCategory.GENERAL);
    }

    public ServerResponse getUserNotifications(String username, int page, int size) {
        UserEntity user = UserUtil.getCurrentUser();
        if (user == null) {
            return ServerResponse.failureResponse(NotificationConst.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        Page<NotificationEntity> notificationPage = notificationRepo.findAllByUserAndDeletedFalse(
                user, 
                PageRequest.of(page, size, Sort.by("createdAt").descending())
        );

        List<NotificationResponse> notifications = notificationPage.getContent()
                .stream()
                .map(n -> NotificationResponse.builder()
                        .id(n.getId())
                        .title(n.getTitle())
                        .body(n.getBody())
                        .category(n.getCategory())
                        .read(n.isRead())
                        .createdAt(n.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return ServerResponse.successObjectResponse(
                NotificationConst.NOTIFICATIONS_FETCHED, 
                HttpStatus.OK, 
                notifications, 
                (int) notificationPage.getTotalElements()
        );
    }

    public void sendToUsers(List<String> usernames, String title, String body, NotificationType type, NotificationCategory category) {
        for (String uname : usernames) {
            sendToUser(uname, title, body, type, category);
        }
    }

    @Transactional
    public ServerResponse markAsRead(Long id) {
        NotificationEntity notification = notificationRepo.findById(id).orElse(null);
        if (notification == null) {
            return ServerResponse.failureResponse("Notification not found", HttpStatus.NOT_FOUND);
        }
        notification.setRead(true);
        notificationRepo.save(notification);
        return ServerResponse.successResponse(NotificationConst.NOTIFICATION_MARKED_READ, HttpStatus.OK);
    }

    @Transactional
    public ServerResponse markAllAsReadForUser(String username) {
        UserEntity user = UserUtil.getCurrentUser();
        if (user == null) {
            return ServerResponse.failureResponse(NotificationConst.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        notificationRepo.markAllAsRead(user);
        return ServerResponse.successResponse("All notifications marked as read", HttpStatus.OK);
    }

    @Transactional
    public ServerResponse deleteNotification(Long id) {
        NotificationEntity notification = notificationRepo.findById(id).orElse(null);
        if (notification == null) {
            return ServerResponse.failureResponse("Notification not found", HttpStatus.NOT_FOUND);
        }
        notification.setDeleted(true);
        notificationRepo.save(notification);
        return ServerResponse.successResponse("Notification deleted permanently", HttpStatus.OK);
    }

    public long getUnreadCount(String username) {
        UserEntity user = UserUtil.getCurrentUser();
        if (user == null) return 0;
        return notificationRepo.countByUserAndReadFalseAndDeletedFalse(user);
    }
}
