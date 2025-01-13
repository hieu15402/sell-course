package com.example.sellcourse.dto.resquest.post;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostCreateRequest {
    @NotBlank(message = "CONTENT_POST_INVALID")
    String content;
}
