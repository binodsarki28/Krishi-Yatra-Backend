package com.krishiYatra.krishiYatra.farmer;

import com.krishiYatra.krishiYatra.common.response.ServerResponse;
import com.krishiYatra.krishiYatra.farmer.dto.FarmerListResponse;
import com.krishiYatra.krishiYatra.farmer.dto.FarmerDetailResponse;
import com.krishiYatra.krishiYatra.farmer.dto.RegisterFarmerRequest;
import com.krishiYatra.krishiYatra.farmer.dto.VerifyFarmerRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/farmer")
@Tag(name = "Farmer Controller", description = "Endpoints for farmer management and registration")
public class FarmerController {

    private final FarmerService farmerService;

    public FarmerController(FarmerService farmerService) {
        this.farmerService = farmerService;
    }

    @Operation(summary = "Register an existing user as a farmer")
    @PostMapping("/register")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ServerResponse> registerForFarmer(@Valid @RequestBody RegisterFarmerRequest request) {
        ServerResponse response = farmerService.registerFarmer(request);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Operation(summary = "Example of a protected farmer endpoint")
    @PostMapping("/dashboard")
    @PreAuthorize("hasAuthority('FARMER')")
    public ServerResponse getFarmerDashboard() {
        return ServerResponse.successResponse(FarmerConst.DASHBOARD_WELCOME, HttpStatus.OK);
    }

    @Operation(summary = "Verify or reject a farmer registration (Admin only)")
    @PostMapping("/verify-farmer")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ServerResponse> verifyFarmer(@Valid @RequestBody VerifyFarmerRequest request) {
        ServerResponse response = farmerService.verifyFarmer(request);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<FarmerListResponse>> getFarmers(@RequestParam Map<String, String> requestParams, Pageable pageable) {
        return new ResponseEntity<>(farmerService.getFarmers(requestParams, pageable), HttpStatus.OK);
    }

    @PostMapping("/block-unblock/{username}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ServerResponse> blockUnblockFarmer(@PathVariable String username, @RequestParam boolean block, @RequestParam(required = false) String reason) {
        return new ResponseEntity<>(farmerService.blockUnblockFarmer(username, block, reason), HttpStatus.OK);
    }

    @GetMapping("/detail/{username}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<FarmerDetailResponse> getFarmerDetail(@PathVariable String username) {
        return new ResponseEntity<>(farmerService.getFarmerDetail(username), HttpStatus.OK);
    }
}
