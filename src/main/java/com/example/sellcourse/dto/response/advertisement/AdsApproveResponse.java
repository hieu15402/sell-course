package com.example.sellcourse.dto.response.advertisement;

import com.example.sellcourse.enums.AdsStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdsApproveResponse {
    Long id;
    String contactEmail;
    String contactPhone;
    String title;
    String description;
    String imageUrl;
    String link;
    LocalDate startDate;
    LocalDate endDate;
    BigDecimal priceAds;
    AdsStatus status;
}
