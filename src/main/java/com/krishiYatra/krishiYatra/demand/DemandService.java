package com.krishiYatra.krishiYatra.demand;

import com.krishiYatra.krishiYatra.buyer.BuyerEntity;
import com.krishiYatra.krishiYatra.buyer.BuyerRepo;
import com.krishiYatra.krishiYatra.common.enums.DemandStatus;
import com.krishiYatra.krishiYatra.common.response.ServerResponse;
import com.krishiYatra.krishiYatra.demand.dao.IDemandDao;
import com.krishiYatra.krishiYatra.demand.dto.DemandCreateRequest;
import com.krishiYatra.krishiYatra.demand.dto.DemandResponse;
import com.krishiYatra.krishiYatra.demand.mapper.DemandMapper;
import com.krishiYatra.krishiYatra.farmer.FarmerEntity;
import com.krishiYatra.krishiYatra.farmer.FarmerRepo;
import com.krishiYatra.krishiYatra.stock.category.CategoryEntity;
import com.krishiYatra.krishiYatra.stock.category.CategoryRepo;
import com.krishiYatra.krishiYatra.stock.subCategory.SubCategoryEntity;
import com.krishiYatra.krishiYatra.stock.subCategory.SubCategoryRepo;
import com.krishiYatra.krishiYatra.notification.handler.DemandNotificationHandler;
import com.krishiYatra.krishiYatra.user.UserEntity;
import com.krishiYatra.krishiYatra.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class DemandService {

    private final DemandRepo demandRepo;
    private final DemandMapper demandMapper;
    private final CategoryRepo categoryRepo;
    private final SubCategoryRepo subCategoryRepo;
    private final BuyerRepo buyerRepo;
    private final FarmerRepo farmerRepo;
    private final IDemandDao demandDao;
    private final DemandNotificationHandler demandNotificationHandler;

    @Transactional
    public ServerResponse createDemand(DemandCreateRequest request) {
        UserEntity currentUser = UserUtil.getCurrentUser();
        if (currentUser == null) return ServerResponse.failureResponse("Unauthorized", HttpStatus.UNAUTHORIZED);

        BuyerEntity buyer = buyerRepo.findByUser(currentUser).orElse(null);
        if (buyer == null) return ServerResponse.failureResponse(DemandConst.ONLY_BUYERS, HttpStatus.FORBIDDEN);

        CategoryEntity category = categoryRepo.findById(request.getCategoryGuid()).orElse(null);
        if (category == null) return ServerResponse.failureResponse("Category not found", HttpStatus.NOT_FOUND);

        SubCategoryEntity subCategory = subCategoryRepo.findById(request.getSubCategoryGuid()).orElse(null);
        if (subCategory == null) return ServerResponse.failureResponse("Sub-category not found", HttpStatus.NOT_FOUND);

        DemandEntity entity = demandMapper.toEntity(request, category, subCategory, buyer);
        entity.setStatus(DemandStatus.OPEN);
        entity.setActive(true);
        DemandEntity saved = demandRepo.save(entity);

        // Notify all verified farmers
        demandNotificationHandler.handleDemandCreated(saved);

        return ServerResponse.successObjectResponse(DemandConst.DEMAND_CREATED, HttpStatus.CREATED, demandMapper.toResponse(saved));
    }

    public ServerResponse getDemands(Map<String, String> params, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<DemandResponse> list = demandDao.getDemands(params, pageable);
        long count = demandDao.countDemands(params);
        return ServerResponse.successObjectResponse(DemandConst.DEMAND_FETCHED, HttpStatus.OK, list, (int) count);
    }

    public ServerResponse getMyDemands(int page, int size) {
        UserEntity currentUser = UserUtil.getCurrentUser();
        BuyerEntity buyer = buyerRepo.findByUser(currentUser).orElse(null);
        if (buyer == null) return ServerResponse.failureResponse(DemandConst.ONLY_BUYERS, HttpStatus.FORBIDDEN);

        Map<String, String> params = Map.of("buyerGuid", buyer.getBuyerId(), "active", "true");
        Pageable pageable = PageRequest.of(page, size);
        List<DemandResponse> list = demandDao.getDemands(params, pageable);
        long count = demandDao.countDemands(params);
        return ServerResponse.successObjectResponse(DemandConst.DEMAND_MY_FETCHED, HttpStatus.OK, list, (int) count);
    }
    
    public ServerResponse getFarmerFulfilledDemands(int page, int size) {
        UserEntity currentUser = UserUtil.getCurrentUser();
        FarmerEntity farmer = farmerRepo.findByUser(currentUser).orElse(null);
        if (farmer == null) return ServerResponse.failureResponse(DemandConst.ONLY_FARMERS, HttpStatus.FORBIDDEN);
        
        Map<String, String> params = Map.of("farmerGuid", farmer.getFarmerId(), "active", "true");
        Pageable pageable = PageRequest.of(page, size);
        List<DemandResponse> list = demandDao.getDemands(params, pageable);
        long count = demandDao.countDemands(params);
        return ServerResponse.successObjectResponse(DemandConst.DEMAND_FETCHED, HttpStatus.OK, list, (int) count);
    }

    @Transactional
    public ServerResponse cancelDemand(String demandId) {
        UserEntity currentUser = UserUtil.getCurrentUser();
        DemandEntity demand = demandRepo.findById(demandId).orElse(null);
        if (demand == null) return ServerResponse.failureResponse(DemandConst.DEMAND_NOT_FOUND, HttpStatus.NOT_FOUND);

        if (!demand.getBuyer().getUser().getUserId().equals(currentUser.getUserId())) {
            return ServerResponse.failureResponse(DemandConst.NOT_AUTHORIZED, HttpStatus.FORBIDDEN);
        }

        if (demand.getStatus() != DemandStatus.OPEN) {
            return ServerResponse.failureResponse(DemandConst.INVALID_STATUS, HttpStatus.BAD_REQUEST);
        }

        demand.setStatus(DemandStatus.CANCELLED);
        demand.setActive(false);
        demandRepo.save(demand);
        return ServerResponse.successResponse(DemandConst.DEMAND_CANCELLED, HttpStatus.OK);
    }

    @Transactional
    public ServerResponse acceptDemand(String demandId) {
        UserEntity currentUser = UserUtil.getCurrentUser();
        FarmerEntity farmer = farmerRepo.findByUser(currentUser).orElse(null);
        if (farmer == null) return ServerResponse.failureResponse(DemandConst.ONLY_FARMERS, HttpStatus.FORBIDDEN);

        DemandEntity demand = demandRepo.findById(demandId).orElse(null);
        if (demand == null) return ServerResponse.failureResponse(DemandConst.DEMAND_NOT_FOUND, HttpStatus.NOT_FOUND);

        if (demand.getStatus() != DemandStatus.OPEN) {
            return ServerResponse.failureResponse(DemandConst.INVALID_STATUS, HttpStatus.BAD_REQUEST);
        }

        demand.setStatus(DemandStatus.ACCEPTED);
        demand.setAcceptedBy(farmer);
        DemandEntity saved = demandRepo.save(demand);

        // Notify Buyer and Farmer
        demandNotificationHandler.handleDemandAccepted(saved, farmer);

        return ServerResponse.successObjectResponse(DemandConst.DEMAND_ACCEPTED, HttpStatus.OK, demandMapper.toResponse(saved));
    }
}
