package com.krishiYatra.krishiYatra.notification;

import com.krishiYatra.krishiYatra.common.response.ServerResponse;
import com.krishiYatra.krishiYatra.notification.dto.FcmTokenRequest;
import com.krishiYatra.krishiYatra.notification.dto.PushTestRequest;
import com.krishiYatra.krishiYatra.user.UserEntity;
import com.krishiYatra.krishiYatra.utils.UserUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notification")
@RequiredArgsConstructor
@Tag(name = "Notification Controller", description = "Endpoints for managing push notifications and FCM tokens")
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "Register or update FCM token for the currently authenticated user")
    @PostMapping("/fcm-token")
    public ResponseEntity<ServerResponse> saveFcmToken(@RequestBody FcmTokenRequest request) {
        UserEntity user = UserUtil.getCurrentUser();
        if (user == null) {
            ServerResponse response = ServerResponse.failureResponse("Unauthorized", HttpStatus.UNAUTHORIZED);
            return new ResponseEntity<>(response, response.getHttpStatus());
        }

        if (request.getFcmToken() == null || request.getFcmToken().trim().isEmpty()) {
            ServerResponse response = ServerResponse.failureResponse("fcmToken is required", HttpStatus.BAD_REQUEST);
            return new ResponseEntity<>(response, response.getHttpStatus());
        }

        notificationService.saveFcmToken(user.getUsername(), request.getFcmToken(), request.getDeviceName());
        
        ServerResponse response = ServerResponse.successResponse("Token saved successfully", HttpStatus.OK);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Operation(summary = "Send a test push notification to yourself for debugging")
    @PostMapping("/test-push")
    public ResponseEntity<ServerResponse> testPush(@RequestBody PushTestRequest request) {
        UserEntity user = UserUtil.getCurrentUser();
        if (user == null) {
            ServerResponse response = ServerResponse.failureResponse("Unauthorized", HttpStatus.UNAUTHORIZED);
            return new ResponseEntity<>(response, response.getHttpStatus());
        }

        notificationService.sendToUser(user.getUsername(), request.getTitle(), request.getBody());
        
        ServerResponse response = ServerResponse.successResponse("Test push sent successfully", HttpStatus.OK);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Operation(summary = "Get paginated notifications for current user")
    @GetMapping("/list")
    public ResponseEntity<ServerResponse> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        UserEntity user = UserUtil.checkCurrentUser();
        if (user == null) {
            ServerResponse response = ServerResponse.failureResponse("Unauthorized", HttpStatus.UNAUTHORIZED);
            return new ResponseEntity<>(response, response.getHttpStatus());
        }
        ServerResponse response = notificationService.getUserNotifications(user.getUsername(), page, size);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Operation(summary = "Get unread notifications count")
    @GetMapping("/unread-count")
    public ResponseEntity<ServerResponse> getUnreadCount() {
        UserEntity user = UserUtil.checkCurrentUser();
        if (user == null) {
            ServerResponse response = ServerResponse.failureResponse("Unauthorized", HttpStatus.UNAUTHORIZED);
            return new ResponseEntity<>(response, response.getHttpStatus());
        }
        long count = notificationService.getUnreadCount(user.getUsername());
        ServerResponse response = ServerResponse.successObjectResponse("Unread count fetched", HttpStatus.OK, count, 1);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Operation(summary = "Mark all notifications as read for current user")
    @PostMapping("/read-all")
    public ResponseEntity<ServerResponse> markAllAsRead() {
        UserEntity user = UserUtil.checkCurrentUser();
        if (user == null) {
            ServerResponse response = ServerResponse.failureResponse("Unauthorized", HttpStatus.UNAUTHORIZED);
            return new ResponseEntity<>(response, response.getHttpStatus());
        }
        ServerResponse response = notificationService.markAllAsReadForUser(user.getUsername());
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Operation(summary = "Mark a notification as read")
    @PostMapping("/{id}/read")
    public ResponseEntity<ServerResponse> markAsRead(@PathVariable Long id) {
        ServerResponse response = notificationService.markAsRead(id);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Operation(summary = "Delete a notification permanently (soft delete)")
    @DeleteMapping("/{id}")
    public ResponseEntity<ServerResponse> deleteNotification(@PathVariable Long id) {
        ServerResponse response = notificationService.deleteNotification(id);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }
}
