package com.krishiYatra.krishiYatra.user;

import com.krishiYatra.krishiYatra.common.enums.RoleType;
import com.krishiYatra.krishiYatra.common.response.ServerResponse;
import com.krishiYatra.krishiYatra.config.security.jwt.JwtTokenProvider;
import com.krishiYatra.krishiYatra.user.dto.JwtResponse;
import com.krishiYatra.krishiYatra.user.dto.UserLoginRequest;
import com.krishiYatra.krishiYatra.user.dto.UserCreateRequest;
import com.krishiYatra.krishiYatra.user.dto.PasswordUpdateRequest;
import com.krishiYatra.krishiYatra.user.dto.ResetPasswordRequest;
import com.krishiYatra.krishiYatra.user.mapper.UserMapper;
import com.krishiYatra.krishiYatra.user.dto.OtpRequestDto;
import com.krishiYatra.krishiYatra.user.dto.OtpVerifyDto;
import com.krishiYatra.krishiYatra.verification.EmailService;
import com.krishiYatra.krishiYatra.verification.InMemoryOtpService;
import com.krishiYatra.krishiYatra.verification.PendingRegistrationStore;
import com.krishiYatra.krishiYatra.farmer.FarmerRepo;
import com.krishiYatra.krishiYatra.buyer.BuyerRepo;
import com.krishiYatra.krishiYatra.delivery.DeliveryRepo;
import com.krishiYatra.krishiYatra.config.CloudinaryService;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import com.krishiYatra.krishiYatra.common.enums.VerificationStatus;

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
    private final RoleRepo roleRepo;
    private final CloudinaryService cloudinaryService;

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
                       RoleRepo roleRepo,
                       PendingRegistrationStore pendingRegistrationStore,
                       CloudinaryService cloudinaryService) {
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
        this.roleRepo = roleRepo;
        this.pendingRegistrationStore = pendingRegistrationStore;
        this.cloudinaryService = cloudinaryService;
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
        Map<String, String> statusMessages = new HashMap<>();

        // Check for role-based profiles (Verified status)
        farmerRepo.findByUser(userDetails).ifPresent(f -> { 
            if (f.getStatus() == VerificationStatus.VERIFIED) verifiedRoles.add("FARMER"); 
            else {
                String msg = f.getStatus() == VerificationStatus.BLOCKED ? "Your Farmer account has been BLOCKED. Reason: " : "Under verification (" + f.getStatus().name() + "). ";
                statusMessages.put("FARMER", f.getStatusMessage() != null ? (f.getStatus() == VerificationStatus.BLOCKED ? "Your account is BLOCKED: " + f.getStatusMessage() : f.getStatusMessage()) : msg);
            }
        });
        if (roles.contains("BUYER")) {
            buyerRepo.findByUser(userDetails).ifPresent(b -> { 
                if (b.getStatus() == VerificationStatus.VERIFIED) verifiedRoles.add("BUYER"); 
                else {
                    String msg = b.getStatus() == VerificationStatus.BLOCKED ? "Your Buyer account has been BLOCKED. Reason: " : "Under verification (" + b.getStatus().name() + "). ";
                    statusMessages.put("BUYER", b.getStatusMessage() != null ? (b.getStatus() == VerificationStatus.BLOCKED ? "Your account is BLOCKED: " + b.getStatusMessage() : b.getStatusMessage()) : msg);
                }
            });
        }
        if (roles.contains("DELIVERY")) {
            deliveryRepo.findByUser(userDetails).ifPresent(d -> { 
                if (d.getStatus() == VerificationStatus.VERIFIED) verifiedRoles.add("DELIVERY"); 
                else {
                    String msg = d.getStatus() == VerificationStatus.BLOCKED ? "Your Linker account has been BLOCKED. Reason: " : "Under verification (" + d.getStatus().name() + "). ";
                    statusMessages.put("DELIVERY", d.getStatusMessage() != null ? (d.getStatus() == VerificationStatus.BLOCKED ? "Your account is BLOCKED: " + d.getStatusMessage() : d.getStatusMessage()) : msg);
                }
            });
        }

        JwtResponse jwtResponse = new JwtResponse(jwt, userDetails.getUsername(), userDetails.getFullName(), userDetails.getEmail(), roles, verifiedRoles, statusMessages, userDetails.getPhoneNumber(), userDetails.getProfileUrl(), userDetails.getDescription());
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

        if (userRepo.existsByPhoneNumber(request.getPhoneNumber())) {
            return ServerResponse.failureResponse(UserConst.PHONE_EXIST, HttpStatus.BAD_REQUEST);
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
        user.setActive(true);
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

        List<String> verifiedRoles = new ArrayList<>();
        Map<String, String> statusMessages = new HashMap<>();

        if (roles.contains("FARMER")) {
            farmerRepo.findByUser(userDetails).ifPresent(f -> { 
                if (f.getStatus() == VerificationStatus.VERIFIED) verifiedRoles.add("FARMER"); 
                else {
                    String msg = f.getStatus() == VerificationStatus.BLOCKED ? "Your Farmer account has been BLOCKED. Reason: " : "Under verification (" + f.getStatus().name() + "). ";
                    statusMessages.put("FARMER", f.getStatusMessage() != null ? (f.getStatus() == VerificationStatus.BLOCKED ? "Your account is BLOCKED: " + f.getStatusMessage() : f.getStatusMessage()) : msg);
                }
            });
        }
        if (roles.contains("BUYER")) {
            buyerRepo.findByUser(userDetails).ifPresent(b -> { 
                if (b.getStatus() == VerificationStatus.VERIFIED) verifiedRoles.add("BUYER"); 
                else {
                    String msg = b.getStatus() == VerificationStatus.BLOCKED ? "Your Buyer account has been BLOCKED. Reason: " : "Under verification (" + b.getStatus().name() + "). ";
                    statusMessages.put("BUYER", b.getStatusMessage() != null ? (b.getStatus() == VerificationStatus.BLOCKED ? "Your account is BLOCKED: " + b.getStatusMessage() : b.getStatusMessage()) : msg);
                }
            });
        }
        if (roles.contains("DELIVERY")) {
            deliveryRepo.findByUser(userDetails).ifPresent(d -> { 
                if (d.getStatus() == VerificationStatus.VERIFIED) verifiedRoles.add("DELIVERY"); 
                else {
                    String msg = d.getStatus() == VerificationStatus.BLOCKED ? "Your Linker account has been BLOCKED. Reason: " : "Under verification (" + d.getStatus().name() + "). ";
                    statusMessages.put("DELIVERY", d.getStatusMessage() != null ? (d.getStatus() == VerificationStatus.BLOCKED ? "Your account is BLOCKED: " + d.getStatusMessage() : d.getStatusMessage()) : msg);
                }
            });
        }

        JwtResponse jwtResponse = new JwtResponse(null, userDetails.getUsername(), userDetails.getFullName(), userDetails.getEmail(), roles, verifiedRoles, statusMessages, userDetails.getPhoneNumber(), userDetails.getProfileUrl(), userDetails.getDescription());
        return ServerResponse.successObjectResponse("User details fetched successfully", HttpStatus.OK, jwtResponse);
    }

    public ServerResponse updateProfile(String username, String firstName, String lastName, String phoneNumber, String description, String newUsername, MultipartFile profileImage) {
        UserEntity user = userRepo.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        
        if (newUsername != null && !newUsername.isEmpty() && !newUsername.equals(user.getUsername())) {
             if (userRepo.existsByUsername(newUsername)) {
                 return ServerResponse.failureResponse("Username already taken", HttpStatus.BAD_REQUEST);
             }
             user.setUsername(newUsername);
        }
        
        if (firstName != null && !firstName.isEmpty()) {
            String updatedFullName = firstName;
            if (lastName != null && !lastName.isEmpty()) {
                updatedFullName += " " + lastName;
            } else {
                // Keep original last name or just leave single first name
                String[] parts = user.getFullName().split(" ");
                if (parts.length > 1) {
                    updatedFullName += " " + parts[1];
                }
            }
            user.setFullName(updatedFullName);
        } else if (lastName != null && !lastName.isEmpty()) {
            String[] parts = user.getFullName().split(" ");
            if (parts.length > 0) {
                user.setFullName(parts[0] + " " + lastName);
            } else {
                user.setFullName(lastName);
            }
        }
        
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            user.setPhoneNumber(phoneNumber);
        }
        if (description != null) {
            user.setDescription(description);
        }
        if (profileImage != null && !profileImage.isEmpty()) {
            String url = cloudinaryService.uploadFile(profileImage);
            user.setProfileUrl(url);
        }
        userRepo.save(user);

        List<String> roles = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        List<String> verifiedRoles = new java.util.ArrayList<>();
        Map<String, String> statusMessages = new HashMap<>();

        if (roles.contains("FARMER")) {
            farmerRepo.findByUser(user).ifPresent(f -> {
                if (f.getStatus() == VerificationStatus.VERIFIED) verifiedRoles.add("FARMER");
                else {
                    String msg = f.getStatus() == VerificationStatus.BLOCKED ? "Your account is BLOCKED." : "Under verification.";
                    statusMessages.put("FARMER", f.getStatusMessage() != null ? (f.getStatus() == VerificationStatus.BLOCKED ? "Your account is BLOCKED: " + f.getStatusMessage() : f.getStatusMessage()) : msg);
                }
            });
        }
        if (roles.contains("BUYER")) {
            buyerRepo.findByUser(user).ifPresent(b -> {
                if (b.getStatus() == VerificationStatus.VERIFIED) verifiedRoles.add("BUYER");
                else {
                    String msg = b.getStatus() == VerificationStatus.BLOCKED ? "Your account is BLOCKED." : "Under verification.";
                    statusMessages.put("BUYER", b.getStatusMessage() != null ? (b.getStatus() == VerificationStatus.BLOCKED ? "Your account is BLOCKED: " + b.getStatusMessage() : b.getStatusMessage()) : msg);
                }
            });
        }
        if (roles.contains("DELIVERY")) {
            deliveryRepo.findByUser(user).ifPresent(d -> {
                if (d.getStatus() == VerificationStatus.VERIFIED) verifiedRoles.add("DELIVERY");
                else {
                    String msg = d.getStatus() == VerificationStatus.BLOCKED ? "Your account is BLOCKED." : "Under verification.";
                    statusMessages.put("DELIVERY", d.getStatusMessage() != null ? (d.getStatus() == VerificationStatus.BLOCKED ? "Your account is BLOCKED: " + d.getStatusMessage() : d.getStatusMessage()) : msg);
                }
            });
        }

        JwtResponse jwtResponse = new JwtResponse(null, user.getUsername(), user.getFullName(), user.getEmail(), roles, verifiedRoles, statusMessages, user.getPhoneNumber(), user.getProfileUrl(), user.getDescription());
        return ServerResponse.successObjectResponse("Profile updated successfully", HttpStatus.OK, jwtResponse);
    }

    public ServerResponse updatePassword(PasswordUpdateRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            return ServerResponse.failureResponse("Incorrect current password.", HttpStatus.BAD_REQUEST);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepo.save(user);
        return ServerResponse.successResponse("Password updated successfully", HttpStatus.OK);
    }
    public ServerResponse forgotPassword(OtpRequestDto request) {
        String genericMessage = "If an account exists with this email, a reset code has been sent to your inbox.";
        
        // Internally check if user exists
        if (!userRepo.existsByEmail(request.getEmail())) {
            // Return success anyway to prevent email enumeration (detecting if an email is registered)
            return ServerResponse.successResponse(genericMessage, HttpStatus.OK);
        }

        try {
            String otpCode = otpService.generateOtp(request.getEmail());
            System.out.println("Forgot Password OTP for " + request.getEmail() + ": " + otpCode);
            emailService.sendOtpEmail(request.getEmail(), otpCode);
            return ServerResponse.successResponse(genericMessage, HttpStatus.OK);
        } catch (Exception e) {
            // Only log the actual error, still return generic or failure if technical
            return ServerResponse.failureResponse("Failed to process request. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ServerResponse resetPassword(ResetPasswordRequest request) {
        boolean isValid = otpService.verifyOtp(request.getEmail(), request.getOtpCode());
        if (!isValid) {
            return ServerResponse.failureResponse(UserConst.OTP_INVALID, HttpStatus.BAD_REQUEST);
        }

        UserEntity user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepo.save(user);
        return ServerResponse.successResponse("Password has been reset successfully. You can now login with your new password.", HttpStatus.OK);
    }
}
