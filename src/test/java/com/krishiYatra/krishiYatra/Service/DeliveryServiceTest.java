package com.krishiYatra.krishiYatra.Service;

import com.krishiYatra.krishiYatra.common.enums.RoleType;
import com.krishiYatra.krishiYatra.common.enums.VehicleType;
import com.krishiYatra.krishiYatra.common.enums.VerificationStatus;
import com.krishiYatra.krishiYatra.common.response.ServerResponse;
import com.krishiYatra.krishiYatra.delivery.*;
import com.krishiYatra.krishiYatra.delivery.dto.RegisterDeliveryRequest;
import com.krishiYatra.krishiYatra.delivery.dto.VerifyDeliveryRequest;
import com.krishiYatra.krishiYatra.delivery.mapper.DeliveryMapper;
import com.krishiYatra.krishiYatra.notification.handler.VerificationNotificationHandler;
import com.krishiYatra.krishiYatra.order.OrderRepo;
import com.krishiYatra.krishiYatra.user.RoleEntity;
import com.krishiYatra.krishiYatra.user.RoleRepo;
import com.krishiYatra.krishiYatra.user.UserEntity;
import com.krishiYatra.krishiYatra.user.UserRepo;
import com.krishiYatra.krishiYatra.utils.UserUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeliveryServiceTest {

    @Mock private DeliveryRepo deliveryRepo;
    @Mock private UserRepo userRepo;
    @Mock private RoleRepo roleRepo;
    @Mock private DeliveryMapper deliveryMapper;
    @Mock private OrderRepo orderRepo;
    @Mock private VerificationNotificationHandler verificationNotificationHandler;

    @InjectMocks
    private DeliveryService deliveryService;

    private UserEntity mockUser;
    private RoleEntity deliveryRole;

    @BeforeEach
    void setUp() {
        mockUser = new UserEntity();
        mockUser.setUserId(UUID.randomUUID().toString());
        mockUser.setUsername("deliveryuser");
        mockUser.setRoles(new HashSet<>());

        deliveryRole = new RoleEntity();
        deliveryRole.setRoleName(RoleType.DELIVERY);
    }

    @Test
    void registerDelivery_Success() {
        try (MockedStatic<UserUtil> utilities = mockStatic(UserUtil.class)) {
            utilities.when(UserUtil::getCurrentUser).thenReturn(mockUser);

            RegisterDeliveryRequest request = new RegisterDeliveryRequest();
            request.setVehicleType(VehicleType.VAN);
            request.setVehicleBrand("Tata");
            request.setNumberPlate("BA 1 PA 1234");
            request.setLicenseNumber("01-01-0012345678");

            DeliveryEntity deliveryEntity = new DeliveryEntity();

            when(userRepo.findByUsername(any())).thenReturn(Optional.of(mockUser));
            when(deliveryMapper.toEntity(any())).thenReturn(deliveryEntity);
            when(roleRepo.findByRoleName(RoleType.DELIVERY)).thenReturn(Optional.of(deliveryRole));

            ServerResponse response = deliveryService.registerDelivery(request);

            assertEquals(HttpStatus.CREATED, response.getHttpStatus());
            assertEquals(DeliveryConst.REGISTRATION_SUCCESS, response.getMessage());
            assertTrue(mockUser.getRoles().contains(deliveryRole));
            verify(deliveryRepo).save(any());
        }
    }

    @Test
    void registerDelivery_AlreadyExists_Failure() {
        try (MockedStatic<UserUtil> utilities = mockStatic(UserUtil.class)) {
            utilities.when(UserUtil::getCurrentUser).thenReturn(mockUser);
            mockUser.getRoles().add(deliveryRole);

            when(userRepo.findByUsername(any())).thenReturn(Optional.of(mockUser));

            ServerResponse response = deliveryService.registerDelivery(new RegisterDeliveryRequest());

            assertEquals(HttpStatus.BAD_REQUEST, response.getHttpStatus());
            assertEquals(DeliveryConst.ALREADY_DELIVERY, response.getMessage());
        }
    }

    @Test
    void verifyDelivery_Approval_Success() {
        VerifyDeliveryRequest request = new VerifyDeliveryRequest();
        request.setUsername("deliveryuser");
        request.setApproved(true);

        DeliveryEntity delivery = new DeliveryEntity();
        delivery.setUser(mockUser);
        delivery.setStatus(VerificationStatus.PENDING);

        when(deliveryRepo.findByUser_Username(any())).thenReturn(Optional.of(delivery));

        ServerResponse response = deliveryService.verifyDelivery(request);

        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertEquals(VerificationStatus.VERIFIED, delivery.getStatus());
        verify(verificationNotificationHandler).notifyDeliveryStatus(any(), eq(true), any());
    }

    @Test
    void verifyDelivery_Rejection_Success() {
        VerifyDeliveryRequest request = new VerifyDeliveryRequest();
        request.setUsername("deliveryuser");
        request.setApproved(false);
        request.setReason("Invalid license");

        DeliveryEntity delivery = new DeliveryEntity();
        delivery.setUser(mockUser);
        mockUser.getRoles().add(deliveryRole);

        when(deliveryRepo.findByUser_Username(any())).thenReturn(Optional.of(delivery));
        when(userRepo.findByUsername(any())).thenReturn(Optional.of(mockUser));

        ServerResponse response = deliveryService.verifyDelivery(request);

        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertFalse(mockUser.getRoles().contains(deliveryRole));
        verify(deliveryRepo).delete(any());
        verify(verificationNotificationHandler).notifyDeliveryStatus(any(), eq(false), any());
    }

    @Test
    void blockDelivery_Success() {
        DeliveryEntity delivery = new DeliveryEntity();
        delivery.setUser(mockUser);
        when(deliveryRepo.findByUser_Username(any())).thenReturn(Optional.of(delivery));

        ServerResponse response = deliveryService.blockUnblockDelivery("deliveryuser", true, "Expired license");

        assertEquals(VerificationStatus.BLOCKED, delivery.getStatus());
        assertTrue(delivery.getStatusMessage().contains("Expired license"));
        assertFalse(delivery.getUser().isActive());
    }

    @Test
    void getDeliveryDashboard_Success() {
        try (MockedStatic<UserUtil> utilities = mockStatic(UserUtil.class)) {
            utilities.when(UserUtil::getCurrentUser).thenReturn(mockUser);

            DeliveryEntity delivery = new DeliveryEntity();
            delivery.setDeliveryId("d-123");
            when(deliveryRepo.findByUser_Username(any())).thenReturn(Optional.of(delivery));

            when(orderRepo.countByDelivery(any())).thenReturn(30L);
            when(orderRepo.countByDeliveryAndOrderStatus(any(), any())).thenReturn(10L);
            when(orderRepo.countByDeliveryAndOrderStatusIn(any(), any())).thenReturn(20L);
            when(orderRepo.sumDeliveryFeeByDelivery(any())).thenReturn(5000.0);
            when(orderRepo.deliveryEarningsTrend(any())).thenReturn(new java.util.ArrayList<>());

            ServerResponse response = deliveryService.getDeliveryDashboard();

            assertEquals(HttpStatus.OK, response.getHttpStatus());
            assertNotNull(response.getResponse());
            assertEquals(DeliveryConst.DASHBOARD_WELCOME, response.getMessage());
        }
    }
}
