package com.krishiYatra.krishiYatra.address;

import com.krishiYatra.krishiYatra.address.dto.AddressRequest;
import com.krishiYatra.krishiYatra.address.dto.AddressResponse;
import com.krishiYatra.krishiYatra.common.response.ServerResponse;
import com.krishiYatra.krishiYatra.user.UserEntity;
import com.krishiYatra.krishiYatra.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class AddressService {
    private final AddressRepo addressRepo;

    @Transactional
    public ServerResponse saveOrUpdateAddress(AddressRequest request) {
        UserEntity currentUser = UserUtil.getCurrentUser();
        if (currentUser == null) {
            return ServerResponse.failureResponse("User not found.", HttpStatus.UNAUTHORIZED);
        }

        Optional<AddressEntity> existingOpt = addressRepo.findByUser(currentUser);
        AddressEntity entity;
        if (existingOpt.isPresent()) {
            entity = existingOpt.get();
        } else {
            entity = new AddressEntity();
            entity.setUser(currentUser);
        }

        entity.setProvince(request.getProvince());
        entity.setDistrict(request.getDistrict());
        entity.setMunicipality(request.getMunicipality());
        entity.setWardNo(request.getWardNo());
        entity.setStreetName(request.getStreetName());

        addressRepo.save(entity);
        return ServerResponse.successObjectResponse("Address saved successfully.", HttpStatus.OK, toResponse(entity));
    }

    public ServerResponse getMyAddress() {
        UserEntity currentUser = UserUtil.getCurrentUser();
        if (currentUser == null) {
            return ServerResponse.failureResponse("User not found.", HttpStatus.UNAUTHORIZED);
        }

        Optional<AddressEntity> addressOpt = addressRepo.findByUser(currentUser);
        if (addressOpt.isEmpty()) {
            return ServerResponse.failureResponse("No address found.", HttpStatus.NOT_FOUND);
        }

        AddressEntity addr = addressOpt.get();
        AddressResponse response = toResponse(addr);
        return ServerResponse.successObjectResponse("Address fetched.", HttpStatus.OK, response);
    }

    @Transactional
    public ServerResponse deleteMyAddress() {
        UserEntity currentUser = UserUtil.getCurrentUser();
        if (currentUser == null) {
            return ServerResponse.failureResponse("User not found.", HttpStatus.UNAUTHORIZED);
        }

        Optional<AddressEntity> addressOpt = addressRepo.findByUser(currentUser);
        if (addressOpt.isEmpty()) {
            return ServerResponse.failureResponse("No address found.", HttpStatus.NOT_FOUND);
        }

        // Use custom delete query to avoid Hibernate dirty-check update issues
        addressRepo.deleteByUser(currentUser);
        
        return ServerResponse.successResponse("Address deleted successfully.", HttpStatus.OK);
    }

    public AddressResponse getAddressByUser(UserEntity user) {
        Optional<AddressEntity> addressOpt = addressRepo.findByUser(user);
        return addressOpt.map(this::toResponse).orElse(null);
    }

    private AddressResponse toResponse(AddressEntity addr) {
        String fullAddress = Stream.of(
                addr.getStreetName(),
                "Ward " + addr.getWardNo(),
                addr.getMunicipality(),
                addr.getDistrict(),
                addr.getProvince()
        ).filter(s -> s != null && !s.isBlank())
         .collect(Collectors.joining(", "));

        return AddressResponse.builder()
                .province(addr.getProvince())
                .district(addr.getDistrict())
                .municipality(addr.getMunicipality())
                .wardNo(addr.getWardNo())
                .streetName(addr.getStreetName())
                .fullAddress(fullAddress)
                .build();
    }
}
