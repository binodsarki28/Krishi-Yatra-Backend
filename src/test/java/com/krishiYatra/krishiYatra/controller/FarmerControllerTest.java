package com.krishiYatra.krishiYatra.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.krishiYatra.krishiYatra.common.response.ServerResponse;
import com.krishiYatra.krishiYatra.config.security.jwt.JwtAuthenticationFilter;
import com.krishiYatra.krishiYatra.config.security.jwt.JwtTokenProvider;
import com.krishiYatra.krishiYatra.config.security.service.CustomUserDetailsService;
import com.krishiYatra.krishiYatra.farmer.FarmerConst;
import com.krishiYatra.krishiYatra.farmer.FarmerController;
import com.krishiYatra.krishiYatra.farmer.FarmerService;
import com.krishiYatra.krishiYatra.farmer.dto.RegisterFarmerRequest;
import com.krishiYatra.krishiYatra.farmer.dto.VerifyFarmerRequest;
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

@WebMvcTest(FarmerController.class)
@AutoConfigureMockMvc(addFilters = false)
public class FarmerControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private FarmerService farmerService;
    @MockitoBean private JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockitoBean private JwtTokenProvider jwtTokenProvider;
    @MockitoBean private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("Endpoint: Register Farmer Success")
    void registerFarmer_ReturnCreated() throws Exception {
        RegisterFarmerRequest request = new RegisterFarmerRequest();
        request.setFarmName("Green Farm");
        request.setFarmLocation("Nepal");
        request.setFarmArea(2.5);
        request.setTypes(java.util.List.of(com.krishiYatra.krishiYatra.common.enums.FarmType.CROP));

        when(farmerService.registerFarmer(any())).thenReturn(ServerResponse.successResponse(FarmerConst.REGISTRATION_SUCCESS, HttpStatus.CREATED));

        mockMvc.perform(post("/api/v1/farmer/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value(FarmerConst.REGISTRATION_SUCCESS));
    }

    @Test
    @DisplayName("Endpoint: Verify Farmer Success")
    void verifyFarmer_ReturnOk() throws Exception {
        VerifyFarmerRequest request = new VerifyFarmerRequest();
        request.setUsername("farmer1");
        request.setApproved(true);

        when(farmerService.verifyFarmer(any())).thenReturn(ServerResponse.successResponse(FarmerConst.VERIFICATION_SUCCESS, HttpStatus.OK));

        mockMvc.perform(post("/api/v1/farmer/verify-farmer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Endpoint: Block Farmer Success")
    void blockFarmer_ReturnOk() throws Exception {
        when(farmerService.blockUnblockFarmer(any(), anyBoolean(), any())).thenReturn(ServerResponse.successResponse("Blocked", HttpStatus.OK));

        mockMvc.perform(post("/api/v1/farmer/block-unblock/farmer1")
                .param("block", "true")
                .param("reason", "Violated terms"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Endpoint: Get Dashboard Success")
    void getDashboard_ReturnOk() throws Exception {
        when(farmerService.getFarmerDashboard()).thenReturn(ServerResponse.successResponse(FarmerConst.DASHBOARD_WELCOME, HttpStatus.OK));

        mockMvc.perform(get("/api/v1/farmer/dashboard"))
                .andExpect(status().isOk());
    }
}
