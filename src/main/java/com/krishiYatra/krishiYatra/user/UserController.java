package com.krishiYatra.krishiYatra.user;

import com.krishiYatra.krishiYatra.common.response.ServerResponse;
import com.krishiYatra.krishiYatra.user.dto.UserCreateRequest;
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

    @PostMapping("/register/user")
    public ResponseEntity<ServerResponse> registerUser(@Validated @RequestBody UserCreateRequest request) {
        ServerResponse response = userService.registerUser(request);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }
}
