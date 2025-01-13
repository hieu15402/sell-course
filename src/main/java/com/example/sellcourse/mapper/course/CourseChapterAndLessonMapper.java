package com.example.sellcourse.mapper.course;

import com.example.sellcourse.dto.response.course.CourseChapterResponse;
import com.example.sellcourse.dto.response.chapter.ChapterResponse;
import com.example.sellcourse.dto.response.lesson.LessonResponse;
import com.example.sellcourse.entities.Course;
import com.example.sellcourse.entities.Chapter;
import com.example.sellcourse.entities.Lesson;
import com.example.sellcourse.exception.AppException;
import com.example.sellcourse.exception.ErrorCode;
import com.example.sellcourse.repository.course.CourseRepository;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CourseChapterAndLessonMapper {

    private final CourseRepository courseRepository;

    public CourseChapterAndLessonMapper(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public CourseChapterResponse getCourseLessonAndLessonContent(Long courseId) {
        // Lấy thông tin khóa học từ repository
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSER_NOT_EXISTED));

        // Chuyển đổi sang CourseChapterResponse
        Set<ChapterResponse> chapterResponses = course.getChapters().stream()
                .map(this::mapToChapterResponse)  // Chuyển đổi từng Chapter sang ChapterResponse
                .collect(Collectors.toSet());

        return CourseChapterResponse.builder()
                .courseId(course.getId())
                .courseTitle(course.getTitle())
                .courseDescription(course.getDescription())
                .chapters(chapterResponses)
                .build();
    }

    private ChapterResponse mapToChapterResponse(Chapter chapter) {
        Set<LessonResponse> lessonResponses = chapter.getLessons().stream()
                .map(this::mapToLessonResponse)  // Chuyển đổi từng Lesson sang LessonResponse
                .collect(Collectors.toSet());

        return ChapterResponse.builder()
                .chapterId(chapter.getId())
                .chapterName(chapter.getChapterName())
                .lessonDto(lessonResponses)
                .build();
    }

    private LessonResponse mapToLessonResponse(Lesson lesson) {
        return LessonResponse.builder()
                .lessonId(lesson.getId())
                .lessonName(lesson.getLessonName())
                .videoUrl(lesson.getVideoUrl())
                .build();
    }
}