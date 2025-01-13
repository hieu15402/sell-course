package com.example.sellcourse.dto.resquest.post;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LikedPostRequest {
    Long postId;
    Boolean isLike;
}
