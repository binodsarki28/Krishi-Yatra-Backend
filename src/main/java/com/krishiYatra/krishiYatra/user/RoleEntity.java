package com.krishiYatra.krishiYatra.user;

import com.krishiYatra.krishiYatra.common.enums.RoleType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ROLES")
@Getter
@Setter
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ROLE_GUID")
    private String roleId;

    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE_NAME", unique = true)
    private RoleType roleName;
}
