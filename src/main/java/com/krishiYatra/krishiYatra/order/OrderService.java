package com.krishiYatra.krishiYatra.order;

import com.krishiYatra.krishiYatra.address.AddressService;
import com.krishiYatra.krishiYatra.address.dto.AddressResponse;
import com.krishiYatra.krishiYatra.buyer.BuyerEntity;
import com.krishiYatra.krishiYatra.buyer.BuyerRepo;
import com.krishiYatra.krishiYatra.common.enums.NotificationCategory;
import com.krishiYatra.krishiYatra.common.enums.NotificationType;
import com.krishiYatra.krishiYatra.common.enums.OrderStatus;
import com.krishiYatra.krishiYatra.common.enums.VerificationStatus;
import com.krishiYatra.krishiYatra.common.response.ServerResponse;
import com.krishiYatra.krishiYatra.delivery.DeliveryEntity;
import com.krishiYatra.krishiYatra.delivery.DeliveryRepo;
import com.krishiYatra.krishiYatra.farmer.FarmerEntity;
import com.krishiYatra.krishiYatra.order.dto.OrderCreateRequest;
import com.krishiYatra.krishiYatra.order.dto.OrderResponse;
import com.krishiYatra.krishiYatra.notification.NotificationConst;
import com.krishiYatra.krishiYatra.notification.NotificationService;
import com.krishiYatra.krishiYatra.notification.dto.OrderNotificationDto;
import com.krishiYatra.krishiYatra.notification.handler.*;
import com.krishiYatra.krishiYatra.stock.StockEntity;
import com.krishiYatra.krishiYatra.stock.StockRepo;
import com.krishiYatra.krishiYatra.user.UserEntity;
import com.krishiYatra.krishiYatra.farmer.FarmerRepo;
import com.krishiYatra.krishiYatra.utils.UserUtil;
import com.krishiYatra.krishiYatra.order.mapper.OrderMapper;
import com.krishiYatra.krishiYatra.order.dao.IOrderDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepo orderRepo;
    private final StockRepo stockRepo;
    private final BuyerRepo buyerRepo;
    private final DeliveryRepo deliveryRepo;
    private final FarmerRepo farmerRepo;
    private final OrderMapper orderMapper;
    private final AddressService addressService;
    private final IOrderDao orderDao;
    private final NotificationService notificationService;
    private final OrderCreatedNotificationHandler orderCreatedNotificationHandler;
    private final OrderAcceptedNotificationHandler orderAcceptedNotificationHandler;
    private final OrderPickedUpNotificationHandler orderPickedUpNotificationHandler;
    private final OrderCheckpointNotificationHandler orderCheckpointNotificationHandler;
    private final OrderDeliveredNotificationHandler orderDeliveredNotificationHandler;
    private final OrderConflictNotificationHandler orderConflictNotificationHandler;
    private final OrderResolvedNotificationHandler orderResolvedNotificationHandler;
    private final OrderCancelledNotificationHandler orderCancelledNotificationHandler;

    @Transactional
    public ServerResponse createOrder(OrderCreateRequest request) {
        UserEntity currentUser = UserUtil.getCurrentUser();
        if (currentUser == null) {
            return ServerResponse.failureResponse(OrderConst.BUYER_NOT_FOUND, HttpStatus.UNAUTHORIZED);
        }

        Optional<BuyerEntity> buyerOptional = buyerRepo.findByUser(currentUser);
        if (buyerOptional.isEmpty()) {
            return ServerResponse.failureResponse(OrderConst.BUYER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        BuyerEntity buyer = buyerOptional.get();

        if (buyer.getStatus() != VerificationStatus.VERIFIED) {
            return ServerResponse.failureResponse(OrderConst.BUYER_NOT_VERIFIED, HttpStatus.FORBIDDEN);
        }

        Optional<StockEntity> stockOptional = stockRepo.findByStockSlug(request.getStockSlug());
        if (stockOptional.isEmpty()) {
            return ServerResponse.failureResponse(OrderConst.STOCK_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        StockEntity stock = stockOptional.get();

        if (!stock.isActive()) {
            return ServerResponse.failureResponse(OrderConst.STOCK_NOT_ACTIVE, HttpStatus.BAD_REQUEST);
        }
        if (stock.getFarmer() == null || stock.getFarmer().getStatus() != VerificationStatus.VERIFIED) {
            return ServerResponse.failureResponse(OrderConst.FARMER_NOT_VERIFIED, HttpStatus.BAD_REQUEST);
        }
        if (request.getOrderQuantity() < stock.getMinQuantity()) {
            return ServerResponse.failureResponse(
                    OrderConst.MINIMUM_ORDER_QUANTITY + " Minimum required: " + stock.getMinQuantity(),
                    HttpStatus.BAD_REQUEST
            );
        }
        if (request.getOrderQuantity() > stock.getQuantity()) {
            return ServerResponse.failureResponse(OrderConst.INSUFFICIENT_STOCK, HttpStatus.BAD_REQUEST);
        }

        OrderEntity entity = orderMapper.toEntity(request, buyer, stock);
        OrderEntity saved = orderRepo.save(entity);

        stock.setQuantity(stock.getQuantity() - request.getOrderQuantity());
        stockRepo.save(stock);

        if (stock.getQuantity() < stock.getMinQuantity()) {
            String farmerName = stock.getFarmer().getUser().getUsername();
            String title = NotificationConst.STOCK_LOW_TITLE;
            String body = String.format(NotificationConst.STOCK_LOW_BODY, stock.getProductName(), stock.getQuantity());
            
            notificationService.sendToUser(farmerName, title, body, 
                NotificationType.PUSH,
                NotificationCategory.STOCK_LOW,
                String.format(NotificationConst.EDIT_STOCK_URL, stock.getStockSlug()));
        }

        // Notify Buyer, Farmer, and Delivery Riders
        try {
            OrderNotificationDto notificationDto = OrderNotificationDto.builder()
                .orderId(saved.getOrderId())
                .productName(stock.getProductName())
                .farmerUsername(stock.getFarmer().getUser().getUsername())
                .buyerUsername(buyer.getUser().getUsername())
                .build();
            orderCreatedNotificationHandler.handle(notificationDto);
        } catch (Exception e) {
            log.error("Failed to send order creation notifications: {}", e.getMessage());
        }

        return ServerResponse.successObjectResponse(OrderConst.CREATE_ORDER, HttpStatus.CREATED, saved.getOrderId());
    }

    @Transactional
    public ServerResponse acceptOrderByDelivery(String orderId) {
        UserEntity currentUser = UserUtil.getCurrentUser();
        if (currentUser == null) {
            return ServerResponse.failureResponse(OrderConst.DELIVERY_NOT_FOUND, HttpStatus.UNAUTHORIZED);
        }

        DeliveryEntity delivery = deliveryRepo.findByUser(currentUser)
                .orElse(null);
        if (delivery == null) {
            return ServerResponse.failureResponse(OrderConst.DELIVERY_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        if (delivery.getStatus() != VerificationStatus.VERIFIED) {
            return ServerResponse.failureResponse(OrderConst.DELIVERY_NOT_VERIFIED, HttpStatus.FORBIDDEN);
        }

        OrderEntity order = orderRepo.findByOrderIdAndDeliveryIsNull(orderId)
                .orElse(null);
        if (order == null || order.getOrderStatus() != OrderStatus.PENDING) {
            return ServerResponse.failureResponse(OrderConst.ORDER_NOT_AVAILABLE_FOR_DELIVERY, HttpStatus.BAD_REQUEST);
        }

        order.setDelivery(delivery);
        order.setOrderStatus(OrderStatus.ACCEPTED);
        OrderEntity savedOrder = orderRepo.save(order);

        // Notify Buyer and Farmer (No notification for rider as they are the ones accepting)
        OrderNotificationDto notificationDto = OrderNotificationDto.builder()
                .orderId(savedOrder.getOrderId())
                .productName(savedOrder.getStock().getProductName())
                .buyerUsername(savedOrder.getBuyer().getUser().getUsername())
                .farmerUsername(savedOrder.getStock().getFarmer().getUser().getUsername())
                .build();
        orderAcceptedNotificationHandler.handle(notificationDto);

        return ServerResponse.successResponse(OrderConst.DELIVERY_ACCEPTED, HttpStatus.OK);
    }

    @Transactional
    public ServerResponse markAsPickedUp(String orderId) {
        OrderEntity order = orderRepo.findById(orderId).orElse(null);
        if (order == null) return ServerResponse.failureResponse(OrderConst.ORDER_NOT_FOUND, HttpStatus.NOT_FOUND);
        
        if (order.getOrderStatus() != OrderStatus.ACCEPTED) {
            return ServerResponse.failureResponse("Order must be ACCEPTED before picking up.", HttpStatus.BAD_REQUEST);
        }
        
        order.setOrderStatus(OrderStatus.SHIPPING);
        OrderEntity savedOrder = orderRepo.save(order);

        // Notify Buyer only (Farmer already knows physically)
        OrderNotificationDto notificationDto = OrderNotificationDto.builder()
                .orderId(savedOrder.getOrderId())
                .productName(savedOrder.getStock().getProductName())
                .buyerUsername(savedOrder.getBuyer().getUser().getUsername())
                .build();
        orderPickedUpNotificationHandler.handle(notificationDto);

        return ServerResponse.successResponse("Order picked up and is now SHIPPING.", HttpStatus.OK);
    }

    public ServerResponse getPendingOrders() {
        UserEntity currentUser = UserUtil.getCurrentUser();
        if (currentUser == null) {
            return ServerResponse.failureResponse("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        
        DeliveryEntity delivery = deliveryRepo.findByUser(currentUser).orElse(null);
        if (delivery == null) {
            return ServerResponse.failureResponse("Delivery profile not found", HttpStatus.NOT_FOUND);
        }

        List<OrderEntity> pendingOrders = orderRepo.findByDeliveryIsNullAndOrderStatus(
            OrderStatus.PENDING
        );

        List<OrderResponse> responses = pendingOrders.stream()
                .map(order -> {
                    OrderResponse resp = new OrderResponse();
                    resp.setOrderId(order.getOrderId());
                    resp.setStockSlug(order.getStock().getStockSlug());
                    resp.setProductName(order.getStock().getProductName());
                    resp.setOrderQuantity(order.getOrderQuantity());
                    resp.setPerUnitPrice(order.getPerUnitPrice());
                    resp.setTotalPrice(order.getTotalPrice());
                    resp.setOrderStatus(order.getOrderStatus());
                    resp.setPickupAddress(order.getPickupAddress());
                    resp.setDropAddress(order.getDropAddress());
                    resp.setDeliveryFee(order.getDeliveryFee());
                    resp.setCheckpoints(order.getCheckpoints());
                    resp.setNotes(order.getNotes());
                    resp.setCreatedAt(order.getCreatedAt());
                    resp.setConflictMessage(order.getConflictMessage());
                    return resp;
                })
                .collect(Collectors.toList());

        return ServerResponse.successObjectResponse(OrderConst.FETCH_ORDER, HttpStatus.OK, responses, responses.size());
    }

    public ServerResponse getOrderById(String orderId) {
        OrderEntity order = orderRepo.findById(orderId).orElse(null);
        if (order == null) return ServerResponse.failureResponse("Order not found", HttpStatus.NOT_FOUND);
        
        OrderResponse resp = new OrderResponse();
        resp.setOrderId(order.getOrderId());
        resp.setStockSlug(order.getStock().getStockSlug());
        resp.setProductName(order.getStock().getProductName());
        resp.setOrderQuantity(order.getOrderQuantity());
        resp.setPerUnitPrice(order.getPerUnitPrice());
        resp.setTotalPrice(order.getTotalPrice());
        resp.setOrderStatus(order.getOrderStatus());
        resp.setPickupAddress(order.getPickupAddress());
        resp.setDropAddress(order.getDropAddress());
        resp.setDeliveryFee(order.getDeliveryFee());
        resp.setCheckpoints(order.getCheckpoints());
        resp.setNotes(order.getNotes());
        resp.setCreatedAt(order.getCreatedAt());
        resp.setConflictMessage(order.getConflictMessage());
        resp.setConflictRaisedAt(order.getConflictRaisedAt());
        
        // Set Farmer Information
        if (order.getFarmer() != null && order.getFarmer().getUser() != null) {
            resp.setFarmerName(order.getFarmer().getUser().getFullName());
            resp.setFarmerPhone(order.getFarmer().getUser().getPhoneNumber());
        }
        
        // Set Buyer Information
        if (order.getBuyer() != null && order.getBuyer().getUser() != null) {
            resp.setBuyerName(order.getBuyer().getUser().getFullName());
            resp.setBuyerPhone(order.getBuyer().getUser().getPhoneNumber());
        }
        
        // Set Delivery Information
        if (order.getDelivery() != null && order.getDelivery().getUser() != null) {
            resp.setDeliveryName(order.getDelivery().getUser().getFullName());
            resp.setDeliveryPhone(order.getDelivery().getUser().getPhoneNumber());
        }
        
        return ServerResponse.successObjectResponse(OrderConst.ORDER_DETAIL, HttpStatus.OK, resp, 1);
    }

    @Transactional
    public ServerResponse updateOrderCheckpoints(String orderId, String checkpoints) {
        OrderEntity order = orderRepo.findById(orderId).orElse(null);
        if (order == null) return ServerResponse.failureResponse(OrderConst.ORDER_NOT_FOUND, HttpStatus.NOT_FOUND);
        
        order.setCheckpoints(checkpoints);
        OrderEntity savedOrder = orderRepo.save(order);

        // Notify Buyer and Farmer about the latest checkpoint reached
        String latestCheckpoint = extractLatestCheckpoint(checkpoints);
        if (latestCheckpoint != null) {
            OrderNotificationDto notificationDto = OrderNotificationDto.builder()
                .orderId(savedOrder.getOrderId())
                .productName(savedOrder.getStock().getProductName())
                .buyerUsername(savedOrder.getBuyer().getUser().getUsername())
                .farmerUsername(savedOrder.getStock().getFarmer().getUser().getUsername())
                .checkpointName(latestCheckpoint)
                .build();
            orderCheckpointNotificationHandler.handle(notificationDto);
        }

        return ServerResponse.successResponse("Checkpoints updated", HttpStatus.OK);
    }

    private String extractLatestCheckpoint(String checkpoints) {
        if (checkpoints == null || !checkpoints.contains("(Reached)")) {
            return null;
        }
        String[] parts = checkpoints.split("\\|");
        for (int i = parts.length - 1; i >= 0; i--) {
            if (parts[i].contains("(Reached)")) {
                return parts[i].replace("(Reached)", "").trim();
            }
        }
        return null;
    }

    @Transactional
    public ServerResponse markOrderAsDelivered(String orderId) {
        OrderEntity order = orderRepo.findById(orderId).orElse(null);
        if (order == null) {
            return ServerResponse.failureResponse(OrderConst.ORDER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        order.setOrderStatus(OrderStatus.DELIVERED);
        order.setDeliveredAt(java.time.LocalDateTime.now());
        OrderEntity savedOrder = orderRepo.save(order);

        // Notify Farmer
        OrderNotificationDto notificationDto = OrderNotificationDto.builder()
                .orderId(savedOrder.getOrderId())
                .productName(savedOrder.getStock().getProductName())
                .farmerUsername(savedOrder.getStock().getFarmer().getUser().getUsername())
                .build();
        orderDeliveredNotificationHandler.handle(notificationDto);

        return ServerResponse.successResponse(OrderConst.ORDER_DELIVERED, HttpStatus.OK);
    }

    public ServerResponse getFarmerAddressByStockSlug(String stockSlug) {
        Optional<StockEntity> stockOpt = stockRepo.findByStockSlug(stockSlug);
        if (stockOpt.isEmpty()) {
            return ServerResponse.failureResponse(OrderConst.STOCK_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        StockEntity stock = stockOpt.get();
        if (stock.getFarmer() == null || stock.getFarmer().getUser() == null) {
            return ServerResponse.failureResponse("Farmer information not available.", HttpStatus.NOT_FOUND);
        }

        AddressResponse address = addressService.getAddressByUser(stock.getFarmer().getUser());
        if (address == null) {
            return ServerResponse.failureResponse("Farmer has not set an address.", HttpStatus.NOT_FOUND);
        }
        return ServerResponse.successObjectResponse("Farmer address fetched.", HttpStatus.OK, address);
    }

    public ServerResponse getMyAcceptedOrders() {
        UserEntity currentUser = UserUtil.getCurrentUser();
        if (currentUser == null) {
            return ServerResponse.failureResponse("Unauthorized", HttpStatus.UNAUTHORIZED);
        }

        DeliveryEntity delivery = deliveryRepo.findByUser(currentUser).orElse(null);
        if (delivery == null) {
            return ServerResponse.failureResponse("Delivery profile not found", HttpStatus.NOT_FOUND);
        }

        List<OrderEntity> acceptedOrders = orderRepo.findByDeliveryAndOrderStatusIn(
            delivery, 
            List.of(OrderStatus.SHIPPING, OrderStatus.ACCEPTED)
        );

        List<OrderResponse> responses = acceptedOrders.stream()
                .map(order -> {
                    OrderResponse resp = new OrderResponse();
                    resp.setOrderId(order.getOrderId());
                    resp.setStockSlug(order.getStock().getStockSlug());
                    resp.setProductName(order.getStock().getProductName());
                    resp.setOrderQuantity(order.getOrderQuantity());
                    resp.setPerUnitPrice(order.getPerUnitPrice());
                    resp.setTotalPrice(order.getTotalPrice());
                    resp.setOrderStatus(order.getOrderStatus());
                    resp.setPickupAddress(order.getPickupAddress());
                    resp.setDropAddress(order.getDropAddress());
                    resp.setDeliveryFee(order.getDeliveryFee());
                    // resp.setVehicleType(order.getVehicleType());
                    resp.setCheckpoints(order.getCheckpoints());
                    resp.setNotes(order.getNotes());
                    resp.setCreatedAt(order.getCreatedAt());
                    resp.setConflictMessage(order.getConflictMessage());
                    return resp;
                })
                .collect(Collectors.toList());

        return ServerResponse.successObjectResponse("Fetched accepted orders", HttpStatus.OK, responses, responses.size());
    }

    @Transactional
    public ServerResponse cancelOrder(String orderId) {
        OrderEntity order = orderRepo.findById(orderId)
                .orElse(null);
        if (order == null) {
            return ServerResponse.failureResponse(OrderConst.ORDER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        if (order.getOrderStatus() == OrderStatus.DELIVERED || order.getOrderStatus() == OrderStatus.CANCELLED || order.getOrderStatus() == OrderStatus.RESOLVED) {
            return ServerResponse.failureResponse("Cannot cancel an order that is already delivered or cancelled.", HttpStatus.BAD_REQUEST);
        }

        // Restore stock quantity
        StockEntity stock = order.getStock();
        if (stock != null) {
            stock.setQuantity(stock.getQuantity() + order.getOrderQuantity());
            stockRepo.save(stock);
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        OrderEntity savedOrder = orderRepo.save(order);

        // Notify all 3 parties (Buyer, Farmer, and Delivery)
        OrderNotificationDto notificationDto = OrderNotificationDto.builder()
                .orderId(savedOrder.getOrderId())
                .productName(savedOrder.getStock().getStockName())
                .buyerUsername(savedOrder.getBuyer().getUser().getUsername())
                .farmerUsername(savedOrder.getStock().getFarmer().getUser().getUsername())
                .deliveryUsername(savedOrder.getDelivery() != null ? savedOrder.getDelivery().getUser().getUsername() : null)
                .build();
        orderCancelledNotificationHandler.handle(notificationDto);

        return ServerResponse.successResponse("Order cancelled successfully and stock restored.", HttpStatus.OK);
    }

    @Transactional
    public ServerResponse reportConflict(String orderId, String message) {
        if (message == null || message.trim().isEmpty()) {
            return ServerResponse.failureResponse("Conflict message is compulsory and cannot be empty.", HttpStatus.BAD_REQUEST);
        }

        OrderEntity order = orderRepo.findById(orderId)
                .orElse(null);
        if (order == null) return ServerResponse.failureResponse(OrderConst.ORDER_NOT_FOUND, HttpStatus.NOT_FOUND);

        if (order.getOrderStatus() != OrderStatus.DELIVERED) {
            return ServerResponse.failureResponse("Conflict can only be reported for delivered orders.", HttpStatus.BAD_REQUEST);
        }

        order.setOrderStatus(OrderStatus.CONFLICT);
        order.setConflictMessage(message);
        order.setConflictRaisedAt(java.time.LocalDateTime.now());
        OrderEntity savedOrder = orderRepo.save(order);

        // Notify Farmer and Delivery (Buyer is already the one reporting it)
        OrderNotificationDto notificationDto = OrderNotificationDto.builder()
                .orderId(savedOrder.getOrderId())
                .productName(savedOrder.getStock().getStockName())
                .farmerUsername(savedOrder.getStock().getFarmer().getUser().getUsername())
                .deliveryUsername(savedOrder.getDelivery() != null ? savedOrder.getDelivery().getUser().getUsername() : null)
                .build();
        orderConflictNotificationHandler.handle(notificationDto, message);

        return ServerResponse.successResponse("Conflict reported successfully. Admin will review it.", HttpStatus.OK);
    }

    @Transactional
    public ServerResponse resolveConflict(String orderId) {
        OrderEntity order = orderRepo.findById(orderId)
                .orElse(null);
        if (order == null) return ServerResponse.failureResponse(OrderConst.ORDER_NOT_FOUND, HttpStatus.NOT_FOUND);

        if (order.getOrderStatus() != OrderStatus.CONFLICT) {
            return ServerResponse.failureResponse("Only orders with CONFLICT status can be resolved.", HttpStatus.BAD_REQUEST);
        }

        order.setOrderStatus(OrderStatus.RESOLVED);
        order.setConflictResolvedAt(java.time.LocalDateTime.now());
        OrderEntity savedOrder = orderRepo.save(order);

        // Notify Buyer, Farmer, and Delivery
        OrderNotificationDto notificationDto = OrderNotificationDto.builder()
                .orderId(savedOrder.getOrderId())
                .productName(savedOrder.getStock().getStockName())
                .buyerUsername(savedOrder.getBuyer().getUser().getUsername())
                .farmerUsername(savedOrder.getStock().getFarmer().getUser().getUsername())
                .deliveryUsername(savedOrder.getDelivery() != null ? savedOrder.getDelivery().getUser().getUsername() : null)
                .build();
        orderResolvedNotificationHandler.handle(notificationDto);

        return ServerResponse.successResponse("Conflict marked as RESOLVED.", HttpStatus.OK);
    }

    public ServerResponse getOrders(Map<String, String> requestParams, org.springframework.data.domain.Pageable pageable) {
        UserEntity currentUser = UserUtil.getCurrentUser();
        if (currentUser != null) {
            boolean isAdmin = currentUser.getRoles().stream().anyMatch(r -> r.getRoleName().name().equals("ADMIN"));
            if (!isAdmin) {
                if (farmerRepo.findByUser(currentUser).isPresent()) {
                    requestParams.put("filterFarmerUserId", currentUser.getUserId());
                } else if (currentUser.getRoles().stream().anyMatch(r -> r.getRoleName() == com.krishiYatra.krishiYatra.common.enums.RoleType.DELIVERY)) {
                    requestParams.put("filterDeliveryUserId", currentUser.getUserId());
                } else if (currentUser.getRoles().stream().anyMatch(r -> r.getRoleName() == com.krishiYatra.krishiYatra.common.enums.RoleType.BUYER)) {
                    requestParams.put("filterBuyerUserId", currentUser.getUserId());
                }
            }
        }

        List<OrderResponse> orders = orderDao.getAllOrders(requestParams, pageable);
        return ServerResponse.successObjectResponse("Orders fetched successfully.", HttpStatus.OK, orders);
    }

    public ServerResponse getOrdersByBuyer(Map<String, String> requestParams, org.springframework.data.domain.Pageable pageable) {
        UserEntity currentUser = UserUtil.getCurrentUser();
        if (currentUser == null) {
            return ServerResponse.failureResponse("Unauthorized", HttpStatus.UNAUTHORIZED);
        }

        Optional<BuyerEntity> buyerOpt = buyerRepo.findByUser(currentUser);
        if (buyerOpt.isEmpty()) {
            return ServerResponse.failureResponse("Buyer profile not found", HttpStatus.NOT_FOUND);
        }

        requestParams.put("filterBuyerUserId", currentUser.getUserId());
        List<OrderResponse> orders = orderDao.getAllOrders(requestParams, pageable);

        return ServerResponse.successObjectResponse("Your bought orders fetched successfully.", HttpStatus.OK, orders, orders.size());
    }

    public ServerResponse getOrdersByFarmer(Map<String, String> requestParams, Pageable pageable) {
        UserEntity currentUser = UserUtil.getCurrentUser();
        if (currentUser == null) {
            return ServerResponse.failureResponse("Unauthorized", HttpStatus.UNAUTHORIZED);
        }

        Optional<FarmerEntity> farmerOpt = farmerRepo.findByUser(currentUser);

        if (farmerOpt.isEmpty()) {
            return ServerResponse.failureResponse("Farmer profile not found", HttpStatus.NOT_FOUND);
        }

        requestParams.put("filterFarmerUserId", currentUser.getUserId());
        List<OrderResponse> orders = orderDao.getAllOrders(requestParams, pageable);

        return ServerResponse.successObjectResponse("Your sold orders fetched successfully.", HttpStatus.OK, orders, orders.size());
    }

    public ServerResponse getOrdersByDelivery(Map<String, String> requestParams, Pageable pageable) {
        UserEntity currentUser = UserUtil.getCurrentUser();
        if (currentUser == null) {
            return ServerResponse.failureResponse("Unauthorized", HttpStatus.UNAUTHORIZED);
        }

        Optional<DeliveryEntity> deliveryOpt = deliveryRepo.findByUser(currentUser);
        if (deliveryOpt.isEmpty()) {
            return ServerResponse.failureResponse("Delivery profile not found", HttpStatus.NOT_FOUND);
        }

        requestParams.put("filterDeliveryUserId", currentUser.getUserId());
        List<OrderResponse> orders = orderDao.getAllOrders(requestParams, pageable);

        return ServerResponse.successObjectResponse("Your delivered orders fetched successfully.", HttpStatus.OK, orders, orders.size());
    }
}
