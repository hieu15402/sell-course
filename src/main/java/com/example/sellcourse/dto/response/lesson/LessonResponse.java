package com.example.sellcourse.dto.response.lesson;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonResponse {
    Long lessonId;
    String lessonName;
    String videoUrl;
    String description;
}
