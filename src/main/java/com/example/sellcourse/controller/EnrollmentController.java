package com.example.sellcourse.controller;

import com.example.sellcourse.dto.response.enrollment.BuyCourseResponse;
import com.example.sellcourse.dto.response.general.ApiResponse;
import com.example.sellcourse.dto.resquest.enrollment.BuyCourseRequest;
import com.example.sellcourse.service.EnrollmentService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/enrollments")
@Slf4j
public class EnrollmentController {

    EnrollmentService enrollmentService;

    @PostMapping
    ApiResponse<BuyCourseResponse> buyCourse(@RequestBody @Valid BuyCourseRequest request) {
        return ApiResponse.<BuyCourseResponse>builder()
                .code(HttpStatus.OK.value())
                .result(enrollmentService.buyCourse(request))
                .build();
    }

    @GetMapping("/my-courses")
    ApiResponse<List<BuyCourseResponse>> getEnrollmentsByCurrentUser() {
        return ApiResponse.<List<BuyCourseResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("My Courses")
                .result(enrollmentService.getEnrollmentsByCurrentUser())
                .build();
    }

}
