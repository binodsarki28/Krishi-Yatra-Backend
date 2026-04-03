package com.krishiYatra.krishiYatra.notification;

import com.krishiYatra.krishiYatra.common.enums.NotificationCategory;
import com.krishiYatra.krishiYatra.db.Auditable;
import com.krishiYatra.krishiYatra.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "NOTIFICATIONS")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class NotificationEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(name = "CATEGORY")
    private NotificationCategory category;

    @Builder.Default
    @Column(name = "IS_READ")
    private boolean read = false;

    @Builder.Default
    @Column(name = "IS_DELETED")
    private boolean deleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_GUID", nullable = false)
    private UserEntity user;
}
