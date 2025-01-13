package com.example.sellcourse.service;

import com.example.sellcourse.dto.response.chapter.ChapterCreateResponse;
import com.example.sellcourse.dto.resquest.chapter.ChapterCreateRequest;
import com.example.sellcourse.entities.Chapter;
import com.example.sellcourse.entities.Course;
import com.example.sellcourse.entities.Lesson;
import com.example.sellcourse.exception.AppException;
import com.example.sellcourse.exception.ErrorCode;
import com.example.sellcourse.mapper.ChapterMapper;
import com.example.sellcourse.repository.ChapterRepository;
import com.example.sellcourse.repository.LessonRepository;
import com.example.sellcourse.repository.course.CourseRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ChapterService {
    ChapterRepository chapterRepository;
    ChapterMapper chapterMapper;
    CourseRepository courseRepository;
    CloudinaryService cloudinaryService;
    LessonRepository lessonRepository;

    @PreAuthorize("isAuthenticated() and hasAnyAuthority('ADMIN', 'TEACHER')")
    public ChapterCreateResponse createChapter(ChapterCreateRequest request, MultipartFile lessonVideo) throws IOException {
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new AppException(ErrorCode.COURSER_NOT_EXISTED));

        Chapter chapter = chapterRepository.findByChapterNameAndCourse(request.getChapterName(), course)
                .orElseGet(() -> {
                    Chapter newChapter = chapterMapper.toChapter(request);
                    newChapter.setCourse(course);
                    chapterRepository.save(newChapter);
                    return newChapter;
                });

        String videoUrl = cloudinaryService.uploadVideoChunked(lessonVideo, "courses").get("url").toString();

        Lesson lesson = Lesson.builder()
                .chapter(chapter)
                .contentType("video")
                .videoUrl(videoUrl)
                .lessonName(request.getLessonName())
                .description("Video content for the lesson")
                .build();

        lessonRepository.save(lesson);

        chapter.getLessons().add(lesson);

        return chapterMapper.toChapterCreateResponse(chapter);
    }
}
