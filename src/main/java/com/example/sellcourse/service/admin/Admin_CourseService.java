package com.example.sellcourse.service.admin;

import com.example.sellcourse.dto.response.course.Admin_CourseResponse;
import com.example.sellcourse.entities.Course;
import com.example.sellcourse.exception.AppException;
import com.example.sellcourse.exception.ErrorCode;
import com.example.sellcourse.mapper.course.Admin_CourseMapper;
import com.example.sellcourse.repository.course.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class Admin_CourseService {

    private final CourseRepository courseRepository;
    private final Admin_CourseMapper courseMapper;

    public Page<Admin_CourseResponse> getCourses(Pageable pageable) {
        return courseRepository.findAll(pageable)
                .map(courseMapper::toCourseResponse);
    }

    public Page<Admin_CourseResponse> getCoursesByKeywords(String[] keywords, Pageable pageable) {
        return courseRepository.searchByMultipleKeywords(keywords, pageable)
                .map(courseMapper::toCourseResponse);
    }

    @Transactional
    public void getBanCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        if (!course.getEnabled()) {
            throw new AppException(ErrorCode.COURSE_ALREADY_BANNED);
        }

        course.setEnabled(false);
        courseRepository.save(course);
    }

    @Transactional
    public void getUnbanCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        if (course.getEnabled()) {
            throw new AppException(ErrorCode.COURSE_NOT_BANNED);
        }

        course.setEnabled(true);
        courseRepository.save(course);
    }

    public Page<Admin_CourseResponse> getBannedCourses(Pageable pageable) {
        return courseRepository.findByEnabled(false, pageable)
                .map(courseMapper::toCourseResponse);
    }

    public Page<Admin_CourseResponse> getActiveCourses(Pageable pageable) {
        return courseRepository.findByEnabled(true, pageable)
                .map(courseMapper::toCourseResponse);
    }

    public Admin_CourseResponse getCourseDetails(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        return Admin_CourseResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .enabled(course.getEnabled())
                .authorId(course.getAuthor() != null ? course.getAuthor().getId() : null)
                .authorName(course.getAuthor() != null ? course.getAuthor().getFullName() : null)
                .language(course.getLanguage())
                .level(course.getCourseLevel().toString())
                .duration(course.getDuration())
                .points(course.getPoints())
                .thumbnail(course.getThumbnail()) // Thêm trường này
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .build();
    }



}
