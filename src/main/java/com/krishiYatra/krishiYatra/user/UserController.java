package com.krishiYatra.krishiYatra.user;

import com.krishiYatra.krishiYatra.common.response.ServerResponse;
import com.krishiYatra.krishiYatra.user.dto.OtpRequestDto;
import com.krishiYatra.krishiYatra.user.dto.OtpVerifyDto;
import com.krishiYatra.krishiYatra.user.dto.UserCreateRequest;
import com.krishiYatra.krishiYatra.user.dto.UserLoginRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/user")
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
        ServerResponse response = userService.requestOtp(request);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }
}
