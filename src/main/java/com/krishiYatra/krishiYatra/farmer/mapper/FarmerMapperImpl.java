package com.krishiYatra.krishiYatra.farmer.mapper;

import com.krishiYatra.krishiYatra.farmer.FarmerEntity;
import com.krishiYatra.krishiYatra.farmer.dto.FarmerListResponse;
import com.krishiYatra.krishiYatra.farmer.dto.FarmerDetailResponse;
import com.krishiYatra.krishiYatra.farmer.dto.RegisterFarmerRequest;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.stream.Collectors;

@Service
public class FarmerMapperImpl implements FarmerMapper {

    @Override
    public FarmerEntity toEntity(RegisterFarmerRequest request) {
        if (request == null) {
            return null;
        }
        FarmerEntity farmer = new FarmerEntity();
        
        // Convert List<FarmType> to Comma-Separated String
        if (request.getTypes() != null && !request.getTypes().isEmpty()) {
            String typesStr = request.getTypes().stream()
                    .map(Enum::name)
                    .collect(Collectors.joining(","));
            farmer.setFarmTypes(typesStr);
        }

        farmer.setFarmName(request.getFarmName());
        farmer.setFarmLocation(request.getFarmLocation());
        farmer.setFarmArea(request.getFarmArea());
        farmer.setVerified(false);
        return farmer;
    }

    @Override
    public FarmerListResponse toResponse(FarmerEntity farmer) {
        if (farmer == null) {
            return null;
        }
        FarmerListResponse response = new FarmerListResponse();
        response.setVerified(farmer.isVerified());

        if (farmer.getUser() != null) {
            response.setFullName(farmer.getUser().getFullName());
            response.setUsername(farmer.getUser().getUsername());
            response.setActive(farmer.getUser().isActive());
        }

        return response;
    }

    @Override
    public FarmerDetailResponse toDetailResponse(FarmerEntity farmer) {
        if (farmer == null) {
            return null;
        }
        FarmerDetailResponse response = new FarmerDetailResponse();
        response.setFarmerId(farmer.getFarmerId());
        response.setFarmName(farmer.getFarmName());
        response.setFarmLocation(farmer.getFarmLocation());
        response.setFarmArea(farmer.getFarmArea());
        response.setVerified(farmer.isVerified());
        response.setCreatedAt(farmer.getCreatedAt());

        if (farmer.getFarmTypes() != null) {
            response.setFarmTypes(Arrays.asList(farmer.getFarmTypes().split(",")));
        }

        if (farmer.getUser() != null) {
            response.setFullName(farmer.getUser().getFullName());
            response.setUsername(farmer.getUser().getUsername());
            response.setEmail(farmer.getUser().getEmail());
            response.setPhoneNumber(farmer.getUser().getPhoneNumber());
            response.setActive(farmer.getUser().isActive());
        }

        return response;
    }
}
