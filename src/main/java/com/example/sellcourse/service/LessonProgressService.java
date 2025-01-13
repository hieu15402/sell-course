package com.example.sellcourse.service;

import com.example.sellcourse.dto.response.lesson.LessonResponseV2;
import com.example.sellcourse.dto.response.progress.UserCompletionResponse;
import com.example.sellcourse.dto.response.progress.LessonProgressResponse;
import com.example.sellcourse.dto.resquest.progress.LessonProgressRequest;
import com.example.sellcourse.entities.Course;
import com.example.sellcourse.entities.Lesson;
import com.example.sellcourse.entities.LessonProgress;
import com.example.sellcourse.entities.User;
import com.example.sellcourse.exception.AppException;
import com.example.sellcourse.exception.ErrorCode;
import com.example.sellcourse.repository.EnrollmentRepository;
import com.example.sellcourse.repository.LessonProgressRepository;
import com.example.sellcourse.repository.LessonRepository;
import com.example.sellcourse.repository.course.CourseRepository;
import com.example.sellcourse.repository.user.UserRepository;
import com.example.sellcourse.utils.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class LessonProgressService {
    UserRepository userRepository;
    CourseRepository courseRepository;
    LessonRepository lessonRepository;
    EnrollmentRepository enrollmentRepository;
    LessonProgressRepository lessonProgressRepository;

    @PreAuthorize("isAuthenticated()")
    public UserCompletionResponse calculateCompletion(Long courseId) {

        String email = SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_INVALID));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_EXISTED));

        if (!enrollmentRepository.existsByUserAndCourse(user, course)) {
            throw new AppException(ErrorCode.COURSE_ACCESS_DENIED);
        }

        int totalLessons = course.getChapters().stream()
                .mapToInt(chapter -> chapter.getLessons().size())
                .sum();

        long completedLessons = lessonProgressRepository.countByUserAndCourseAndCompleted(user, course, true);

        if (totalLessons == 0) {
            return UserCompletionResponse.builder()
                    .totalLessonComplete(0L)
                    .totalLessons(0L)
                    .completionPercentage(BigDecimal.ZERO)
                    .build();
        }
        List<LessonResponseV2> listCompletedLessons = lessonProgressRepository.findByUserAndCourse(user, course, true)
                .stream()
                .map(progress -> LessonResponseV2.builder()
                        .lessonId(progress.getLesson().getId())
                        .lessonName(progress.getLesson().getLessonName())
                        .build())
                .toList();
        BigDecimal completionPercentage = BigDecimal.valueOf(completedLessons)
                .divide(BigDecimal.valueOf(totalLessons), 3, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP);

        return UserCompletionResponse.builder()
                .totalLessonComplete(completedLessons)
                .totalLessons((long) totalLessons)
                .lessonCompletes(listCompletedLessons)
                .completionPercentage(completionPercentage)
                .build();
    }
    @PreAuthorize("isAuthenticated()")
    @Transactional
    public LessonProgressResponse markLessonAsCompleted(LessonProgressRequest request) {
        String email = SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_INVALID));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Lesson lesson = lessonRepository.findById(request.getLessonId())
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_EXISTED));

        Course course = lesson.getChapter().getCourse();

        if (!enrollmentRepository.existsByUserAndCourse(user, course)) {
            throw new AppException(ErrorCode.COURSE_ACCESS_DENIED);
        }

        LessonProgress existingProgress = lessonProgressRepository.findByUserAndLesson(user, lesson);
        if (existingProgress != null) {
            return LessonProgressResponse.builder()
                    .lessonId(existingProgress.getLesson().getId())
                    .lessonName(existingProgress.getLesson().getLessonName())
                    .isComplete(existingProgress.getCompleted())
                    .build();
        }

        lessonProgressRepository.save(LessonProgress.builder()
                .user(user)
                .lesson(lesson)
                .completed(true)
                .build());

        return LessonProgressResponse.builder()
                .lessonId(lesson.getId())
                .lessonName(lesson.getLessonName())
                .isComplete(true)
                .build();
    }
}
