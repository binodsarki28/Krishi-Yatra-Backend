package com.krishiYatra.krishiYatra.user;

import com.krishiYatra.krishiYatra.common.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepo extends JpaRepository<RoleEntity, String> {
    Optional<RoleEntity> findByRoleName(RoleType roleName);
}
