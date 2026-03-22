package com.krishiYatra.krishiYatra.address;

import com.krishiYatra.krishiYatra.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AddressRepo extends JpaRepository<AddressEntity, String> {
    Optional<AddressEntity> findByUser(UserEntity user);
}
