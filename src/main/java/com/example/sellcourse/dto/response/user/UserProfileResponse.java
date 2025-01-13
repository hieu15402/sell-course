package com.example.sellcourse.dto.response.user;

import com.example.sellcourse.entities.Role;
import com.example.sellcourse.enums.CourseLevel;
import com.example.sellcourse.enums.Gender;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfileResponse {

    String avatar;
    String firstName;
    String lastName;
    Gender gender;
    String phone;
    LocalDate dob;
    String address;
    String description;
    CourseLevel courseLevel;
    Role role;
}