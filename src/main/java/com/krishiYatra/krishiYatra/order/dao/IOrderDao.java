package com.krishiYatra.krishiYatra.order.dao;

import com.krishiYatra.krishiYatra.order.dto.OrderResponse;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Map;

public interface IOrderDao {
    List<OrderResponse> getAllOrders(Map<String, String> params, Pageable pageable);
}
