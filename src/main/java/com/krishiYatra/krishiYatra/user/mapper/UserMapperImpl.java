package com.krishiYatra.krishiYatra.user.mapper;

import com.krishiYatra.krishiYatra.user.UserEntity;
import com.krishiYatra.krishiYatra.user.dto.UserCreateRequest;
import org.springframework.stereotype.Component;

@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserEntity entityToUserCreateRequest(UserCreateRequest request) {
        UserEntity user = new UserEntity();
        user.setFullName(request.getFullName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(request.getPassword());
        user.setActive(true);
        return user;
    }
}
