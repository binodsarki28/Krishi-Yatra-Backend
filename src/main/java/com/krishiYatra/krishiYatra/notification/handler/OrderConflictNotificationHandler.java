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
public class OrderConflictNotificationHandler {
    private final NotificationService notificationService;

    public void handle(OrderNotificationDto dto, String conflictMessage) {
        String orderId = dto.getOrderId();
        String productName = dto.getProductName();

        // 1. Notify the Buyer
        if (dto.getBuyerUsername() != null) {
            String buyerTitle = NotificationConst.ORDER_CONFLICT_BUYER_TITLE;
            String buyerBody = String.format(NotificationConst.ORDER_CONFLICT_BUYER_BODY, productName, orderId);
            String url = String.format(NotificationConst.TRACK_URL_BUYER, orderId);
            notificationService.sendToUser(dto.getBuyerUsername(), buyerTitle, buyerBody, 
                NotificationType.PUSH, NotificationCategory.ORDER_CONFLICT, url);
        }

        // 2. Notify the Farmer
        if (dto.getFarmerUsername() != null) {
            String farmerTitle = NotificationConst.ORDER_CONFLICT_FARMER_TITLE;
            String farmerBody = String.format(NotificationConst.ORDER_CONFLICT_FARMER_BODY, productName, orderId);
            String url = String.format(NotificationConst.TRACK_URL_FARMER, orderId);
            notificationService.sendToUser(dto.getFarmerUsername(), farmerTitle, farmerBody, 
                NotificationType.PUSH, NotificationCategory.ORDER_CONFLICT, url);
        }
        
        // 3. Notify the Delivery Rider (if assigned)
        if (dto.getDeliveryUsername() != null) {
            String riderTitle = NotificationConst.ORDER_CONFLICT_RIDER_TITLE;
            String riderBody = String.format(NotificationConst.ORDER_CONFLICT_RIDER_BODY, productName, orderId);
            String url = String.format(NotificationConst.TRACK_URL_DELIVERY, orderId);
            notificationService.sendToUser(dto.getDeliveryUsername(), riderTitle, riderBody, 
                NotificationType.PUSH, NotificationCategory.ORDER_CONFLICT, url);
        }
    }
}
