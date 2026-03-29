package com.krishiYatra.krishiYatra.address;

import com.krishiYatra.krishiYatra.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AddressRepo extends JpaRepository<AddressEntity, String> {
    Optional<AddressEntity> findByUser(UserEntity user);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM AddressEntity a WHERE a.user = :user")
    void deleteByUser(@Param("user") UserEntity user);
}
