package com.krishiYatra.krishiYatra.user;

import com.krishiYatra.krishiYatra.common.enums.RoleType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<UserEntity, String> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<UserEntity> findByUsername(String username);

    @Query("SELECT u.username FROM UserEntity u JOIN u.roles r WHERE r.roleName = :roleName")
    List<String> findUsernamesByRole(@Param("roleName") RoleType roleName);

    boolean existsByPhoneNumber(String phoneNumber);
}
