package com.krishiYatra.krishiYatra.delivery;

import com.krishiYatra.krishiYatra.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryRepo extends JpaRepository<DeliveryEntity, String> {
    List<DeliveryEntity> findByVerifiedFalse();
    Optional<DeliveryEntity> findByUser(UserEntity user);
    Optional<DeliveryEntity> findByUser_Username(String username);
    long countByVerifiedFalse();
}
