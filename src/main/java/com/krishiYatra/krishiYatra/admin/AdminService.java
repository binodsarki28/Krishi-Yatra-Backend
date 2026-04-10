package com.krishiYatra.krishiYatra.admin;

import com.krishiYatra.krishiYatra.admin.dto.AdminDashboardResponse;
import com.krishiYatra.krishiYatra.buyer.BuyerRepo;
import com.krishiYatra.krishiYatra.common.enums.OrderStatus;
import com.krishiYatra.krishiYatra.common.enums.VerificationStatus;
import com.krishiYatra.krishiYatra.common.response.ServerResponse;
import com.krishiYatra.krishiYatra.delivery.DeliveryRepo;
import com.krishiYatra.krishiYatra.farmer.FarmerRepo;
import com.krishiYatra.krishiYatra.order.OrderRepo;
import com.krishiYatra.krishiYatra.stock.StockRepo;
import com.krishiYatra.krishiYatra.user.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepo userRepo;
    private final FarmerRepo farmerRepo;
    private final BuyerRepo buyerRepo;
    private final DeliveryRepo deliveryRepo;
    private final OrderRepo orderRepo;
    private final StockRepo stockRepo;

    @Transactional(readOnly = true)
    public ServerResponse getAdminDashboard() {
        long totalUsers = userRepo.count();
        long totalFarmers = farmerRepo.count();
        long totalBuyers = buyerRepo.count();
        long totalDeliveryPartners = deliveryRepo.count();
        
        long pendingVerifications = farmerRepo.countByStatus(VerificationStatus.PENDING) +
                buyerRepo.countByStatus(VerificationStatus.PENDING) +
                deliveryRepo.countByStatus(VerificationStatus.PENDING);
                
        long activeStocks = stockRepo.count();
        long totalOrders = orderRepo.count();
        Double revenue = orderRepo.sumTotalPlatformRevenue();
        
        // Orders by Status distribution
        List<Object[]> statusData = orderRepo.countOrdersByStatus();
        Map<String, Long> ordersByStatus = statusData.stream()
                .collect(Collectors.toMap(
                        obj -> (String) obj[0],
                        obj -> obj[1] != null ? ((Number) obj[1]).longValue() : 0L
                ));

        // Registration trend
        List<Object[]> trendData = userRepo.getRegistrationTrend();
        Map<String, Long> userTrend = trendData.stream()
                .collect(Collectors.toMap(
                        obj -> (String) obj[0],
                        obj -> obj[1] != null ? ((Number) obj[1]).longValue() : 0L
                ));

        AdminDashboardResponse dashboard = AdminDashboardResponse.builder()
                .totalUsers(totalUsers)
                .totalFarmers(totalFarmers)
                .totalBuyers(totalBuyers)
                .totalDeliveryPartners(totalDeliveryPartners)
                .pendingVerifications(pendingVerifications)
                .activeStocks(activeStocks)
                .totalOrders(totalOrders)
                .platformRevenue(revenue != null ? revenue : 0.0)
                .ordersByStatus(ordersByStatus)
                .userRegistrationTrend(userTrend)
                .build();

        return ServerResponse.successObjectResponse("Admin dashboard fetch success", HttpStatus.OK, dashboard);
    }
}
