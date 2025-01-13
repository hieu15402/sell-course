package com.example.sellcourse.dto.response.user;

import com.example.sellcourse.enums.RegistrationStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRegisterTeacherResponse {

    Long id;
    String email;
    String fullName;
    String phone;

    String expertise;
    Double yearsOfExperience;
    String bio;
    String facebookLink;
    String certificate;
    String cvUrl;

    RegistrationStatus registrationStatus;

}
