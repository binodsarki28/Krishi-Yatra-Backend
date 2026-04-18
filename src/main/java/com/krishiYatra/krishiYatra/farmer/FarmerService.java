package com.krishiYatra.krishiYatra.farmer;

import com.krishiYatra.krishiYatra.common.enums.RoleType;
import com.krishiYatra.krishiYatra.common.response.ServerResponse;
import com.krishiYatra.krishiYatra.farmer.dao.IFarmerDao;
import com.krishiYatra.krishiYatra.farmer.dto.FarmerListResponse;
import com.krishiYatra.krishiYatra.farmer.dto.FarmerDetailResponse;
import com.krishiYatra.krishiYatra.farmer.dto.RegisterFarmerRequest;
import com.krishiYatra.krishiYatra.farmer.dto.VerifyFarmerRequest;
import com.krishiYatra.krishiYatra.farmer.mapper.FarmerMapper;
import com.krishiYatra.krishiYatra.notification.handler.VerificationNotificationHandler;
import com.krishiYatra.krishiYatra.user.RoleRepo;
import com.krishiYatra.krishiYatra.user.UserConst;
import com.krishiYatra.krishiYatra.user.UserEntity;
import com.krishiYatra.krishiYatra.user.UserRepo;
import com.krishiYatra.krishiYatra.utils.UserUtil;
import com.krishiYatra.krishiYatra.stock.StockRepo;
import com.krishiYatra.krishiYatra.order.OrderRepo;
import com.krishiYatra.krishiYatra.demand.DemandRepo;
import com.krishiYatra.krishiYatra.common.enums.OrderStatus;
import com.krishiYatra.krishiYatra.farmer.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.krishiYatra.krishiYatra.common.enums.VerificationStatus;

@Service
@Slf4j
@RequiredArgsConstructor
public class FarmerService {

    private final FarmerRepo farmerRepo;
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final FarmerMapper farmerMapper;
    private final IFarmerDao farmerDao;
    private final StockRepo stockRepo;
    private final OrderRepo orderRepo;
    private final DemandRepo demandRepo;
    private final VerificationNotificationHandler verificationNotificationHandler;

    @Transactional(readOnly = true)
    public List<FarmerListResponse> getFarmers(java.util.Map<String, String> params, Pageable pageable) {
        return farmerDao.getAllFarmers(params, pageable);
    }

    @Transactional
    public ServerResponse registerFarmer(RegisterFarmerRequest request) {
        UserEntity user = UserUtil.getCurrentUser();

        if (user == null) {
            return ServerResponse.failureResponse(FarmerConst.USER_NOT_AUTHENTICATED, HttpStatus.UNAUTHORIZED);
        }

        UserEntity managedUser = userRepo.findByUsername(user.getUsername())
                .orElseThrow(() -> new RuntimeException(FarmerConst.USER_NOT_FOUND));

        if (farmerRepo.findByUser(managedUser).isPresent()) {
            // Fix for existing users: If they registered before the role-assignment
            // give them the role now if they try to register again.
            if (!managedUser.getRoles().stream().anyMatch(r -> r.getRoleName() == RoleType.FARMER)) {
                roleRepo.findByRoleName(RoleType.FARMER).ifPresent(role -> {
                    managedUser.getRoles().add(role);
                    userRepo.save(managedUser);
                });
                return ServerResponse.successResponse("Roles synced! Your Farmer dashboard should now be visible.", HttpStatus.OK);
            }
            return ServerResponse.failureResponse(FarmerConst.ALREADY_FARMER, HttpStatus.BAD_REQUEST);
        }

        FarmerEntity farmer = farmerMapper.toEntity(request);
        farmer.setUser(managedUser);
        farmerRepo.save(farmer);

        // Add a Farmer role to a user set (Crucial for frontend dashboard visibility)
        roleRepo.findByRoleName(RoleType.FARMER).ifPresent(role -> {
            managedUser.getRoles().add(role);
            userRepo.save(managedUser);
        });

        return ServerResponse.successResponse(FarmerConst.REGISTRATION_SUCCESS, HttpStatus.CREATED);
    }

