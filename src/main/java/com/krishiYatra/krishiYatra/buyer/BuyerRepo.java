package com.krishiYatra.krishiYatra.buyer;

import com.krishiYatra.krishiYatra.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import com.krishiYatra.krishiYatra.common.enums.VerificationStatus;

@Repository
public interface BuyerRepo extends JpaRepository<BuyerEntity, String> {
    List<BuyerEntity> findByStatus(VerificationStatus status);
    Optional<BuyerEntity> findByUser(UserEntity user);
    Optional<BuyerEntity> findByUser_Username(String username);
    long countByStatus(VerificationStatus status);
}
