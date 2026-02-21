package com.krishiYatra.krishiYatra.buyer.mapper;

import com.krishiYatra.krishiYatra.buyer.BuyerEntity;
import com.krishiYatra.krishiYatra.buyer.dto.RegisterBuyerRequest;

public interface BuyerMapper {
    BuyerEntity toEntity(RegisterBuyerRequest request);
}
