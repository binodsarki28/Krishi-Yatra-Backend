package com.krishiYatra.krishiYatra.Service;

import com.krishiYatra.krishiYatra.address.AddressService;
import com.krishiYatra.krishiYatra.buyer.BuyerEntity;
import com.krishiYatra.krishiYatra.buyer.BuyerRepo;
import com.krishiYatra.krishiYatra.common.enums.OrderStatus;
import com.krishiYatra.krishiYatra.common.enums.VerificationStatus;
import com.krishiYatra.krishiYatra.common.response.ServerResponse;
import com.krishiYatra.krishiYatra.delivery.DeliveryEntity;
import com.krishiYatra.krishiYatra.delivery.DeliveryRepo;
import com.krishiYatra.krishiYatra.farmer.FarmerEntity;
import com.krishiYatra.krishiYatra.farmer.FarmerRepo;
import com.krishiYatra.krishiYatra.notification.NotificationService;
import com.krishiYatra.krishiYatra.notification.handler.*;
import com.krishiYatra.krishiYatra.order.*;
import com.krishiYatra.krishiYatra.order.dao.IOrderDao;
import com.krishiYatra.krishiYatra.order.dto.OrderCreateRequest;
import com.krishiYatra.krishiYatra.order.mapper.OrderMapper;
import com.krishiYatra.krishiYatra.stock.StockEntity;
import com.krishiYatra.krishiYatra.stock.StockRepo;
import com.krishiYatra.krishiYatra.user.UserEntity;
import com.krishiYatra.krishiYatra.utils.UserUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock private OrderRepo orderRepo;
    @Mock private StockRepo stockRepo;
    @Mock private BuyerRepo buyerRepo;
    @Mock private DeliveryRepo deliveryRepo;
    @Mock private FarmerRepo farmerRepo;
    @Mock private OrderMapper orderMapper;
    @Mock private AddressService addressService;
    @Mock private IOrderDao orderDao;
    @Mock private NotificationService notificationService;
    
    @Mock private OrderCreatedNotificationHandler orderCreatedNotificationHandler;
    @Mock private OrderAcceptedNotificationHandler orderAcceptedNotificationHandler;
    @Mock private OrderPickedUpNotificationHandler orderPickedUpNotificationHandler;
    @Mock private OrderCheckpointNotificationHandler orderCheckpointNotificationHandler;
    @Mock private OrderDeliveredNotificationHandler orderDeliveredNotificationHandler;
    @Mock private OrderConflictNotificationHandler orderConflictNotificationHandler;
    @Mock private OrderResolvedNotificationHandler orderResolvedNotificationHandler;
    @Mock private OrderCancelledNotificationHandler orderCancelledNotificationHandler;

    @InjectMocks
    private OrderService orderService;

    private UserEntity mockUser;
    private BuyerEntity mockBuyer;
    private FarmerEntity mockFarmer;
    private StockEntity mockStock;
    private OrderEntity mockOrder;
    private OrderCreateRequest mockRequest;

    @BeforeEach
    void setUp() {
        mockUser = new UserEntity();
        mockUser.setUserId(UUID.randomUUID().toString());
        mockUser.setUsername("testuser");

        mockBuyer = new BuyerEntity();
        mockBuyer.setBuyerId(UUID.randomUUID().toString());
        mockBuyer.setUser(mockUser);
        mockBuyer.setStatus(VerificationStatus.VERIFIED);

        UserEntity farmerUser = new UserEntity();
        farmerUser.setUserId(UUID.randomUUID().toString());
        farmerUser.setUsername("farmer-1");

        mockFarmer = new FarmerEntity();
        mockFarmer.setFarmerId(UUID.randomUUID().toString());
        mockFarmer.setUser(farmerUser);
        mockFarmer.setStatus(VerificationStatus.VERIFIED);

        mockStock = new StockEntity();
        mockStock.setStockId(UUID.randomUUID().toString());
        mockStock.setFarmer(mockFarmer);
        mockStock.setActive(true);
        mockStock.setQuantity(100.0);
        mockStock.setMinQuantity(10);
        mockStock.setStockSlug("basmati-rice");

        mockOrder = new OrderEntity();
        mockOrder.setOrderId(UUID.randomUUID().toString());
        mockOrder.setBuyer(mockBuyer);
        mockOrder.setStock(mockStock);
        mockOrder.setOrderQuantity(20.0);
        mockOrder.setOrderStatus(OrderStatus.PENDING);

        mockRequest = new OrderCreateRequest();
        mockRequest.setStockSlug("basmati-rice");
        mockRequest.setOrderQuantity(20.0);
    }

    @Test
    void createOrder_Success() {
        try (MockedStatic<UserUtil> utilities = mockStatic(UserUtil.class)) {
            utilities.when(UserUtil::getCurrentUser).thenReturn(mockUser);
            
            when(stockRepo.findByStockSlug(any())).thenReturn(Optional.of(mockStock));
            when(buyerRepo.findByUser(any())).thenReturn(Optional.of(mockBuyer));
            when(orderMapper.toEntity(any(), any(), any())).thenReturn(mockOrder);
            when(orderRepo.save(any())).thenReturn(mockOrder);

            ServerResponse response = orderService.createOrder(mockRequest);

            assertEquals(HttpStatus.CREATED, response.getHttpStatus());
            assertEquals(OrderConst.CREATE_ORDER, response.getMessage());
            verify(stockRepo).save(mockStock);
            assertEquals(80.0, mockStock.getQuantity());
        }
    }

    @Test
    void createOrder_OwnStock_Failure() {
        try (MockedStatic<UserUtil> utilities = mockStatic(UserUtil.class)) {
            // Buyer is the same as Stock Owner
            utilities.when(UserUtil::getCurrentUser).thenReturn(mockFarmer.getUser());
            when(stockRepo.findByStockSlug(any())).thenReturn(Optional.of(mockStock));

            ServerResponse response = orderService.createOrder(mockRequest);

            assertEquals(HttpStatus.BAD_REQUEST, response.getHttpStatus());
            assertEquals(OrderConst.OWN_STOCK_ORDER, response.getMessage());
        }
    }

    @Test
    void createOrder_InsufficientStock_Failure() {
        try (MockedStatic<UserUtil> utilities = mockStatic(UserUtil.class)) {
            utilities.when(UserUtil::getCurrentUser).thenReturn(mockUser);
            mockRequest.setOrderQuantity(200.0);
            
            when(stockRepo.findByStockSlug(any())).thenReturn(Optional.of(mockStock));
            when(buyerRepo.findByUser(any())).thenReturn(Optional.of(mockBuyer));

            ServerResponse response = orderService.createOrder(mockRequest);

            assertEquals(HttpStatus.BAD_REQUEST, response.getHttpStatus());
            assertEquals(OrderConst.INSUFFICIENT_STOCK, response.getMessage());
        }
    }

    @Test
    void acceptOrderByDelivery_Success() {
        try (MockedStatic<UserUtil> utilities = mockStatic(UserUtil.class)) {
            UserEntity riderUser = new UserEntity();
            riderUser.setUserId("rider-1");
            utilities.when(UserUtil::getCurrentUser).thenReturn(riderUser);

            DeliveryEntity rider = new DeliveryEntity();
            rider.setDeliveryId("del-123");
            rider.setStatus(VerificationStatus.VERIFIED);
            
            when(deliveryRepo.findByUser(any())).thenReturn(Optional.of(rider));
            when(orderRepo.findByOrderIdAndDeliveryIsNull(any())).thenReturn(Optional.of(mockOrder));
            when(orderRepo.save(any())).thenReturn(mockOrder);

            ServerResponse response = orderService.acceptOrderByDelivery(mockOrder.getOrderId());

            assertEquals(HttpStatus.OK, response.getHttpStatus());
            assertEquals(OrderStatus.ACCEPTED, mockOrder.getOrderStatus());
            assertEquals(rider, mockOrder.getDelivery());
        }
    }

    @Test
    void markAsPickedUp_Success() {
        mockOrder.setOrderStatus(OrderStatus.ACCEPTED);
        when(orderRepo.findById(any())).thenReturn(Optional.of(mockOrder));
        when(orderRepo.save(any())).thenReturn(mockOrder);

        ServerResponse response = orderService.markAsPickedUp(mockOrder.getOrderId());

        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertEquals(OrderStatus.SHIPPING, mockOrder.getOrderStatus());
    }

    @Test
    void markOrderAsDelivered_Success() {
        when(orderRepo.findById(any())).thenReturn(Optional.of(mockOrder));
        when(orderRepo.save(any())).thenReturn(mockOrder);

        ServerResponse response = orderService.markOrderAsDelivered(mockOrder.getOrderId());

        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertEquals(OrderStatus.DELIVERED, mockOrder.getOrderStatus());
        assertNotNull(mockOrder.getDeliveredAt());
    }

    @Test
    void cancelOrder_Success() {
        mockOrder.setOrderStatus(OrderStatus.PENDING);
        when(orderRepo.findById(any())).thenReturn(Optional.of(mockOrder));
        when(orderRepo.save(any())).thenReturn(mockOrder);

        ServerResponse response = orderService.cancelOrder(mockOrder.getOrderId());

        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertEquals(OrderStatus.CANCELLED, mockOrder.getOrderStatus());
        // Verify stock restoration (100 + 20)
        assertEquals(120.0, mockStock.getQuantity());
    }

    @Test
    void reportConflict_Success() {
        mockOrder.setOrderStatus(OrderStatus.DELIVERED);
        when(orderRepo.findById(any())).thenReturn(Optional.of(mockOrder));
        when(orderRepo.save(any())).thenReturn(mockOrder);

        ServerResponse response = orderService.reportConflict(mockOrder.getOrderId(), "Damaged product");

        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertEquals(OrderStatus.CONFLICT, mockOrder.getOrderStatus());
        assertEquals("Damaged product", mockOrder.getConflictMessage());
    }

    @Test
    void resolveConflict_Success() {
        mockOrder.setOrderStatus(OrderStatus.CONFLICT);
        when(orderRepo.findById(any())).thenReturn(Optional.of(mockOrder));
        when(orderRepo.save(any())).thenReturn(mockOrder);

        ServerResponse response = orderService.resolveConflict(mockOrder.getOrderId());

        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertEquals(OrderStatus.RESOLVED, mockOrder.getOrderStatus());
    }
}
