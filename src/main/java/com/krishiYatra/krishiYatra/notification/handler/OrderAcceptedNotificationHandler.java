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
public class OrderAcceptedNotificationHandler {

    private final NotificationService notificationService;

    public void handle(OrderNotificationDto dto) {
        String orderId = dto.getOrderId();
        String productName = dto.getProductName();

        // Notify the Buyer
        if (dto.getBuyerUsername() != null) {
            String buyerTitle = NotificationConst.ORDER_ACCEPTED_BUYER_TITLE;
            String buyerBody = String.format(NotificationConst.ORDER_ACCEPTED_BUYER_BODY, productName, orderId);
            String url = String.format(NotificationConst.TRACK_URL_BUYER, orderId);
            notificationService.sendToUser(dto.getBuyerUsername(), buyerTitle, buyerBody, 
                NotificationType.PUSH, NotificationCategory.ORDER_ACCEPTED, url);
        }
        
        //Notify the Farmer
        if (dto.getFarmerUsername() != null) {
            String farmerTitle = NotificationConst.ORDER_ACCEPTED_FARMER_TITLE;
            String farmerBody = String.format(NotificationConst.ORDER_ACCEPTED_FARMER_BODY, productName, orderId);
            String url = String.format(NotificationConst.TRACK_URL_FARMER, orderId);
            notificationService.sendToUser(dto.getFarmerUsername(), farmerTitle, farmerBody, 
                NotificationType.PUSH, NotificationCategory.ORDER_ACCEPTED, url);
        }
    }
}
