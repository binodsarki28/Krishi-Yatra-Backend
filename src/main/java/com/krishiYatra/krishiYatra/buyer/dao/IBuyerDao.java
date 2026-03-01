package com.krishiYatra.krishiYatra.buyer.dao;

import com.krishiYatra.krishiYatra.buyer.dto.BuyerListResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface IBuyerDao {
    List<BuyerListResponse> getAllBuyers(Map<String, String> allRequestParams, Pageable pageable);
}
