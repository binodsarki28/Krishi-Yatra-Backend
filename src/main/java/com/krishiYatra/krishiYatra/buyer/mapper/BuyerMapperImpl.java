package com.krishiYatra.krishiYatra.buyer.mapper;

import com.krishiYatra.krishiYatra.buyer.BuyerEntity;
import com.krishiYatra.krishiYatra.buyer.dto.RegisterBuyerRequest;
import org.springframework.stereotype.Service;

@Service
public class BuyerMapperImpl implements BuyerMapper {

    @Override
    public BuyerEntity toEntity(RegisterBuyerRequest request) {
        if (request == null) {
            return null;
        }
        BuyerEntity buyer = new BuyerEntity();
        buyer.setConsumerType(request.getConsumerType());
        buyer.setBusinessName(request.getBusinessName());
        buyer.setBusinessLocation(request.getBusinessLocation());
        buyer.setVerified(false);
        return buyer;
    }
}
