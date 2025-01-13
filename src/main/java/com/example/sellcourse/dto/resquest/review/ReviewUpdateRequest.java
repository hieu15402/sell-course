package com.example.sellcourse.dto.resquest.review;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewUpdateRequest {
    String content;
    Integer rating;
}
