package com.krishiYatra.krishiYatra.delivery;

import com.krishiYatra.krishiYatra.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import com.krishiYatra.krishiYatra.common.enums.VerificationStatus;

@Repository
public interface DeliveryRepo extends JpaRepository<DeliveryEntity, String> {
    Optional<DeliveryEntity> findByUser(UserEntity user);
    Optional<DeliveryEntity> findByUser_Username(String username);
    long countByStatus(VerificationStatus status);

    @Query("SELECT d.user.username FROM DeliveryEntity d WHERE d.status = :status")
    java.util.List<String> findUsernamesByStatus(@Param("status") VerificationStatus status);
}
