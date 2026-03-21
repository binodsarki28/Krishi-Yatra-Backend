package com.krishiYatra.krishiYatra.delivery;

import com.krishiYatra.krishiYatra.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import com.krishiYatra.krishiYatra.common.enums.VerificationStatus;

@Repository
public interface DeliveryRepo extends JpaRepository<DeliveryEntity, String> {
    List<DeliveryEntity> findByStatus(VerificationStatus status);
    Optional<DeliveryEntity> findByUser(UserEntity user);
    Optional<DeliveryEntity> findByUser_Username(String username);
    long countByStatus(VerificationStatus status);
}
