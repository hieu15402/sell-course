package com.example.sellcourse.repository.course;

import com.example.sellcourse.entities.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course>, CustomCourseRepository {

    @Query("SELECT c FROM Course c WHERE c.author.id = :authorId")
    List<Course> findByAuthorId(@Param("authorId") Long authorId);

    @Query("SELECT c.title FROM Course c WHERE c.title LIKE %:query%")
    List<String> findTitleSuggestions(String query);

    Page<Course> findByEnabled(boolean enabled, Pageable pageable);

    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.author WHERE c.id = :id")
    Optional<Course> findCourseDetailsById(@Param("id") Long id);
}

