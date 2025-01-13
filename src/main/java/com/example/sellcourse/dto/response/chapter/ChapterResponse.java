package com.example.sellcourse.dto.response.chapter;

import com.example.sellcourse.dto.response.lesson.LessonResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChapterResponse {
    Long chapterId;
    String chapterName;

    @Builder.Default
    Set<LessonResponse> lessonDto = new HashSet<>();
}
