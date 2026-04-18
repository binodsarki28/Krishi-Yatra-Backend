package com.krishiYatra.krishiYatra.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.krishiYatra.krishiYatra.common.enums.VehicleType;
import com.krishiYatra.krishiYatra.common.response.ServerResponse;
import com.krishiYatra.krishiYatra.config.security.jwt.JwtAuthenticationFilter;
import com.krishiYatra.krishiYatra.config.security.jwt.JwtTokenProvider;
import com.krishiYatra.krishiYatra.config.security.service.CustomUserDetailsService;
import com.krishiYatra.krishiYatra.delivery.DeliveryConst;
import com.krishiYatra.krishiYatra.delivery.DeliveryController;
import com.krishiYatra.krishiYatra.delivery.DeliveryService;
import com.krishiYatra.krishiYatra.delivery.dto.RegisterDeliveryRequest;
import com.krishiYatra.krishiYatra.delivery.dto.VerifyDeliveryRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DeliveryController.class)
@AutoConfigureMockMvc(addFilters = false)
public class DeliveryControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private DeliveryService deliveryService;
    @MockitoBean private JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockitoBean private JwtTokenProvider jwtTokenProvider;
    @MockitoBean private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("Endpoint: Register Delivery Success")
    void registerDelivery_ReturnCreated() throws Exception {
        RegisterDeliveryRequest request = new RegisterDeliveryRequest();
        request.setVehicleType(VehicleType.VAN);
        request.setVehicleBrand("Tata Intra V30");
        request.setNumberPlate("BA 1 PA 1234");
        request.setLicenseNumber("01-01-0012345678");

        when(deliveryService.registerDelivery(any())).thenReturn(ServerResponse.successResponse(DeliveryConst.REGISTRATION_SUCCESS, HttpStatus.CREATED));

        mockMvc.perform(post("/api/v1/delivery/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value(DeliveryConst.REGISTRATION_SUCCESS));
    }

    @Test
    @DisplayName("Endpoint: Verify Delivery Success")
    void verifyDelivery_ReturnOk() throws Exception {
        VerifyDeliveryRequest request = new VerifyDeliveryRequest();
        request.setUsername("delivery1");
        request.setApproved(true);

        when(deliveryService.verifyDelivery(any())).thenReturn(ServerResponse.successResponse(DeliveryConst.VERIFICATION_SUCCESS, HttpStatus.OK));

        mockMvc.perform(post("/api/v1/delivery/verify-delivery")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(DeliveryConst.VERIFICATION_SUCCESS));
    }

    @Test
    @DisplayName("Endpoint: Block Delivery Success")
    void blockDelivery_ReturnOk() throws Exception {
        when(deliveryService.blockUnblockDelivery(any(), anyBoolean(), any())).thenReturn(ServerResponse.successResponse("Delivery partner blocked successfully", HttpStatus.OK));

        mockMvc.perform(post("/api/v1/delivery/block-unblock/delivery1")
                .param("block", "true")
                .param("reason", "Expired license"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Endpoint: Get Dashboard Success")
    void getDashboard_ReturnOk() throws Exception {
        when(deliveryService.getDeliveryDashboard()).thenReturn(ServerResponse.successResponse(DeliveryConst.DASHBOARD_WELCOME, HttpStatus.OK));

        mockMvc.perform(get("/api/v1/delivery/dashboard"))
                .andExpect(status().isOk());
    }
}
