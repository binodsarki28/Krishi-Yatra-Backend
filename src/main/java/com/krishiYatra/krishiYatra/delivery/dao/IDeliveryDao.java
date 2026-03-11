package com.krishiYatra.krishiYatra.delivery.dao;

import com.krishiYatra.krishiYatra.delivery.dto.DeliveryListResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface IDeliveryDao {
    List<DeliveryListResponse> getAllDeliveries(Map<String, String> allRequestParams, Pageable pageable);
}
