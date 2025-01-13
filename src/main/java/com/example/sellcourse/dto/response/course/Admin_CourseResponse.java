package com.example.sellcourse.dto.response.course;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Admin_CourseResponse {
    private Long id;
    private Long authorId; // Thêm trường này vào DTO
    private String title;
    private String description;
    private Boolean enabled;
    private String authorName;
    private String language;
    private String level;
    private Integer duration;
    private Long points;
    private String thumbnail;
    private LocalDateTime createdAt; // Định dạng đúng kiểu dữ liệu
    private LocalDateTime updatedAt;
}
