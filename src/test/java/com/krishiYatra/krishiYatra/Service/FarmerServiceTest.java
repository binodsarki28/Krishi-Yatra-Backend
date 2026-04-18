package com.krishiYatra.krishiYatra.Service;

import com.krishiYatra.krishiYatra.common.enums.RoleType;
import com.krishiYatra.krishiYatra.common.enums.VerificationStatus;
import com.krishiYatra.krishiYatra.common.response.ServerResponse;
import com.krishiYatra.krishiYatra.demand.DemandRepo;
import com.krishiYatra.krishiYatra.farmer.*;
import com.krishiYatra.krishiYatra.farmer.dao.IFarmerDao;
import com.krishiYatra.krishiYatra.farmer.dto.RegisterFarmerRequest;
import com.krishiYatra.krishiYatra.farmer.dto.VerifyFarmerRequest;
import com.krishiYatra.krishiYatra.farmer.mapper.FarmerMapper;
import com.krishiYatra.krishiYatra.notification.handler.VerificationNotificationHandler;
import com.krishiYatra.krishiYatra.order.OrderRepo;
import com.krishiYatra.krishiYatra.stock.StockRepo;
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
public class FarmerServiceTest {

    @Mock private FarmerRepo farmerRepo;
    @Mock private UserRepo userRepo;
    @Mock private RoleRepo roleRepo;
    @Mock private FarmerMapper farmerMapper;
    @Mock private IFarmerDao farmerDao;
    @Mock private StockRepo stockRepo;
    @Mock private OrderRepo orderRepo;
    @Mock private DemandRepo demandRepo;
    @Mock private VerificationNotificationHandler verificationNotificationHandler;

    @InjectMocks
    private FarmerService farmerService;

    private UserEntity mockUser;
    private RoleEntity farmerRole;

    @BeforeEach
    void setUp() {
        mockUser = new UserEntity();
        mockUser.setUserId(UUID.randomUUID().toString());
        mockUser.setUsername("farmeruser");
        mockUser.setRoles(new HashSet<>());

        farmerRole = new RoleEntity();
        farmerRole.setRoleName(RoleType.FARMER);
    }

    @Test
    void registerFarmer_Success() {
        try (MockedStatic<UserUtil> utilities = mockStatic(UserUtil.class)) {
            utilities.when(UserUtil::getCurrentUser).thenReturn(mockUser);
            
            RegisterFarmerRequest request = new RegisterFarmerRequest();
            FarmerEntity farmerEntity = new FarmerEntity();

            when(userRepo.findByUsername(any())).thenReturn(Optional.of(mockUser));
            when(farmerRepo.findByUser(any())).thenReturn(Optional.empty());
            when(farmerMapper.toEntity(any())).thenReturn(farmerEntity);
            when(roleRepo.findByRoleName(RoleType.FARMER)).thenReturn(Optional.of(farmerRole));

            ServerResponse response = farmerService.registerFarmer(request);

            assertEquals(HttpStatus.CREATED, response.getHttpStatus());
            assertEquals(FarmerConst.REGISTRATION_SUCCESS, response.getMessage());
            assertTrue(mockUser.getRoles().contains(farmerRole));
            verify(farmerRepo).save(any());
        }
    }

    @Test
    void registerFarmer_AlreadyExists_SyncRoles_Success() {
        try (MockedStatic<UserUtil> utilities = mockStatic(UserUtil.class)) {
            utilities.when(UserUtil::getCurrentUser).thenReturn(mockUser);
            
            when(userRepo.findByUsername(any())).thenReturn(Optional.of(mockUser));
            when(farmerRepo.findByUser(any())).thenReturn(Optional.of(new FarmerEntity()));
            when(roleRepo.findByRoleName(RoleType.FARMER)).thenReturn(Optional.of(farmerRole));

            ServerResponse response = farmerService.registerFarmer(new RegisterFarmerRequest());

            assertEquals(HttpStatus.OK, response.getHttpStatus());
            assertTrue(mockUser.getRoles().contains(farmerRole));
        }
    }

    @Test
    void verifyFarmer_Approval_Success() {
        VerifyFarmerRequest request = new VerifyFarmerRequest();
        request.setUsername("farmeruser");
        request.setApproved(true);

        FarmerEntity farmer = new FarmerEntity();
        farmer.setUser(mockUser);
        farmer.setStatus(VerificationStatus.PENDING);

        when(farmerRepo.findByUser_Username(any())).thenReturn(Optional.of(farmer));

        ServerResponse response = farmerService.verifyFarmer(request);

        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertEquals(VerificationStatus.VERIFIED, farmer.getStatus());
        verify(verificationNotificationHandler).notifyFarmerStatus(any(), eq(true), any());
    }

    @Test
    void verifyFarmer_Rejection_Success() {
        VerifyFarmerRequest request = new VerifyFarmerRequest();
        request.setUsername("farmeruser");
        request.setApproved(false);
        request.setReason("Incomplete Docs");

        FarmerEntity farmer = new FarmerEntity();
        farmer.setUser(mockUser);
        mockUser.getRoles().add(farmerRole);

        when(farmerRepo.findByUser_Username(any())).thenReturn(Optional.of(farmer));
        when(userRepo.findByUsername(any())).thenReturn(Optional.of(mockUser));

        ServerResponse response = farmerService.verifyFarmer(request);

        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertFalse(mockUser.getRoles().contains(farmerRole));
        verify(farmerRepo).delete(any());
        verify(verificationNotificationHandler).notifyFarmerStatus(any(), eq(false), any());
    }

    @Test
    void blockFarmer_Success() {
        FarmerEntity farmer = new FarmerEntity();
        when(farmerRepo.findByUser_Username(any())).thenReturn(Optional.of(farmer));

        ServerResponse response = farmerService.blockUnblockFarmer("farmeruser", true, "Bad behavior");

        assertEquals(VerificationStatus.BLOCKED, farmer.getStatus());
        assertTrue(farmer.getStatusMessage().contains("Bad behavior"));
    }

    @Test
    void getFarmerDashboard_Success() {
        try (MockedStatic<UserUtil> utilities = mockStatic(UserUtil.class)) {
            utilities.when(UserUtil::getCurrentUser).thenReturn(mockUser);
            
            FarmerEntity farmer = new FarmerEntity();
            farmer.setFarmerId("f-123");
            when(farmerRepo.findByUser_Username(any())).thenReturn(Optional.of(farmer));
            
            when(stockRepo.countByFarmer(any())).thenReturn(10L);
            when(orderRepo.countByFarmer(any())).thenReturn(5L);
            when(orderRepo.sumTotalPriceByFarmer(any())).thenReturn(5000.0);
            when(stockRepo.countStocksByCategory(any())).thenReturn(new java.util.ArrayList<>());
            when(orderRepo.farmerRevenueTrend(any())).thenReturn(new java.util.ArrayList<>());

            ServerResponse response = farmerService.getFarmerDashboard();

            assertEquals(HttpStatus.OK, response.getHttpStatus());
            assertNotNull(response.getResponse());
        }
    }
}
