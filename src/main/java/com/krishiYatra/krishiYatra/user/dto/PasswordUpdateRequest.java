package com.krishiYatra.krishiYatra.user.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordUpdateRequest {

    @NotNull(message = "Current password is required")
    private String currentPassword;

    @NotNull(message = "New password is required")
    @jakarta.validation.constraints.Size(min = 6, message = "Password must be at least 6 characters long")
    private String newPassword;
}
