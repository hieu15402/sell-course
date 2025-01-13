package com.example.sellcourse.dto.response.progress;

import com.example.sellcourse.dto.response.lesson.LessonResponseV2;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCompletionResponse {
    Long totalLessonComplete;
    Long totalLessons;
    BigDecimal completionPercentage;

    @Builder.Default
    List<LessonResponseV2> lessonCompletes = Collections.emptyList();
}
