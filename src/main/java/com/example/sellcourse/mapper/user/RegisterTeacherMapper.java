package com.example.sellcourse.mapper.user;

import com.example.sellcourse.dto.response.user.UserRegisterTeacherResponse;
import com.example.sellcourse.dto.resquest.user.UserRegisterTeacherRequest;
import com.example.sellcourse.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RegisterTeacherMapper {

    void toUpdateTeacher(UserRegisterTeacherRequest request, @MappingTarget User user);

    @Mapping(target = "id", source = "id") // Thêm ánh xạ cho trường id
    UserRegisterTeacherResponse toTeacherResponse(User user);
}
