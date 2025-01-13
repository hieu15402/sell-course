package com.example.sellcourse.controller.admin;

import com.example.sellcourse.dto.response.user.Admin_TeacherResponse;
import com.example.sellcourse.dto.response.user.Admin_UserResponse;
import com.example.sellcourse.service.admin.Admin_TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/teachers")
@RequiredArgsConstructor
public class AdminTeacherController {

    private final Admin_TeacherService teacherService;

    @GetMapping
    public ResponseEntity<Page<Admin_TeacherResponse>> getAllTeachers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name,asc") String[] sort) {
        Pageable pageable = PageRequest.of(page - 1, size, getSortOrder(sort));
        Page<Admin_TeacherResponse> teachers = teacherService.getTeachers(pageable);
        return ResponseEntity.ok(teachers);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Admin_TeacherResponse>> searchTeachersByKeywords(
            @RequestParam String keywords,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name,asc") String[] sort) {
        String[] keywordArray = keywords.split("\\s+");
        Pageable pageable = PageRequest.of(page - 1, size, getSortOrder(sort));
        Page<Admin_TeacherResponse> teachers = teacherService.searchTeachersByKeywords(keywordArray, pageable);
        return ResponseEntity.ok(teachers);
    }

    @PostMapping("/{userId}/approve")
    public ResponseEntity<String> approveTeacherRegistration(@PathVariable Long userId) {
        teacherService.approveTeacherRegistration(userId);
        return ResponseEntity.ok("Teacher registration approved successfully.");
    }

    @PostMapping("/{userId}/reject")
    public ResponseEntity<String> rejectTeacherRegistration(@PathVariable Long userId) {
        teacherService.rejectTeacherRegistration(userId);
        return ResponseEntity.ok("Teacher registration rejected successfully.");
    }

    @PutMapping("/{userId}/remove-role")
    public ResponseEntity<String> removeTeacherRole(@PathVariable Long userId) {
        teacherService.removeTeacherRole(userId);
        return ResponseEntity.ok("User role updated to USER and registration status set to null successfully");
    }

    @GetMapping("/applications")
    public ResponseEntity<Page<Admin_UserResponse>> getPendingTeacherApplications(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name,asc") String[] sort) {

        Pageable pageable = PageRequest.of(page - 1, size, getSortOrder(sort));
        Page<Admin_UserResponse> applications = teacherService.getPendingTeacherApplications(pageable);
        return ResponseEntity.ok(applications);
    }

    private Sort getSortOrder(String[] sort) {
        String sortBy = sort.length > 0 ? sort[0] : "name";
        String sortDir = sort.length > 1 ? sort[1] : "asc";
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(direction, sortBy);
    }
}
