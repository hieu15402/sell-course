package com.example.sellcourse.dto.resquest.enrollment;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BuyCourseRequest {

    @NotNull(message = "COURSE_ID_INVALID")
    Long courseId;
}
