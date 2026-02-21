package com.krishiYatra.krishiYatra.delivery;

import com.krishiYatra.krishiYatra.common.enums.RoleType;
import com.krishiYatra.krishiYatra.common.response.ServerResponse;
import com.krishiYatra.krishiYatra.delivery.dto.RegisterDeliveryRequest;
import com.krishiYatra.krishiYatra.delivery.dto.VerifyDeliveryRequest;
import com.krishiYatra.krishiYatra.delivery.mapper.DeliveryMapper;
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
public class DeliveryService {

    private final DeliveryRepo deliveryRepo;
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final DeliveryMapper deliveryMapper;

    @Transactional
    public ServerResponse registerDelivery(RegisterDeliveryRequest request) {
        UserEntity user = UserUtil.getCurrentUser();

        if (user == null) {
            return ServerResponse.failureResponse(DeliveryConst.USER_NOT_AUTHENTICATED, HttpStatus.UNAUTHORIZED);
        }

        UserEntity managedUser = userRepo.findByUsername(user.getUsername())
                .orElseThrow(() -> new RuntimeException(DeliveryConst.USER_NOT_FOUND));

        if (managedUser.getRoles().stream().anyMatch(r -> r.getRoleName() == RoleType.DELIVERY)) {
            return ServerResponse.failureResponse(DeliveryConst.ALREADY_DELIVERY, HttpStatus.BAD_REQUEST);
        }

        DeliveryEntity delivery = deliveryMapper.toEntity(request);
        delivery.setUser(managedUser);
        deliveryRepo.save(delivery);

        // Add Delivery role to user
        roleRepo.findByRoleName(RoleType.DELIVERY).ifPresent(role -> {
            managedUser.getRoles().add(role);
            userRepo.save(managedUser);
        });

        return ServerResponse.successResponse(DeliveryConst.REGISTRATION_SUCCESS, HttpStatus.CREATED);
    }

    @Transactional
    public ServerResponse verifyDelivery(VerifyDeliveryRequest request) {
        DeliveryEntity delivery = deliveryRepo.findById(request.getDeliveryId())
                .orElseThrow(() -> new RuntimeException(DeliveryConst.REGISTRATION_NOT_FOUND));

        if (request.getApproved()) {
            delivery.setVerified(true);
            deliveryRepo.save(delivery);
            return ServerResponse.successResponse(DeliveryConst.VERIFICATION_SUCCESS, HttpStatus.OK);
        } else {
            UserEntity user = delivery.getUser();
            deliveryRepo.delete(delivery);
            
            roleRepo.findByRoleName(RoleType.DELIVERY).ifPresent(role -> {
                user.getRoles().remove(role);
                userRepo.save(user);
            });

            String message = DeliveryConst.REJECTION_PREFIX + request.getReason();
            log.info(message);
            return ServerResponse.successResponse(message, HttpStatus.OK);
        }
    }
}
