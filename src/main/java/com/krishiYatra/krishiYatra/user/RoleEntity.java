package com.krishiYatra.krishiYatra.user;

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

    @Column(name = "ROLE_NAME", unique = true)
    private String roleName;
}
