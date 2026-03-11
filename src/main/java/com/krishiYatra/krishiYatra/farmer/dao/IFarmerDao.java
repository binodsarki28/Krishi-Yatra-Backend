package com.krishiYatra.krishiYatra.farmer.dao;

import com.krishiYatra.krishiYatra.farmer.dto.FarmerListResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface IFarmerDao {
    List<FarmerListResponse> getAllFarmers(Map<String, String> allRequestParams, Pageable pageable);
}
