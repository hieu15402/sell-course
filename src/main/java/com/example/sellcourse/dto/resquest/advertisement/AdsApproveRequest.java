package com.example.sellcourse.dto.resquest.advertisement;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdsApproveRequest {
    Long id;
    String content;

}
