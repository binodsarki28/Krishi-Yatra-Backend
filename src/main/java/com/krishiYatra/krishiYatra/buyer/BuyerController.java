package com.krishiYatra.krishiYatra.buyer;

import com.krishiYatra.krishiYatra.buyer.dto.BuyerListResponse;
import com.krishiYatra.krishiYatra.buyer.dto.BuyerDetailResponse;
import com.krishiYatra.krishiYatra.buyer.dto.RegisterBuyerRequest;
import com.krishiYatra.krishiYatra.buyer.dto.VerifyBuyerRequest;
import com.krishiYatra.krishiYatra.common.response.ServerResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/buyer")
@Tag(name = "Buyer Controller", description = "Endpoints for buyer management and registration")
@RequiredArgsConstructor
public class BuyerController {

    private final BuyerService buyerService;

    @Operation(summary = "Register an existing user as a buyer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Buyer registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or user already a buyer")
    })
    @PostMapping("/register")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ServerResponse> registerForBuyer(@Valid @RequestBody RegisterBuyerRequest request) {
        ServerResponse response = buyerService.registerBuyer(request);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Operation(summary = "Get buyer dashboard data")
    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('BUYER')")
    public ServerResponse getBuyerDashboard() {
        return buyerService.getBuyerDashboard();
    }

    @Operation(summary = "Verify or reject a buyer registration (Admin only)")
    @PostMapping("/verify-buyer")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ServerResponse> verifyBuyer(@Valid @RequestBody VerifyBuyerRequest request) {
        ServerResponse response = buyerService.verifyBuyer(request);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Operation(summary = "Get all buyer registrations (Admin only)")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<BuyerListResponse>> getBuyers(@RequestParam Map<String, String> requestParams, Pageable pageable) {
        return new ResponseEntity<>(buyerService.getBuyers(requestParams, pageable), HttpStatus.OK);
    }

    @PostMapping("/block-unblock/{username}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ServerResponse> blockUnblockBuyer(@PathVariable String username, @RequestParam boolean block, @RequestParam(required = false) String reason) {
        return new ResponseEntity<>(buyerService.blockUnblockBuyer(username, block, reason), HttpStatus.OK);
    }

    @GetMapping("/detail/{username}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<BuyerDetailResponse> getBuyerDetail(@PathVariable String username) {
        return new ResponseEntity<>(buyerService.getBuyerDetail(username), HttpStatus.OK);
    }
}
