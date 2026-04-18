package com.krishiYatra.krishiYatra.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.krishiYatra.krishiYatra.common.response.ServerResponse;
import com.krishiYatra.krishiYatra.config.security.jwt.JwtAuthenticationFilter;
import com.krishiYatra.krishiYatra.config.security.jwt.JwtTokenProvider;
import com.krishiYatra.krishiYatra.config.security.service.CustomUserDetailsService;
import com.krishiYatra.krishiYatra.demand.DemandConst;
import com.krishiYatra.krishiYatra.demand.DemandController;
import com.krishiYatra.krishiYatra.demand.DemandService;
import com.krishiYatra.krishiYatra.demand.dto.DemandCreateRequest;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DemandController.class)
@AutoConfigureMockMvc(addFilters = false)
public class DemandControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private DemandService demandService;
    @MockitoBean private JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockitoBean private JwtTokenProvider jwtTokenProvider;
    @MockitoBean private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("Endpoint: Create Demand Success")
    void createDemand_ReturnCreated() throws Exception {
        DemandCreateRequest request = new DemandCreateRequest();
        request.setCategoryId(1);
        
        when(demandService.createDemand(any())).thenReturn(ServerResponse.successResponse(DemandConst.DEMAND_CREATED, HttpStatus.CREATED));

        mockMvc.perform(post("/api/v1/demand/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value(DemandConst.DEMAND_CREATED));
    }

    @Test
    @DisplayName("Endpoint: Get All Demands Success")
    void getDemands_ReturnOk() throws Exception {
        when(demandService.getDemands(any(), anyInt(), anyInt())).thenReturn(ServerResponse.successResponse(DemandConst.DEMAND_FETCHED, HttpStatus.OK));

        mockMvc.perform(get("/api/v1/demand/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(DemandConst.DEMAND_FETCHED));
    }

    @Test
    @DisplayName("Endpoint: Get My Demands Success")
    void getMyDemands_ReturnOk() throws Exception {
        when(demandService.getMyDemands(anyInt(), anyInt())).thenReturn(ServerResponse.successResponse(DemandConst.DEMAND_MY_FETCHED, HttpStatus.OK));

        mockMvc.perform(get("/api/v1/demand/my-demands"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(DemandConst.DEMAND_MY_FETCHED));
    }

    @Test
    @DisplayName("Endpoint: Cancel Demand Success")
    void cancelDemand_ReturnOk() throws Exception {
        when(demandService.cancelDemand(any())).thenReturn(ServerResponse.successResponse(DemandConst.DEMAND_CANCELLED, HttpStatus.OK));

        mockMvc.perform(post("/api/v1/demand/cancel/dem-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(DemandConst.DEMAND_CANCELLED));
    }

    @Test
    @DisplayName("Endpoint: Accept Demand Success")
    void acceptDemand_ReturnOk() throws Exception {
        when(demandService.acceptDemand(any())).thenReturn(ServerResponse.successResponse(DemandConst.DEMAND_ACCEPTED, HttpStatus.OK));

        mockMvc.perform(post("/api/v1/demand/accept/dem-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(DemandConst.DEMAND_ACCEPTED));
    }
}
