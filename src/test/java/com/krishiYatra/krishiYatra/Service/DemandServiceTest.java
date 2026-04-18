package com.krishiYatra.krishiYatra.Service;

import com.krishiYatra.krishiYatra.buyer.BuyerEntity;
import com.krishiYatra.krishiYatra.buyer.BuyerRepo;
import com.krishiYatra.krishiYatra.common.enums.DemandStatus;
import com.krishiYatra.krishiYatra.common.response.ServerResponse;
import com.krishiYatra.krishiYatra.demand.*;
import com.krishiYatra.krishiYatra.demand.dao.IDemandDao;
import com.krishiYatra.krishiYatra.demand.dto.DemandCreateRequest;
import com.krishiYatra.krishiYatra.demand.mapper.DemandMapper;
import com.krishiYatra.krishiYatra.farmer.FarmerEntity;
import com.krishiYatra.krishiYatra.farmer.FarmerRepo;
import com.krishiYatra.krishiYatra.notification.handler.DemandNotificationHandler;
import com.krishiYatra.krishiYatra.stock.category.CategoryEntity;
import com.krishiYatra.krishiYatra.stock.category.CategoryRepo;
import com.krishiYatra.krishiYatra.stock.subCategory.SubCategoryEntity;
import com.krishiYatra.krishiYatra.stock.subCategory.SubCategoryRepo;
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

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DemandServiceTest {

    @Mock private DemandRepo demandRepo;
    @Mock private DemandMapper demandMapper;
    @Mock private CategoryRepo categoryRepo;
    @Mock private SubCategoryRepo subCategoryRepo;
    @Mock private BuyerRepo buyerRepo;
    @Mock private FarmerRepo farmerRepo;
    @Mock private IDemandDao demandDao;
    @Mock private DemandNotificationHandler demandNotificationHandler;

    @InjectMocks
    private DemandService demandService;

    private UserEntity mockUser;
    private BuyerEntity mockBuyer;
    private FarmerEntity mockFarmer;
    private DemandEntity mockDemand;
    private DemandCreateRequest mockRequest;

    @BeforeEach
    void setUp() {
        mockUser = new UserEntity();
        mockUser.setUserId(UUID.randomUUID().toString());
        mockUser.setUsername("testuser");

        mockBuyer = new BuyerEntity();
        mockBuyer.setBuyerId(UUID.randomUUID().toString());
        mockBuyer.setUser(mockUser);

        mockFarmer = new FarmerEntity();
        mockFarmer.setFarmerId(UUID.randomUUID().toString());
        mockFarmer.setUser(mockUser);

        mockDemand = new DemandEntity();
        mockDemand.setDemandId(UUID.randomUUID().toString());
        mockDemand.setBuyer(mockBuyer);
        mockDemand.setStatus(DemandStatus.OPEN);

        mockRequest = new DemandCreateRequest();
        mockRequest.setCategoryId(1);
        mockRequest.setSubCategoryId(1);
        mockRequest.setQuantity(100.0);
    }

    @Test
    void createDemand_Success() {
        try (MockedStatic<UserUtil> utilities = mockStatic(UserUtil.class)) {
            utilities.when(UserUtil::getCurrentUser).thenReturn(mockUser);
            when(buyerRepo.findByUser(mockUser)).thenReturn(Optional.of(mockBuyer));
            when(categoryRepo.findById(any())).thenReturn(Optional.of(new CategoryEntity()));
            when(subCategoryRepo.findById(any())).thenReturn(Optional.of(new SubCategoryEntity()));
            when(demandMapper.toEntity(any(), any(), any(), any())).thenReturn(mockDemand);
            when(demandRepo.save(any())).thenReturn(mockDemand);

            ServerResponse response = demandService.createDemand(mockRequest);

            assertEquals(HttpStatus.CREATED, response.getHttpStatus());
            assertEquals(DemandConst.DEMAND_CREATED, response.getMessage());
            verify(demandRepo).save(any());
            verify(demandNotificationHandler).handleDemandCreated(any());
        }
    }

    @Test
    void createDemand_Unauthorized_Failure() {
        try (MockedStatic<UserUtil> utilities = mockStatic(UserUtil.class)) {
            utilities.when(UserUtil::getCurrentUser).thenReturn(null);

            ServerResponse response = demandService.createDemand(mockRequest);

            assertEquals(HttpStatus.UNAUTHORIZED, response.getHttpStatus());
            assertEquals(DemandConst.UNAUTHORIZED, response.getMessage());
        }
    }

    @Test
    void createDemand_OnlyBuyers_Failure() {
        try (MockedStatic<UserUtil> utilities = mockStatic(UserUtil.class)) {
            utilities.when(UserUtil::getCurrentUser).thenReturn(mockUser);
            when(buyerRepo.findByUser(mockUser)).thenReturn(Optional.empty());

            ServerResponse response = demandService.createDemand(mockRequest);

            assertEquals(HttpStatus.FORBIDDEN, response.getHttpStatus());
            assertEquals(DemandConst.ONLY_BUYERS, response.getMessage());
        }
    }

    @Test
    void getDemands_Success() {
        when(demandDao.getDemands(any(), any())).thenReturn(Collections.emptyList());
        when(demandDao.countDemands(any())).thenReturn(0L);

        ServerResponse response = demandService.getDemands(Collections.emptyMap(), 0, 10);

        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertEquals(DemandConst.DEMAND_FETCHED, response.getMessage());
    }

    @Test
    void cancelDemand_Success() {
        try (MockedStatic<UserUtil> utilities = mockStatic(UserUtil.class)) {
            utilities.when(UserUtil::getCurrentUser).thenReturn(mockUser);
            when(demandRepo.findById(any())).thenReturn(Optional.of(mockDemand));

            ServerResponse response = demandService.cancelDemand(mockDemand.getDemandId());

            assertEquals(HttpStatus.OK, response.getHttpStatus());
            assertEquals(DemandStatus.CANCELLED, mockDemand.getStatus());
            verify(demandRepo).save(mockDemand);
        }
    }

    @Test
    void cancelDemand_NotAuthorized_Failure() {
        UserEntity otherUser = new UserEntity();
        otherUser.setUserId("other-id");
        
        try (MockedStatic<UserUtil> utilities = mockStatic(UserUtil.class)) {
            utilities.when(UserUtil::getCurrentUser).thenReturn(otherUser);
            when(demandRepo.findById(any())).thenReturn(Optional.of(mockDemand));

            ServerResponse response = demandService.cancelDemand(mockDemand.getDemandId());

            assertEquals(HttpStatus.FORBIDDEN, response.getHttpStatus());
            assertEquals(DemandConst.NOT_AUTHORIZED, response.getMessage());
        }
    }

    @Test
    void acceptDemand_Success() {
        try (MockedStatic<UserUtil> utilities = mockStatic(UserUtil.class)) {
            utilities.when(UserUtil::getCurrentUser).thenReturn(mockUser);
            when(farmerRepo.findByUser(mockUser)).thenReturn(Optional.of(mockFarmer));
            when(demandRepo.findById(any())).thenReturn(Optional.of(mockDemand));
            when(demandRepo.save(any())).thenReturn(mockDemand);

            ServerResponse response = demandService.acceptDemand(mockDemand.getDemandId());

            assertEquals(HttpStatus.OK, response.getHttpStatus());
            assertEquals(DemandStatus.ACCEPTED, mockDemand.getStatus());
            assertEquals(mockFarmer, mockDemand.getAcceptedBy());
            verify(demandNotificationHandler).handleDemandAccepted(any(), any());
        }
    }

    @Test
    void acceptDemand_InvalidStatus_Failure() {
        mockDemand.setStatus(DemandStatus.ACCEPTED);
        
        try (MockedStatic<UserUtil> utilities = mockStatic(UserUtil.class)) {
            utilities.when(UserUtil::getCurrentUser).thenReturn(mockUser);
            when(farmerRepo.findByUser(mockUser)).thenReturn(Optional.of(mockFarmer));
            when(demandRepo.findById(any())).thenReturn(Optional.of(mockDemand));

            ServerResponse response = demandService.acceptDemand(mockDemand.getDemandId());

            assertEquals(HttpStatus.BAD_REQUEST, response.getHttpStatus());
            assertEquals(DemandConst.INVALID_STATUS, response.getMessage());
        }
    }
}
