package com.krishiYatra.krishiYatra.user;

import com.krishiYatra.krishiYatra.common.response.ServerResponse;
import com.krishiYatra.krishiYatra.config.security.jwt.JwtTokenProvider;
import com.krishiYatra.krishiYatra.user.dto.JwtResponse;
import com.krishiYatra.krishiYatra.user.dto.UserLoginRequest;
import com.krishiYatra.krishiYatra.user.constant.UserConst;
import com.krishiYatra.krishiYatra.user.dto.UserCreateRequest;
import com.krishiYatra.krishiYatra.user.mapper.UserMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserMapper userMapper;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public UserService(UserMapper userMapper,
                       UserRepo userRepo,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtTokenProvider tokenProvider) {
        this.userMapper = userMapper;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    public ServerResponse loginUser(UserLoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        UserEntity userDetails = (UserEntity) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        JwtResponse jwtResponse = new JwtResponse(jwt, userDetails.getUsername(), roles);
        return ServerResponse.successObjectResponse(UserConst.USER_LOGIN, HttpStatus.OK, jwtResponse);
    }

    public ServerResponse registerUser(UserCreateRequest request) {
        if (userRepo.existsByUsername(request.getUsername())) {
            return ServerResponse.failureResponse(UserConst.USERNAME_EXISTS, HttpStatus.BAD_REQUEST);
        }
        if (userRepo.existsByEmail(request.getEmail())) {
            return ServerResponse.failureResponse(UserConst.EMAIL_EXISTS, HttpStatus.BAD_REQUEST);
        }

        UserEntity user = userMapper.entityToUserCreateRequest(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepo.save(user);
        return ServerResponse.successResponse(UserConst.USER_CREATED, HttpStatus.CREATED);
    }
}
