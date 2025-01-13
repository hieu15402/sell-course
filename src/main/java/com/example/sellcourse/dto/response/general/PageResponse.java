package com.example.sellcourse.dto.response.general;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Collections;
import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageResponse<T> {
    int currentPage; // trang hiện tại
    int totalPages; // tổng số trang
    int pageSize;   // size của trang là bao nhiêu
    long totalElements; // tổng số phần tử, element

    @Builder.Default
    List<T> data = Collections.emptyList();

}
