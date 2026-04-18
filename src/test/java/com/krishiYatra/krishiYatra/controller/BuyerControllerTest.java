package com.krishiYatra.krishiYatra.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.krishiYatra.krishiYatra.buyer.BuyerConst;
import com.krishiYatra.krishiYatra.buyer.BuyerController;
import com.krishiYatra.krishiYatra.buyer.BuyerService;
import com.krishiYatra.krishiYatra.buyer.dto.RegisterBuyerRequest;
import com.krishiYatra.krishiYatra.buyer.dto.VerifyBuyerRequest;
import com.krishiYatra.krishiYatra.common.enums.ConsumerType;
import com.krishiYatra.krishiYatra.common.response.ServerResponse;
import com.krishiYatra.krishiYatra.config.security.jwt.JwtAuthenticationFilter;
import com.krishiYatra.krishiYatra.config.security.jwt.JwtTokenProvider;
import com.krishiYatra.krishiYatra.config.security.service.CustomUserDetailsService;
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

@WebMvcTest(BuyerController.class)
@AutoConfigureMockMvc(addFilters = false)
public class BuyerControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private BuyerService buyerService;
    @MockitoBean private JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockitoBean private JwtTokenProvider jwtTokenProvider;
    @MockitoBean private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("Endpoint: Register Buyer Success")
    void registerBuyer_ReturnCreated() throws Exception {
        RegisterBuyerRequest request = new RegisterBuyerRequest();
        request.setConsumerType(ConsumerType.RETAILER);
        request.setBusinessName("Organic Mart");
        request.setBusinessLocation("Kathmandu");

        when(buyerService.registerBuyer(any())).thenReturn(ServerResponse.successResponse(BuyerConst.REGISTRATION_SUCCESS, HttpStatus.CREATED));

        mockMvc.perform(post("/api/v1/buyer/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value(BuyerConst.REGISTRATION_SUCCESS));
    }

    @Test
    @DisplayName("Endpoint: Verify Buyer Success")
    void verifyBuyer_ReturnOk() throws Exception {
        VerifyBuyerRequest request = new VerifyBuyerRequest();
        request.setUsername("buyer1");
        request.setApproved(true);

        when(buyerService.verifyBuyer(any())).thenReturn(ServerResponse.successResponse(BuyerConst.VERIFICATION_SUCCESS, HttpStatus.OK));

        mockMvc.perform(post("/api/v1/buyer/verify-buyer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(BuyerConst.VERIFICATION_SUCCESS));
    }

    @Test
    @DisplayName("Endpoint: Block Buyer Success")
    void blockBuyer_ReturnOk() throws Exception {
        when(buyerService.blockUnblockBuyer(any(), anyBoolean(), any())).thenReturn(ServerResponse.successResponse("Buyer blocked successfully", HttpStatus.OK));

        mockMvc.perform(post("/api/v1/buyer/block-unblock/buyer1")
                .param("block", "true")
                .param("reason", "Violation of terms"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Endpoint: Get Dashboard Success")
    void getDashboard_ReturnOk() throws Exception {
        when(buyerService.getBuyerDashboard()).thenReturn(ServerResponse.successResponse(BuyerConst.DASHBOARD_WELCOME, HttpStatus.OK));

        mockMvc.perform(get("/api/v1/buyer/dashboard"))
                .andExpect(status().isOk());
    }
}
