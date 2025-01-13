package com.example.sellcourse.mapper.course;

import com.example.sellcourse.dto.response.course.CourseResponse;
import com.example.sellcourse.dto.response.course.UploadCourseResponse;
import com.example.sellcourse.dto.resquest.course.CourseRequest;
import com.example.sellcourse.dto.resquest.course.UploadCourseRequest;
import com.example.sellcourse.entities.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CourseMapper {
    @Mapping(source = "author.fullName", target = "author")
    @Mapping(target = "averageRating", ignore = true)
    CourseResponse toCourseResponse(Course course);

    Course toCourse(CourseRequest courseRequest);

    Course updateCourse(UploadCourseRequest request);

    @Mapping(source = "author.fullName", target = "author")
    @Mapping(source = "thumbnail", target = "thumbnail")
    UploadCourseResponse toUploadCourseResponse(Course course);
}
