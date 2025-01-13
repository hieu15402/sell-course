package com.example.sellcourse.dto.response.lesson;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonResponseV2 {
    Long lessonId;
    String lessonName;
}
