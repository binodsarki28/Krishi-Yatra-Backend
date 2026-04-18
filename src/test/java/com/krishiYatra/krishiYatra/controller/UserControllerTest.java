package com.krishiYatra.krishiYatra.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.krishiYatra.krishiYatra.common.response.ServerResponse;
import com.krishiYatra.krishiYatra.user.UserController;
import com.krishiYatra.krishiYatra.user.UserService;
import com.krishiYatra.krishiYatra.user.dto.*;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private UserService userService;
    @MockitoBean private JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockitoBean private JwtTokenProvider jwtTokenProvider;
    @MockitoBean private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("Endpoint: Login Success")
    void loginUser_ReturnOk() throws Exception {
        UserLoginRequest request = new UserLoginRequest();
        request.setUsername("testuser");
        request.setPassword("password");

        when(userService.loginUser(any())).thenReturn(ServerResponse.successResponse("Login Success", HttpStatus.OK));

        mockMvc.perform(post("/api/v1/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Endpoint: Register Success")
    void registerUser_ReturnOk() throws Exception {
        UserCreateRequest request = new UserCreateRequest();
        request.setFullName("John Doe");
        request.setEmail("test@gmail.com");
        request.setUsername("testuser");
        request.setPhoneNumber("9812345678");
        request.setPassword("Password123!");

        when(userService.registerUser(any())).thenReturn(ServerResponse.successResponse("Success", HttpStatus.OK));

        mockMvc.perform(post("/api/v1/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Endpoint: Verify OTP Success")
    void verifyOtp_ReturnCreated() throws Exception {
        OtpVerifyDto request = new OtpVerifyDto();
        request.setEmail("test@gmail.com");
        request.setOtpCode("123456");

        when(userService.verifyOtp(any())).thenReturn(ServerResponse.successResponse("Verified", HttpStatus.CREATED));

        mockMvc.perform(post("/api/v1/user/verify-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Endpoint: Update Profile - Authorized")
    void updateProfile_ReturnOk() throws Exception {
        // Mock authentication object to avoid 401 Unauthorized
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("testuser", null, Collections.emptyList());

        when(userService.updateProfile(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(ServerResponse.successResponse("Updated", HttpStatus.OK));

        mockMvc.perform(put("/api/v1/user/profile")
                .principal(auth) // Provide valid principal
                .param("firstName", "John")
                .param("lastName", "Doe"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Endpoint: Update Profile - Fail if Not Authenticated")
    void updateProfile_ReturnUnauthorized() throws Exception {
        mockMvc.perform(put("/api/v1/user/profile")
                .param("firstName", "John"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Endpoint: Forgot Password Success")
    void forgotPassword_ReturnOk() throws Exception {
        OtpRequestDto request = new OtpRequestDto();
        request.setEmail("test@gmail.com");

        when(userService.forgotPassword(any())).thenReturn(ServerResponse.successResponse("Sent", HttpStatus.OK));

        mockMvc.perform(post("/api/v1/user/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isOk());
    }
}
