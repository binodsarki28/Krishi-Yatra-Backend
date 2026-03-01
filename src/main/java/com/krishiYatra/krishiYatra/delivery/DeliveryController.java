package com.krishiYatra.krishiYatra.delivery;

import com.krishiYatra.krishiYatra.common.response.ServerResponse;
import com.krishiYatra.krishiYatra.delivery.dto.DeliveryListResponse;
import com.krishiYatra.krishiYatra.delivery.dto.DeliveryDetailResponse;
import com.krishiYatra.krishiYatra.delivery.dto.RegisterDeliveryRequest;
import com.krishiYatra.krishiYatra.delivery.dto.VerifyDeliveryRequest;
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
@RequestMapping("api/v1/delivery")
@Tag(name = "Delivery Controller", description = "Endpoints for delivery partner management and registration")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    @Operation(summary = "Register an existing user as a delivery partner")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Delivery partner registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or user already a partner")
    })
    @PostMapping("/register")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ServerResponse> registerForDelivery(@Valid @RequestBody RegisterDeliveryRequest request) {
        ServerResponse response = deliveryService.registerDelivery(request);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Operation(summary = "Example of a protected delivery endpoint")
    @PostMapping("/dashboard")
    @PreAuthorize("hasAuthority('DELIVERY')")
    public ServerResponse getDeliveryDashboard() {
        return ServerResponse.successResponse(DeliveryConst.DASHBOARD_WELCOME, HttpStatus.OK);
    }

    @Operation(summary = "Verify or reject a delivery partner registration (Admin only)")
    @PostMapping("/verify-delivery")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ServerResponse> verifyDelivery(@Valid @RequestBody VerifyDeliveryRequest request) {
        ServerResponse response = deliveryService.verifyDelivery(request);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Operation(summary = "Get all delivery partner registrations (Admin only)")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<DeliveryListResponse>> getDeliveries(@RequestParam Map<String, String> requestParams, Pageable pageable) {
        return new ResponseEntity<>(deliveryService.getDeliveries(requestParams, pageable), HttpStatus.OK);
    }

    @PostMapping("/block-unblock/{username}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ServerResponse> blockUnblockDelivery(@PathVariable String username, @RequestParam boolean block) {
        return new ResponseEntity<>(deliveryService.blockUnblockDelivery(username, block), HttpStatus.OK);
    }

    @GetMapping("/detail/{username}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<DeliveryDetailResponse> getDeliveryDetail(@PathVariable String username) {
        return new ResponseEntity<>(deliveryService.getDeliveryDetail(username), HttpStatus.OK);
    }
}
