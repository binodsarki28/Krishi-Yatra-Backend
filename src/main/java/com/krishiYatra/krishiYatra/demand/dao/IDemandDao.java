package com.krishiYatra.krishiYatra.demand.dao;

import com.krishiYatra.krishiYatra.demand.dto.DemandResponse;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Map;

public interface IDemandDao {
    List<DemandResponse> getDemands(Map<String, String> params, Pageable pageable);
    long countDemands(Map<String, String> params);
}
