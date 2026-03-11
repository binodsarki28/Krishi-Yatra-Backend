package com.krishiYatra.krishiYatra.farmer.mapper;

import com.krishiYatra.krishiYatra.farmer.FarmerEntity;
import com.krishiYatra.krishiYatra.farmer.dto.FarmerListResponse;
import com.krishiYatra.krishiYatra.farmer.dto.FarmerDetailResponse;
import com.krishiYatra.krishiYatra.farmer.dto.RegisterFarmerRequest;

public interface FarmerMapper {
    FarmerEntity toEntity(RegisterFarmerRequest request);
    FarmerListResponse toResponse(FarmerEntity farmer);
    FarmerDetailResponse toDetailResponse(FarmerEntity farmer);
}
