package com.krishiYatra.krishiYatra.verification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpData implements Serializable {
    private String email;
    private String otpCode;
    private LocalDateTime createdAt;
    private int attempts;
}
