package com.example.sellcourse.dto.response.user;

import com.example.sellcourse.enums.RoleName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeacherApplicationDetailResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String gender;
    private String avatar;
    private LocalDate dob;               // Giữ nguyên kiểu LocalDate
    private String cvUrl;
    private String certificate;
    private String facebookLink;
    private String description;
    private Double yearsOfExperience;
    private Long points;
    private RoleName role;
}