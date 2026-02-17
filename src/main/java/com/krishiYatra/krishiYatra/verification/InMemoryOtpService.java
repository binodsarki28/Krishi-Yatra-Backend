package com.krishiYatra.krishiYatra.verification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory OTP service - No Redis required
 * Safe alternative for development/testing
 */
@Service
public class InMemoryOtpService {

    private final Map<String, OtpData> otpStore = new ConcurrentHashMap<>();
    private final Random random = new Random();

    @Value("${app.otp.expiration-minutes:5}")
    private int otpExpirationMinutes;

    @Value("${app.otp.max-attempts:3}")
    private int maxAttempts;

    /**
     * Generate a 6-digit OTP and store it in memory
     */
    public String generateOtp(String email) {
        // Clean up expired OTPs first
        cleanupExpiredOtps();

        String otpCode = String.format("%06d", random.nextInt(1000000));
        
        OtpData otpData = new OtpData();
        otpData.setEmail(email);
        otpData.setOtpCode(otpCode);
        otpData.setCreatedAt(LocalDateTime.now());
        otpData.setAttempts(0);

        otpStore.put(email, otpData);

        return otpCode;
    }

    /**
     * Verify OTP code
     */
    public boolean verifyOtp(String email, String otpCode) {
        OtpData otpData = otpStore.get(email);

        if (otpData == null) {
            return false; // OTP doesn't exist
        }

        // Check if expired
        if (isExpired(otpData)) {
            otpStore.remove(email);
            return false;
        }

        // Check max attempts
        if (otpData.getAttempts() >= maxAttempts) {
            otpStore.remove(email);
            return false;
        }

        // Increment attempts
        otpData.setAttempts(otpData.getAttempts() + 1);

        // Verify OTP
        if (otpData.getOtpCode().equals(otpCode)) {
            otpStore.remove(email); // Delete after successful verification
            return true;
        }

        return false;
    }

    /**
     * Delete OTP from memory
     */
    public void deleteOtp(String email) {
        otpStore.remove(email);
    }

    /**
     * Check if OTP is expired
     */
    private boolean isExpired(OtpData otpData) {
        LocalDateTime expirationTime = otpData.getCreatedAt().plusMinutes(otpExpirationMinutes);
        return LocalDateTime.now().isAfter(expirationTime);
    }

    /**
     * Clean up expired OTPs (runs on each generation)
     */
    private void cleanupExpiredOtps() {
        otpStore.entrySet().removeIf(entry -> isExpired(entry.getValue()));
    }

    /**
     * Get current OTP count (for monitoring)
     */
    public int getActiveOtpCount() {
        cleanupExpiredOtps();
        return otpStore.size();
    }
}
