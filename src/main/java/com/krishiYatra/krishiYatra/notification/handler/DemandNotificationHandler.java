package com.krishiYatra.krishiYatra.notification.handler;

import com.krishiYatra.krishiYatra.common.enums.NotificationCategory;
import com.krishiYatra.krishiYatra.common.enums.NotificationType;
import com.krishiYatra.krishiYatra.common.enums.VerificationStatus;
import com.krishiYatra.krishiYatra.demand.DemandEntity;
import com.krishiYatra.krishiYatra.farmer.FarmerEntity;
import com.krishiYatra.krishiYatra.farmer.FarmerRepo;
import com.krishiYatra.krishiYatra.notification.NotificationConst;
import com.krishiYatra.krishiYatra.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DemandNotificationHandler {

    private final NotificationService notificationService;
    private final FarmerRepo farmerRepo;

    public void handleDemandCreated(DemandEntity demand) {
        try {
            List<FarmerEntity> verifiedFarmers = farmerRepo.findByStatus(VerificationStatus.VERIFIED);
            String title = NotificationConst.DEMAND_CREATED_TITLE;
            String body = String.format(NotificationConst.DEMAND_CREATED_BODY, 
                demand.getSubCategory().getSubCategoryName(), demand.getQuantity());
            
            for (FarmerEntity farmer : verifiedFarmers) {
                notificationService.sendToUser(farmer.getUser().getUsername(), title, body, 
                    NotificationType.PUSH, NotificationCategory.DEMAND_CREATED, NotificationConst.DEMAND_MARKET_URL);
            }
        } catch (Exception e) {
            log.error("Failed to send demand creation notifications: {}", e.getMessage());
        }
    }

    public void handleDemandAccepted(DemandEntity demand, FarmerEntity farmer) {
        try {
            // Notify Buyer
            String titleBuyer = NotificationConst.DEMAND_ACCEPTED_BUYER_TITLE;
            String bodyBuyer = String.format(NotificationConst.DEMAND_ACCEPTED_BUYER_BODY, 
                farmer.getUser().getFullName(), demand.getSubCategory().getSubCategoryName());
            
            notificationService.sendToUser(demand.getBuyer().getUser().getUsername(), titleBuyer, bodyBuyer, 
                NotificationType.PUSH, NotificationCategory.DEMAND_ACCEPTED, NotificationConst.BUYER_DEMANDS_URL);

            // Notify accepting Farmer (Confirmation)
            String titleFarmer = NotificationConst.DEMAND_ACCEPTED_FARMER_TITLE;
            String bodyFarmer = String.format(NotificationConst.DEMAND_ACCEPTED_FARMER_BODY, demand.getSubCategory().getSubCategoryName());
            
            notificationService.sendToUser(farmer.getUser().getUsername(), titleFarmer, bodyFarmer, 
                NotificationType.PUSH, NotificationCategory.DEMAND_ACCEPTED, NotificationConst.FARMER_STOCKS_URL);
        } catch (Exception e) {
            log.error("Failed to send demand acceptance notifications: {}", e.getMessage());
        }
    }
}
