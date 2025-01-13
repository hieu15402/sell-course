package com.example.sellcourse.mapper.course;

import com.example.sellcourse.dto.response.course.Admin_CourseResponse;
import com.example.sellcourse.entities.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface Admin_CourseMapper {

    @Mapping(source = "courseLevel", target = "level") // ánh xạ enum CourseLevel thành chuỗi level
    @Mapping(source = "author.fullName", target = "authorName", defaultValue = "Unknown") // ánh xạ tên tác giả hoặc gán giá trị mặc định
    @Mapping(source = "createdAt", target = "createdAt") // ánh xạ trực tiếp
    @Mapping(source = "updatedAt", target = "updatedAt") // ánh xạ trực tiếp
    Admin_CourseResponse toCourseResponse(Course course);
}
