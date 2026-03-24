package com.krishiYatra.krishiYatra.order;

import com.krishiYatra.krishiYatra.address.AddressService;
import com.krishiYatra.krishiYatra.address.dto.AddressResponse;
import com.krishiYatra.krishiYatra.buyer.BuyerEntity;
import com.krishiYatra.krishiYatra.buyer.BuyerRepo;
import com.krishiYatra.krishiYatra.common.enums.OrderStatus;
import com.krishiYatra.krishiYatra.common.enums.VerificationStatus;
import com.krishiYatra.krishiYatra.common.response.ServerResponse;
import com.krishiYatra.krishiYatra.delivery.DeliveryEntity;
import com.krishiYatra.krishiYatra.delivery.DeliveryRepo;
import com.krishiYatra.krishiYatra.order.dto.OrderCreateRequest;
import com.krishiYatra.krishiYatra.order.dto.OrderResponse;
import com.krishiYatra.krishiYatra.order.mapper.OrderMapper;
import com.krishiYatra.krishiYatra.stock.StockEntity;
import com.krishiYatra.krishiYatra.stock.StockRepo;
import com.krishiYatra.krishiYatra.user.UserEntity;
import com.krishiYatra.krishiYatra.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
    private final OrderMapper orderMapper;
    private final AddressService addressService;
    private final com.krishiYatra.krishiYatra.order.dao.IOrderDao orderDao;

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

        return ServerResponse.successResponse(OrderConst.CREATE_ORDER, HttpStatus.CREATED);
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

        // Check vehicle compatibility
        if (order.getVehicleType() != null && !order.getVehicleType().equalsIgnoreCase(delivery.getVehicleType().name())) {
            return ServerResponse.failureResponse("Your vehicle type (" + delivery.getVehicleType() + ") does not match the required: " + order.getVehicleType(), HttpStatus.BAD_REQUEST);
        }

        order.setDelivery(delivery);
        order.setOrderStatus(OrderStatus.SHIPPING);
        orderRepo.save(order);

        return ServerResponse.successResponse(OrderConst.DELIVERY_ACCEPTED, HttpStatus.OK);
    }

    public ServerResponse getPendingOrders() {
        UserEntity currentUser = UserUtil.getCurrentUser();
        if (currentUser == null) return ServerResponse.failureResponse("Unauthorized", HttpStatus.UNAUTHORIZED);
        
        DeliveryEntity delivery = deliveryRepo.findByUser(currentUser).orElse(null);
        if (delivery == null) return ServerResponse.failureResponse("Delivery profile not found", HttpStatus.NOT_FOUND);

        List<OrderEntity> pendingOrders = orderRepo.findByDeliveryIsNullAndOrderStatusAndVehicleType(
            OrderStatus.PENDING, 
            delivery.getVehicleType().name()
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
                    resp.setVehicleType(order.getVehicleType());
                    resp.setCheckpoints(order.getCheckpoints());
                    resp.setNotes(order.getNotes());
                    resp.setCreatedAt(order.getCreatedAt());
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
        resp.setVehicleType(order.getVehicleType());
        resp.setCheckpoints(order.getCheckpoints());
        resp.setNotes(order.getNotes());
        resp.setCreatedAt(order.getCreatedAt());
        
        return ServerResponse.successObjectResponse("Order details fetched", HttpStatus.OK, resp, 1);
    }

    @Transactional
    public ServerResponse updateOrderCheckpoints(String orderId, String checkpoints) {
        OrderEntity order = orderRepo.findById(orderId).orElse(null);
        if (order == null) return ServerResponse.failureResponse("Order not found", HttpStatus.NOT_FOUND);
        
        order.setCheckpoints(checkpoints);
        orderRepo.save(order);
        return ServerResponse.successResponse("Checkpoints updated", HttpStatus.OK);
    }

    @Transactional
    public ServerResponse markOrderAsDelivered(String orderId) {
        OrderEntity order = orderRepo.findById(orderId).orElse(null);
        if (order == null) return ServerResponse.failureResponse("Order not found", HttpStatus.NOT_FOUND);
        
        order.setOrderStatus(OrderStatus.DELIVERED);
        order.setDeliveredAt(java.time.LocalDateTime.now());
        orderRepo.save(order);
        return ServerResponse.successResponse("Order delivered successfully", HttpStatus.OK);
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
        if (currentUser == null) return ServerResponse.failureResponse("Unauthorized", HttpStatus.UNAUTHORIZED);

        DeliveryEntity delivery = deliveryRepo.findByUser(currentUser).orElse(null);
        if (delivery == null) return ServerResponse.failureResponse("Delivery profile not found", HttpStatus.NOT_FOUND);

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
                    resp.setVehicleType(order.getVehicleType());
                    resp.setCheckpoints(order.getCheckpoints());
                    resp.setNotes(order.getNotes());
                    resp.setCreatedAt(order.getCreatedAt());
                    return resp;
                })
                .collect(Collectors.toList());

        return ServerResponse.successObjectResponse("Fetched accepted orders", HttpStatus.OK, responses, responses.size());
    }

    public ServerResponse getOrders(java.util.Map<String, String> requestParams, org.springframework.data.domain.Pageable pageable) {
        UserEntity currentUser = UserUtil.getCurrentUser();
        if (currentUser != null) {
            boolean isAdmin = currentUser.getRoles().stream().anyMatch(r -> r.getRoleName().name().equals("ADMIN"));
            if (!isAdmin) {
                if (currentUser.getRoles().stream().anyMatch(r -> r.getRoleName() == com.krishiYatra.krishiYatra.common.enums.RoleType.FARMER)) {
                    requestParams.put("filterFarmerUserId", currentUser.getUserId());
                } else if (currentUser.getRoles().stream().anyMatch(r -> r.getRoleName() == com.krishiYatra.krishiYatra.common.enums.RoleType.DELIVERY)) {
                    requestParams.put("filterDeliveryUserId", currentUser.getUserId());
                } else if (currentUser.getRoles().stream().anyMatch(r -> r.getRoleName() == com.krishiYatra.krishiYatra.common.enums.RoleType.BUYER)) {
                    requestParams.put("filterBuyerUserId", currentUser.getUserId());
                }
            }
        }

        java.util.List<OrderResponse> orders = orderDao.getAllOrders(requestParams, pageable);
        return ServerResponse.successObjectResponse("Orders fetched successfully.", HttpStatus.OK, orders);
    }
}
