package com.example.sellcourse.dto.response.user;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InfoTeacherByCourseResponse {

    Long id;

    String author;
    Integer reviewAmount;
    BigDecimal rating;
    Integer courseAmount;
    String description;
}
