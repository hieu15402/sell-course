package com.example.sellcourse.mapper;

import com.example.sellcourse.dto.response.enrollment.BuyCourseResponse;
import com.example.sellcourse.entities.Enrollment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EnrollmentMapper {
    @Mapping(target = "courseLevel", source = "course.courseLevel")
    @Mapping(target = "courseId", source ="course.id")
    @Mapping(target = "title", source = "course.title")
    @Mapping(target = "points", source = "course.points")
    @Mapping(target = "author", source = "course.author.fullName")
    @Mapping(target = "thumbnail", source = "course.thumbnail")
    @Mapping(target = "createAt", source = "course.createdAt")
    BuyCourseResponse toBuyCourseResponse(Enrollment enrollment);
}
