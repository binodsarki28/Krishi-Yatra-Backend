package com.krishiYatra.krishiYatra.farmer;

import com.krishiYatra.krishiYatra.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import com.krishiYatra.krishiYatra.common.enums.VerificationStatus;

@Repository
public interface FarmerRepo extends JpaRepository<FarmerEntity, String> {
    List<FarmerEntity> findByStatus(VerificationStatus status);
    Optional<FarmerEntity> findByUser(UserEntity user);
    Optional<FarmerEntity> findByUser_Username(String username);
    long countByStatus(VerificationStatus status);
}
