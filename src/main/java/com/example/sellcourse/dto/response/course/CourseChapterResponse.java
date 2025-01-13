package com.example.sellcourse.dto.response.course;

import com.example.sellcourse.dto.response.chapter.ChapterResponse;
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
public class CourseChapterResponse {

    Long courseId;
    String courseTitle;
    String courseDescription;

    @Builder.Default
    Set<ChapterResponse> chapters = new HashSet<>();
}
