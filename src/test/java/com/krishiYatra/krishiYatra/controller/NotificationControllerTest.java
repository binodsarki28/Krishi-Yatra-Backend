package com.krishiYatra.krishiYatra.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.krishiYatra.krishiYatra.common.response.ServerResponse;
import com.krishiYatra.krishiYatra.config.security.jwt.JwtAuthenticationFilter;
import com.krishiYatra.krishiYatra.config.security.jwt.JwtTokenProvider;
import com.krishiYatra.krishiYatra.config.security.service.CustomUserDetailsService;
import com.krishiYatra.krishiYatra.notification.NotificationController;
import com.krishiYatra.krishiYatra.notification.NotificationService;
import com.krishiYatra.krishiYatra.notification.dto.FcmTokenRequest;
import com.krishiYatra.krishiYatra.notification.dto.PushTestRequest;
import com.krishiYatra.krishiYatra.user.UserEntity;
import com.krishiYatra.krishiYatra.utils.UserUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
@AutoConfigureMockMvc(addFilters = false)
public class NotificationControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private NotificationService notificationService;
    @MockitoBean private JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockitoBean private JwtTokenProvider jwtTokenProvider;
    @MockitoBean private CustomUserDetailsService customUserDetailsService;

    private UserEntity mockUser;
    MockedStatic<UserUtil> mockedSettings;

    @BeforeEach
    void setUp() {
        mockUser = new UserEntity();
        mockUser.setUserId(UUID.randomUUID().toString());
        mockUser.setUsername("testuser");

        mockedSettings = mockStatic(UserUtil.class);
        mockedSettings.when(UserUtil::getCurrentUser).thenReturn(mockUser);
        mockedSettings.when(UserUtil::checkCurrentUser).thenReturn(mockUser);
    }

    @AfterEach
    void tearDown() {
        mockedSettings.close();
    }

    @Test
    @DisplayName("Endpoint: Save FCM Token Success")
    void saveFcmToken_ReturnOk() throws Exception {
        FcmTokenRequest request = new FcmTokenRequest();
        request.setFcmToken("sample-token");
        request.setDeviceName("device1");

        when(notificationService.saveFcmToken(anyString(), anyString(), anyString()))
                .thenReturn(ServerResponse.successResponse("Token saved successfully", HttpStatus.OK));

        mockMvc.perform(post("/api/v1/notification/fcm-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Token saved successfully"));
    }

    @Test
    @DisplayName("Endpoint: Save FCM Token Failure - Empty Token")
    void saveFcmToken_EmptyToken_ReturnBadRequest() throws Exception {
        FcmTokenRequest request = new FcmTokenRequest();
        request.setFcmToken(""); // Empty token

        mockMvc.perform(post("/api/v1/notification/fcm-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("fcmToken is required"));
    }

    @Test
    @DisplayName("Endpoint: Test Push Notification Success")
    void testPush_ReturnOk() throws Exception {
        PushTestRequest request = new PushTestRequest();
        request.setTitle("Title");
        request.setBody("Body");

        when(notificationService.sendToUser(anyString(), anyString(), anyString()))
                .thenReturn(ServerResponse.successResponse("Test push sent successfully", HttpStatus.OK));

        mockMvc.perform(post("/api/v1/notification/test-push")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Test push sent successfully"));
    }

    @Test
    @DisplayName("Endpoint: Get Notifications Success")
    void getNotifications_ReturnOk() throws Exception {
        when(notificationService.getUserNotifications(anyString(), anyInt(), anyInt()))
                .thenReturn(ServerResponse.successResponse("Notifications fetched successfully", HttpStatus.OK));

        mockMvc.perform(get("/api/v1/notification/list")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Notifications fetched successfully"));
    }

    @Test
    @DisplayName("Endpoint: Get Unread Count Success")
    void getUnreadCount_ReturnOk() throws Exception {
        when(notificationService.getUnreadCount(anyString())).thenReturn(5L);

        mockMvc.perform(get("/api/v1/notification/unread-count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Unread count fetched"))
                .andExpect(jsonPath("$.response").value(5));
    }

    @Test
    @DisplayName("Endpoint: Mark All As Read Success")
    void markAllAsRead_ReturnOk() throws Exception {
        when(notificationService.markAllAsReadForUser(anyString()))
                .thenReturn(ServerResponse.successResponse("All notifications marked as read", HttpStatus.OK));

        mockMvc.perform(post("/api/v1/notification/read-all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("All notifications marked as read"));
    }

    @Test
    @DisplayName("Endpoint: Mark As Read Success")
    void markAsRead_ReturnOk() throws Exception {
        when(notificationService.markAsRead(anyLong()))
                .thenReturn(ServerResponse.successResponse("Notification marked as read", HttpStatus.OK));

        mockMvc.perform(post("/api/v1/notification/1/read"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Notification marked as read"));
    }

    @Test
    @DisplayName("Endpoint: Delete Notification Success")
    void deleteNotification_ReturnOk() throws Exception {
        when(notificationService.deleteNotification(anyLong()))
                .thenReturn(ServerResponse.successResponse("Notification deleted successfully", HttpStatus.OK));

        mockMvc.perform(delete("/api/v1/notification/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Notification deleted successfully"));
    }
}
