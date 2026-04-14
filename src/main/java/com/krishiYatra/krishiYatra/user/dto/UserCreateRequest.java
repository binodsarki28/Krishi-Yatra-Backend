package com.krishiYatra.krishiYatra.user.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateRequest {

    @NotNull(message = "Full name is required")
    @Size(min = 5, max = 20, message = "Full name must be greater than 5 letters")
    @Pattern(regexp = "^[A-Z][a-z]+(\\s[A-Z][a-z]+)+$", message = "Full name must be valid.")
    private String fullName;

    @NotNull
    private String username;


    @NotNull
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = "Please enter a valid email address.")
    private String email;

    @NotNull
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$", message = "Password must be at least 8 characters and include uppercase, lowercase, a number, and a special character.")
    private String password;

    @Size(min = 10, max = 10, message = "Mobile number must contain 10 characters")
    @NotNull(message = "Mobile number can not be null")
    @Pattern(regexp = "^(98|97|96|95|94)\\d{8}$", message = "Please enter a valid phone number")
    private String phoneNumber;
}
