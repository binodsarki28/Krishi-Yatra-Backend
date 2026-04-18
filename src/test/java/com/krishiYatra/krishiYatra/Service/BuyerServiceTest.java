package com.krishiYatra.krishiYatra.Service;

import com.krishiYatra.krishiYatra.buyer.*;
import com.krishiYatra.krishiYatra.buyer.dto.RegisterBuyerRequest;
import com.krishiYatra.krishiYatra.buyer.dto.VerifyBuyerRequest;
import com.krishiYatra.krishiYatra.buyer.mapper.BuyerMapper;
import com.krishiYatra.krishiYatra.common.enums.ConsumerType;
import com.krishiYatra.krishiYatra.common.enums.RoleType;
import com.krishiYatra.krishiYatra.common.enums.VerificationStatus;
import com.krishiYatra.krishiYatra.common.response.ServerResponse;
import com.krishiYatra.krishiYatra.demand.DemandRepo;
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
public class BuyerServiceTest {

    @Mock private BuyerRepo buyerRepo;
    @Mock private UserRepo userRepo;
    @Mock private RoleRepo roleRepo;
    @Mock private BuyerMapper buyerMapper;
    @Mock private OrderRepo orderRepo;
    @Mock private DemandRepo demandRepo;
    @Mock private VerificationNotificationHandler verificationNotificationHandler;

    @InjectMocks
    private BuyerService buyerService;

    private UserEntity mockUser;
    private RoleEntity buyerRole;

    @BeforeEach
    void setUp() {
        mockUser = new UserEntity();
        mockUser.setUserId(UUID.randomUUID().toString());
        mockUser.setUsername("buyeruser");
        mockUser.setRoles(new HashSet<>());

        buyerRole = new RoleEntity();
        buyerRole.setRoleName(RoleType.BUYER);
    }

    @Test
    void registerBuyer_Success() {
        try (MockedStatic<UserUtil> utilities = mockStatic(UserUtil.class)) {
            utilities.when(UserUtil::getCurrentUser).thenReturn(mockUser);

            RegisterBuyerRequest request = new RegisterBuyerRequest();
            request.setConsumerType(ConsumerType.RETAILER);
            request.setBusinessName("Mart");
            request.setBusinessLocation("Kathmandu");
            
            BuyerEntity buyerEntity = new BuyerEntity();

            when(userRepo.findByUsername(any())).thenReturn(Optional.of(mockUser));
            when(buyerMapper.toEntity(any())).thenReturn(buyerEntity);
            when(roleRepo.findByRoleName(RoleType.BUYER)).thenReturn(Optional.of(buyerRole));

            ServerResponse response = buyerService.registerBuyer(request);

            assertEquals(HttpStatus.CREATED, response.getHttpStatus());
            assertEquals(BuyerConst.REGISTRATION_SUCCESS, response.getMessage());
            assertTrue(mockUser.getRoles().contains(buyerRole));
            verify(buyerRepo).save(any());
        }
    }

    @Test
    void registerBuyer_AlreadyExists_Failure() {
        try (MockedStatic<UserUtil> utilities = mockStatic(UserUtil.class)) {
            utilities.when(UserUtil::getCurrentUser).thenReturn(mockUser);
            mockUser.getRoles().add(buyerRole);

            when(userRepo.findByUsername(any())).thenReturn(Optional.of(mockUser));

            ServerResponse response = buyerService.registerBuyer(new RegisterBuyerRequest());

            assertEquals(HttpStatus.BAD_REQUEST, response.getHttpStatus());
            assertEquals(BuyerConst.ALREADY_BUYER, response.getMessage());
        }
    }

    @Test
    void verifyBuyer_Approval_Success() {
        VerifyBuyerRequest request = new VerifyBuyerRequest();
        request.setUsername("buyeruser");
        request.setApproved(true);

        BuyerEntity buyer = new BuyerEntity();
        buyer.setUser(mockUser);
        buyer.setStatus(VerificationStatus.PENDING);

        when(buyerRepo.findByUser_Username(any())).thenReturn(Optional.of(buyer));

        ServerResponse response = buyerService.verifyBuyer(request);

        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertEquals(VerificationStatus.VERIFIED, buyer.getStatus());
        verify(verificationNotificationHandler).notifyBuyerStatus(any(), eq(true), any());
    }

    @Test
    void verifyBuyer_Rejection_Success() {
        VerifyBuyerRequest request = new VerifyBuyerRequest();
        request.setUsername("buyeruser");
        request.setApproved(false);
        request.setReason("Invalid documents");

        BuyerEntity buyer = new BuyerEntity();
        buyer.setUser(mockUser);
        mockUser.getRoles().add(buyerRole);

        when(buyerRepo.findByUser_Username(any())).thenReturn(Optional.of(buyer));
        when(userRepo.findByUsername(any())).thenReturn(Optional.of(mockUser));

        ServerResponse response = buyerService.verifyBuyer(request);

        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertFalse(mockUser.getRoles().contains(buyerRole));
        verify(buyerRepo).delete(any());
        verify(verificationNotificationHandler).notifyBuyerStatus(any(), eq(false), any());
    }

    @Test
    void blockBuyer_Success() {
        BuyerEntity buyer = new BuyerEntity();
        buyer.setUser(mockUser);
        when(buyerRepo.findByUser_Username(any())).thenReturn(Optional.of(buyer));

        ServerResponse response = buyerService.blockUnblockBuyer("buyeruser", true, "Violation of rules");

        assertEquals(VerificationStatus.BLOCKED, buyer.getStatus());
        assertTrue(buyer.getStatusMessage().contains("Violation of rules"));
        assertFalse(buyer.getUser().isActive());
    }

    @Test
    void getBuyerDashboard_Success() {
        try (MockedStatic<UserUtil> utilities = mockStatic(UserUtil.class)) {
            utilities.when(UserUtil::getCurrentUser).thenReturn(mockUser);

            BuyerEntity buyer = new BuyerEntity();
            buyer.setBuyerId("b-123");
            when(buyerRepo.findByUser_Username(any())).thenReturn(Optional.of(buyer));

            when(orderRepo.countByBuyer(any())).thenReturn(20L);
            when(orderRepo.countByBuyerAndOrderStatus(any(), any())).thenReturn(5L);
            when(orderRepo.sumTotalPriceByBuyer(any())).thenReturn(10000.0);
            when(demandRepo.countByBuyer(any())).thenReturn(3L);
            when(orderRepo.buyerSpendingTrend(any())).thenReturn(new java.util.ArrayList<>());

            ServerResponse response = buyerService.getBuyerDashboard();

            assertEquals(HttpStatus.OK, response.getHttpStatus());
            assertNotNull(response.getResponse());
            assertEquals(BuyerConst.DASHBOARD_WELCOME, response.getMessage());
        }
    }
}
