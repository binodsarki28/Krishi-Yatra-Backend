package com.krishiYatra.krishiYatra.verification;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks verified emails temporarily (15 minutes)
 */
@Service
public class VerifiedEmailTracker {

    private final Map<String, LocalDateTime> verifiedEmails = new ConcurrentHashMap<>();
    private static final int VERIFICATION_VALIDITY_MINUTES = 15;

    /**
     * Mark email as verified
     */
    public void markAsVerified(String email) {
        verifiedEmails.put(email, LocalDateTime.now());
    }

    /**
     * Check if email is verified and still valid
     */
    public boolean isVerified(String email) {
        LocalDateTime verifiedAt = verifiedEmails.get(email);
        if (verifiedAt == null) {
            return false;
        }

        // Check if verification is still valid (within 15 minutes)
        if (LocalDateTime.now().isAfter(verifiedAt.plusMinutes(VERIFICATION_VALIDITY_MINUTES))) {
            verifiedEmails.remove(email);
            return false;
        }

        return true;
    }

    /**
     * Remove email from verified list (after successful registration)
     */
    public void removeVerification(String email) {
        verifiedEmails.remove(email);
    }

    /**
     * Clean up expired verifications
     */
    public void cleanupExpired() {
        LocalDateTime now = LocalDateTime.now();
        verifiedEmails.entrySet().removeIf(entry -> 
            now.isAfter(entry.getValue().plusMinutes(VERIFICATION_VALIDITY_MINUTES))
        );
    }
}
