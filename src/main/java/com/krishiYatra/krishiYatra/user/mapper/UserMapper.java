package com.krishiYatra.krishiYatra.user.mapper;

import com.krishiYatra.krishiYatra.user.UserEntity;
import com.krishiYatra.krishiYatra.user.dto.UserCreateRequest;

public interface UserMapper {

    UserEntity entityToUserCreateRequest(UserCreateRequest request);
}
