package com.krishiYatra.krishiYatra.delivery;

import com.krishiYatra.krishiYatra.common.enums.RoleType;
import com.krishiYatra.krishiYatra.common.enums.VerificationStatus;
import com.krishiYatra.krishiYatra.common.response.ServerResponse;
import com.krishiYatra.krishiYatra.delivery.dao.IDeliveryDao;
import com.krishiYatra.krishiYatra.delivery.dto.DeliveryListResponse;
import com.krishiYatra.krishiYatra.delivery.dto.DeliveryDetailResponse;
import com.krishiYatra.krishiYatra.delivery.dto.RegisterDeliveryRequest;
import com.krishiYatra.krishiYatra.delivery.dto.VerifyDeliveryRequest;
import com.krishiYatra.krishiYatra.delivery.mapper.DeliveryMapper;
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
public class DeliveryService {

    private final DeliveryRepo deliveryRepo;
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final DeliveryMapper deliveryMapper;

    private final IDeliveryDao deliveryDao;
    private final VerificationNotificationHandler verificationNotificationHandler;

    @Transactional(readOnly = true)
    public List<DeliveryListResponse> getDeliveries(Map<String, String> params, Pageable pageable) {
        return deliveryDao.getAllDeliveries(params, pageable);
    }

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
        DeliveryEntity delivery = deliveryRepo.findByUser_Username(request.getUsername())
                .orElseThrow(() -> new RuntimeException(DeliveryConst.REGISTRATION_NOT_FOUND));

        if (request.getApproved()) {
            delivery.setStatus(VerificationStatus.VERIFIED);
            deliveryRepo.save(delivery);

            // Notify Rider
            try {
                verificationNotificationHandler.notifyDeliveryStatus(delivery.getUser(), true, null);
            } catch (Exception e) {
                log.error("Failed to send delivery verification notification: {}", e.getMessage());
            }

            return ServerResponse.successResponse(DeliveryConst.VERIFICATION_SUCCESS, HttpStatus.OK);
        } else {
            // Store user for notification
            UserEntity user = delivery.getUser();

            // If rejected, delete the delivery entity so they can re-apply
            deliveryRepo.delete(delivery);

            // Notify Rider
            try {
                verificationNotificationHandler.notifyDeliveryStatus(user, false, request.getReason());
            } catch (Exception e) {
                log.error("Failed to send delivery rejection notification: {}", e.getMessage());
            }
            
            String message = DeliveryConst.REJECTION_PREFIX + request.getReason();
            log.info(message);
            return ServerResponse.successResponse(message, HttpStatus.OK);
        }
    }

    @Transactional
    public ServerResponse blockUnblockDelivery(String username, boolean block, String reason) {
        DeliveryEntity delivery = deliveryRepo.findByUser_Username(username)
                .orElseThrow(() -> new RuntimeException(DeliveryConst.REGISTRATION_NOT_FOUND));
        
        if (block) {
            delivery.setStatus(VerificationStatus.BLOCKED);
            delivery.setStatusMessage(reason);
        } else {
            delivery.setStatus(VerificationStatus.VERIFIED);
            delivery.setStatusMessage(null);
        }
        
        UserEntity user = delivery.getUser();
        user.setActive(!block);
        userRepo.save(user);
        deliveryRepo.save(delivery);
        
        String action = block ? "blocked" : "unblocked";
        return ServerResponse.successResponse("Delivery partner " + action + " successfully", HttpStatus.OK);
    }


    @Transactional(readOnly = true)
    public DeliveryDetailResponse getDeliveryDetail(String username) {
        DeliveryEntity delivery = deliveryRepo.findByUser_Username(username)
                .orElseThrow(() -> new RuntimeException(DeliveryConst.REGISTRATION_NOT_FOUND));
        
        return deliveryMapper.toDetailResponse(delivery);
    }
}
