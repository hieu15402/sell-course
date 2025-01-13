package com.example.sellcourse.dto.response.favorite;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FavoriteResponse {
    Integer favoriteId;
    String name;
    String author;
    String title;
    String thumbnail;
    Long points;
    Long courseId;
}
