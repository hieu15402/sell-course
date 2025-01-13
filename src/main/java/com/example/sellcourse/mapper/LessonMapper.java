package com.example.sellcourse.mapper;

import com.example.sellcourse.dto.response.lesson.LessonResponse;
import com.example.sellcourse.entities.Lesson;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LessonMapper {
    @Mapping(target = "lessonId", source = "id")
    @Mapping(target = "lessonName", source = "lessonName")
    @Mapping(target = "videoUrl", source = "videoUrl")
    @Mapping(target = "description", source = "description")
    LessonResponse toLessonResponse(Lesson lesson);
}
