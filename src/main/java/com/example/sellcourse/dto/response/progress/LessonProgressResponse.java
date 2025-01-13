package com.example.sellcourse.dto.response.progress;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonProgressResponse {

    Long lessonId;
    String lessonName;
    Boolean isComplete;
}
