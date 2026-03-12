package com.krishiYatra.krishiYatra.user;

import com.krishiYatra.krishiYatra.common.response.ServerResponse;
import com.krishiYatra.krishiYatra.config.security.jwt.JwtTokenProvider;
import com.krishiYatra.krishiYatra.user.dto.JwtResponse;
import com.krishiYatra.krishiYatra.user.dto.UserLoginRequest;
import com.krishiYatra.krishiYatra.user.constant.UserConst;
import com.krishiYatra.krishiYatra.user.dto.UserCreateRequest;
import com.krishiYatra.krishiYatra.user.mapper.UserMapper;
import com.krishiYatra.krishiYatra.user.dto.OtpRequestDto;
import com.krishiYatra.krishiYatra.user.dto.OtpVerifyDto;
import com.krishiYatra.krishiYatra.verification.EmailService;
import com.krishiYatra.krishiYatra.verification.InMemoryOtpService;
import com.krishiYatra.krishiYatra.verification.PendingRegistrationStore;
import com.krishiYatra.krishiYatra.farmer.FarmerRepo;
import com.krishiYatra.krishiYatra.buyer.BuyerRepo;
import com.krishiYatra.krishiYatra.delivery.DeliveryRepo;
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
    private final InMemoryOtpService otpService;
    private final EmailService emailService;
    private final PendingRegistrationStore pendingRegistrationStore;
    private final FarmerRepo farmerRepo;
    private final BuyerRepo buyerRepo;
    private final DeliveryRepo deliveryRepo;

    public UserService(UserMapper userMapper,
                       UserRepo userRepo,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtTokenProvider tokenProvider,
                       InMemoryOtpService otpService,
                       EmailService emailService,
                       FarmerRepo farmerRepo,
                       BuyerRepo buyerRepo,
                       DeliveryRepo deliveryRepo,
                       PendingRegistrationStore pendingRegistrationStore) {
        this.userMapper = userMapper;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.otpService = otpService;
        this.emailService = emailService;
        this.farmerRepo = farmerRepo;
        this.buyerRepo = buyerRepo;
        this.deliveryRepo = deliveryRepo;
        this.pendingRegistrationStore = pendingRegistrationStore;
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

        List<String> verifiedRoles = new java.util.ArrayList<>();
        if (roles.contains("FARMER")) {
            farmerRepo.findByUser(userDetails).ifPresent(f -> { if (f.isVerified()) verifiedRoles.add("FARMER"); });
        }
        if (roles.contains("BUYER")) {
            buyerRepo.findByUser(userDetails).ifPresent(b -> { if (b.isVerified()) verifiedRoles.add("BUYER"); });
        }
        if (roles.contains("DELIVERY")) {
            deliveryRepo.findByUser(userDetails).ifPresent(d -> { if (d.isVerified()) verifiedRoles.add("DELIVERY"); });
        }

        JwtResponse jwtResponse = new JwtResponse(jwt, userDetails.getUsername(), userDetails.getFullName(), userDetails.getEmail(), roles, verifiedRoles);
        return ServerResponse.successObjectResponse(UserConst.USER_LOGIN, HttpStatus.OK, jwtResponse);
    }

    public ServerResponse registerUser(UserCreateRequest request) {
        System.out.println("Processing registration for email: [" + request.getEmail() + "]");
        // Check if email already exists
        if (userRepo.existsByEmail(request.getEmail())) {
            return ServerResponse.failureResponse(UserConst.EMAIL_EXISTS, HttpStatus.BAD_REQUEST);
        }

        if (userRepo.existsByUsername(request.getUsername())) {
            return ServerResponse.failureResponse(UserConst.USERNAME_EXISTS, HttpStatus.BAD_REQUEST);
        }

        try {
            // Store registration data
            pendingRegistrationStore.store(request.getEmail(), request);

            // Generate and send OTP
            String otpCode = otpService.generateOtp(request.getEmail());
            System.out.println("Generated OTP for " + request.getEmail() + ": " + otpCode);
            emailService.sendOtpEmail(request.getEmail(), otpCode);

            return ServerResponse.successResponse("Verification code sent to your email.", HttpStatus.OK);
        } catch (Exception e) {
            return ServerResponse.failureResponse("Failed to send verification code: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    //Request/Resend OTP for email verification
    public ServerResponse requestOtp(OtpRequestDto request) {
        System.out.println("Processing OTP resend request for email: [" + request.getEmail() + "]");
        // Check if there's pending registration data
        UserCreateRequest pendingData = pendingRegistrationStore.get(request.getEmail());
        if (pendingData == null) {
            return ServerResponse.failureResponse("No pending registration found. Please register first.", HttpStatus.BAD_REQUEST);
        }

        // Check if email already exists in database
        if (userRepo.existsByEmail(request.getEmail())) {
            return ServerResponse.failureResponse(UserConst.EMAIL_EXISTS, HttpStatus.BAD_REQUEST);
        }

        try {
            // Generate OTP
            String otpCode = otpService.generateOtp(request.getEmail());
            
            // Send email
            emailService.sendOtpEmail(request.getEmail(), otpCode);
            
            return ServerResponse.successResponse("OTP sent to your email successfully.", HttpStatus.OK);
        } catch (Exception e) {
            return ServerResponse.failureResponse("Failed to send OTP: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //Verify OTP code and create user account
    public ServerResponse verifyOtp(OtpVerifyDto request) {
        boolean isValid = otpService.verifyOtp(request.getEmail(), request.getOtpCode());
        
        if (!isValid) {
            return ServerResponse.failureResponse(UserConst.OTP_INVALID, HttpStatus.BAD_REQUEST);
        }

        // Get pending registration data
        UserCreateRequest userData = pendingRegistrationStore.get(request.getEmail());
        if (userData == null) {
            return ServerResponse.failureResponse("Registration data not found. Please register again.", HttpStatus.BAD_REQUEST);
        }

        // Create user account
        if (userRepo.existsByUsername(userData.getUsername())) {
            return ServerResponse.failureResponse(UserConst.USERNAME_EXISTS, HttpStatus.BAD_REQUEST);
        }
        if (userRepo.existsByEmail(userData.getEmail())) {
            return ServerResponse.failureResponse(UserConst.EMAIL_EXISTS, HttpStatus.BAD_REQUEST);
        }

        UserEntity user = userMapper.entityToUserCreateRequest(userData);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepo.save(user);

        // Clean up
        pendingRegistrationStore.remove(request.getEmail());

        return ServerResponse.successResponse(UserConst.USER_CREATED, HttpStatus.CREATED);
    }

    public ServerResponse getCurrentUserRoles(String username) {
        UserEntity userDetails = userRepo.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
            
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        List<String> verifiedRoles = new java.util.ArrayList<>();
        if (roles.contains("FARMER")) {
            farmerRepo.findByUser(userDetails).ifPresent(f -> { if (f.isVerified()) verifiedRoles.add("FARMER"); });
        }
        if (roles.contains("BUYER")) {
            buyerRepo.findByUser(userDetails).ifPresent(b -> { if (b.isVerified()) verifiedRoles.add("BUYER"); });
        }
        if (roles.contains("DELIVERY")) {
            deliveryRepo.findByUser(userDetails).ifPresent(d -> { if (d.isVerified()) verifiedRoles.add("DELIVERY"); });
        }

        JwtResponse jwtResponse = new JwtResponse(null, userDetails.getUsername(), userDetails.getFullName(), userDetails.getEmail(), roles, verifiedRoles);
        return ServerResponse.successObjectResponse("User details fetched successfully", HttpStatus.OK, jwtResponse);
    }
}