    @Transactional
    public ServerResponse verifyFarmer(VerifyFarmerRequest request) {
        FarmerEntity farmer = farmerRepo.findByUser_Username(request.getUsername())
                .orElseThrow(() -> new RuntimeException(FarmerConst.REGISTRATION_NOT_FOUND));

        if (request.getApproved()) {
            farmer.setStatus(VerificationStatus.VERIFIED);
            farmerRepo.save(farmer);
            
            // Notify user of approval and take to dashboard
            try {
                verificationNotificationHandler.notifyFarmerStatus(farmer.getUser(), true, null);
            } catch (Exception e) {
                log.error("Failed to send farmer verification notification: {}", e.getMessage());
            }

            return ServerResponse.successResponse(FarmerConst.VERIFICATION_SUCCESS, HttpStatus.OK);
        } else {
            // Store user for notification before deleting farmer entity
            UserEntity user = farmer.getUser();
            
            // If rejected, delete the farmer entity
            farmerRepo.delete(farmer);

            // Remove Farmer role from user so they can try again
            UserEntity managedUser = userRepo.findByUsername(user.getUsername())
                    .orElseThrow(() -> new RuntimeException(UserConst.USER_NOT_FOUND));
            managedUser.getRoles().removeIf(role -> role.getRoleName() == RoleType.FARMER);
            userRepo.save(managedUser);

            // Notify user of rejection and take to the registration page
            try {
                verificationNotificationHandler.notifyFarmerStatus(user, false, request.getReason());
            } catch (Exception e) {
                log.error("Failed to send farmer rejection notification: {}", e.getMessage());
            }

            String message = FarmerConst.REJECTION_PREFIX + request.getReason();
            log.info(message);
            return ServerResponse.successResponse(message, HttpStatus.OK);
        }
    }

    @Transactional
    public ServerResponse blockUnblockFarmer(String username, boolean block, String reason) {
        FarmerEntity farmer = farmerRepo.findByUser_Username(username)
                .orElseThrow(() -> new RuntimeException(FarmerConst.REGISTRATION_NOT_FOUND));
        
        if (block) {
            farmer.setStatus(VerificationStatus.BLOCKED);
            farmer.setStatusMessage(reason != null && !reason.trim().isEmpty() ? "Blocked by admin. Reason: " + reason : "Your account has been blocked by the admin.");
        } else {
            farmer.setStatus(VerificationStatus.VERIFIED);
            farmer.setStatusMessage(null);
        }
        farmerRepo.save(farmer);
        
        String action = block ? "blocked" : "unblocked";
        return ServerResponse.successResponse("Farmer " + action + " successfully", HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public FarmerDetailResponse getFarmerDetail(String username) {
        FarmerEntity farmer = farmerRepo.findByUser_Username(username)
                .orElseThrow(() -> new RuntimeException(FarmerConst.REGISTRATION_NOT_FOUND));
        
        return farmerMapper.toDetailResponse(farmer);
    }

    @Transactional(readOnly = true)
    public ServerResponse getFarmerDashboard() {
        UserEntity user = UserUtil.getCurrentUser();
        FarmerEntity farmer = farmerRepo.findByUser_Username(user.getUsername())
                .orElseThrow(() -> new RuntimeException(FarmerConst.REGISTRATION_NOT_FOUND));

        // Stats
        long totalStocks = stockRepo.countByFarmer(farmer);
        long activeStocks = stockRepo.countByFarmerAndActive(farmer, true);
        long outOfStock = stockRepo.countByFarmerAndQuantityLessThanEqual(farmer, 0.0);
        
        long totalOrders = orderRepo.countByFarmer(farmer);
        long pendingOrders = orderRepo.countByFarmerAndOrderStatus(farmer, OrderStatus.PENDING);
        long completedOrders = orderRepo.countByFarmerAndOrderStatusIn(farmer, List.of(OrderStatus.DELIVERED, OrderStatus.RESOLVED));
        
        long acceptedDemands = demandRepo.countByAcceptedBy(farmer);
        
        Double revenue = orderRepo.sumTotalPriceByFarmer(farmer);
        
        // Category distribution for pie chart
        List<Object[]> categoryData = stockRepo.countStocksByCategory(farmer);
        Map<String, Long> stocksByCategory = categoryData.stream()
                .collect(Collectors.toMap(
                        obj -> (String) obj[0],
                        obj -> obj[1] != null ? ((Number) obj[1]).longValue() : 0L
                ));

        // Revenue trend for line chart
        List<Object[]> trendData = orderRepo.farmerRevenueTrend(farmer.getFarmerId());
        Map<String, Double> revenueTrend = trendData.stream()
                .collect(Collectors.toMap(
                        obj -> (String) obj[0],
                        obj -> obj[1] != null ? ((Number) obj[1]).doubleValue() : 0.0
                ));

        FarmerDashboardResponse dashboard = FarmerDashboardResponse.builder()
                .totalStocks(totalStocks)
                .activeStocks(activeStocks)
                .outOfStock(outOfStock)
                .totalOrders(totalOrders)
                .pendingOrders(pendingOrders)
                .completedOrders(completedOrders)
                .acceptedDemands(acceptedDemands)
                .totalRevenue(revenue != null ? revenue : 0.0)
                .stocksByCategory(stocksByCategory)
                .revenueByMonth(revenueTrend)
                .build();

        return ServerResponse.successObjectResponse(FarmerConst.DASHBOARD_WELCOME, HttpStatus.OK, dashboard);
    }
}
