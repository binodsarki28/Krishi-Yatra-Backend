package com.krishiYatra.krishiYatra.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<UserEntity, String> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<UserEntity> findByUsername(String username);

    @org.springframework.data.jpa.repository.Query("SELECT u.username FROM UserEntity u JOIN u.roles r WHERE r.roleName = :roleName")
    java.util.List<String> findUsernamesByRole(@org.springframework.data.repository.query.Param("roleName") com.krishiYatra.krishiYatra.common.enums.RoleType roleName);
}
