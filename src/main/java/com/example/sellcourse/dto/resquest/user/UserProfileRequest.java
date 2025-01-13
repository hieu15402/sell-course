package com.example.sellcourse.dto.resquest.user;

import com.example.sellcourse.enums.CourseLevel;
import com.example.sellcourse.enums.Gender;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfileRequest {

    String avatar;
    String firstName;
    String lastName;
    Gender gender;
    String phone;
    LocalDate dob;
    String address;
    String description;
    CourseLevel courseLevel;
}
