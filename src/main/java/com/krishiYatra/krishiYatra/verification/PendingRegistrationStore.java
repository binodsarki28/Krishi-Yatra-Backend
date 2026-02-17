package com.krishiYatra.krishiYatra.verification;

import com.krishiYatra.krishiYatra.user.dto.UserCreateRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stores pending registration data temporarily
 */
@Service
public class PendingRegistrationStore {

    private final Map<String, RegistrationData> pendingRegistrations = new ConcurrentHashMap<>();
    private static final int VALIDITY_MINUTES = 15;

    public static class RegistrationData {
        public UserCreateRequest userData;
        public LocalDateTime createdAt;

        public RegistrationData(UserCreateRequest userData) {
            this.userData = userData;
            this.createdAt = LocalDateTime.now();
        }
    }

    /**
     * Store registration data
     */
    public void store(String email, UserCreateRequest userData) {
        pendingRegistrations.put(email, new RegistrationData(userData));
    }

    /**
     * Get registration data
     */
    public UserCreateRequest get(String email) {
        RegistrationData data = pendingRegistrations.get(email);
        if (data == null) {
            return null;
        }

        // Check if expired
        if (LocalDateTime.now().isAfter(data.createdAt.plusMinutes(VALIDITY_MINUTES))) {
            pendingRegistrations.remove(email);
            return null;
        }

        return data.userData;
    }

    /**
     * Remove registration data
     */
    public void remove(String email) {
        pendingRegistrations.remove(email);
    }
}
