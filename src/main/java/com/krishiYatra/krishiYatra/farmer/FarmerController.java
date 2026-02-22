package com.krishiYatra.krishiYatra.farmer;

import com.krishiYatra.krishiYatra.common.response.ServerResponse;
import com.krishiYatra.krishiYatra.farmer.dto.FarmerResponse;
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

import java.util.List;

@RestController
@RequestMapping("api/v1/farmer")
@Tag(name = "Farmer Controller", description = "Endpoints for farmer management and registration")
public class FarmerController {

    private final FarmerService farmerService;

    public FarmerController(FarmerService farmerService) {
        this.farmerService = farmerService;
    }

    @Operation(summary = "Register an existing user as a farmer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Farmer registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or user already a farmer"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User lacks required authorities")
    })
    @PostMapping("/register")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ServerResponse> registerForFarmer(@Valid @RequestBody RegisterFarmerRequest request) {
        ServerResponse response = farmerService.registerFarmer(request);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Operation(summary = "Example of a protected farmer endpoint")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Requires FARMER role")
    })
    @PostMapping("/dashboard")
    @PreAuthorize("hasAuthority('FARMER')")
    public ServerResponse getFarmerDashboard() {
        return ServerResponse.successResponse(FarmerConst.DASHBOARD_WELCOME, HttpStatus.OK);
    }

    @Operation(summary = "Verify or reject a farmer registration (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Farmer status updated successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role")
    })
    @PostMapping("/verify-farmer")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ServerResponse> verifyFarmer(@Valid @RequestBody VerifyFarmerRequest request) {
        ServerResponse response = farmerService.verifyFarmer(request);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @GetMapping("/unverified")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<FarmerResponse>> getUnverifiedFarmers() {
        return new ResponseEntity<>(farmerService.getUnverifiedFarmers(), HttpStatus.OK);
    }
}
