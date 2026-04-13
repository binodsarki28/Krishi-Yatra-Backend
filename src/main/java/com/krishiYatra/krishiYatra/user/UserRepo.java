package com.krishiYatra.krishiYatra.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<UserEntity, String> {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    @Query(value = "SELECT DATE_FORMAT(u.CREATED_TIME, '%b') as month_label, COUNT(*) FROM USERS u GROUP BY month_label", nativeQuery = true)
    List<Object[]> getRegistrationTrend();
}
