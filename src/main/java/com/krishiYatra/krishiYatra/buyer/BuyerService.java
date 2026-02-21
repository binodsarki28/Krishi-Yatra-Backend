package com.krishiYatra.krishiYatra.buyer;

import com.krishiYatra.krishiYatra.buyer.dto.RegisterBuyerRequest;
import com.krishiYatra.krishiYatra.buyer.dto.VerifyBuyerRequest;
import com.krishiYatra.krishiYatra.buyer.mapper.BuyerMapper;
import com.krishiYatra.krishiYatra.common.enums.RoleType;
import com.krishiYatra.krishiYatra.common.response.ServerResponse;
import com.krishiYatra.krishiYatra.user.RoleRepo;
import com.krishiYatra.krishiYatra.user.UserEntity;
import com.krishiYatra.krishiYatra.user.UserRepo;
import com.krishiYatra.krishiYatra.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BuyerService {

    private final BuyerRepo buyerRepo;
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final BuyerMapper buyerMapper;

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
        BuyerEntity buyer = buyerRepo.findById(request.getBuyerId())
                .orElseThrow(() -> new RuntimeException(BuyerConst.REGISTRATION_NOT_FOUND));

        if (request.getApproved()) {
            buyer.setVerified(true);
            buyerRepo.save(buyer);
            return ServerResponse.successResponse(BuyerConst.VERIFICATION_SUCCESS, HttpStatus.OK);
        } else {
            UserEntity user = buyer.getUser();
            buyerRepo.delete(buyer);
            
            roleRepo.findByRoleName(RoleType.BUYER).ifPresent(role -> {
                user.getRoles().remove(role);
                userRepo.save(user);
            });

            String message = BuyerConst.REJECTION_PREFIX + request.getReason();
            log.info(message);
            return ServerResponse.successResponse(message, HttpStatus.OK);
        }
    }
}
