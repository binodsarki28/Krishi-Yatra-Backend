package com.krishiYatra.krishiYatra.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.krishiYatra.krishiYatra.address.AddressController;
import com.krishiYatra.krishiYatra.address.AddressService;
import com.krishiYatra.krishiYatra.address.dto.AddressRequest;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AddressController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AddressControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private AddressService addressService;
    @MockitoBean private JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockitoBean private JwtTokenProvider jwtTokenProvider;
    @MockitoBean private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("Endpoint: Save Address Success")
    void saveAddress_ReturnOk() throws Exception {
        AddressRequest request = new AddressRequest();
        request.setProvince("Bagmati");
        request.setDistrict("Kathmandu");
        request.setMunicipality("KMC");
        request.setWardNo(10);
        request.setStreetName("Baneshwor");

        when(addressService.saveOrUpdateAddress(any())).thenReturn(ServerResponse.successResponse("Address saved successfully.", HttpStatus.OK));

        mockMvc.perform(post("/api/v1/address/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Address saved successfully."));
    }

    @Test
    @DisplayName("Endpoint: Get My Address Success")
    void getMyAddress_ReturnOk() throws Exception {
        when(addressService.getMyAddress()).thenReturn(ServerResponse.successResponse("Address fetched.", HttpStatus.OK));

        mockMvc.perform(get("/api/v1/address/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Address fetched."));
    }

    @Test
    @DisplayName("Endpoint: Delete Address Success")
    void deleteAddress_ReturnOk() throws Exception {
        when(addressService.deleteMyAddress()).thenReturn(ServerResponse.successResponse("Address deleted successfully.", HttpStatus.OK));

        mockMvc.perform(delete("/api/v1/address/delete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Address deleted successfully."));
    }
}
