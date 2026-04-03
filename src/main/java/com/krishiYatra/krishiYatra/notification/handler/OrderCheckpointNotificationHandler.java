package com.krishiYatra.krishiYatra.notification.handler;

import com.krishiYatra.krishiYatra.common.enums.NotificationCategory;
import com.krishiYatra.krishiYatra.common.enums.NotificationType;
import com.krishiYatra.krishiYatra.notification.NotificationConst;
import com.krishiYatra.krishiYatra.notification.NotificationService;
import com.krishiYatra.krishiYatra.notification.dto.OrderNotificationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderCheckpointNotificationHandler {

    private final NotificationService notificationService;

    public void handle(OrderNotificationDto dto) {
        String orderId = dto.getOrderId();
        String productName = dto.getProductName();
        String checkpoint = dto.getCheckpointName();

        if (checkpoint == null) return;

        String title = NotificationConst.ORDER_CHECKPOINT_TITLE;
        String body = String.format(NotificationConst.ORDER_CHECKPOINT_BODY, checkpoint, productName, orderId);

        // Notify both Buyer and Farmer
        if (dto.getBuyerUsername() != null) {
            notificationService.sendToUser(dto.getBuyerUsername(), title, body, 
                NotificationType.PUSH, NotificationCategory.ORDER_CHECKPOINT, String.format(NotificationConst.TRACK_URL_BUYER, orderId));
        }
        
        if (dto.getFarmerUsername() != null) {
            notificationService.sendToUser(dto.getFarmerUsername(), title, body, 
                NotificationType.PUSH, NotificationCategory.ORDER_CHECKPOINT, String.format(NotificationConst.TRACK_URL_FARMER, orderId));
        }
    }
}
