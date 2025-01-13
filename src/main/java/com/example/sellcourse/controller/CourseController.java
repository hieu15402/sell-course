package com.example.sellcourse.controller;

import com.example.sellcourse.dto.response.course.CourseChapterResponse;
import com.example.sellcourse.dto.response.course.CourseResponse;
import com.example.sellcourse.dto.response.course.UploadCourseResponse;
import com.example.sellcourse.dto.response.general.ApiResponse;
import com.example.sellcourse.dto.response.general.PageResponse;
import com.example.sellcourse.dto.resquest.course.UploadCourseRequest;
import com.example.sellcourse.entities.Course;
import com.example.sellcourse.service.CourseService;
import com.nimbusds.jose.shaded.gson.Gson;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import com.turkraft.springfilter.boot.Filter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/courses")
@Slf4j
public class CourseController {

    CourseService courseService;

    @GetMapping
    ApiResponse<PageResponse<CourseResponse>> getCourses(
            @Filter Specification<Course> spec,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "6") int size) {

        PageResponse<CourseResponse> result = courseService.getCourses(spec, page, size);

        return ApiResponse.<PageResponse<CourseResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Get All Courses Successfully")
                .result(result)
                .build();
    }

    @GetMapping("/title")
    public ApiResponse<List<String>> getTitleSuggestions(@RequestParam("query") String query) {
        List<String> suggestions = courseService.getTitleSuggestions(query);
        return ApiResponse.<List<String>>builder()
                .code(HttpStatus.OK.value())
                .message("Suggestions fetched successfully")
                .result(suggestions)
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<CourseResponse> getCourseById(@PathVariable Long id){
        var result = courseService.getCourseById(id);

        return ApiResponse.<CourseResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Get Course Successfully")
                .result(result)
                .build();
    }

    @GetMapping("/my-courses")
    ApiResponse<List<CourseResponse>> getOwnCourses(){
        var result = courseService.getOwnCourses();

        return ApiResponse.<List<CourseResponse>>builder()
                .code(HttpStatus.OK.value())
                .result(result)
                .build();
    }

    @GetMapping("/info-course/{id}")
    ApiResponse<CourseChapterResponse> infoCourse(@PathVariable Long id){
        return ApiResponse.<CourseChapterResponse>builder()
                .code(HttpStatus.OK.value())
                .result(courseService.getAllInfoCourse(id))
                .build();
    }

    @PostMapping(value = "/upload-course", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Upload course with JSON and files",
            description = "Uploads course details (JSON) and associated course file and thumbnail."
    )
    public ApiResponse<UploadCourseResponse> uploadCourse(
            @RequestPart(value = "course")
            @Parameter(
                    description = "The course details in JSON format, including title, description, duration, and price.",
                    schema = @Schema(implementation = UploadCourseRequest.class)
            ) String requestBodyAsJson,
            @RequestPart("file") MultipartFile courseFile,
            @RequestPart("thumbnail") MultipartFile thumbnail) throws IOException {

        // Chuyển đổi chuỗi JSON thành đối tượng UploadCourseRequest
        UploadCourseRequest request = new Gson().fromJson(requestBodyAsJson, UploadCourseRequest.class);

        // Gọi service để xử lý dữ liệu
        return ApiResponse.<UploadCourseResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Upload Course Successfully")
                .result(courseService.uploadCourse(request, courseFile, thumbnail))
                .build();
    }


}
