package com.example.sellcourse.service;

import com.example.sellcourse.dto.response.enrollment.BuyCourseResponse;
import com.example.sellcourse.dto.resquest.enrollment.BuyCourseRequest;
import com.example.sellcourse.entities.Course;
import com.example.sellcourse.entities.Enrollment;
import com.example.sellcourse.entities.User;
import com.example.sellcourse.exception.AppException;
import com.example.sellcourse.exception.ErrorCode;
import com.example.sellcourse.mapper.EnrollmentMapper;
import com.example.sellcourse.repository.EnrollmentRepository;
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

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class EnrollmentService {
    EnrollmentRepository enrollmentRepository;
    UserRepository userRepository;
    CourseRepository courseRepository;
    EnrollmentMapper enrollmentMapper;

    @PreAuthorize("isAuthenticated()")
    public List<BuyCourseResponse> getEnrollmentsByCurrentUser(){
        String email = SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_INVALID));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        List<Enrollment> enrollments = enrollmentRepository.findByUser(user);

        return enrollments.stream().map(enrollmentMapper::toBuyCourseResponse).toList();
    }

    @Transactional
    @PreAuthorize("isAuthenticated()")
    public BuyCourseResponse buyCourse(BuyCourseRequest request){
        String email = SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_INVALID));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new AppException(ErrorCode.COURSER_NOT_EXISTED));

        if (enrollmentRepository.existsByUserAndCourse(user, course)) {
            throw new AppException(ErrorCode.COURSE_ALREADY_PURCHASED);
        }

        Long pointsCourse = Objects.requireNonNull(course.getPoints(), "Course points cannot be null");
        Long pointsUser = Objects.requireNonNull(user.getPoints(), "User points cannot be null");

        if(pointsUser < pointsCourse){
            throw new AppException(ErrorCode.BUY_COURSE_INVALID);
        }
        user.setPoints(pointsUser - pointsCourse);
        userRepository.save(user);

        Enrollment enrollment = Enrollment.builder()
                .user(user)
                .course(course)
                .build();

        enrollmentRepository.save(enrollment);
        return enrollmentMapper.toBuyCourseResponse(enrollment);
    }
}
