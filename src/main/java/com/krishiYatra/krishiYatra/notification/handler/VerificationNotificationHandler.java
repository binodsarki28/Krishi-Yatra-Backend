package com.krishiYatra.krishiYatra.notification.handler;

import com.krishiYatra.krishiYatra.common.enums.NotificationCategory;
import com.krishiYatra.krishiYatra.common.enums.NotificationType;
import com.krishiYatra.krishiYatra.notification.NotificationConst;
import com.krishiYatra.krishiYatra.notification.NotificationService;
import com.krishiYatra.krishiYatra.user.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class VerificationNotificationHandler {

    private final NotificationService notificationService;

    public void notifyFarmerStatus(UserEntity user, boolean approved, String reason) {
        String role = "Farmer";
        String title = approved ? NotificationConst.VERIFICATION_APPROVED_TITLE : NotificationConst.VERIFICATION_REJECTED_TITLE;
        String body = approved ? 
            String.format(NotificationConst.VERIFICATION_APPROVED_BODY, role) : 
            String.format(NotificationConst.VERIFICATION_REJECTED_BODY, role, reason);
        
        String actionUrl = approved ? NotificationConst.URL_FARMER_DASHBOARD : NotificationConst.URL_FARMER_REGISTER;
        NotificationCategory category = approved ? NotificationCategory.VERIFICATION_SUCCESS : NotificationCategory.VERIFICATION_FAILURE;
        
        notificationService.sendToUser(user.getUsername(), title, body, NotificationType.PUSH, category, actionUrl);
    }

    public void notifyBuyerStatus(UserEntity user, boolean approved, String reason) {
        String role = "Buyer";
        String title = approved ? NotificationConst.VERIFICATION_APPROVED_TITLE : NotificationConst.VERIFICATION_REJECTED_TITLE;
        String body = approved ? 
            String.format(NotificationConst.VERIFICATION_APPROVED_BODY, role) : 
            String.format(NotificationConst.VERIFICATION_REJECTED_BODY, role, reason);
        
        String actionUrl = approved ? NotificationConst.URL_BUYER_DASHBOARD : NotificationConst.URL_BUYER_REGISTER;
        NotificationCategory category = approved ? NotificationCategory.VERIFICATION_SUCCESS : NotificationCategory.VERIFICATION_FAILURE;
        
        notificationService.sendToUser(user.getUsername(), title, body, NotificationType.PUSH, category, actionUrl);
    }

    public void notifyDeliveryStatus(UserEntity user, boolean approved, String reason) {
        String role = "Delivery Rider";
        String title = approved ? NotificationConst.VERIFICATION_APPROVED_TITLE : NotificationConst.VERIFICATION_REJECTED_TITLE;
        String body = approved ? 
            String.format(NotificationConst.VERIFICATION_APPROVED_BODY, role) : 
            String.format(NotificationConst.VERIFICATION_REJECTED_BODY, role, reason);
        
        String actionUrl = approved ? NotificationConst.URL_DELIVERY_DASHBOARD : NotificationConst.URL_DELIVERY_REGISTER;
        NotificationCategory category = approved ? NotificationCategory.VERIFICATION_SUCCESS : NotificationCategory.VERIFICATION_FAILURE;
        
        notificationService.sendToUser(user.getUsername(), title, body, NotificationType.PUSH, category, actionUrl);
    }
}
