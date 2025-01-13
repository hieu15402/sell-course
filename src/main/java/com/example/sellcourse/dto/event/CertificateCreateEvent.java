package com.example.sellcourse.dto.event;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CertificateCreateEvent {
    Long userId;
    Long courseId;
}
