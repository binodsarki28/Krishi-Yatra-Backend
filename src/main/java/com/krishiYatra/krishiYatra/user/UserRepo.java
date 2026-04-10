package com.krishiYatra.krishiYatra.user;

import com.krishiYatra.krishiYatra.common.enums.RoleType;
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
    Optional<UserEntity> findByEmail(String email);

    @Query("SELECT u.username FROM UserEntity u JOIN u.roles r WHERE r.roleName = :roleName")
    List<String> findUsernamesByRole(@Param("roleName") RoleType roleName);

    boolean existsByPhoneNumber(String phoneNumber);

    @org.springframework.data.jpa.repository.Query(value = "SELECT FORMATDATETIME(u.CREATED_TIME, 'MMM') as month_label, COUNT(*) FROM USERS u GROUP BY month_label", nativeQuery = true)
    List<Object[]> getRegistrationTrend();
}
