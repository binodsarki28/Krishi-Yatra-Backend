package com.krishiYatra.krishiYatra.farmer;

import com.krishiYatra.krishiYatra.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FarmerRepo extends JpaRepository<FarmerEntity, String> {
    List<FarmerEntity> findByVerifiedFalse();
    Optional<FarmerEntity> findByUser(UserEntity user);
    Optional<FarmerEntity> findByUser_Username(String username);
    long countByVerifiedFalse();
}
