package com.example.sellcourse.dto.resquest.advertisement;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdsCreateRequest {
    @NotBlank(message = "EMAIL_CONTACT_INVALID")
    String contactEmail;

    @NotBlank(message = "PHONE_CONTACT_INVALID")
    String contactPhone;

    @NotBlank(message = "TITLE_ADS_INVALID")
    String title;

    String image;

    String location;

    String description;

    @NotBlank(message = "LINK_ADS_INVALID")
    String link;

    @Future(message = "START_DATE_INVALID")
    LocalDate startDate;

    @FutureOrPresent(message = "START_END_INVALID")
    LocalDate endDate;
}
