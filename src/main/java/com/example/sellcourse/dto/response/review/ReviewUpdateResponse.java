package com.example.sellcourse.dto.response.review;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewUpdateResponse {
    Long id;
    String content;
}
