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
public class OrderDeliveredNotificationHandler {
    private final NotificationService notificationService;

    public void handle(OrderNotificationDto dto) {
        String title = NotificationConst.ORDER_DELIVERED_TITLE;
        String body = String.format(NotificationConst.ORDER_DELIVERED_BODY, dto.getProductName(), dto.getOrderId());

        String orderId = dto.getOrderId();

        // Notify Buyer, Farmer, and Delivery Rider (if assigned)
        if (dto.getBuyerUsername() != null) {
            notificationService.sendToUser(dto.getBuyerUsername(), title, body, NotificationType.PUSH, NotificationCategory.ORDER_DELIVERED, String.format(NotificationConst.TRACK_URL_BUYER, orderId));
        }
        if (dto.getFarmerUsername() != null) {
            notificationService.sendToUser(dto.getFarmerUsername(), title, body, NotificationType.PUSH, NotificationCategory.ORDER_DELIVERED, String.format(NotificationConst.TRACK_URL_FARMER, orderId));
        }
        if (dto.getDeliveryUsername() != null) {
            notificationService.sendToUser(dto.getDeliveryUsername(), title, body, NotificationType.PUSH, NotificationCategory.ORDER_DELIVERED, String.format(NotificationConst.TRACK_URL_DELIVERY, orderId));
        }
    }
}
