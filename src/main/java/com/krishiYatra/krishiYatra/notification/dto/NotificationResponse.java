package com.krishiYatra.krishiYatra.notification.dto;

import com.krishiYatra.krishiYatra.common.enums.NotificationCategory;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponse {
    private Long id;
    private String title;
    private String body;
    private NotificationCategory category;
    private boolean read;
    private String actionUrl;
    private LocalDateTime createdAt;
}
