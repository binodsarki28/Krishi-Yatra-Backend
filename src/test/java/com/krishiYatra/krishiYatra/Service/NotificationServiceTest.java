package com.krishiYatra.krishiYatra.Service;

import com.krishiYatra.krishiYatra.common.enums.NotificationCategory;
import com.krishiYatra.krishiYatra.common.enums.NotificationType;
import com.krishiYatra.krishiYatra.common.enums.VerificationStatus;
import com.krishiYatra.krishiYatra.common.response.ServerResponse;
import com.krishiYatra.krishiYatra.delivery.DeliveryRepo;
import com.krishiYatra.krishiYatra.notification.FirebaseEntity;
import com.krishiYatra.krishiYatra.notification.FirebaseRepo;
import com.krishiYatra.krishiYatra.notification.NotificationConst;
import com.krishiYatra.krishiYatra.notification.NotificationEntity;
import com.krishiYatra.krishiYatra.notification.NotificationRepo;
import com.krishiYatra.krishiYatra.notification.NotificationService;
import com.krishiYatra.krishiYatra.user.UserEntity;
import com.krishiYatra.krishiYatra.user.UserRepo;
import com.krishiYatra.krishiYatra.utils.UserUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock private FirebaseRepo firebaseRepo;
    @Mock private UserRepo userRepo;
    @Mock private NotificationRepo notificationRepo;
    @Mock private DeliveryRepo deliveryRepo;

    @InjectMocks
    private NotificationService notificationService;

    private UserEntity mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new UserEntity();
        mockUser.setUserId(UUID.randomUUID().toString());
        mockUser.setUsername("testuser");
        mockUser.setEmail("testuser@gmail.com");
    }

    @Test
    void saveFcmToken_NewToken_Success() {
        try (MockedStatic<UserUtil> utilities = mockStatic(UserUtil.class)) {
            utilities.when(UserUtil::getCurrentUser).thenReturn(mockUser);

            when(firebaseRepo.findByFcmToken(anyString())).thenReturn(Optional.empty());

            ServerResponse response = notificationService.saveFcmToken("testuser", "newfcmtoken", "device1");

            assertEquals(HttpStatus.OK, response.getHttpStatus());
            assertEquals(NotificationConst.TOKEN_SAVED, response.getMessage());
            verify(firebaseRepo).save(any(FirebaseEntity.class));
        }
    }

    @Test
    void saveFcmToken_ExistingToken_Success() {
        try (MockedStatic<UserUtil> utilities = mockStatic(UserUtil.class)) {
            utilities.when(UserUtil::getCurrentUser).thenReturn(mockUser);

            FirebaseEntity existToken = new FirebaseEntity();
            when(firebaseRepo.findByFcmToken(anyString())).thenReturn(Optional.of(existToken));

            ServerResponse response = notificationService.saveFcmToken("testuser", "existingtoken", "device1");

            assertEquals(HttpStatus.OK, response.getHttpStatus());
            assertEquals(NotificationConst.TOKEN_SAVED, response.getMessage());
            verify(firebaseRepo).save(any(FirebaseEntity.class));
        }
    }

    @Test
    void saveFcmToken_UserNotAuthenticated_Failure() {
        try (MockedStatic<UserUtil> utilities = mockStatic(UserUtil.class)) {
            utilities.when(UserUtil::getCurrentUser).thenReturn(null);

            ServerResponse response = notificationService.saveFcmToken("testuser", "token", "device");

            assertEquals(HttpStatus.NOT_FOUND, response.getHttpStatus());
            assertEquals(NotificationConst.USER_NOT_FOUND, response.getMessage());
        }
    }

    @Test
    void sendToUser_Success() {
        when(userRepo.findByUsername(anyString())).thenReturn(Optional.of(mockUser));
        when(firebaseRepo.findAllByUser(any())).thenReturn(Collections.emptyList());

        ServerResponse response = notificationService.sendToUser("testuser", "Title", "Body", NotificationType.PUSH, NotificationCategory.SYSTEM, null);

        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertEquals(NotificationConst.NOTIFICATION_SENT, response.getMessage());
        verify(notificationRepo).save(any(NotificationEntity.class));
    }

    @Test
    void sendToUser_UserNotFound_Failure() {
        when(userRepo.findByUsername(anyString())).thenReturn(Optional.empty());

        ServerResponse response = notificationService.sendToUser("fakeuser", "Title", "Body", NotificationType.PUSH, NotificationCategory.SYSTEM, null);

        assertEquals(HttpStatus.NOT_FOUND, response.getHttpStatus());
        assertEquals(NotificationConst.USER_NOT_FOUND, response.getMessage());
    }

    @Test
    void getUserNotifications_Success() {
        try (MockedStatic<UserUtil> utilities = mockStatic(UserUtil.class)) {
            utilities.when(UserUtil::getCurrentUser).thenReturn(mockUser);

            NotificationEntity n = new NotificationEntity();
            n.setId(1L);
            n.setTitle("Title");
            n.setBody("Body");

            when(notificationRepo.findAllByUserAndDeletedFalse(eq(mockUser), any()))
                    .thenReturn(new PageImpl<>(Collections.singletonList(n)));

            ServerResponse response = notificationService.getUserNotifications("testuser", 0, 10);

            assertEquals(HttpStatus.OK, response.getHttpStatus());
            assertEquals(NotificationConst.NOTIFICATIONS_FETCHED, response.getMessage());
            assertNotNull(response.getResponse());
            assertEquals(1, response.getTotalItems());
        }
    }

    @Test
    void getUserNotifications_Unauthenticated_Failure() {
        try (MockedStatic<UserUtil> utilities = mockStatic(UserUtil.class)) {
            utilities.when(UserUtil::getCurrentUser).thenReturn(null);

            ServerResponse response = notificationService.getUserNotifications("testuser", 0, 10);

            assertEquals(HttpStatus.NOT_FOUND, response.getHttpStatus());
            assertEquals(NotificationConst.USER_NOT_FOUND, response.getMessage());
        }
    }

    @Test
    void markAsRead_Success() {
        NotificationEntity notification = new NotificationEntity();
        notification.setRead(false);

        when(notificationRepo.findById(1L)).thenReturn(Optional.of(notification));

        ServerResponse response = notificationService.markAsRead(1L);

        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertEquals(NotificationConst.NOTIFICATION_MARKED_READ, response.getMessage());
        assertTrue(notification.isRead());
        verify(notificationRepo).save(notification);
    }

    @Test
    void markAllAsReadForUser_Success() {
        try (MockedStatic<UserUtil> utilities = mockStatic(UserUtil.class)) {
            utilities.when(UserUtil::getCurrentUser).thenReturn(mockUser);

            ServerResponse response = notificationService.markAllAsReadForUser("testuser");

            assertEquals(HttpStatus.OK, response.getHttpStatus());
            assertEquals(NotificationConst.ALL_MARKED_READ, response.getMessage());
            verify(notificationRepo).markAllAsRead(mockUser);
        }
    }

    @Test
    void deleteNotification_Success() {
        NotificationEntity notification = new NotificationEntity();
        notification.setDeleted(false);

        when(notificationRepo.findById(1L)).thenReturn(Optional.of(notification));

        ServerResponse response = notificationService.deleteNotification(1L);

        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertEquals(NotificationConst.NOTIFICATION_DELETED, response.getMessage());
        assertTrue(notification.isDeleted());
        verify(notificationRepo).save(notification);
    }

    @Test
    void getUnreadCount_Success() {
        try (MockedStatic<UserUtil> utilities = mockStatic(UserUtil.class)) {
            utilities.when(UserUtil::getCurrentUser).thenReturn(mockUser);

            when(notificationRepo.countByUserAndReadFalseAndDeletedFalse(mockUser)).thenReturn(5L);

            long count = notificationService.getUnreadCount("testuser");

            assertEquals(5L, count);
        }
    }

    @Test
    void getUnreadCount_Unauthenticated_Success() {
        try (MockedStatic<UserUtil> utilities = mockStatic(UserUtil.class)) {
            utilities.when(UserUtil::getCurrentUser).thenReturn(null);

            long count = notificationService.getUnreadCount("testuser");

            assertEquals(0L, count);
        }
    }

    @Test
    void findVerifiedDeliveryUsernames_Success() {
        when(deliveryRepo.findUsernamesByStatus(VerificationStatus.VERIFIED))
                .thenReturn(List.of("rider1", "rider2"));

        List<String> usernames = notificationService.findVerifiedDeliveryUsernames();

        assertEquals(2, usernames.size());
        assertTrue(usernames.contains("rider1"));
    }
}
