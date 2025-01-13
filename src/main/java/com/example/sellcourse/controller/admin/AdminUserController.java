package com.example.sellcourse.controller.admin;

import com.example.sellcourse.dto.response.user.Admin_UserResponse;
import com.example.sellcourse.dto.response.user.TeacherApplicationDetailResponse;
import com.example.sellcourse.enums.RoleName;
import com.example.sellcourse.service.admin.Admin_UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final Admin_UserService userService;

    @GetMapping
    public ResponseEntity<Page<Admin_UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name,asc") String[] sort) {
        
        Pageable pageable = PageRequest.of(page - 1, size, getSortOrder(sort));
        Page<Admin_UserResponse> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Admin_UserResponse>> searchUsersByKeywords(
            @RequestParam String keywords,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name,asc") String[] sort) {

        String[] keywordArray = keywords.split("\\s+");
        Pageable pageable = PageRequest.of(page - 1, size, getSortOrder(sort));
        Page<Admin_UserResponse> users = userService.searchUsersByKeywords(keywordArray, pageable);
        return ResponseEntity.ok(users);
    }

    @PostMapping("/ban/{userId}")
    public ResponseEntity<String> banUser(@PathVariable Long userId) {
        userService.banUser(userId);
        return ResponseEntity.ok("User banned successfully");
    }

    @PostMapping("/unban/{userId}")
    public ResponseEntity<String> unbanUser(@PathVariable Long userId) {
        userService.unbanUser(userId);
        return ResponseEntity.ok("User unbanned successfully");
    }

    @PutMapping("/{userId}/role")
    public ResponseEntity<String> updateRole(
            @PathVariable Long userId,
            @RequestParam RoleName roleName) {
        userService.updateUserRole(userId, roleName);
        return ResponseEntity.ok("User role updated successfully");
    }
    @GetMapping("/{userId}/details")
    public ResponseEntity<TeacherApplicationDetailResponse> getUserApplicationDetail(@PathVariable Long userId) {
        TeacherApplicationDetailResponse details = userService.getUserApplicationDetail(userId);
        return ResponseEntity.ok(details);
    }

    private Sort getSortOrder(String[] sort) {
        String sortBy = sort.length > 0 ? sort[0] : "name";
        String sortDir = sort.length > 1 ? sort[1] : "asc";

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(direction, sortBy);
    }
}
