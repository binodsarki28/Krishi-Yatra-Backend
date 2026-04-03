package com.krishiYatra.krishiYatra.order;

import com.krishiYatra.krishiYatra.common.response.ServerResponse;
import com.krishiYatra.krishiYatra.order.dto.OrderCreateRequest;
import io.swagger.v3.oas.annotations.Operation;
import java.util.Map;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @Operation(summary = "Get list of orders dynamically mapped to current user")
    @GetMapping("/list")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ServerResponse> getOrders(
            @RequestParam Map<String, String> requestParams,
            org.springframework.data.domain.Pageable pageable) {
        ServerResponse response = orderService.getOrders(requestParams, pageable);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Operation(summary = "Create order (Buyer only)")
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('BUYER')")
    public ResponseEntity<ServerResponse> createOrder(@Valid @RequestBody OrderCreateRequest request) {
        ServerResponse response = orderService.createOrder(request);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Operation(summary = "Delivery accepts an order")
    @PostMapping("/delivery/accept/{orderId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ServerResponse> acceptOrderByDelivery(@PathVariable String orderId) {
        ServerResponse response = orderService.acceptOrderByDelivery(orderId);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Operation(summary = "Get all pending orders available for delivery")
    @GetMapping("/pending")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ServerResponse> getPendingOrders() {
        ServerResponse response = orderService.getPendingOrders();
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Operation(summary = "Get farmer address for a stock")
    @GetMapping("/farmer-address/{stockSlug}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ServerResponse> getFarmerAddress(@PathVariable String stockSlug) {
        ServerResponse response = orderService.getFarmerAddressByStockSlug(stockSlug);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Operation(summary = "Get order details by ID")
    @GetMapping("/detail/{orderId}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ServerResponse> getOrderDetails(@PathVariable String orderId) {
        ServerResponse response = orderService.getOrderById(orderId);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Operation(summary = "Update order checkpoints")
    @PostMapping("/update-checkpoints/{orderId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ServerResponse> updateCheckpoints(@PathVariable String orderId, @RequestBody Map<String, String> body) {
        String checkpoints = body.getOrDefault("checkpoints", "");
        ServerResponse response = orderService.updateOrderCheckpoints(orderId, checkpoints);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Operation(summary = "Mark order as delivered")
    @PostMapping("/mark-as-delivered/{orderId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ServerResponse> markAsDelivered(@PathVariable String orderId) {
        ServerResponse response = orderService.markOrderAsDelivered(orderId);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Operation(summary = "Mark order as picked up")
    @PostMapping("/mark-as-picked-up/{orderId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ServerResponse> markAsPickedUp(@PathVariable String orderId) {
        ServerResponse response = orderService.markAsPickedUp(orderId);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Operation(summary = "Get all orders accepted by current delivery linker")
    @GetMapping("/linker/accepted")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ServerResponse> getMyAcceptedOrders() {
        ServerResponse response = orderService.getMyAcceptedOrders();
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Operation(summary = "Cancel an order")
    @PutMapping("/cancel/{orderId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ServerResponse> cancelOrder(@PathVariable String orderId) {
        ServerResponse response = orderService.cancelOrder(orderId);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Operation(summary = "Report a conflict on an order")
    @PostMapping("/report-conflict/{orderId}")
    @PreAuthorize("hasAuthority('BUYER')")
    public ResponseEntity<ServerResponse> reportConflict(@PathVariable String orderId, @RequestBody Map<String, String> body) {
        String message = body.get("conflictMessage");
        ServerResponse response = orderService.reportConflict(orderId, message);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Operation(summary = "Resolve a conflict on an order (Admin only)")
    @PutMapping("/resolve-conflict/{orderId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ServerResponse> resolveConflict(@PathVariable String orderId) {
        ServerResponse response = orderService.resolveConflict(orderId);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Operation(summary = "Get buyer orders with pagination and sorting")
    @GetMapping("/buyer/my-orders")
    @PreAuthorize("hasAuthority('BUYER')")
    public ResponseEntity<ServerResponse> getOrdersByBuyer(
            @RequestParam Map<String, String> requestParams,
            org.springframework.data.domain.Pageable pageable) {
        ServerResponse response = orderService.getOrdersByBuyer(requestParams, pageable);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Operation(summary = "Get farmer orders with pagination and sorting")
    @GetMapping("/farmer/my-orders")
    @PreAuthorize("hasAuthority('FARMER')")
    public ResponseEntity<ServerResponse> getOrdersByFarmer(
            @RequestParam Map<String, String> requestParams, Pageable pageable) {
        ServerResponse response = orderService.getOrdersByFarmer(requestParams, pageable);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Operation(summary = "Get delivery orders with pagination and sorting")
    @GetMapping("/delivery/my-orders")
    @PreAuthorize("hasAuthority('DELIVERY')")
    public ResponseEntity<ServerResponse> getOrdersByDelivery(
            @RequestParam Map<String, String> requestParams, Pageable pageable) {
        ServerResponse response = orderService.getOrdersByDelivery(requestParams, pageable);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }
}
