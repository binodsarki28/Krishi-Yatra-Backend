package com.krishiYatra.krishiYatra.user;

import com.krishiYatra.krishiYatra.common.response.ServerResponse;
import com.krishiYatra.krishiYatra.user.dto.OtpRequestDto;
import com.krishiYatra.krishiYatra.user.dto.OtpVerifyDto;
import com.krishiYatra.krishiYatra.user.dto.UserCreateRequest;
import com.krishiYatra.krishiYatra.user.dto.UserLoginRequest;
import com.krishiYatra.krishiYatra.user.dto.PasswordUpdateRequest;
import com.krishiYatra.krishiYatra.user.dto.ResetPasswordRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/login")
    public ResponseEntity<ServerResponse> loginUser(@Validated @RequestBody UserLoginRequest request) {
        ServerResponse response = userService.loginUser(request);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @PostMapping("/register")
    public ResponseEntity<ServerResponse> registerUser(@Validated @RequestBody UserCreateRequest request) {
        System.out.println("UserController: Received registration request for " + request.getEmail());
        ServerResponse response = userService.registerUser(request);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ServerResponse> verifyOtp(@Validated @RequestBody OtpVerifyDto request) {
        ServerResponse response = userService.verifyOtp(request);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<ServerResponse> resendOtp(@Validated @RequestBody OtpRequestDto request) {
        System.out.println("UserController: Received resend-otp request for " + request.getEmail());
        ServerResponse response = userService.requestOtp(request);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @GetMapping("/me")
    public ResponseEntity<ServerResponse> getCurrentUser(org.springframework.security.core.Authentication authentication) {
        if (authentication == null) {
            return new ResponseEntity<>(ServerResponse.failureResponse("Unauthorized", org.springframework.http.HttpStatus.UNAUTHORIZED), org.springframework.http.HttpStatus.UNAUTHORIZED);
        }
        ServerResponse response = userService.getCurrentUserRoles(authentication.getName());
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @PutMapping("/profile")
    public ResponseEntity<ServerResponse> updateProfile(
            org.springframework.security.core.Authentication authentication,
            @RequestParam(value = "firstName", required = false) String firstName,
            @RequestParam(value = "lastName", required = false) String lastName,
            @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "currentUsername", required = false) String currentUsername,
            @RequestParam(value = "profileImage", required = false) org.springframework.web.multipart.MultipartFile profileImage) {
        if (authentication == null) {
            return new ResponseEntity<>(ServerResponse.failureResponse("Unauthorized", org.springframework.http.HttpStatus.UNAUTHORIZED), org.springframework.http.HttpStatus.UNAUTHORIZED);
        }
        ServerResponse response = userService.updateProfile(authentication.getName(), firstName, lastName, phoneNumber, description, currentUsername, profileImage);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @PutMapping("/password")
    public ResponseEntity<ServerResponse> updatePassword(@Validated @RequestBody PasswordUpdateRequest request) {
        ServerResponse response = userService.updatePassword(request);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ServerResponse> forgotPassword(@Validated @RequestBody OtpRequestDto request) {
        ServerResponse response = userService.forgotPassword(request);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ServerResponse> resetPassword(@Validated @RequestBody ResetPasswordRequest request) {
        ServerResponse response = userService.resetPassword(request);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }
}
