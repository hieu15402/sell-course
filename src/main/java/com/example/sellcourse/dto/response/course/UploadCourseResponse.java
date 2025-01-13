package com.example.sellcourse.dto.response.course;

import com.example.sellcourse.enums.CourseLevel;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UploadCourseResponse {

    String author;
    String title;
    String description;
    CourseLevel courseLevel;
    Integer duration;
    BigDecimal price;
    String thumbnail;
    String videoUrl;
}
