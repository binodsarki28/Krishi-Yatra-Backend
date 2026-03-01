package com.krishiYatra.krishiYatra.stock;

import com.krishiYatra.krishiYatra.common.response.ServerResponse;
import com.krishiYatra.krishiYatra.stock.dto.CreateStockDto;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/stock")
public class StockController {

    private final StockService stockService;

    public  StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @Operation(summary = "create the stock by the farmer.")
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('FARMER')")
    public ResponseEntity<ServerResponse> createStock(@Valid @RequestBody CreateStockDto createStockDto) {
        ServerResponse response = stockService.createStock(createStockDto);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

}
