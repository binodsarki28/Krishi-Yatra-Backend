package com.krishiYatra.krishiYatra.demand;

import com.krishiYatra.krishiYatra.common.response.ServerResponse;
import com.krishiYatra.krishiYatra.demand.dto.DemandCreateRequest;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/demand")
@RequiredArgsConstructor
public class DemandController {

    private final DemandService demandService;

    @Operation(summary = "Create a new demand (Buyer only)")
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('BUYER')")
    public ResponseEntity<ServerResponse> createDemand(@RequestBody DemandCreateRequest request) {
        ServerResponse response = demandService.createDemand(request);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Operation(summary = "List all active demands with filters")
    @GetMapping("/list")
    public ResponseEntity<ServerResponse> getDemands(
            @RequestParam Map<String, String> params,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size) {
        ServerResponse response = demandService.getDemands(params, page, size);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Operation(summary = "Get current buyer's demands")
    @GetMapping("/my-demands")
    @PreAuthorize("hasAuthority('BUYER')")
    public ResponseEntity<ServerResponse> getMyDemands(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size) {
        ServerResponse response = demandService.getMyDemands(page, size);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }
    
    @Operation(summary = "Get demands fulfilled by current farmer")
    @GetMapping("/farmer-fulfilled")
    @PreAuthorize("hasAuthority('FARMER')")
    public ResponseEntity<ServerResponse> getFarmerFulfilledDemands(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size) {
        ServerResponse response = demandService.getFarmerFulfilledDemands(page, size);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Operation(summary = "Cancel a demand (Buyer owner only)")
    @PostMapping("/cancel/{demandId}")
    @PreAuthorize("hasAuthority('BUYER')")
    public ResponseEntity<ServerResponse> cancelDemand(@PathVariable String demandId) {
        ServerResponse response = demandService.cancelDemand(demandId);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Operation(summary = "Accept a demand (Farmer only)")
    @PostMapping(value = "/accept/{demandId}")
    @PreAuthorize("hasAuthority('FARMER')")
    public ResponseEntity<ServerResponse> acceptDemand(@PathVariable String demandId) {
        ServerResponse response = demandService.acceptDemand(demandId);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }
}
