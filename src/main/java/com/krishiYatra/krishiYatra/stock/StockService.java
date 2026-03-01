package com.krishiYatra.krishiYatra.stock;

import com.krishiYatra.krishiYatra.common.response.ServerResponse;
import com.krishiYatra.krishiYatra.stock.dto.CreateStockDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class StockService {

    private final StockRepo stockRepo;

    public StockService(StockRepo stockRepo) {
        this.stockRepo = stockRepo;
    }

    public ServerResponse createStock(CreateStockDto createStockDto) {
        return ServerResponse.successResponse(StockConst.CREATE_STOCK, HttpStatus.CREATED);
    }
}
