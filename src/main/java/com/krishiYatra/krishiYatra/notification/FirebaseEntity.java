package com.krishiYatra.krishiYatra.notification;

import com.krishiYatra.krishiYatra.user.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "FIREBASE_TOKENS")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FirebaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "FCM_TOKEN", length = 512, nullable = false)
    private String fcmToken;

    @Column(name = "DEVICE_NAME")
    private String deviceName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_GUID", nullable = false)
    private UserEntity user;
}
