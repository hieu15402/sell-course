package com.example.sellcourse.dto.response.certificate;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class CertificateResponse {

    Long certificateId;
    String courseName;
    String email;
    String username;
    String author;
    @JsonFormat(pattern = "EEEE, dd MMMM yyyy")
    LocalDate issueDate;
    String certificateUrl;

}
