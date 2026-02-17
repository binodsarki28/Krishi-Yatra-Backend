package com.krishiYatra.krishiYatra.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OtpRequestDto {
    
    @NotNull(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
}
