package com.example.sellcourse.controller;

import com.example.sellcourse.dto.response.general.ApiResponse;
import com.example.sellcourse.dto.response.progress.LessonProgressResponse;
import com.example.sellcourse.dto.response.progress.UserCompletionResponse;
import com.example.sellcourse.dto.resquest.progress.LessonProgressRequest;
import com.example.sellcourse.service.LessonProgressService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/progresses")
@Slf4j
public class LessonProgressController {
    LessonProgressService lessonProgressService;

    @GetMapping("/calculate-completion/{courseId}")
    ApiResponse<UserCompletionResponse> calculateCompletion (@PathVariable Long courseId) {

        return ApiResponse.<UserCompletionResponse>builder()
                .code(HttpStatus.OK.value())
                .result(lessonProgressService.calculateCompletion(courseId))
                .build();
    }

    @PostMapping("/update-lesson-progress")
    ApiResponse<LessonProgressResponse> markLessonAsCompleted(@RequestBody LessonProgressRequest request) {
        return ApiResponse.<LessonProgressResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Update successfully")
                .result(lessonProgressService.markLessonAsCompleted(request))
                .build();
    }
}
