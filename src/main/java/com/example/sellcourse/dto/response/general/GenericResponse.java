package com.example.sellcourse.dto.response.general;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenericResponse<T> {
    private int code = 200;
    private String message;
    private T result;

}
