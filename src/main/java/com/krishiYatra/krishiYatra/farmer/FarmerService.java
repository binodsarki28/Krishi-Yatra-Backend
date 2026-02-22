package com.krishiYatra.krishiYatra.farmer;

import com.krishiYatra.krishiYatra.common.enums.RoleType;
import com.krishiYatra.krishiYatra.common.response.ServerResponse;
import com.krishiYatra.krishiYatra.farmer.dto.FarmerResponse;
import com.krishiYatra.krishiYatra.farmer.dto.RegisterFarmerRequest;
import com.krishiYatra.krishiYatra.farmer.dto.VerifyFarmerRequest;
import com.krishiYatra.krishiYatra.farmer.mapper.FarmerMapper;
import com.krishiYatra.krishiYatra.user.RoleRepo;
import com.krishiYatra.krishiYatra.user.UserEntity;
import com.krishiYatra.krishiYatra.user.UserRepo;
import com.krishiYatra.krishiYatra.utils.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FarmerService {

    private final FarmerRepo farmerRepo;
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final FarmerMapper farmerMapper;

    public FarmerService(FarmerRepo farmerRepo,
                         UserRepo userRepo,
                         RoleRepo roleRepo,
                         FarmerMapper farmerMapper) {
        this.farmerRepo = farmerRepo;
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.farmerMapper = farmerMapper;
    }

    @Transactional(readOnly = true)
    public List<FarmerResponse> getUnverifiedFarmers() {
        return farmerRepo.findByIsVerifiedFalse().stream()
                .map(farmerMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ServerResponse registerFarmer(RegisterFarmerRequest request) {
        UserEntity user = UserUtil.getCurrentUser();

        if (user == null) {
            return ServerResponse.failureResponse(FarmerConst.USER_NOT_AUTHENTICATED, HttpStatus.UNAUTHORIZED);
        }

        UserEntity managedUser = userRepo.findByUsername(user.getUsername())
                .orElseThrow(() -> new RuntimeException(FarmerConst.USER_NOT_FOUND));

        if (managedUser.getRoles().stream().anyMatch(r -> r.getRoleName() == RoleType.FARMER)) {
            return ServerResponse.failureResponse(FarmerConst.ALREADY_FARMER, HttpStatus.BAD_REQUEST);
        }

        FarmerEntity farmer = farmerMapper.toEntity(request);
        farmer.setUser(managedUser);
        farmerRepo.save(farmer);

        // Add Farmer role to user
        roleRepo.findByRoleName(RoleType.FARMER).ifPresent(role -> {
            managedUser.getRoles().add(role);
            userRepo.save(managedUser);
        });

        return ServerResponse.successResponse(FarmerConst.REGISTRATION_SUCCESS, HttpStatus.CREATED);
    }

    @Transactional
    public ServerResponse verifyFarmer(VerifyFarmerRequest request) {
        FarmerEntity farmer = farmerRepo.findById(request.getFarmerId())
                .orElseThrow(() -> new RuntimeException(FarmerConst.REGISTRATION_NOT_FOUND));

        if (request.getApproved()) {
            farmer.setVerified(true);
            farmerRepo.save(farmer);
            return ServerResponse.successResponse(FarmerConst.VERIFICATION_SUCCESS, HttpStatus.OK);
        } else {
            // If rejected, delete the farmer entity
            UserEntity user = farmer.getUser();
            farmerRepo.delete(farmer);

            // Remove FARMER role from user if they were rejected
            roleRepo.findByRoleName(RoleType.FARMER).ifPresent(role -> {
                user.getRoles().remove(role);
                userRepo.save(user);
            });

            String message = FarmerConst.REJECTION_PREFIX + request.getReason();
            log.info(message);
            return ServerResponse.successResponse(message, HttpStatus.OK);
        }
    }
}
