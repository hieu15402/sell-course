package com.example.sellcourse.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Admin_TeacherResponse {
    private Long id;
    private String name;
    private String email;
    private String gender;
    private String role; // Giữ định dạng String
    private LocalDateTime createdAt; // Thay đổi thành "createdAt" để đồng bộ với entity User
}
