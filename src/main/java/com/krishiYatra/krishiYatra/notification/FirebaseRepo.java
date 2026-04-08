package com.krishiYatra.krishiYatra.notification;

import com.krishiYatra.krishiYatra.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FirebaseRepo extends JpaRepository<FirebaseEntity, Long> {

    Optional<FirebaseEntity> findByFcmToken(String fcmToken);

    List<FirebaseEntity> findAllByUser(UserEntity user);
}
