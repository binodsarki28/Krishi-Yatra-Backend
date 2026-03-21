package com.krishiYatra.krishiYatra.buyer.mapper;

import com.krishiYatra.krishiYatra.buyer.BuyerEntity;
import com.krishiYatra.krishiYatra.buyer.dto.BuyerListResponse;
import com.krishiYatra.krishiYatra.buyer.dto.BuyerDetailResponse;
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
        buyer.setStatus(com.krishiYatra.krishiYatra.common.enums.VerificationStatus.PENDING);
        return buyer;
    }

    @Override
    public BuyerListResponse toResponse(BuyerEntity buyer) {
        if (buyer == null) {
            return null;
        }
        BuyerListResponse response = new BuyerListResponse();
        response.setStatus(buyer.getStatus());

        if (buyer.getUser() != null) {
            response.setFullName(buyer.getUser().getFullName());
            response.setUsername(buyer.getUser().getUsername());
            response.setActive(buyer.getUser().isActive());
        }

        return response;
    }

    @Override
    public BuyerDetailResponse toDetailResponse(BuyerEntity buyer) {
        if (buyer == null) {
            return null;
        }
        BuyerDetailResponse response = new BuyerDetailResponse();
        response.setBuyerId(buyer.getBuyerId());
        response.setConsumerType(buyer.getConsumerType());
        response.setBusinessName(buyer.getBusinessName());
        response.setBusinessLocation(buyer.getBusinessLocation());
        response.setStatus(buyer.getStatus());
        response.setCreatedAt(buyer.getCreatedAt());

        if (buyer.getUser() != null) {
            response.setFullName(buyer.getUser().getFullName());
            response.setUsername(buyer.getUser().getUsername());
            response.setEmail(buyer.getUser().getEmail());
            response.setPhoneNumber(buyer.getUser().getPhoneNumber());
            response.setActive(buyer.getUser().isActive());
        }

        return response;
    }
}
