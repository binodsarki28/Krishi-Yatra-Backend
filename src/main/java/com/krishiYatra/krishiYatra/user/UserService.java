package com.krishiYatra.krishiYatra.user;

import com.krishiYatra.krishiYatra.common.response.ServerResponse;
import com.krishiYatra.krishiYatra.user.dto.UserCreateRequest;
import com.krishiYatra.krishiYatra.user.mapper.UserMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserMapper userMapper;
    private final UserRepo userRepo;

    public UserService(UserMapper userMapper,
                       UserRepo userRepo) {
        this.userMapper = userMapper;
        this.userRepo = userRepo;
    }

    public ServerResponse registerUser(UserCreateRequest request) {
        UserEntity user = userMapper.entityToUserCreateRequest(request);
        userRepo.save(user);
        return ServerResponse.successResponse(UserConst.USER_CREATED, HttpStatus.CREATED);
    }
}
