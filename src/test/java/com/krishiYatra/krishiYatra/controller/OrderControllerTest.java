package com.krishiYatra.krishiYatra.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.krishiYatra.krishiYatra.common.response.ServerResponse;
import com.krishiYatra.krishiYatra.config.security.jwt.JwtAuthenticationFilter;
import com.krishiYatra.krishiYatra.config.security.jwt.JwtTokenProvider;
import com.krishiYatra.krishiYatra.config.security.service.CustomUserDetailsService;
import com.krishiYatra.krishiYatra.order.OrderConst;
import com.krishiYatra.krishiYatra.order.OrderController;
import com.krishiYatra.krishiYatra.order.OrderService;
import com.krishiYatra.krishiYatra.order.dto.OrderCreateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
public class OrderControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private OrderService orderService;
    @MockitoBean private JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockitoBean private JwtTokenProvider jwtTokenProvider;
    @MockitoBean private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("Endpoint: Create Order Success")
    void createOrder_ReturnCreated() throws Exception {
        OrderCreateRequest request = new OrderCreateRequest();
        request.setStockSlug("rice-slug");
        request.setOrderQuantity(10.0);
        request.setPickupAddress("Farmer Farm");
        request.setDropAddress("Buyer House");
        request.setNotes("Handle with care");

        ServerResponse response = ServerResponse.successObjectResponse(OrderConst.CREATE_ORDER, HttpStatus.CREATED, "ord-123");
        when(orderService.createOrder(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/order/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value(OrderConst.CREATE_ORDER));
    }

    @Test
    @DisplayName("Endpoint: Get Orders Success")
    void getOrders_ReturnOk() throws Exception {
        when(orderService.getOrders(any(), any())).thenReturn(ServerResponse.successResponse(OrderConst.FETCH_ORDER, HttpStatus.OK));

        mockMvc.perform(get("/api/v1/order/list"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Endpoint: Accept Order Success")
    void acceptOrderByDelivery_ReturnOk() throws Exception {
        when(orderService.acceptOrderByDelivery(any())).thenReturn(ServerResponse.successResponse(OrderConst.DELIVERY_ACCEPTED, HttpStatus.OK));

        mockMvc.perform(post("/api/v1/order/delivery/accept/ord-123"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Endpoint: Mark as Delivered Success")
    void markAsDelivered_ReturnOk() throws Exception {
        when(orderService.markOrderAsDelivered(any())).thenReturn(ServerResponse.successResponse(OrderConst.ORDER_DELIVERED, HttpStatus.OK));

        mockMvc.perform(post("/api/v1/order/mark-as-delivered/ord-123"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Endpoint: Report Conflict Success")
    void reportConflict_ReturnOk() throws Exception {
        when(orderService.reportConflict(any(), any())).thenReturn(ServerResponse.successResponse(OrderConst.CONFLICT_REPORTED, HttpStatus.OK));

        mockMvc.perform(post("/api/v1/order/report-conflict/ord-123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("conflictMessage", "Bad quantity"))))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Endpoint: Resolve Conflict Success")
    void resolveConflict_ReturnOk() throws Exception {
        when(orderService.resolveConflict(any())).thenReturn(ServerResponse.successResponse(OrderConst.CONFLICT_RESOLVED, HttpStatus.OK));

        mockMvc.perform(put("/api/v1/order/resolve-conflict/ord-123"))
                .andExpect(status().isOk());
    }
}
