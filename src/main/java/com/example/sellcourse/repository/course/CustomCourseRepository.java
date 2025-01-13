package com.example.sellcourse.repository.course;

import com.example.sellcourse.entities.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomCourseRepository {
    Page<Course> searchByMultipleKeywords(String[] keywords, Pageable pageable);
}
