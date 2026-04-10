package com.krishiYatra.krishiYatra.buyer;

import com.krishiYatra.krishiYatra.buyer.dao.IBuyerDao;
import com.krishiYatra.krishiYatra.buyer.dto.BuyerListResponse;
import com.krishiYatra.krishiYatra.buyer.dto.BuyerDetailResponse;
import com.krishiYatra.krishiYatra.buyer.dto.RegisterBuyerRequest;
import com.krishiYatra.krishiYatra.buyer.dto.VerifyBuyerRequest;
import com.krishiYatra.krishiYatra.buyer.mapper.BuyerMapper;
import com.krishiYatra.krishiYatra.common.enums.RoleType;
import com.krishiYatra.krishiYatra.common.enums.VerificationStatus;
import com.krishiYatra.krishiYatra.common.response.ServerResponse;
import com.krishiYatra.krishiYatra.notification.handler.VerificationNotificationHandler;
import com.krishiYatra.krishiYatra.user.RoleRepo;
import com.krishiYatra.krishiYatra.user.UserEntity;
import com.krishiYatra.krishiYatra.user.UserRepo;
import com.krishiYatra.krishiYatra.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class BuyerService {

    private final BuyerRepo buyerRepo;
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final BuyerMapper buyerMapper;

    private final IBuyerDao buyerDao;
    private final VerificationNotificationHandler verificationNotificationHandler;

    @Transactional(readOnly = true)
    public List<BuyerListResponse> getBuyers(Map<String, String> params, Pageable pageable) {
        return buyerDao.getAllBuyers(params, pageable);
    }

    @Transactional
    public ServerResponse registerBuyer(RegisterBuyerRequest request) {
        UserEntity user = UserUtil.getCurrentUser();

        if (user == null) {
            return ServerResponse.failureResponse(BuyerConst.USER_NOT_AUTHENTICATED, HttpStatus.UNAUTHORIZED);
        }

        UserEntity managedUser = userRepo.findByUsername(user.getUsername())
                .orElseThrow(() -> new RuntimeException(BuyerConst.USER_NOT_FOUND));

        if (managedUser.getRoles().stream().anyMatch(r -> r.getRoleName() == RoleType.BUYER)) {
            return ServerResponse.failureResponse(BuyerConst.ALREADY_BUYER, HttpStatus.BAD_REQUEST);
        }

        BuyerEntity buyer = buyerMapper.toEntity(request);
        buyer.setUser(managedUser);
        buyerRepo.save(buyer);

        // Add Buyer role to user
        roleRepo.findByRoleName(RoleType.BUYER).ifPresent(role -> {
            managedUser.getRoles().add(role);
            userRepo.save(managedUser);
        });

        return ServerResponse.successResponse(BuyerConst.REGISTRATION_SUCCESS, HttpStatus.CREATED);
    }

    @Transactional
    public ServerResponse verifyBuyer(VerifyBuyerRequest request) {
        BuyerEntity buyer = buyerRepo.findByUser_Username(request.getUsername())
                .orElseThrow(() -> new RuntimeException(BuyerConst.REGISTRATION_NOT_FOUND));

        if (request.getApproved()) {
            buyer.setStatus(VerificationStatus.VERIFIED);
            buyerRepo.save(buyer);

            // Notify buyer
            try {
                verificationNotificationHandler.notifyBuyerStatus(buyer.getUser(), true, null);
            } catch (Exception e) {
                log.error("Failed to send buyer verification notification: {}", e.getMessage());
            }

            return ServerResponse.successResponse(BuyerConst.VERIFICATION_SUCCESS, HttpStatus.OK);
        } else {
            // Store user for notification
            UserEntity user = buyer.getUser();

            // If rejected, delete the buyer entity so they can re-apply
            buyerRepo.delete(buyer);

            // Notify buyer
            try {
                verificationNotificationHandler.notifyBuyerStatus(user, false, request.getReason());
            } catch (Exception e) {
                log.error("Failed to send buyer rejection notification: {}", e.getMessage());
            }
            
            String message = BuyerConst.REJECTION_PREFIX + request.getReason();
            log.info(message);
            return ServerResponse.successResponse(message, HttpStatus.OK);
        }
    }

    @Transactional
    public ServerResponse blockUnblockBuyer(String username, boolean block, String reason) {
        BuyerEntity buyer = buyerRepo.findByUser_Username(username)
                .orElseThrow(() -> new RuntimeException(BuyerConst.REGISTRATION_NOT_FOUND));
        
        if (block) {
            buyer.setStatus(VerificationStatus.BLOCKED);
            buyer.setStatusMessage(reason);
        } else {
            buyer.setStatus(VerificationStatus.VERIFIED);
            buyer.setStatusMessage(null);
        }
        
        UserEntity user = buyer.getUser();
        user.setActive(!block);
        userRepo.save(user);
        buyerRepo.save(buyer);
        
        String action = block ? "blocked" : "unblocked";
        return ServerResponse.successResponse("Buyer " + action + " successfully", HttpStatus.OK);
    }


    @Transactional(readOnly = true)
    public BuyerDetailResponse getBuyerDetail(String username) {
        BuyerEntity buyer = buyerRepo.findByUser_Username(username)
                .orElseThrow(() -> new RuntimeException(BuyerConst.REGISTRATION_NOT_FOUND));
        
        return buyerMapper.toDetailResponse(buyer);
    }
}
