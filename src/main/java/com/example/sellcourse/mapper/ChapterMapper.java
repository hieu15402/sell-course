package com.example.sellcourse.mapper;

import com.example.sellcourse.dto.response.chapter.ChapterCreateResponse;
import com.example.sellcourse.dto.resquest.chapter.ChapterCreateRequest;
import com.example.sellcourse.entities.Chapter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChapterMapper {

    @Mapping(source = "courseId", target = "course.id")
    @Mapping(source = "chapterName", target = "chapterName")
    @Mapping(source = "description", target = "description")
    Chapter toChapter(ChapterCreateRequest request);

    @Mapping(target = "userId", source = "course.author.id")
    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "courseTitle", source = "course.title")
    @Mapping(target = "chapterId", source = "id")
    @Mapping(target = "lessons", source = "lessons")
    ChapterCreateResponse toChapterCreateResponse(Chapter chapter);
}
