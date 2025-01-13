package com.example.sellcourse.mapper.user;

import com.example.sellcourse.dto.response.user.UserProfileResponse;
import com.example.sellcourse.dto.resquest.user.UserProfileRequest;
import com.example.sellcourse.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    void updateUser(UserProfileRequest request, @MappingTarget User user);

    UserProfileResponse getInfoUser(User user);
}
