package com.krishiYatra.krishiYatra.notification.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FcmTokenRequest {
    private String fcmToken;
    private String deviceName;
}
