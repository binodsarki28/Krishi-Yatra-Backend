package com.krishiYatra.krishiYatra.Service;

import com.krishiYatra.krishiYatra.common.response.ServerResponse;
import com.krishiYatra.krishiYatra.user.*;
import com.krishiYatra.krishiYatra.user.dto.*;
import com.krishiYatra.krishiYatra.user.mapper.UserMapper;
import com.krishiYatra.krishiYatra.verification.InMemoryOtpService;
import com.krishiYatra.krishiYatra.verification.PendingRegistrationStore;
import com.krishiYatra.krishiYatra.config.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock private UserRepo userRepo;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private InMemoryOtpService otpService;
    @Mock private PendingRegistrationStore pendingRegistrationStore;
    @Mock private UserMapper userMapper;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtTokenProvider tokenProvider;

    @InjectMocks
    private UserService userService;

    private UserCreateRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new UserCreateRequest();
        registerRequest.setFullName("John Doe");
        registerRequest.setEmail("test@gmail.com");
        registerRequest.setUsername("testuser");
        registerRequest.setPhoneNumber("9800000000");
        registerRequest.setPassword("Password123!");
    }

    // --- 1. LOGIN TESTS ---

    @Test
    @DisplayName("Login: Success Case")
    void loginUser_Success() {
        UserLoginRequest loginRequest = new UserLoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password");

        UserEntity user = new UserEntity();
        user.setUsername("testuser");
        user.setRoles(Collections.emptySet());

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(user);
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(tokenProvider.generateToken(any())).thenReturn("mocked_jwt");

        ServerResponse response = userService.loginUser(loginRequest);

        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertNotNull(response.getResponse());
    }

    // --- 2. REGISTRATION WORKFLOW ---

    @Test
    @DisplayName("Registration: Fail - Email Taken")
    void registerUser_EmailExists() {
        when(userRepo.existsByEmail("test@gmail.com")).thenReturn(true);
        ServerResponse response = userService.registerUser(registerRequest);
        assertEquals(HttpStatus.BAD_REQUEST, response.getHttpStatus());
        assertEquals(UserConst.EMAIL_EXISTS, response.getMessage());
    }

    @Test
    @DisplayName("Registration: Fail - Phone Number Taken")
    void registerUser_PhoneExists() {
        when(userRepo.existsByEmail(anyString())).thenReturn(false);
        when(userRepo.existsByUsername(anyString())).thenReturn(false);
        when(userRepo.existsByPhoneNumber("9800000000")).thenReturn(true);

        ServerResponse response = userService.registerUser(registerRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getHttpStatus());
        assertEquals(UserConst.PHONE_EXIST, response.getMessage());
    }

    // --- 3. OTP VERIFICATION WORKFLOW ---

    @Test
    @DisplayName("Verify OTP: Success")
    void verifyOtp_Success() {
        OtpVerifyDto verifyDto = new OtpVerifyDto();
        verifyDto.setEmail("test@gmail.com");
        verifyDto.setOtpCode("123456");

        UserEntity user = new UserEntity();
        user.setPassword("raw_password");

        when(otpService.verifyOtp(anyString(), anyString())).thenReturn(true);
        when(pendingRegistrationStore.get(anyString())).thenReturn(registerRequest);
        when(userMapper.entityToUserCreateRequest(any())).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed_pass");

        ServerResponse response = userService.verifyOtp(verifyDto);

        assertEquals(HttpStatus.CREATED, response.getHttpStatus());
        assertEquals(UserConst.USER_CREATED, response.getMessage());
        verify(userRepo).save(user);
    }

    @Test
    @DisplayName("Verify OTP: Fail - Invalid Code")
    void verifyOtp_InvalidCode() {
        OtpVerifyDto verifyDto = new OtpVerifyDto();
        verifyDto.setEmail("test@gmail.com");
        verifyDto.setOtpCode("000000");

        when(otpService.verifyOtp(anyString(), anyString())).thenReturn(false);

        ServerResponse response = userService.verifyOtp(verifyDto);


        assertEquals(HttpStatus.BAD_REQUEST, response.getHttpStatus());
        assertEquals(UserConst.OTP_INVALID, response.getMessage());
    }

    // --- 4. PROFILE AND ROLES ---

    @Test
    @DisplayName("Fetch Details: Success Case")
    void getCurrentUserRoles_Success() {
        UserEntity user = new UserEntity();
        user.setUsername("testuser");
        user.setRoles(Collections.emptySet());

        when(userRepo.findByUsername("testuser")).thenReturn(Optional.of(user));

        ServerResponse response = userService.getCurrentUserRoles("testuser");

        assertEquals(HttpStatus.OK, response.getHttpStatus());
        verify(userRepo).findByUsername("testuser");
    }

    @Test
    @DisplayName("Update Profile: Change Username Success")
    void updateProfile_UsernameChange() {
        UserEntity user = new UserEntity();
        user.setUsername("olduser");

        when(userRepo.findByUsername("olduser")).thenReturn(Optional.of(user));
        when(userRepo.existsByUsername("newuser")).thenReturn(false);

        ServerResponse response = userService.updateProfile("olduser", null, null, null, null, "newuser", null);

        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertEquals("newuser", user.getUsername());
    }
}
