package com.example.sellcourse.dto.response.vnpay;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VNPAYResponse {
    String code;
    String message;
    String paymentUrl;
}
