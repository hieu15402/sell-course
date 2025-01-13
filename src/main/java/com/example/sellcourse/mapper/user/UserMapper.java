package com.example.sellcourse.mapper.user;

import com.example.sellcourse.dto.response.user.UserResponse;
import com.example.sellcourse.dto.resquest.user.UserCreateRequest;
import com.example.sellcourse.entities.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(UserCreateRequest request);
    UserResponse toUserResponse(User user);
}
