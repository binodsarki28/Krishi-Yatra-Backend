package com.krishiYatra.krishiYatra.address;

import com.krishiYatra.krishiYatra.address.dto.AddressRequest;
import com.krishiYatra.krishiYatra.common.response.ServerResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/address")
@RequiredArgsConstructor
public class AddressController {
    private final AddressService addressService;

    @Operation(summary = "Save or update the logged-in user's address")
    @PostMapping("/save")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ServerResponse> saveAddress(@Valid @RequestBody AddressRequest request) {
        ServerResponse response = addressService.saveOrUpdateAddress(request);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Operation(summary = "Get the logged-in user's address")
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ServerResponse> getMyAddress() {
        ServerResponse response = addressService.getMyAddress();
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Operation(summary = "Delete the logged-in user's address")
    @DeleteMapping("/delete")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ServerResponse> deleteAddress() {
        ServerResponse response = addressService.deleteMyAddress();
        return new ResponseEntity<>(response, response.getHttpStatus());
    }
}
