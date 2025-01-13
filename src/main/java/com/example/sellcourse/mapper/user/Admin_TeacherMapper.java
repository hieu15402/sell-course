package com.example.sellcourse.mapper.user;

import com.example.sellcourse.dto.response.user.Admin_TeacherResponse;
import com.example.sellcourse.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface Admin_TeacherMapper {
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "role.roleName", target = "role")
    Admin_TeacherResponse toTeacherResponse(User user);
}
