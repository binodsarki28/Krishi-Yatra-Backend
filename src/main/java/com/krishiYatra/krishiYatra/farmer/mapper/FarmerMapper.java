package com.krishiYatra.krishiYatra.farmer.mapper;

import com.krishiYatra.krishiYatra.farmer.FarmerEntity;
import com.krishiYatra.krishiYatra.farmer.dto.FarmerResponse;
import com.krishiYatra.krishiYatra.farmer.dto.RegisterFarmerRequest;

public interface FarmerMapper {
    FarmerEntity toEntity(RegisterFarmerRequest request);
    FarmerResponse toResponse(FarmerEntity farmer);
}
