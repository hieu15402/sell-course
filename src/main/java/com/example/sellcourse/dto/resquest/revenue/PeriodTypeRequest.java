package com.example.sellcourse.dto.resquest.revenue;

import com.example.sellcourse.enums.PeriodType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PeriodTypeRequest {
    Integer year;
    Integer month;
    PeriodType periodType;
}
