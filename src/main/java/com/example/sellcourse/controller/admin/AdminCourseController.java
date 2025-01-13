package com.example.sellcourse.controller.admin;

import com.example.sellcourse.dto.response.course.Admin_CourseResponse;
import com.example.sellcourse.service.admin.Admin_CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/courses")
@RequiredArgsConstructor
public class AdminCourseController {

    private final Admin_CourseService courseService;

    @GetMapping
    public ResponseEntity<Page<Admin_CourseResponse>> getAllCourses(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title,asc") String[] sort) {

        Pageable pageable = PageRequest.of(page - 1, size, getSortOrder(sort));
        Page<Admin_CourseResponse> courses = courseService.getCourses(pageable);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Admin_CourseResponse>> searchCourses(
            @RequestParam String keywords,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title,asc") String[] sort) {

        String[] keywordArray = keywords.split("\\s+");
        Pageable pageable = PageRequest.of(page - 1, size, getSortOrder(sort));
        Page<Admin_CourseResponse> courses = courseService.getCoursesByKeywords(keywordArray, pageable);
        return ResponseEntity.ok(courses);
    }

    @PostMapping("/{courseId}/ban")
    public ResponseEntity<String> banCourse(@PathVariable Long courseId) {
        courseService.getBanCourse(courseId);
        return ResponseEntity.ok("Course banned successfully.");
    }

    @PostMapping("/{courseId}/unban")
    public ResponseEntity<String> unbanCourse(@PathVariable Long courseId) {
        courseService.getUnbanCourse(courseId);
        return ResponseEntity.ok("Course unbanned successfully.");
    }

    @GetMapping("/banned")
    public ResponseEntity<Page<Admin_CourseResponse>> getBannedCourses(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title,asc") String[] sort) {

        Pageable pageable = PageRequest.of(page - 1, size, getSortOrder(sort));
        Page<Admin_CourseResponse> courses = courseService.getBannedCourses(pageable);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/active")
    public ResponseEntity<Page<Admin_CourseResponse>> getActiveCourses(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title,asc") String[] sort) {

        Pageable pageable = PageRequest.of(page - 1, size, getSortOrder(sort));
        Page<Admin_CourseResponse> courses = courseService.getActiveCourses(pageable);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Admin_CourseResponse> getCourseDetails(@PathVariable Long id) {
        Admin_CourseResponse response = courseService.getCourseDetails(id);
        return ResponseEntity.ok(response);
    }

    private Sort getSortOrder(String[] sort) {
        String sortBy = sort.length > 0 ? sort[0] : "title";
        String sortDir = sort.length > 1 ? sort[1] : "asc";
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(direction, sortBy);
    }
}
