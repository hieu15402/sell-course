package com.example.sellcourse.dto.response.enrollment;

import com.example.sellcourse.enums.CourseLevel;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BuyCourseResponse {
    Long courseId;
    String title;
    String author;
    CourseLevel courseLevel;
    String thumbnail;
    Long points;
    LocalDateTime createAt;
}
