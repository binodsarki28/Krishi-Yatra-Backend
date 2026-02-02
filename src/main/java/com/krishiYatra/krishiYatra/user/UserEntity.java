package com.krishiYatra.krishiYatra.user;

import com.krishiYatra.krishiYatra.address.AddressEntity;
import com.krishiYatra.krishiYatra.db.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "USERS")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class UserEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "USER_GUID")
    private String userId;

    @Column(name = "FULL_NAME")
    private String fullName;

    @Column(name = "USERNAME", unique = true)
    private String username;

    @Column(name = "EMAIL", unique = true)
    private String email;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "USER_GUID"),
            inverseJoinColumns = @JoinColumn(name = "ROLE_GUID")
    )
    private Set<RoleEntity> roles = new HashSet<>();

    @Column(name = "IS_ACTIVE")
    private boolean isActive;

    @Column(name = "DELETED_AT")
    private LocalDateTime deletedAt;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private AddressEntity address;

}
