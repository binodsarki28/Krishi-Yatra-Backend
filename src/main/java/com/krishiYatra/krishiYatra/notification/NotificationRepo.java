package com.krishiYatra.krishiYatra.notification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.krishiYatra.krishiYatra.user.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepo extends JpaRepository<NotificationEntity, Long> {
    Page<NotificationEntity> findAllByUserAndDeletedFalse(UserEntity user, Pageable pageable);
    long countByUserAndReadFalseAndDeletedFalse(UserEntity user);

    @Modifying
    @Query("UPDATE NotificationEntity n SET n.read = true WHERE n.user = :user AND n.read = false AND n.deleted = false")
    void markAllAsRead(@Param("user") UserEntity user);
}
