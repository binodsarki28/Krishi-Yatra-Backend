package com.krishiYatra.krishiYatra.notification.handler;

import com.krishiYatra.krishiYatra.common.enums.NotificationCategory;
import com.krishiYatra.krishiYatra.common.enums.NotificationType;
import com.krishiYatra.krishiYatra.notification.NotificationConst;
import com.krishiYatra.krishiYatra.notification.NotificationService;
import com.krishiYatra.krishiYatra.notification.dto.OrderNotificationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderCreatedNotificationHandler {

    private final NotificationService notificationService;

    public void handle(OrderNotificationDto dto) {
        String orderId = dto.getOrderId();
        String productName = dto.getProductName();

        // 1. Notify the Buyer
        if (dto.getBuyerUsername() != null) {
            String buyerTitle = NotificationConst.ORDER_CREATED_BUYER_TITLE;
            String buyerBody = String.format(NotificationConst.ORDER_CREATED_BUYER_BODY, productName, orderId);
            notificationService.sendToUser(dto.getBuyerUsername(), buyerTitle, buyerBody, 
                NotificationType.BOTH, NotificationCategory.ORDER_CREATED);
        }
        
        // 2. Notify the Farmer
        if (dto.getFarmerUsername() != null) {
            String farmerTitle = NotificationConst.ORDER_CREATED_FARMER_TITLE;
            String farmerBody = String.format(NotificationConst.ORDER_CREATED_FARMER_BODY, productName, orderId);
            
            notificationService.sendToUser(dto.getFarmerUsername(), farmerTitle, farmerBody, 
                NotificationType.BOTH, NotificationCategory.ORDER_CREATED);
        }

        // 3. Notify all VERIFIED Delivery Riders (Exclude the Buyer and the Farmer themselves)
        List<String> deliveryUsernames = notificationService.findVerifiedDeliveryUsernames();
        
        // Filter out the Farmer and Buyer if they are also Riders
        deliveryUsernames = deliveryUsernames.stream()
            .filter(uname -> !uname.equals(dto.getFarmerUsername()) && !uname.equals(dto.getBuyerUsername()))
            .collect(java.util.stream.Collectors.toList());

        if (!deliveryUsernames.isEmpty()) {
            String deliveryTitle = NotificationConst.ORDER_CREATED_DELIVERY_TITLE;
            String deliveryBody = String.format(NotificationConst.ORDER_CREATED_DELIVERY_BODY, productName, orderId);
            
            notificationService.sendToUsers(deliveryUsernames, deliveryTitle, deliveryBody, 
                NotificationType.PUSH, NotificationCategory.ORDER_CREATED);
        }
    }
}
