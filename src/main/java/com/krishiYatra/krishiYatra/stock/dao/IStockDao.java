package com.krishiYatra.krishiYatra.stock.dao;

import com.krishiYatra.krishiYatra.stock.dto.StockListResponse;
import java.util.List;
import java.util.Map;

public interface IStockDao {
    List<StockListResponse> getAllStocks(Map<String, String> params);
}
