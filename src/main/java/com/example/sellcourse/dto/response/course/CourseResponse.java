package com.example.sellcourse.dto.response.course;

import com.example.sellcourse.enums.CourseLevel;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CourseResponse {

    Long id;
    String author;
    String title;
    String description;
    Integer duration;
    String language;
    CourseLevel courseLevel;
    String thumbnail;
    String videoUrl;
    Double averageRating;
    Long points;

}
