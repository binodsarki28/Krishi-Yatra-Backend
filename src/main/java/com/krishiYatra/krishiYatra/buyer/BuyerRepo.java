package com.krishiYatra.krishiYatra.buyer;

import com.krishiYatra.krishiYatra.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BuyerRepo extends JpaRepository<BuyerEntity, String> {
    List<BuyerEntity> findByIsVerifiedFalse();
    Optional<BuyerEntity> findByUser(UserEntity user);
}
